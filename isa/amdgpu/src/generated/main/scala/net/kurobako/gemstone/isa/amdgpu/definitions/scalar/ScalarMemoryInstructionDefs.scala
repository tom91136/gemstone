// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object ScalarMemoryInstructionDefs {
  case object SAtcProbe extends InstructionDef(
        "s_atc_probe",
        decoder(form(3)((operands, mods) => Instruction.SAtcProbe(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            38,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            38,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            38,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            38,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            34,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SMEM",
            38,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SMEM",
            34,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            34,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            38,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            38,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtcProbeBuffer extends InstructionDef(
        "s_atc_probe_buffer",
        decoder(form(3)((operands, mods) => Instruction.SAtcProbeBuffer(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            39,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            39,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            39,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            39,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            35,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SMEM",
            39,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SMEM",
            35,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            35,
            Col(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            39,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            39,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicAdd extends InstructionDef(
        "s_atomic_add",
        decoder(form(3)((operands, mods) => Instruction.SAtomicAdd(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            130,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            130,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            130,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            130,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            130,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicAddX2 extends InstructionDef(
        "s_atomic_add_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicAddX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            162,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            162,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            162,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            162,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            162,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicAnd extends InstructionDef(
        "s_atomic_and",
        decoder(form(3)((operands, mods) => Instruction.SAtomicAnd(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            136,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            136,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            136,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            136,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            136,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicAndX2 extends InstructionDef(
        "s_atomic_and_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicAndX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            168,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            168,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            168,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            168,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            168,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicCmpswap extends InstructionDef(
        "s_atomic_cmpswap",
        decoder(form(3)((operands, mods) => Instruction.SAtomicCmpswap(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            129,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            129,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            129,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            129,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            129,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicCmpswapX2 extends InstructionDef(
        "s_atomic_cmpswap_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicCmpswapX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            161,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            161,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            161,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            161,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            161,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicDec extends InstructionDef(
        "s_atomic_dec",
        decoder(form(3)((operands, mods) => Instruction.SAtomicDec(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            140,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            140,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            140,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            140,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            140,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicDecX2 extends InstructionDef(
        "s_atomic_dec_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicDecX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            172,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            172,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            172,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            172,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            172,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicInc extends InstructionDef(
        "s_atomic_inc",
        decoder(form(3)((operands, mods) => Instruction.SAtomicInc(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            139,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            139,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            139,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            139,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            139,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicIncX2 extends InstructionDef(
        "s_atomic_inc_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicIncX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            171,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            171,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            171,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            171,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            171,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicOr extends InstructionDef(
        "s_atomic_or",
        decoder(form(3)((operands, mods) => Instruction.SAtomicOr(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            137,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            137,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            137,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            137,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            137,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicOrX2 extends InstructionDef(
        "s_atomic_or_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicOrX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            169,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            169,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            169,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            169,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            169,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSmax extends InstructionDef(
        "s_atomic_smax",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSmax(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            134,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            134,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            134,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            134,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            134,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSmaxX2 extends InstructionDef(
        "s_atomic_smax_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSmaxX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            166,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            166,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            166,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            166,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            166,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSmin extends InstructionDef(
        "s_atomic_smin",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSmin(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            132,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            132,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            132,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            132,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            132,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSminX2 extends InstructionDef(
        "s_atomic_smin_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSminX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            164,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            164,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            164,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            164,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            164,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSub extends InstructionDef(
        "s_atomic_sub",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSub(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            131,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            131,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            131,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            131,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            131,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSubX2 extends InstructionDef(
        "s_atomic_sub_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSubX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            163,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            163,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            163,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            163,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            163,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSwap extends InstructionDef(
        "s_atomic_swap",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSwap(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            128,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            128,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            128,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            128,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            128,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSwapX2 extends InstructionDef(
        "s_atomic_swap_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSwapX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            160,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            160,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            160,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            160,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            160,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicUmax extends InstructionDef(
        "s_atomic_umax",
        decoder(form(3)((operands, mods) => Instruction.SAtomicUmax(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            135,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            135,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            135,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            135,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            135,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicUmaxX2 extends InstructionDef(
        "s_atomic_umax_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicUmaxX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            167,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            167,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            167,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            167,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            167,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicUmin extends InstructionDef(
        "s_atomic_umin",
        decoder(form(3)((operands, mods) => Instruction.SAtomicUmin(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            133,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            133,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            133,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            133,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            133,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicUminX2 extends InstructionDef(
        "s_atomic_umin_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicUminX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            165,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            165,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            165,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            165,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            165,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicXor extends InstructionDef(
        "s_atomic_xor",
        decoder(form(3)((operands, mods) => Instruction.SAtomicXor(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            138,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            138,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            138,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            138,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            138,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicXorX2 extends InstructionDef(
        "s_atomic_xor_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicXorX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            170,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            170,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            170,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            170,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            170,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicAdd extends InstructionDef(
        "s_buffer_atomic_add",
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicAdd(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            66,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            66,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            66,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            66,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            66,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicAddX2 extends InstructionDef(
        "s_buffer_atomic_add_x2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicAddX2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            98,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            98,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            98,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            98,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            98,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicAnd extends InstructionDef(
        "s_buffer_atomic_and",
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicAnd(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            72,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            72,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            72,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            72,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            72,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicAndX2 extends InstructionDef(
        "s_buffer_atomic_and_x2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicAndX2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            104,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            104,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            104,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            104,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            104,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicCmpswap extends InstructionDef(
        "s_buffer_atomic_cmpswap",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicCmpswap(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            65,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            65,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            65,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            65,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            65,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicCmpswapX2 extends InstructionDef(
        "s_buffer_atomic_cmpswap_x2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicCmpswapX2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            97,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            97,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            97,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            97,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            97,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicDec extends InstructionDef(
        "s_buffer_atomic_dec",
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicDec(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            76,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            76,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            76,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            76,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            76,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicDecX2 extends InstructionDef(
        "s_buffer_atomic_dec_x2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicDecX2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            108,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            108,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            108,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            108,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            108,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicInc extends InstructionDef(
        "s_buffer_atomic_inc",
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicInc(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            75,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            75,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            75,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            75,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            75,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicIncX2 extends InstructionDef(
        "s_buffer_atomic_inc_x2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicIncX2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            107,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            107,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            107,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            107,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            107,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicOr extends InstructionDef(
        "s_buffer_atomic_or",
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicOr(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            73,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            73,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            73,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            73,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            73,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicOrX2 extends InstructionDef(
        "s_buffer_atomic_or_x2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicOrX2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            105,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            105,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            105,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            105,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            105,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicSmax extends InstructionDef(
        "s_buffer_atomic_smax",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSmax(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            70,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            70,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            70,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            70,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            70,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicSmaxX2 extends InstructionDef(
        "s_buffer_atomic_smax_x2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSmaxX2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            102,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            102,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            102,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            102,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            102,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicSmin extends InstructionDef(
        "s_buffer_atomic_smin",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSmin(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            68,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            68,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            68,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            68,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            68,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicSminX2 extends InstructionDef(
        "s_buffer_atomic_smin_x2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSminX2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            100,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            100,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            100,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            100,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            100,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicSub extends InstructionDef(
        "s_buffer_atomic_sub",
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicSub(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            67,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            67,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            67,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            67,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            67,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicSubX2 extends InstructionDef(
        "s_buffer_atomic_sub_x2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSubX2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            99,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            99,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            99,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            99,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            99,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicSwap extends InstructionDef(
        "s_buffer_atomic_swap",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSwap(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            64,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            64,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            64,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            64,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            64,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicSwapX2 extends InstructionDef(
        "s_buffer_atomic_swap_x2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSwapX2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            96,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            96,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            96,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            96,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            96,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicUmax extends InstructionDef(
        "s_buffer_atomic_umax",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicUmax(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            71,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            71,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            71,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            71,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            71,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicUmaxX2 extends InstructionDef(
        "s_buffer_atomic_umax_x2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicUmaxX2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            103,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            103,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            103,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            103,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            103,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicUmin extends InstructionDef(
        "s_buffer_atomic_umin",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicUmin(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            69,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            69,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            69,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            69,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            69,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicUminX2 extends InstructionDef(
        "s_buffer_atomic_umin_x2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicUminX2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            101,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            101,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            101,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            101,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            101,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicXor extends InstructionDef(
        "s_buffer_atomic_xor",
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicXor(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            74,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            74,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            74,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            74,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            74,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferAtomicXorX2 extends InstructionDef(
        "s_buffer_atomic_xor_x2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicXorX2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            106,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            106,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            106,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            106,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            106,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferLoadB128 extends InstructionDef(
        "s_buffer_load_b128",
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadB128(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            18,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SMEM",
            10,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SMEM",
            10,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            18,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBufferLoadB256 extends InstructionDef(
        "s_buffer_load_b256",
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadB256(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            19,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SMEM",
            11,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SMEM",
            11,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            19,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBufferLoadB32 extends InstructionDef(
        "s_buffer_load_b32",
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            16,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SMEM",
            8,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SMEM",
            8,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            16,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBufferLoadB512 extends InstructionDef(
        "s_buffer_load_b512",
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadB512(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            20,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SMEM",
            12,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SMEM",
            12,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            20,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBufferLoadB64 extends InstructionDef(
        "s_buffer_load_b64",
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            17,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SMEM",
            9,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SMEM",
            9,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            17,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBufferLoadB96 extends InstructionDef(
        "s_buffer_load_b96",
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadB96(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            21,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 96, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            21,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 96, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBufferLoadDword extends InstructionDef(
        "s_buffer_load_dword",
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadDword(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            8,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            8,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            8,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            8,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SMEM",
            8,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SMEM",
            8,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SMEM",
            8,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SMEM",
            8,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            8,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            8,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferLoadDwordx16 extends InstructionDef(
        "s_buffer_load_dwordx16",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferLoadDwordx16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            12,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            12,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            12,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            12,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SMEM",
            12,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SMEM",
            12,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 512, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 512, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SMEM",
            12,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SMEM",
            12,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            12,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            12,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferLoadDwordx2 extends InstructionDef(
        "s_buffer_load_dwordx2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferLoadDwordx2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            9,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            9,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            9,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            9,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SMEM",
            9,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SMEM",
            9,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SMEM",
            9,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SMEM",
            9,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            9,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            9,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferLoadDwordx4 extends InstructionDef(
        "s_buffer_load_dwordx4",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferLoadDwordx4(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            10,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            10,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            10,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            10,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SMEM",
            10,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SMEM",
            10,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SMEM",
            10,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SMEM",
            10,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            10,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            10,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferLoadDwordx8 extends InstructionDef(
        "s_buffer_load_dwordx8",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferLoadDwordx8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            11,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            11,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            11,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            11,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SMEM",
            11,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SMEM",
            11,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 256, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 256, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SMEM",
            11,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SMEM",
            11,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            11,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            11,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferLoadI16 extends InstructionDef(
        "s_buffer_load_i16",
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadI16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            26,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            26,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBufferLoadI8 extends InstructionDef(
        "s_buffer_load_i8",
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadI8(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            24,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            24,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBufferLoadU16 extends InstructionDef(
        "s_buffer_load_u16",
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadU16(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            27,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            27,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBufferLoadU8 extends InstructionDef(
        "s_buffer_load_u8",
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadU8(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            25,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            25,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBufferPrefetchData extends InstructionDef(
        "s_buffer_prefetch_data",
        decoder(form(4)((operands, mods) =>
          Instruction.SBufferPrefetchData(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            39,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("IOFFSET"), "SIMM24", 32, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET_NOK", 32, true, false, false),
              OperandInfo(Some("SDATA"), "SIMM5", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            39,
            Col(
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("IOFFSET"), "SIMM24", 32, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET_NOK", 32, true, false, false),
              OperandInfo(Some("SDATA"), "SIMM5", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBufferStoreDword extends InstructionDef(
        "s_buffer_store_dword",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferStoreDword(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            24,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            24,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            24,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            24,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            24,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            24,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferStoreDwordx2 extends InstructionDef(
        "s_buffer_store_dwordx2",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferStoreDwordx2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            25,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            25,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            25,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            25,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            25,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            25,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBufferStoreDwordx4 extends InstructionDef(
        "s_buffer_store_dwordx4",
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferStoreDwordx4(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            26,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            26,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            26,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            26,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            26,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            26,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SDcacheDiscard extends InstructionDef(
        "s_dcache_discard",
        decoder(form(2)((operands, mods) => Instruction.SDcacheDiscard(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX9,
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
            Family.CDNA1,
            "cdna1",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "SMEM",
            31,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SMEM",
            31,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            32,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
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
            Family.CDNA1,
            "cdna1",
            "SMEM",
            34,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            34,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            34,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            34,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SMEM",
            29,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            34,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
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
            Family.CDNA1,
            "cdna1",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            33,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            33,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
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
            Family.CDNA1,
            "cdna1",
            "SMEM",
            35,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            35,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            35,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            35,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            35,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            35,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SGetWaveidInWorkgroup extends InstructionDef(
        "s_get_waveid_in_workgroup",
        decoder(form(1)((operands, mods) => Instruction.SGetWaveidInWorkgroup(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
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
  case object SGl1Inv extends InstructionDef(
        "s_gl1_inv",
        decoder(form(0)((_, mods) => Instruction.SGl1Inv(mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "SMEM",
            31,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "SMEM",
            31,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "SMEM",
            32,
            Col.empty,
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            31,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            31,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SLoadB128 extends InstructionDef(
        "s_load_b128",
        decoder(form(3)((operands, mods) => Instruction.SLoadB128(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
  case object SLoadB32 extends InstructionDef(
        "s_load_b32",
        decoder(form(3)((operands, mods) => Instruction.SLoadB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.GFX11,
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
            Family.GFX11,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.GFX12,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
            "gfx6",
            "SMEM",
            0,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SMEM",
            0,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            0,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
            "gfx6",
            "SMEM",
            4,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SMEM",
            4,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            4,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
            "gfx6",
            "SMEM",
            1,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SMEM",
            1,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            1,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
            "gfx6",
            "SMEM",
            2,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SMEM",
            2,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            2,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
            "gfx6",
            "SMEM",
            3,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SMEM",
            3,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            3,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
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
            Family.CDNA5,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.GFX12,
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
  case object SMemrealtime extends InstructionDef(
        "s_memrealtime",
        decoder(form(1)((operands, mods) => Instruction.SMemrealtime(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX8,
            "gfx8",
            "SMEM",
            37,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
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
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX10,
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
            Family.GFX6,
            "gfx6",
            "SMEM",
            30,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "SMEM",
            30,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            36,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            36,
            Col.empty,
            Some("smem"),
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
            Family.CDNA5,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.GFX12,
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
            Family.CDNA5,
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
            Family.GFX12,
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
  case object SScratchLoadDword extends InstructionDef(
        "s_scratch_load_dword",
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchLoadDword(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
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
            Family.CDNA2,
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
            Family.CDNA3,
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
            Family.CDNA4,
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
            Family.GFX10,
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
            Family.GFX9,
            "gfx9",
            "SMEM",
            5,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SScratchLoadDwordx2 extends InstructionDef(
        "s_scratch_load_dwordx2",
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchLoadDwordx2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            6,
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
            Family.CDNA2,
            "cdna2",
            "SMEM",
            6,
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
            Family.CDNA3,
            "cdna3",
            "SMEM",
            6,
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
            Family.CDNA4,
            "cdna4",
            "SMEM",
            6,
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
            Family.GFX10,
            "rdna1",
            "SMEM",
            6,
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
            Family.GFX9,
            "gfx9",
            "SMEM",
            6,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SScratchLoadDwordx4 extends InstructionDef(
        "s_scratch_load_dwordx4",
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchLoadDwordx4(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            7,
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
            Family.CDNA2,
            "cdna2",
            "SMEM",
            7,
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
            Family.CDNA3,
            "cdna3",
            "SMEM",
            7,
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
            Family.CDNA4,
            "cdna4",
            "SMEM",
            7,
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
            Family.GFX10,
            "rdna1",
            "SMEM",
            7,
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
            Family.GFX9,
            "gfx9",
            "SMEM",
            7,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SScratchStoreDword extends InstructionDef(
        "s_scratch_store_dword",
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchStoreDword(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            21,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            21,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            21,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            21,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            21,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SScratchStoreDwordx2 extends InstructionDef(
        "s_scratch_store_dwordx2",
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchStoreDwordx2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            22,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            22,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            22,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            22,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            22,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SScratchStoreDwordx4 extends InstructionDef(
        "s_scratch_store_dwordx4",
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchStoreDwordx4(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            23,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            23,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            23,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            23,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            23,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SStoreDword extends InstructionDef(
        "s_store_dword",
        decoder(form(3)((operands, mods) => Instruction.SStoreDword(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            16,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            16,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            16,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            16,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            16,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            16,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SStoreDwordx2 extends InstructionDef(
        "s_store_dwordx2",
        decoder(form(3)((operands, mods) => Instruction.SStoreDwordx2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            17,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            17,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            17,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            17,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            17,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            17,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SStoreDwordx4 extends InstructionDef(
        "s_store_dwordx4",
        decoder(form(3)((operands, mods) => Instruction.SStoreDwordx4(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "SMEM",
            18,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "SMEM",
            18,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "SMEM",
            18,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "SMEM",
            18,
            Col(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "SMEM",
            18,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "SMEM",
            18,
            Col.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(
    SAtcProbe,
    SAtcProbeBuffer,
    SAtomicAdd,
    SAtomicAddX2,
    SAtomicAnd,
    SAtomicAndX2,
    SAtomicCmpswap,
    SAtomicCmpswapX2,
    SAtomicDec,
    SAtomicDecX2,
    SAtomicInc,
    SAtomicIncX2,
    SAtomicOr,
    SAtomicOrX2,
    SAtomicSmax,
    SAtomicSmaxX2,
    SAtomicSmin,
    SAtomicSminX2,
    SAtomicSub,
    SAtomicSubX2,
    SAtomicSwap,
    SAtomicSwapX2,
    SAtomicUmax,
    SAtomicUmaxX2,
    SAtomicUmin,
    SAtomicUminX2,
    SAtomicXor,
    SAtomicXorX2,
    SBufferAtomicAdd,
    SBufferAtomicAddX2,
    SBufferAtomicAnd,
    SBufferAtomicAndX2,
    SBufferAtomicCmpswap,
    SBufferAtomicCmpswapX2,
    SBufferAtomicDec,
    SBufferAtomicDecX2,
    SBufferAtomicInc,
    SBufferAtomicIncX2,
    SBufferAtomicOr,
    SBufferAtomicOrX2,
    SBufferAtomicSmax,
    SBufferAtomicSmaxX2,
    SBufferAtomicSmin,
    SBufferAtomicSminX2,
    SBufferAtomicSub,
    SBufferAtomicSubX2,
    SBufferAtomicSwap,
    SBufferAtomicSwapX2,
    SBufferAtomicUmax,
    SBufferAtomicUmaxX2,
    SBufferAtomicUmin,
    SBufferAtomicUminX2,
    SBufferAtomicXor,
    SBufferAtomicXorX2,
    SBufferLoadB128,
    SBufferLoadB256,
    SBufferLoadB32,
    SBufferLoadB512,
    SBufferLoadB64,
    SBufferLoadB96,
    SBufferLoadDword,
    SBufferLoadDwordx16,
    SBufferLoadDwordx2,
    SBufferLoadDwordx4,
    SBufferLoadDwordx8,
    SBufferLoadI16,
    SBufferLoadI8,
    SBufferLoadU16,
    SBufferLoadU8,
    SBufferPrefetchData,
    SBufferStoreDword,
    SBufferStoreDwordx2,
    SBufferStoreDwordx4,
    SDcacheDiscard,
    SDcacheDiscardX2,
    SDcacheInv,
    SDcacheInvVol,
    SDcacheWb,
    SDcacheWbVol,
    SGetWaveidInWorkgroup,
    SGl1Inv,
    SLoadB128,
    SLoadB256,
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
    SMemrealtime,
    SMemtime,
    SPrefetchData,
    SPrefetchDataPcRel,
    SPrefetchInst,
    SPrefetchInstPcRel,
    SScratchLoadDword,
    SScratchLoadDwordx2,
    SScratchLoadDwordx4,
    SScratchStoreDword,
    SScratchStoreDwordx2,
    SScratchStoreDwordx4,
    SStoreDword,
    SStoreDwordx2,
    SStoreDwordx4
  )
}
