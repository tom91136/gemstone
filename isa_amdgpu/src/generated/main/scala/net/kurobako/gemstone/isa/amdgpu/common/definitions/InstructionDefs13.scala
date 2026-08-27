// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

private[common] object InstructionDefs13 {
  case object ScratchLoadI8 extends InstructionDef(
        "scratch_load_i8",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadI8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadI8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchLoadLdsDword extends InstructionDef(
        "scratch_load_lds_dword",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadLdsDword_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadLdsDword_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            42,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            42,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchLoadLdsSbyte extends InstructionDef(
        "scratch_load_lds_sbyte",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadLdsSbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadLdsSbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchLoadLdsSshort extends InstructionDef(
        "scratch_load_lds_sshort",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadLdsSshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadLdsSshort_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            41,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            41,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchLoadLdsUbyte extends InstructionDef(
        "scratch_load_lds_ubyte",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadLdsUbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadLdsUbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchLoadLdsUshort extends InstructionDef(
        "scratch_load_lds_ushort",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadLdsUshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadLdsUshort_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchLoadSbyte extends InstructionDef(
        "scratch_load_sbyte",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadSbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadSbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            9,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            9,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            17,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            17,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchLoadSbyteD16 extends InstructionDef(
        "scratch_load_sbyte_d16",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadSbyteD16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadSbyteD16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            34,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            34,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchLoadSbyteD16Hi extends InstructionDef(
        "scratch_load_sbyte_d16_hi",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadSbyteD16Hi_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadSbyteD16Hi_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            35,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            35,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchLoadShortD16 extends InstructionDef(
        "scratch_load_short_d16",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadShortD16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadShortD16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            36,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            36,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchLoadShortD16Hi extends InstructionDef(
        "scratch_load_short_d16_hi",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadShortD16Hi_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadShortD16Hi_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            37,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            37,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchLoadSshort extends InstructionDef(
        "scratch_load_sshort",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadSshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadSshort_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            11,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            11,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            19,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            19,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchLoadU16 extends InstructionDef(
        "scratch_load_u16",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadU16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchLoadU8 extends InstructionDef(
        "scratch_load_u8",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadU8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadU8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchLoadUbyte extends InstructionDef(
        "scratch_load_ubyte",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadUbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadUbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            8,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            8,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            16,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            16,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchLoadUbyteD16 extends InstructionDef(
        "scratch_load_ubyte_d16",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadUbyteD16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadUbyteD16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            32,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            32,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchLoadUbyteD16Hi extends InstructionDef(
        "scratch_load_ubyte_d16_hi",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadUbyteD16Hi_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadUbyteD16Hi_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            33,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            33,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchLoadUshort extends InstructionDef(
        "scratch_load_ushort",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadUshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadUshort_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            10,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            10,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            18,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            18,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchStoreB128 extends InstructionDef(
        "scratch_store_b128",
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreB128(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            29,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            29,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchStoreB16 extends InstructionDef(
        "scratch_store_b16",
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreB16(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            25,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            25,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchStoreB32 extends InstructionDef(
        "scratch_store_b32",
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            26,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            26,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchStoreB64 extends InstructionDef(
        "scratch_store_b64",
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            27,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            27,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchStoreB8 extends InstructionDef(
        "scratch_store_b8",
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreB8(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            24,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            24,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchStoreB96 extends InstructionDef(
        "scratch_store_b96",
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreB96(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            28,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            28,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchStoreBlock extends InstructionDef(
        "scratch_store_block",
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreBlock(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            84,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 1024, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 1024, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            84,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 1024, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 1024, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchStoreByte extends InstructionDef(
        "scratch_store_byte",
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreByte(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchStoreByteD16Hi extends InstructionDef(
        "scratch_store_byte_d16_hi",
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreByteD16Hi(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            25,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            25,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchStoreD16HiB16 extends InstructionDef(
        "scratch_store_d16_hi_b16",
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreD16HiB16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            37,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            37,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            37,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            37,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            37,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchStoreD16HiB8 extends InstructionDef(
        "scratch_store_d16_hi_b8",
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreD16HiB8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            36,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            36,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            36,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            36,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            36,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchStoreDword extends InstructionDef(
        "scratch_store_dword",
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreDword(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchStoreDwordx2 extends InstructionDef(
        "scratch_store_dwordx2",
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreDwordx2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            29,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            29,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchStoreDwordx3 extends InstructionDef(
        "scratch_store_dwordx3",
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreDwordx3(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            30,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            30,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchStoreDwordx4 extends InstructionDef(
        "scratch_store_dwordx4",
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreDwordx4(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            31,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            31,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchStoreShort extends InstructionDef(
        "scratch_store_short",
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreShort(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            26,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            26,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchStoreShortD16Hi extends InstructionDef(
        "scratch_store_short_d16_hi",
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreShortD16Hi(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            27,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            27,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferLoadD16FormatX extends InstructionDef(
        "tbuffer_load_d16_format_x",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadD16FormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            136,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object TbufferLoadD16FormatXy extends InstructionDef(
        "tbuffer_load_d16_format_xy",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadD16FormatXy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            137,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object TbufferLoadD16FormatXyz extends InstructionDef(
        "tbuffer_load_d16_format_xyz",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadD16FormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            138,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object TbufferLoadD16FormatXyzw extends InstructionDef(
        "tbuffer_load_d16_format_xyzw",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadD16FormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            139,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object TbufferLoadFormatD16X extends InstructionDef(
        "tbuffer_load_format_d16_x",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatD16X(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            8,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            8,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferLoadFormatD16Xy extends InstructionDef(
        "tbuffer_load_format_d16_xy",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatD16Xy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            9,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            9,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferLoadFormatD16Xyz extends InstructionDef(
        "tbuffer_load_format_d16_xyz",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatD16Xyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            10,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            10,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferLoadFormatD16Xyzw extends InstructionDef(
        "tbuffer_load_format_d16_xyzw",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatD16Xyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            11,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            11,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferLoadFormatX extends InstructionDef(
        "tbuffer_load_format_x",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            0,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            0,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            0,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            0,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            0,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            0,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            0,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            0,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            128,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MTBUF",
            0,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MTBUF",
            0,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            0,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            0,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferLoadFormatXy extends InstructionDef(
        "tbuffer_load_format_xy",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatXy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            1,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            1,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            1,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            1,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            1,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            1,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            1,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            1,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            129,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MTBUF",
            1,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MTBUF",
            1,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            1,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            1,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferLoadFormatXyz extends InstructionDef(
        "tbuffer_load_format_xyz",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            2,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            2,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            2,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            2,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            2,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            2,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            2,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            2,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            130,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MTBUF",
            2,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MTBUF",
            2,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            2,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            2,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferLoadFormatXyzw extends InstructionDef(
        "tbuffer_load_format_xyzw",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            3,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            3,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            3,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            3,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            3,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            3,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            3,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            3,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            131,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MTBUF",
            3,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MTBUF",
            3,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            3,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            3,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferStoreD16FormatX extends InstructionDef(
        "tbuffer_store_d16_format_x",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreD16FormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            12,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            12,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            140,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object TbufferStoreD16FormatXy extends InstructionDef(
        "tbuffer_store_d16_format_xy",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreD16FormatXy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            13,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            13,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            141,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object TbufferStoreD16FormatXyz extends InstructionDef(
        "tbuffer_store_d16_format_xyz",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreD16FormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            14,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            14,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            142,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object TbufferStoreD16FormatXyzw extends InstructionDef(
        "tbuffer_store_d16_format_xyzw",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreD16FormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            15,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            15,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            143,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object TbufferStoreFormatD16X extends InstructionDef(
        "tbuffer_store_format_d16_x",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatD16X(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            12,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            12,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            12,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            12,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            12,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            12,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            12,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            12,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferStoreFormatD16Xy extends InstructionDef(
        "tbuffer_store_format_d16_xy",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatD16Xy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            13,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            13,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            13,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            13,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            13,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            13,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            13,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            13,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferStoreFormatD16Xyz extends InstructionDef(
        "tbuffer_store_format_d16_xyz",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatD16Xyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            14,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            14,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            14,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            14,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            14,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            14,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            14,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            14,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferStoreFormatD16Xyzw extends InstructionDef(
        "tbuffer_store_format_d16_xyzw",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatD16Xyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            15,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            15,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            15,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            15,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            15,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            15,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            15,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            15,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferStoreFormatX extends InstructionDef(
        "tbuffer_store_format_x",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            4,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            4,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            4,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            4,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            4,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            4,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            4,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            4,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            132,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MTBUF",
            4,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MTBUF",
            4,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            4,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            4,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferStoreFormatXy extends InstructionDef(
        "tbuffer_store_format_xy",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatXy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            5,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            5,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            5,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            5,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            5,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            5,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            5,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            5,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            133,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MTBUF",
            5,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MTBUF",
            5,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            5,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            5,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferStoreFormatXyz extends InstructionDef(
        "tbuffer_store_format_xyz",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            6,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            6,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 96, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            6,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 96, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            6,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 96, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            6,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            6,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            6,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            6,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            134,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MTBUF",
            6,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MTBUF",
            6,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            6,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            6,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TbufferStoreFormatXyzw extends InstructionDef(
        "tbuffer_store_format_xyzw",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MTBUF",
            7,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MTBUF",
            7,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MTBUF",
            7,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MTBUF",
            7,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MTBUF",
            7,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MTBUF",
            7,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MTBUF",
            7,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MTBUF",
            7,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            135,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MTBUF",
            7,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MTBUF",
            7,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MTBUF",
            7,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MTBUF",
            7,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object TensorLoadToLds extends InstructionDef(
        "tensor_load_to_lds",
        decoder(form(4)((operands, mods) =>
          Instruction.TensorLoadToLds(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VIMAGE",
            196,
            Vector(
              OperandInfo(Some("VADDR0"), "SGPR", 128, true, false, false),
              OperandInfo(Some("VADDR1"), "SGPR", 256, true, false, false),
              OperandInfo(Some("VADDR2"), "SREG", 128, true, false, false),
              OperandInfo(Some("VADDR3"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object TensorStoreFromLds extends InstructionDef(
        "tensor_store_from_lds",
        decoder(form(4)((operands, mods) =>
          Instruction.TensorStoreFromLds(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VIMAGE",
            197,
            Vector(
              OperandInfo(Some("VADDR0"), "SGPR", 128, true, false, false),
              OperandInfo(Some("VADDR1"), "SGPR", 256, true, false, false),
              OperandInfo(Some("VADDR2"), "SREG", 128, true, false, false),
              OperandInfo(Some("VADDR3"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VAccvgprMovB32 extends InstructionDef(
        "v_accvgpr_mov_b32",
        decoder(form(2)((operands, mods) => Instruction.VAccvgprMovB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1",
            82,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_INST_LITERAL",
            82,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_VOP_DPP",
            82,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_VOP_SDWA",
            82,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            402,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1",
            82,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_INST_LITERAL",
            82,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_VOP_DPP",
            82,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_VOP_SDWA",
            82,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            402,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1",
            82,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_INST_LITERAL",
            82,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_VOP_DPP",
            82,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_VOP_SDWA",
            82,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            402,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VAccvgprRead extends InstructionDef(
        "v_accvgpr_read",
        decoder(form(2)((operands, mods) => Instruction.VAccvgprRead(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P",
            88,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P",
            88,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P",
            88,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P",
            88,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_ACCVGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VAccvgprWrite extends InstructionDef(
        "v_accvgpr_write",
        decoder(form(2)((operands, mods) => Instruction.VAccvgprWrite(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3P",
            89,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3P",
            89,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3P",
            89,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3P",
            89,
            Vector(
              OperandInfo(Some("VDST"), "ACCVGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VAdd3U32 extends InstructionDef(
        "v_add3_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.VAdd3U32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            511,
            Vector(
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
            511,
            Vector(
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
            511,
            Vector(
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
            511,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            877,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
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
            597,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            511,
            Vector(
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
  case object VAddCoCiU32 extends InstructionDef(
        "v_add_co_ci_u32",
        decoder(form(5)((operands, mods) =>
          Instruction.VAddCoCiU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2",
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            40,
            Vector(
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
            40,
            Vector(
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
            40,
            Vector(
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
            40,
            Vector(
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
            40,
            Vector(
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
            296,
            Vector(
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
            296,
            Vector(
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
            296,
            Vector(
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
            296,
            Vector(
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
            40,
            Vector(
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
            40,
            Vector(
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
            40,
            Vector(
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
            40,
            Vector(
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
            40,
            Vector(
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
            296,
            Vector(
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
            296,
            Vector(
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
            296,
            Vector(
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
            296,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
            288,
            Vector(
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
  case object VAddCoU32 extends InstructionDef(
        "v_add_co_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.VAddCoU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            25,
            Vector(
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
            25,
            Vector(
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
            25,
            Vector(
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
            25,
            Vector(
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
            281,
            Vector(
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
            25,
            Vector(
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
            25,
            Vector(
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
            25,
            Vector(
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
            25,
            Vector(
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
            281,
            Vector(
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
            25,
            Vector(
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
            25,
            Vector(
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
            25,
            Vector(
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
            25,
            Vector(
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
            281,
            Vector(
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
            25,
            Vector(
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
            25,
            Vector(
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
            25,
            Vector(
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
            25,
            Vector(
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
            281,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            783,
            Vector(
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
            783,
            Vector(
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
            783,
            Vector(
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
            783,
            Vector(
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
            783,
            Vector(
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
            783,
            Vector(
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
            783,
            Vector(
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
            783,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            768,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            25,
            Vector(
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
            25,
            Vector(
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
  case object VAddF16 extends InstructionDef(
        "v_add_f16",
        decoder(form(3)((operands, mods) => Instruction.VAddF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            31,
            Vector(
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
            31,
            Vector(
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
            31,
            Vector(
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
            31,
            Vector(
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
            287,
            Vector(
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
            31,
            Vector(
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
            31,
            Vector(
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
            31,
            Vector(
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
            31,
            Vector(
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
            287,
            Vector(
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
            31,
            Vector(
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
            31,
            Vector(
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
            31,
            Vector(
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
            31,
            Vector(
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
            287,
            Vector(
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
            31,
            Vector(
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
            31,
            Vector(
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
            31,
            Vector(
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
            31,
            Vector(
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
            287,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            50,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            306,
            Vector(
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
            31,
            Vector(
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
            31,
            Vector(
              OperandInfo(None, "float", 16, false, true, false),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAddF32 extends InstructionDef(
        "v_add_f32",
        decoder(form(3)((operands, mods) => Instruction.VAddF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            257,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            257,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            257,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            257,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            259,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAddF64 extends InstructionDef(
        "v_add_f64",
        decoder(form(3)((operands, mods) => Instruction.VAddF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            640,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            640,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            640,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            640,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2",
            2,
            Vector(
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
            2,
            Vector(
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
            2,
            Vector(
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
            2,
            Vector(
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
            258,
            Vector(
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
            258,
            Vector(
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
            258,
            Vector(
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
            258,
            Vector(
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
            258,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            356,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            356,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            356,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            356,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            356,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            356,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            356,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            356,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            807,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            807,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            807,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            807,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            807,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            807,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            807,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            807,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2",
            2,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP2_INST_LITERAL",
            2,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            258,
            Vector(
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
            258,
            Vector(
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
            258,
            Vector(
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
            258,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VAddF64E64 extends InstructionDef(
        "v_add_f64_e64",
        decoder(form(3)((operands, mods) => Instruction.VAddF64E64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP3",
            356,
            Vector(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double_add"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP3",
            356,
            Vector(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double_add"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            640,
            Vector(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double_add"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            640,
            Vector(
              OperandInfo(None, "float", 64, false, true, false),
              OperandInfo(None, "float", 64, true, false, false),
              OperandInfo(None, "float", 64, true, false, false)
            ),
            Some("valu_double_add"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAddI16 extends InstructionDef(
        "v_add_i16",
        decoder(form(3)((operands, mods) => Instruction.VAddI16(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            670,
            Vector(
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
            670,
            Vector(
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
            670,
            Vector(
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
            670,
            Vector(
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
            670,
            Vector(
              OperandInfo(None, "uint", 16, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAddI32 extends InstructionDef(
        "v_add_i32",
        decoder(
          form(3)((operands, mods) => Instruction.VAddI32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VAddI32_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            668,
            Vector(
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
            668,
            Vector(
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
            668,
            Vector(
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
            668,
            Vector(
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
            668,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAddLshlU32 extends InstructionDef(
        "v_add_lshl_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.VAddLshlU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            510,
            Vector(
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
            510,
            Vector(
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
            510,
            Vector(
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
            510,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            839,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
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
            583,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            510,
            Vector(
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
  case object VAddMaxI32 extends InstructionDef(
        "v_add_max_i32",
        decoder(form(4)((operands, mods) =>
          Instruction.VAddMaxI32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            606,
            Vector(
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
            606,
            Vector(
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
            606,
            Vector(
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
            606,
            Vector(
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
            606,
            Vector(
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
            606,
            Vector(
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
            606,
            Vector(
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
            606,
            Vector(
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
            606,
            Vector(
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
            606,
            Vector(
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
  case object VAddMaxU32 extends InstructionDef(
        "v_add_max_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.VAddMaxU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            607,
            Vector(
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
            607,
            Vector(
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
            607,
            Vector(
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
            607,
            Vector(
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
            607,
            Vector(
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
            607,
            Vector(
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
            607,
            Vector(
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
            607,
            Vector(
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
            607,
            Vector(
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
            607,
            Vector(
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
  case object VAddMinI32 extends InstructionDef(
        "v_add_min_i32",
        decoder(form(4)((operands, mods) =>
          Instruction.VAddMinI32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            608,
            Vector(
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
            608,
            Vector(
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
            608,
            Vector(
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
            608,
            Vector(
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
            608,
            Vector(
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
            608,
            Vector(
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
            608,
            Vector(
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
            608,
            Vector(
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
            608,
            Vector(
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
            608,
            Vector(
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
  case object VAddMinU32 extends InstructionDef(
        "v_add_min_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.VAddMinU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            609,
            Vector(
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
            609,
            Vector(
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
            609,
            Vector(
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
            609,
            Vector(
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
            609,
            Vector(
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
            609,
            Vector(
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
            609,
            Vector(
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
            609,
            Vector(
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
            609,
            Vector(
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
            609,
            Vector(
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
  case object VAddNcI16 extends InstructionDef(
        "v_add_nc_i16",
        decoder(form(3)((operands, mods) => Instruction.VAddNcI16(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
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
            781,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VAddNcI32 extends InstructionDef(
        "v_add_nc_i32",
        decoder(form(3)((operands, mods) => Instruction.VAddNcI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            895,
            Vector(
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
            895,
            Vector(
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
            895,
            Vector(
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
            895,
            Vector(
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
            895,
            Vector(
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
            895,
            Vector(
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
            895,
            Vector(
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
            895,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
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
            806,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VAddNcU16 extends InstructionDef(
        "v_add_nc_u16",
        decoder(form(3)((operands, mods) => Instruction.VAddNcU16(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
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
            771,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VAddNcU32 extends InstructionDef(
        "v_add_nc_u32",
        decoder(form(3)((operands, mods) => Instruction.VAddNcU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2",
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
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
            293,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VAddNcU64 extends InstructionDef(
        "v_add_nc_u64",
        decoder(form(3)((operands, mods) => Instruction.VAddNcU64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2",
            40,
            Vector(
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
            40,
            Vector(
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
            40,
            Vector(
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
            40,
            Vector(
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
            296,
            Vector(
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
            296,
            Vector(
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
            296,
            Vector(
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
            296,
            Vector(
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
            296,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VAddU16 extends InstructionDef(
        "v_add_u16",
        decoder(form(3)((operands, mods) => Instruction.VAddU16(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            38,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            294,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            294,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            294,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            294,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
              OperandInfo(None, "uint", 16, false, true, false),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAddU32 extends InstructionDef(
        "v_add_u32",
        decoder(
          form(3)((operands, mods) => Instruction.VAddU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VAddU32_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            52,
            Vector(
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
            52,
            Vector(
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
            52,
            Vector(
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
            52,
            Vector(
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
            308,
            Vector(
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
            52,
            Vector(
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
            52,
            Vector(
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
            52,
            Vector(
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
            52,
            Vector(
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
            308,
            Vector(
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
            52,
            Vector(
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
            52,
            Vector(
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
            52,
            Vector(
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
            52,
            Vector(
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
            308,
            Vector(
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
            52,
            Vector(
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
            52,
            Vector(
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
            52,
            Vector(
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
            52,
            Vector(
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
            308,
            Vector(
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
            52,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAddcCoU32 extends InstructionDef(
        "v_addc_co_u32",
        decoder(form(5)((operands, mods) =>
          Instruction.VAddcCoU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            284,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            284,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            284,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            284,
            Vector(
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
            40,
            Vector(
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
            40,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
  case object VAlignbitB32 extends InstructionDef(
        "v_alignbit_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.VAlignbitB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            462,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            462,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            462,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            462,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            334,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            534,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP3",
            334,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP3",
            334,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            462,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            462,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAlignbyteB32 extends InstructionDef(
        "v_alignbyte_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.VAlignbyteB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            463,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            463,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            463,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            463,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP8",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            335,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 8, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP16",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_VOP_DPP8",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP16",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_VOP_DPP8",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP16",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_VOP_DPP8",
            535,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP3",
            335,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP3",
            335,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            463,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            463,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAndB16 extends InstructionDef(
        "v_and_b16",
        decoder(form(3)((operands, mods) => Instruction.VAndB16(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
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
            866,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VAndB32 extends InstructionDef(
        "v_and_b32",
        decoder(form(3)((operands, mods) => Instruction.VAndB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            275,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            275,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            275,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            275,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            283,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAndOrB32 extends InstructionDef(
        "v_and_or_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.VAndOrB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            513,
            Vector(
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
            513,
            Vector(
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
            513,
            Vector(
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
            513,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            881,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
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
            599,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            513,
            Vector(
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
  case object VAshrI32 extends InstructionDef(
        "v_ashr_i32",
        decoder(form(3)((operands, mods) => Instruction.VAshrI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP2",
            23,
            Vector(
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
            23,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAshrI64 extends InstructionDef(
        "v_ashr_i64",
        decoder(form(3)((operands, mods) => Instruction.VAshrI64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP3",
            355,
            Vector(
              OperandInfo(None, "int", 64, false, true, false),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP3",
            355,
            Vector(
              OperandInfo(None, "int", 64, false, true, false),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAshrPkI8I32 extends InstructionDef(
        "v_ashr_pk_i8_i32",
        decoder(form(4)((operands, mods) =>
          Instruction.VAshrPkI8I32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            613,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            656,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            656,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            656,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            656,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            656,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            656,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            656,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            656,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            656,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            656,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VAshrPkU8I32 extends InstructionDef(
        "v_ashr_pk_u8_i32",
        decoder(form(4)((operands, mods) =>
          Instruction.VAshrPkU8I32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            614,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
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
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VAshrrevI16 extends InstructionDef(
        "v_ashrrev_i16",
        decoder(form(3)((operands, mods) => Instruction.VAshrrevI16(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            44,
            Vector(
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
            44,
            Vector(
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
            44,
            Vector(
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
            44,
            Vector(
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
            300,
            Vector(
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
            44,
            Vector(
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
            44,
            Vector(
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
            44,
            Vector(
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
            44,
            Vector(
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
            300,
            Vector(
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
            44,
            Vector(
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
            44,
            Vector(
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
            44,
            Vector(
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
            44,
            Vector(
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
            300,
            Vector(
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
            44,
            Vector(
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
            44,
            Vector(
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
            44,
            Vector(
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
            44,
            Vector(
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
            300,
            Vector(
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
            "VOP3",
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            776,
            Vector(
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
            776,
            Vector(
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
            776,
            Vector(
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
            776,
            Vector(
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
            "VOP3",
            776,
            Vector(
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
            776,
            Vector(
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
            776,
            Vector(
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
            776,
            Vector(
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
            "VOP3",
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            826,
            Vector(
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
            44,
            Vector(
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
            44,
            Vector(
              OperandInfo(None, "uint", 16, false, true, false),
              OperandInfo(None, "uint", 16, true, false, false),
              OperandInfo(None, "uint", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAshrrevI32 extends InstructionDef(
        "v_ashrrev_i32",
        decoder(form(3)((operands, mods) => Instruction.VAshrrevI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP2",
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            273,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            273,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            273,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            273,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP2",
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            24,
            Vector(
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
            24,
            Vector(
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
            24,
            Vector(
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
            24,
            Vector(
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
            24,
            Vector(
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
            280,
            Vector(
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
            280,
            Vector(
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
            280,
            Vector(
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
            280,
            Vector(
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
            24,
            Vector(
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
            24,
            Vector(
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
            24,
            Vector(
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
            24,
            Vector(
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
            24,
            Vector(
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
            280,
            Vector(
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
            280,
            Vector(
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
            280,
            Vector(
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
            280,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            282,
            Vector(
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
            24,
            Vector(
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
            24,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VAshrrevI64 extends InstructionDef(
        "v_ashrrev_i64",
        decoder(form(3)((operands, mods) => Instruction.VAshrrevI64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            657,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            830,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            830,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            830,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_VOP_DPP16",
            830,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            769,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            769,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            769,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            769,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            769,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            769,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3",
            830,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            830,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "VOP3_INST_LITERAL",
            830,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3",
            830,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            830,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "VOP3_INST_LITERAL",
            830,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3",
            830,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            830,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VOP3_INST_LITERAL",
            830,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            657,
            Vector(
              OperandInfo(None, "int", 64, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            657,
            Vector(
              OperandInfo(None, "int", 64, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "int", 64, true, false, false)
            ),
            Some("valu64"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VBcntU32B32 extends InstructionDef(
        "v_bcnt_u32_b32",
        decoder(form(3)((operands, mods) => Instruction.VBcntU32B32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            651,
            Vector(
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
            651,
            Vector(
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
            651,
            Vector(
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
            651,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            868,
            Vector(
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
            868,
            Vector(
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
            868,
            Vector(
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
            868,
            Vector(
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
            868,
            Vector(
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
            868,
            Vector(
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
            868,
            Vector(
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
            868,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            798,
            Vector(
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
            290,
            Vector(
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
            290,
            Vector(
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
            651,
            Vector(
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
            651,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VBfeI32 extends InstructionDef(
        "v_bfe_i32",
        decoder(form(4)((operands, mods) =>
          Instruction.VBfeI32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            457,
            Vector(
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
            457,
            Vector(
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
            457,
            Vector(
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
            457,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            529,
            Vector(
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
            329,
            Vector(
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
            329,
            Vector(
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
            457,
            Vector(
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
            457,
            Vector(
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

  val definitions: Vector[InstructionDef] = Vector(
    ScratchLoadI8,
    ScratchLoadLdsDword,
    ScratchLoadLdsSbyte,
    ScratchLoadLdsSshort,
    ScratchLoadLdsUbyte,
    ScratchLoadLdsUshort,
    ScratchLoadSbyte,
    ScratchLoadSbyteD16,
    ScratchLoadSbyteD16Hi,
    ScratchLoadShortD16,
    ScratchLoadShortD16Hi,
    ScratchLoadSshort,
    ScratchLoadU16,
    ScratchLoadU8,
    ScratchLoadUbyte,
    ScratchLoadUbyteD16,
    ScratchLoadUbyteD16Hi,
    ScratchLoadUshort,
    ScratchStoreB128,
    ScratchStoreB16,
    ScratchStoreB32,
    ScratchStoreB64,
    ScratchStoreB8,
    ScratchStoreB96,
    ScratchStoreBlock,
    ScratchStoreByte,
    ScratchStoreByteD16Hi,
    ScratchStoreD16HiB16,
    ScratchStoreD16HiB8,
    ScratchStoreDword,
    ScratchStoreDwordx2,
    ScratchStoreDwordx3,
    ScratchStoreDwordx4,
    ScratchStoreShort,
    ScratchStoreShortD16Hi,
    TbufferLoadD16FormatX,
    TbufferLoadD16FormatXy,
    TbufferLoadD16FormatXyz,
    TbufferLoadD16FormatXyzw,
    TbufferLoadFormatD16X,
    TbufferLoadFormatD16Xy,
    TbufferLoadFormatD16Xyz,
    TbufferLoadFormatD16Xyzw,
    TbufferLoadFormatX,
    TbufferLoadFormatXy,
    TbufferLoadFormatXyz,
    TbufferLoadFormatXyzw,
    TbufferStoreD16FormatX,
    TbufferStoreD16FormatXy,
    TbufferStoreD16FormatXyz,
    TbufferStoreD16FormatXyzw,
    TbufferStoreFormatD16X,
    TbufferStoreFormatD16Xy,
    TbufferStoreFormatD16Xyz,
    TbufferStoreFormatD16Xyzw,
    TbufferStoreFormatX,
    TbufferStoreFormatXy,
    TbufferStoreFormatXyz,
    TbufferStoreFormatXyzw,
    TensorLoadToLds,
    TensorStoreFromLds,
    VAccvgprMovB32,
    VAccvgprRead,
    VAccvgprWrite,
    VAdd3U32,
    VAddCoCiU32,
    VAddCoU32,
    VAddF16,
    VAddF32,
    VAddF64,
    VAddF64E64,
    VAddI16,
    VAddI32,
    VAddLshlU32,
    VAddMaxI32,
    VAddMaxU32,
    VAddMinI32,
    VAddMinU32,
    VAddNcI16,
    VAddNcI32,
    VAddNcU16,
    VAddNcU32,
    VAddNcU64,
    VAddU16,
    VAddU32,
    VAddcCoU32,
    VAlignbitB32,
    VAlignbyteB32,
    VAndB16,
    VAndB32,
    VAndOrB32,
    VAshrI32,
    VAshrI64,
    VAshrPkI8I32,
    VAshrPkU8I32,
    VAshrrevI16,
    VAshrrevI32,
    VAshrrevI64,
    VBcntU32B32,
    VBfeI32
  )
}
