// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.Col

private[common] object InstructionDefs11 {
  case object SLoadB32 extends InstructionDef(
        "s_load_b32",
        decoder(form(3)((operands, mods) => Instruction.SLoadB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            0,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SMEM",
            0,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SMEM",
            0,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            0,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SLoadB512 extends InstructionDef(
        "s_load_b512",
        decoder(form(3)((operands, mods) => Instruction.SLoadB512(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            4,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SMEM",
            4,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SMEM",
            4,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            4,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SLoadB64 extends InstructionDef(
        "s_load_b64",
        decoder(form(3)((operands, mods) => Instruction.SLoadB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            1,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SMEM",
            1,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SMEM",
            1,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            1,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SLoadB96 extends InstructionDef(
        "s_load_b96",
        decoder(form(3)((operands, mods) => Instruction.SLoadB96(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            5,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 96, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            5,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 96, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SLoadDword extends InstructionDef(
        "s_load_dword",
        decoder(form(3)((operands, mods) => Instruction.SLoadDword(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            0,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            0,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            0,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            0,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SMEM",
            0,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SMEM",
            0,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SMEM",
            0,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SMEM",
            0,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            0,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            0,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SLoadDwordx16 extends InstructionDef(
        "s_load_dwordx16",
        decoder(form(3)((operands, mods) => Instruction.SLoadDwordx16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            4,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            4,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            4,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            4,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SMEM",
            4,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SMEM",
            4,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SMEM",
            4,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SMEM",
            4,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            4,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            4,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SLoadDwordx2 extends InstructionDef(
        "s_load_dwordx2",
        decoder(form(3)((operands, mods) => Instruction.SLoadDwordx2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            1,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            1,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            1,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            1,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SMEM",
            1,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SMEM",
            1,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SMEM",
            1,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SMEM",
            1,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            1,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            1,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SLoadDwordx4 extends InstructionDef(
        "s_load_dwordx4",
        decoder(form(3)((operands, mods) => Instruction.SLoadDwordx4(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
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
            Generation.CDNA3,
            "cdna3",
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
            Generation.CDNA4,
            "cdna4",
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
            Generation.GFX10,
            "rdna1",
            "SMEM",
            2,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SMEM",
            2,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SMEM",
            2,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SMEM",
            2,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            2,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            2,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SLoadDwordx8 extends InstructionDef(
        "s_load_dwordx8",
        decoder(form(3)((operands, mods) => Instruction.SLoadDwordx8(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
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
            Generation.CDNA3,
            "cdna3",
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
            Generation.CDNA4,
            "cdna4",
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
            Generation.GFX10,
            "rdna1",
            "SMEM",
            3,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SMEM",
            3,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SMEM",
            3,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SMEM",
            3,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            3,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            3,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SLoadI16 extends InstructionDef(
        "s_load_i16",
        decoder(form(3)((operands, mods) => Instruction.SLoadI16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            10,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            10,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SLoadI8 extends InstructionDef(
        "s_load_i8",
        decoder(form(3)((operands, mods) => Instruction.SLoadI8(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            8,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            8,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SLoadU16 extends InstructionDef(
        "s_load_u16",
        decoder(form(3)((operands, mods) => Instruction.SLoadU16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            11,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            11,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SLoadU8 extends InstructionDef(
        "s_load_u8",
        decoder(form(3)((operands, mods) => Instruction.SLoadU8(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            9,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            9,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SLshl1AddU32 extends InstructionDef(
        "s_lshl1_add_u32",
        decoder(form(3)((operands, mods) => Instruction.SLshl1AddU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            46,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SLshl2AddU32 extends InstructionDef(
        "s_lshl2_add_u32",
        decoder(form(3)((operands, mods) => Instruction.SLshl2AddU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            47,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            47,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SLshl3AddU32 extends InstructionDef(
        "s_lshl3_add_u32",
        decoder(form(3)((operands, mods) => Instruction.SLshl3AddU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            48,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            48,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SLshl4AddU32 extends InstructionDef(
        "s_lshl4_add_u32",
        decoder(form(3)((operands, mods) => Instruction.SLshl4AddU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            49,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            49,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SLshlB32 extends InstructionDef(
        "s_lshl_b32",
        decoder(form(3)((operands, mods) => Instruction.SLshlB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            30,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            30,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            28,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            28,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SLshlB64 extends InstructionDef(
        "s_lshl_b64",
        decoder(form(3)((operands, mods) => Instruction.SLshlB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP2",
            31,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP2",
            31,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP2",
            29,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            29,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SLshrB32 extends InstructionDef(
        "s_lshr_b32",
        decoder(form(3)((operands, mods) => Instruction.SLshrB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            32,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            32,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            32,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            32,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            32,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            30,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            30,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SLshrB64 extends InstructionDef(
        "s_lshr_b64",
        decoder(form(3)((operands, mods) => Instruction.SLshrB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2",
            33,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2",
            33,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            33,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP2",
            33,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP2",
            33,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP2",
            31,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            31,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMaxF16 extends InstructionDef(
        "s_max_f16",
        decoder(form(3)((operands, mods) => Instruction.SMaxF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            76,
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
            76,
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
            76,
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
            76,
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
  case object SMaxF32 extends InstructionDef(
        "s_max_f32",
        decoder(form(3)((operands, mods) => Instruction.SMaxF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            67,
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
            67,
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
            67,
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
            67,
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
  case object SMaxI32 extends InstructionDef(
        "s_max_i32",
        decoder(form(3)((operands, mods) => Instruction.SMaxI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            8,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMaxNumF16 extends InstructionDef(
        "s_max_num_f16",
        decoder(form(3)((operands, mods) => Instruction.SMaxNumF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            76,
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
            76,
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
            76,
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
            76,
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
            76,
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
            76,
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
            76,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            76,
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
            76,
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
            76,
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
            76,
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
  case object SMaxNumF32 extends InstructionDef(
        "s_max_num_f32",
        decoder(form(3)((operands, mods) => Instruction.SMaxNumF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            67,
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
            67,
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
            67,
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
            67,
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
            67,
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
            67,
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
            67,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            67,
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
            67,
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
            67,
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
            67,
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
  case object SMaxU32 extends InstructionDef(
        "s_max_u32",
        decoder(form(3)((operands, mods) => Instruction.SMaxU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            9,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMaximumF16 extends InstructionDef(
        "s_maximum_f16",
        decoder(form(3)((operands, mods) => Instruction.SMaximumF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            82,
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
            82,
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
            82,
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
            82,
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
            82,
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
            82,
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
            82,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            82,
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
            82,
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
            82,
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
            82,
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
  case object SMaximumF32 extends InstructionDef(
        "s_maximum_f32",
        decoder(form(3)((operands, mods) => Instruction.SMaximumF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            80,
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
            80,
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
            80,
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
            80,
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
            80,
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
            80,
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
            80,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            80,
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
            80,
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
            80,
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
            80,
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
  case object SMemrealtime extends InstructionDef(
        "s_memrealtime",
        decoder(form(1)((operands, mods) => Instruction.SMemrealtime(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            37,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            37,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            37,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            37,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SMEM",
            37,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SMEM",
            37,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            37,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            37,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMemtime extends InstructionDef(
        "s_memtime",
        decoder(form(1)((operands, mods) => Instruction.SMemtime(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            36,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            36,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            36,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            36,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SMEM",
            36,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SMEM",
            36,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SMEM",
            30,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SMEM",
            30,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            36,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            36,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMinF16 extends InstructionDef(
        "s_min_f16",
        decoder(form(3)((operands, mods) => Instruction.SMinF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            75,
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
            75,
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
            75,
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
            75,
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
  case object SMinF32 extends InstructionDef(
        "s_min_f32",
        decoder(form(3)((operands, mods) => Instruction.SMinF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            66,
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
            66,
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
            66,
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
            66,
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
  case object SMinI32 extends InstructionDef(
        "s_min_i32",
        decoder(form(3)((operands, mods) => Instruction.SMinI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            6,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            6,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            6,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            6,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMinNumF16 extends InstructionDef(
        "s_min_num_f16",
        decoder(form(3)((operands, mods) => Instruction.SMinNumF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            75,
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
            75,
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
            75,
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
            75,
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
            75,
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
            75,
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
            75,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            75,
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
            75,
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
            75,
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
            75,
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
  case object SMinNumF32 extends InstructionDef(
        "s_min_num_f32",
        decoder(form(3)((operands, mods) => Instruction.SMinNumF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            66,
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
            66,
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
            66,
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
            66,
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
            66,
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
            66,
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
            66,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            66,
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
            66,
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
            66,
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
            66,
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
  case object SMinU32 extends InstructionDef(
        "s_min_u32",
        decoder(form(3)((operands, mods) => Instruction.SMinU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            19,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            7,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SMinimumF16 extends InstructionDef(
        "s_minimum_f16",
        decoder(form(3)((operands, mods) => Instruction.SMinimumF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            81,
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
            81,
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
            81,
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
            81,
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
            81,
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
            81,
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
            81,
            Col(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            81,
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
            81,
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
            81,
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
            81,
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
  case object SMinimumF32 extends InstructionDef(
        "s_minimum_f32",
        decoder(form(3)((operands, mods) => Instruction.SMinimumF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            79,
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
            79,
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
            79,
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
            79,
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
            79,
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
            79,
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
            79,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            79,
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
            79,
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
            79,
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
            79,
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
  case object SMonitorSleep extends InstructionDef(
        "s_monitor_sleep",
        decoder(form(1)((operands, mods) => Instruction.SMonitorSleep(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            4,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SMovB32 extends InstructionDef(
        "s_mov_b32",
        decoder(form(2)((operands, mods) => Instruction.SMovB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
            Generation.CDNA1,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
            Generation.GFX6,
            "gfx6",
            "SOP1",
            53,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP1",
            53,
            Col.empty,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
  case object SMovreldB32 extends InstructionDef(
        "s_movreld_b32",
        decoder(form(2)((operands, mods) => Instruction.SMovreldB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
            Generation.CDNA1,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
  case object SMulF16 extends InstructionDef(
        "s_mul_f16",
        decoder(form(3)((operands, mods) => Instruction.SMulF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            77,
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
            77,
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
            77,
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
            77,
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
            77,
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
            77,
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
            77,
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
            77,
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
            77,
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
            77,
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
            77,
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
            77,
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
            77,
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
            77,
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
            77,
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
  case object SMulF32 extends InstructionDef(
        "s_mul_f32",
        decoder(form(3)((operands, mods) => Instruction.SMulF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
  case object SMulHiI32 extends InstructionDef(
        "s_mul_hi_i32",
        decoder(form(3)((operands, mods) => Instruction.SMulHiI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            45,
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
            "SOP2",
            46,
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
            46,
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
            46,
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
            46,
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
            46,
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
            46,
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
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2",
            54,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2",
            54,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            54,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            46,
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
            "rdna3",
            "SOP2_INST_LITERAL",
            46,
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
            "rdna3",
            "SOP2_INST_LITERAL",
            46,
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
            "rdna3",
            "SOP2_INST_LITERAL",
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            46,
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
            46,
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
            46,
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
            46,
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
            46,
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
            46,
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
            46,
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
            46,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            45,
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
  case object SMulHiU32 extends InstructionDef(
        "s_mul_hi_u32",
        decoder(form(3)((operands, mods) => Instruction.SMulHiU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            44,
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
            "SOP2",
            45,
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
            45,
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
            45,
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
            45,
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
            45,
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
            45,
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
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            45,
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
            "rdna3",
            "SOP2_INST_LITERAL",
            45,
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
            "rdna3",
            "SOP2_INST_LITERAL",
            45,
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
            "rdna3",
            "SOP2_INST_LITERAL",
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            45,
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
            45,
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
            45,
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
            45,
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
            45,
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
            45,
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
            45,
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
            45,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            44,
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
  case object SMulI32 extends InstructionDef(
        "s_mul_i32",
        decoder(form(3)((operands, mods) => Instruction.SMulI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            36,
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
            "SOP2",
            44,
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
            44,
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
            44,
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
            44,
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
            44,
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
            44,
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
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2",
            38,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2",
            38,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            38,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            44,
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
            "rdna3",
            "SOP2_INST_LITERAL",
            44,
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
            "rdna3",
            "SOP2_INST_LITERAL",
            44,
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
            "rdna3",
            "SOP2_INST_LITERAL",
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            44,
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
            44,
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
            44,
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
            44,
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
            44,
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
            44,
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
            44,
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
            44,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP2",
            38,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP2",
            38,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP2",
            36,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            36,
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
  case object SMulU64 extends InstructionDef(
        "s_mul_u64",
        decoder(form(3)((operands, mods) => Instruction.SMulU64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            85,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            85,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            85,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            85,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            85,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            85,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SMulkI32 extends InstructionDef(
        "s_mulk_i32",
        decoder(form(2)((operands, mods) => Instruction.SMulkI32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
  case object SNandB32 extends InstructionDef(
        "s_nand_b32",
        decoder(form(3)((operands, mods) => Instruction.SNandB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            28,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            24,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            24,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            22,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            22,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SNandB64 extends InstructionDef(
        "s_nand_b64",
        decoder(form(3)((operands, mods) => Instruction.SNandB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL64",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL64",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL64",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            29,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP2",
            25,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP2",
            25,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP2",
            23,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
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
            "SOP2",
            23,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SNandSaveexecB32 extends InstructionDef(
        "s_nand_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SNandSaveexecB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.CDNA1,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
  case object SNop extends InstructionDef(
        "s_nop",
        decoder(form(1)((operands, mods) => Instruction.SNop(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            0,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            0,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            0,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            0,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            0,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            0,
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
            0,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            0,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            0,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            0,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            0,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            0,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            0,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            0,
            Col.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SNorB32 extends InstructionDef(
        "s_nor_b32",
        decoder(form(3)((operands, mods) => Instruction.SNorB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            26,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            30,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            26,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            26,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            24,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            24,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SNorB64 extends InstructionDef(
        "s_nor_b64",
        decoder(form(3)((operands, mods) => Instruction.SNorB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL64",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL64",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL64",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            27,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            31,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP2",
            27,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP2",
            27,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP2",
            25,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
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
            "SOP2",
            25,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.CDNA1,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
            Generation.CDNA1,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
            Generation.CDNA1,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
  case object SOrB32 extends InstructionDef(
        "s_or_b32",
        decoder(form(3)((operands, mods) => Instruction.SOrB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            14,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            24,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            16,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            16,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            14,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            14,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SOrB64 extends InstructionDef(
        "s_or_b64",
        decoder(form(3)((operands, mods) => Instruction.SOrB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            15,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL64",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL64",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL64",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            25,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP2",
            17,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP2",
            17,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP2",
            15,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
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
            "SOP2",
            15,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
  case object SOrNot1B32 extends InstructionDef(
        "s_or_not1_b32",
        decoder(form(3)((operands, mods) => Instruction.SOrNot1B32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL64",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            36,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SOrNot1B64 extends InstructionDef(
        "s_or_not1_b64",
        decoder(form(3)((operands, mods) => Instruction.SOrNot1B64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL64",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL64",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL64",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            37,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.CDNA1,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.CDNA1,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA4,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX9,
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
  case object SOrn2B32 extends InstructionDef(
        "s_orn2_b32",
        decoder(form(3)((operands, mods) => Instruction.SOrn2B32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            20,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            "SOP2_INST_LITERAL",
            22,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP2",
            22,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            22,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            20,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
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
            "SOP2",
            20,
            Col(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SOrn2B64 extends InstructionDef(
        "s_orn2_b64",
        decoder(form(3)((operands, mods) => Instruction.SOrn2B64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            21,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
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
            "SOP2_INST_LITERAL",
            23,
            Col(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP2",
            23,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP2",
            23,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP2",
            21,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
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
            "SOP2",
            21,
            Col(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false),
              OperandInfo(None, "uint", 64, true, false, false)
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.CDNA1,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA4,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
  case object SPackHhB32B16 extends InstructionDef(
        "s_pack_hh_b32_b16",
        decoder(form(3)((operands, mods) => Instruction.SPackHhB32B16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            52,
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
            "SOP2",
            52,
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
            52,
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
            52,
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
            52,
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
            52,
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
            52,
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
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            52,
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
            "rdna3",
            "SOP2_INST_LITERAL",
            52,
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
            "rdna3",
            "SOP2_INST_LITERAL",
            52,
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
            "rdna3",
            "SOP2_INST_LITERAL",
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            52,
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
            52,
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
            52,
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
            52,
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
            52,
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
            52,
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
            52,
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
            52,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            52,
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
  case object SPackHlB32B16 extends InstructionDef(
        "s_pack_hl_b32_b16",
        decoder(form(3)((operands, mods) => Instruction.SPackHlB32B16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            53,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SPackLhB32B16 extends InstructionDef(
        "s_pack_lh_b32_b16",
        decoder(form(3)((operands, mods) => Instruction.SPackLhB32B16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            51,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            51,
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
  case object SPackLlB32B16 extends InstructionDef(
        "s_pack_ll_b32_b16",
        decoder(form(3)((operands, mods) => Instruction.SPackLlB32B16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
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
            50,
            Col(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            50,
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
  case object SPrefetchData extends InstructionDef(
        "s_prefetch_data",
        decoder(form(4)((operands, mods) =>
          Instruction.SPrefetchData(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            38,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("IOFFSET"), "SIMM24", 32, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET_NOK", 32, true, false, false),
              OperandInfo(Some("SDATA"), "SIMM5", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            38,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("IOFFSET"), "SIMM24", 32, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET_NOK", 32, true, false, false),
              OperandInfo(Some("SDATA"), "SIMM5", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SPrefetchDataPcRel extends InstructionDef(
        "s_prefetch_data_pc_rel",
        decoder(form(3)((operands, mods) =>
          Instruction.SPrefetchDataPcRel(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            40,
            Col(
              OperandInfo(Some("IOFFSET"), "SIMM24", 32, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET_NOK", 32, true, false, false),
              OperandInfo(Some("SDATA"), "SIMM5", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            40,
            Col(
              OperandInfo(Some("IOFFSET"), "SIMM24", 32, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET_NOK", 32, true, false, false),
              OperandInfo(Some("SDATA"), "SIMM5", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SPrefetchInst extends InstructionDef(
        "s_prefetch_inst",
        decoder(form(4)((operands, mods) =>
          Instruction.SPrefetchInst(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            36,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("IOFFSET"), "SIMM24", 32, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET_NOK", 32, true, false, false),
              OperandInfo(Some("SDATA"), "SIMM5", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            36,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("IOFFSET"), "SIMM24", 32, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET_NOK", 32, true, false, false),
              OperandInfo(Some("SDATA"), "SIMM5", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SPrefetchInstPcRel extends InstructionDef(
        "s_prefetch_inst_pc_rel",
        decoder(form(3)((operands, mods) =>
          Instruction.SPrefetchInstPcRel(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            37,
            Col(
              OperandInfo(Some("IOFFSET"), "SIMM24", 32, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET_NOK", 32, true, false, false),
              OperandInfo(Some("SDATA"), "SIMM5", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            37,
            Col(
              OperandInfo(Some("IOFFSET"), "SIMM24", 32, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET_NOK", 32, true, false, false),
              OperandInfo(Some("SDATA"), "SIMM5", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SQuadmaskB32 extends InstructionDef(
        "s_quadmask_b32",
        decoder(form(2)((operands, mods) => Instruction.SQuadmaskB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
            Generation.CDNA1,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.CDNA4,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
            Generation.CDNA1,
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
            Generation.CDNA2,
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
            Generation.CDNA3,
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
            Generation.CDNA4,
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
            Generation.GFX10,
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
            Generation.GFX10,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX6,
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
            Generation.GFX7,
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
            Generation.GFX8,
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
            Generation.GFX9,
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
            Generation.CDNA5,
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
  case object SRfeRestoreB64 extends InstructionDef(
        "s_rfe_restore_b64",
        decoder(form(2)((operands, mods) => Instruction.SRfeRestoreB64(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            43,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2",
            43,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2",
            43,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2",
            43,
            Col(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP2",
            43,
            Col.empty,
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            43,
            Col.empty,
            Some("branch"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SRndneF16 extends InstructionDef(
        "s_rndne_f16",
        decoder(form(2)((operands, mods) => Instruction.SRndneF16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.CDNA5,
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
            Generation.GFX11,
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
            Generation.GFX11,
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
            Generation.GFX12,
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
            Generation.GFX12,
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
  case object SRoundMode extends InstructionDef(
        "s_round_mode",
        decoder(form(1)((operands, mods) => Instruction.SRoundMode(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            17,
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
            36,
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
            36,
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
            17,
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
            17,
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
            17,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SScratchLoadDword extends InstructionDef(
        "s_scratch_load_dword",
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchLoadDword(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            5,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            5,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            5,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            5,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SMEM",
            5,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            5,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(
    SLoadB32,
    SLoadB512,
    SLoadB64,
    SLoadB96,
    SLoadDword,
    SLoadDwordx16,
    SLoadDwordx2,
    SLoadDwordx4,
    SLoadDwordx8,
    SLoadI16,
    SLoadI8,
    SLoadU16,
    SLoadU8,
    SLshl1AddU32,
    SLshl2AddU32,
    SLshl3AddU32,
    SLshl4AddU32,
    SLshlB32,
    SLshlB64,
    SLshrB32,
    SLshrB64,
    SMaxF16,
    SMaxF32,
    SMaxI32,
    SMaxNumF16,
    SMaxNumF32,
    SMaxU32,
    SMaximumF16,
    SMaximumF32,
    SMemrealtime,
    SMemtime,
    SMinF16,
    SMinF32,
    SMinI32,
    SMinNumF16,
    SMinNumF32,
    SMinU32,
    SMinimumF16,
    SMinimumF32,
    SMonitorSleep,
    SMovB32,
    SMovB64,
    SMovFedB32,
    SMovkI32,
    SMovreldB32,
    SMovreldB64,
    SMovrelsB32,
    SMovrelsB64,
    SMovrelsd2B32,
    SMulF16,
    SMulF32,
    SMulHiI32,
    SMulHiU32,
    SMulI32,
    SMulU64,
    SMulkI32,
    SNandB32,
    SNandB64,
    SNandSaveexecB32,
    SNandSaveexecB64,
    SNop,
    SNorB32,
    SNorB64,
    SNorSaveexecB32,
    SNorSaveexecB64,
    SNotB32,
    SNotB64,
    SOrB32,
    SOrB64,
    SOrNot0SaveexecB32,
    SOrNot0SaveexecB64,
    SOrNot1B32,
    SOrNot1B64,
    SOrNot1SaveexecB32,
    SOrNot1SaveexecB64,
    SOrSaveexecB32,
    SOrSaveexecB64,
    SOrn1SaveexecB32,
    SOrn1SaveexecB64,
    SOrn2B32,
    SOrn2B64,
    SOrn2SaveexecB32,
    SOrn2SaveexecB64,
    SPackHhB32B16,
    SPackHlB32B16,
    SPackLhB32B16,
    SPackLlB32B16,
    SPrefetchData,
    SPrefetchDataPcRel,
    SPrefetchInst,
    SPrefetchInstPcRel,
    SQuadmaskB32,
    SQuadmaskB64,
    SRfeB64,
    SRfeI64,
    SRfeRestoreB64,
    SRndneF16,
    SRndneF32,
    SRoundMode,
    SScratchLoadDword
  )
}
