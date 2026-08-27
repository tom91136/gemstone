// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

private[common] object InstructionDefs12 {
  case object SScratchLoadDwordx2 extends InstructionDef(
        "s_scratch_load_dwordx2",
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchLoadDwordx2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            6,
            Vector(
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
            6,
            Vector(
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
            6,
            Vector(
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
            6,
            Vector(
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
            6,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            6,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            7,
            Vector(
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
            7,
            Vector(
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
            7,
            Vector(
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
            7,
            Vector(
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
            7,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            7,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            21,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            21,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            21,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            21,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            21,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            22,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            22,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            22,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            22,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            22,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            23,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            23,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            23,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            23,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            23,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSendmsg extends InstructionDef(
        "s_sendmsg",
        decoder(form(1)((operands, mods) => Instruction.SSendmsg(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            16,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            16,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            16,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            16,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            54,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            16,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            16,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            54,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            54,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            54,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            16,
            Vector.empty,
            Some("sendmsg"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            16,
            Vector.empty,
            Some("sendmsg"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            16,
            Vector.empty,
            Some("sendmsg"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            16,
            Vector.empty,
            Some("sendmsg"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSendmsgRtnB32 extends InstructionDef(
        "s_sendmsg_rtn_b32",
        decoder(form(2)((operands, mods) => Instruction.SSendmsgRtnB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            76,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG_RTN", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP1",
            76,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            76,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG_RTN", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            76,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            77,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG_RTN", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP1",
            77,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            77,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG_RTN", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            77,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SENDMSG_RTN", 32, true, false, false)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSendmsghalt extends InstructionDef(
        "s_sendmsghalt",
        decoder(form(1)((operands, mods) => Instruction.SSendmsghalt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            17,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            17,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            17,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            17,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            55,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            17,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            17,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            55,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            55,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            55,
            Vector(
              OperandInfo(Some("SIMM16"), "SENDMSG", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            17,
            Vector.empty,
            Some("sendmsg"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            17,
            Vector.empty,
            Some("sendmsg"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            17,
            Vector.empty,
            Some("sendmsg"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            17,
            Vector.empty,
            Some("sendmsg"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSetGprIdxIdx extends InstructionDef(
        "s_set_gpr_idx_idx",
        decoder(form(1)((operands, mods) => Instruction.SSetGprIdxIdx(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            50,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            50,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            50,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            50,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            50,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            50,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            50,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            50,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP1",
            50,
            Vector(
              OperandInfo(None, "uint", 32, false, true, true),
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
            50,
            Vector(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSetGprIdxMode extends InstructionDef(
        "s_set_gpr_idx_mode",
        decoder(form(1)((operands, mods) => Instruction.SSetGprIdxMode(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            29,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            29,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            29,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            29,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            29,
            Vector(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            29,
            Vector(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSetGprIdxOff extends InstructionDef(
        "s_set_gpr_idx_off",
        decoder(form(0)((_, mods) => Instruction.SSetGprIdxOff(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            28,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            28,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            28,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            28,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            28,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            28,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSetGprIdxOn extends InstructionDef(
        "s_set_gpr_idx_on",
        decoder(form(2)((operands, mods) => Instruction.SSetGprIdxOn(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC",
            17,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            17,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            17,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            17,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC",
            17,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            17,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            17,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            17,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC",
            17,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            17,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            17,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            17,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC",
            17,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            17,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            17,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            17,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM4", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPC",
            17,
            Vector(
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
            17,
            Vector(
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSetInstPrefetchDistance extends InstructionDef(
        "s_set_inst_prefetch_distance",
        decoder(form(1)((operands, mods) => Instruction.SSetInstPrefetchDistance(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            4,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            4,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSetPcI64 extends InstructionDef(
        "s_set_pc_i64",
        decoder(form(1)((operands, mods) => Instruction.SSetPcI64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            72,
            Vector(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSetValuCoexecMode extends InstructionDef(
        "s_set_valu_coexec_mode",
        decoder(form(1)((operands, mods) => Instruction.SSetValuCoexecMode(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            31,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            31,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSetVgprMsb extends InstructionDef(
        "s_set_vgpr_msb",
        decoder(form(1)((operands, mods) => Instruction.SSetVgprMsb(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            6,
            Vector(
              OperandInfo(Some("SIMM16"), "SET_VGPR_MSB", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSethalt extends InstructionDef(
        "s_sethalt",
        decoder(form(1)((operands, mods) => Instruction.SSethalt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            13,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            13,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            13,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            13,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            2,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            13,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            13,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            2,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            2,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            2,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            13,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            13,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            13,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            13,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSetkill extends InstructionDef(
        "s_setkill",
        decoder(form(1)((operands, mods) => Instruction.SSetkill(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            11,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            11,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            11,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            11,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            11,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            11,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            1,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            1,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            1,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            11,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            11,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            11,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSetpcB64 extends InstructionDef(
        "s_setpc_b64",
        decoder(form(1)((operands, mods) => Instruction.SSetpcB64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            29,
            Vector(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            29,
            Vector(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            29,
            Vector(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            29,
            Vector(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            32,
            Vector(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1",
            32,
            Vector(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP1",
            72,
            Vector(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            72,
            Vector(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            72,
            Vector(
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP1",
            32,
            Vector(
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP1",
            32,
            Vector(
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP1",
            29,
            Vector(
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP1",
            29,
            Vector(
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSetprio extends InstructionDef(
        "s_setprio",
        decoder(form(1)((operands, mods) => Instruction.SSetprio(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            15,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            15,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            15,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            15,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            53,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            15,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            15,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            53,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            53,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            53,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            15,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            15,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            15,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            15,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSetprioIncWg extends InstructionDef(
        "s_setprio_inc_wg",
        decoder(form(1)((operands, mods) => Instruction.SSetprioIncWg(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            62,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSetregB32 extends InstructionDef(
        "s_setreg_b32",
        decoder(
          form(1)((operands, mods) => Instruction.SSetregB32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SSetregB32_2(operands(0), operands(1), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPK",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            19,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            19,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPK",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            19,
            Vector(
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            19,
            Vector(
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            18,
            Vector(
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            18,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK_INST_LITERAL",
            20,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK_INST_LITERAL",
            20,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK_INST_LITERAL",
            20,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK_INST_LITERAL",
            20,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPK_INST_LITERAL",
            19,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK_INST_LITERAL",
            21,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK_INST_LITERAL",
            21,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK_INST_LITERAL",
            19,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK_INST_LITERAL",
            19,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPK_INST_LITERAL",
            19,
            Vector(
              OperandInfo(Some("SIMM16"), "HWREG", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            21,
            Vector(
              OperandInfo(None, "none", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPK",
            21,
            Vector(
              OperandInfo(None, "none", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPK",
            20,
            Vector(
              OperandInfo(None, "none", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPK",
            20,
            Vector(
              OperandInfo(None, "none", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSetvskip extends InstructionDef(
        "s_setvskip",
        decoder(form(2)((operands, mods) => Instruction.SSetvskip(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC",
            16,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            16,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            16,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPC_INST_LITERAL",
            16,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC",
            16,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            16,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            16,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPC_INST_LITERAL",
            16,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC",
            16,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            16,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            16,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPC_INST_LITERAL",
            16,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC",
            16,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            16,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            16,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPC_INST_LITERAL",
            16,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPC",
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSextI32I16 extends InstructionDef(
        "s_sext_i32_i16",
        decoder(form(2)((operands, mods) => Instruction.SSextI32I16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            23,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            23,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            23,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            23,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            23,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            23,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            23,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            23,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            15,
            Vector(
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
            15,
            Vector(
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
            15,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            26,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            26,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1",
            26,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            26,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP1",
            15,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            15,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            15,
            Vector(
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
            15,
            Vector(
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
            15,
            Vector(
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
            15,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP1",
            26,
            Vector(
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
            26,
            Vector(
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
            23,
            Vector(
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
            23,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            22,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1_INST_LITERAL",
            22,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            22,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1_INST_LITERAL",
            22,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            22,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1_INST_LITERAL",
            22,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            22,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1_INST_LITERAL",
            22,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            14,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            14,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            14,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            25,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1_INST_LITERAL",
            25,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1",
            25,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1_INST_LITERAL",
            25,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP1",
            14,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP1_INST_LITERAL",
            14,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            14,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1_INST_LITERAL",
            14,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            14,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            14,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 8, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP1",
            25,
            Vector(
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
            25,
            Vector(
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
            22,
            Vector(
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
            22,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSleep extends InstructionDef(
        "s_sleep",
        decoder(form(1)((operands, mods) => Instruction.SSleep(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            14,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            14,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            14,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            14,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            3,
            Vector(
              OperandInfo(Some("SIMM16"), "SLEEP", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            14,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            14,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            3,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            3,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            3,
            Vector(
              OperandInfo(Some("SIMM16"), "SLEEP", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            14,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            14,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            14,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            14,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSleepVar extends InstructionDef(
        "s_sleep_var",
        decoder(form(1)((operands, mods) => Instruction.SSleepVar(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            88,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            88,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            88,
            Vector(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            88,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1_INST_LITERAL",
            88,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SStoreDword extends InstructionDef(
        "s_store_dword",
        decoder(form(3)((operands, mods) => Instruction.SStoreDword(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            16,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            16,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            16,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            16,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            16,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            16,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SStoreDwordx2 extends InstructionDef(
        "s_store_dwordx2",
        decoder(form(3)((operands, mods) => Instruction.SStoreDwordx2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            17,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            17,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            17,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            17,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            17,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            17,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SStoreDwordx4 extends InstructionDef(
        "s_store_dwordx4",
        decoder(form(3)((operands, mods) => Instruction.SStoreDwordx4(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            18,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            18,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            18,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            18,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 128, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            18,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            18,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSubCoCiU32 extends InstructionDef(
        "s_sub_co_ci_u32",
        decoder(form(3)((operands, mods) => Instruction.SSubCoCiU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2_INST_LITERAL64",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSubCoI32 extends InstructionDef(
        "s_sub_co_i32",
        decoder(form(3)((operands, mods) => Instruction.SSubCoI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
  case object SSubCoU32 extends InstructionDef(
        "s_sub_co_u32",
        decoder(form(3)((operands, mods) => Instruction.SSubCoU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP2",
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
  case object SSubF16 extends InstructionDef(
        "s_sub_f16",
        decoder(form(3)((operands, mods) => Instruction.SSubF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            74,
            Vector(
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
            74,
            Vector(
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
            74,
            Vector(
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
            74,
            Vector(
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
            74,
            Vector(
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
            74,
            Vector(
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
            74,
            Vector(
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
            74,
            Vector(
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
            74,
            Vector(
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
            74,
            Vector(
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
            74,
            Vector(
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
            74,
            Vector(
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
            74,
            Vector(
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
            74,
            Vector(
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
            74,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 16, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSubF32 extends InstructionDef(
        "s_sub_f32",
        decoder(form(3)((operands, mods) => Instruction.SSubF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            65,
            Vector(
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
            65,
            Vector(
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
            65,
            Vector(
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
            65,
            Vector(
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
            65,
            Vector(
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
            65,
            Vector(
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
            65,
            Vector(
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
            65,
            Vector(
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
            65,
            Vector(
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
            65,
            Vector(
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
            65,
            Vector(
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
            65,
            Vector(
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
            65,
            Vector(
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
            65,
            Vector(
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
            65,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSubI32 extends InstructionDef(
        "s_sub_i32",
        decoder(form(3)((operands, mods) => Instruction.SSubI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
            3,
            Vector(
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
  case object SSubNcU64 extends InstructionDef(
        "s_sub_nc_u64",
        decoder(form(3)((operands, mods) => Instruction.SSubNcU64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            84,
            Vector(
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
            84,
            Vector(
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
            84,
            Vector(
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
            84,
            Vector(
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
            84,
            Vector(
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
            84,
            Vector(
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
            84,
            Vector(
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
            84,
            Vector(
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
            84,
            Vector(
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
            84,
            Vector(
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
            84,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SSubU32 extends InstructionDef(
        "s_sub_u32",
        decoder(form(3)((operands, mods) => Instruction.SSubU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
            1,
            Vector(
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
  case object SSubbU32 extends InstructionDef(
        "s_subb_u32",
        decoder(form(3)((operands, mods) => Instruction.SSubbU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("SSRC1"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP2_INST_LITERAL",
            5,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP2",
            5,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
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
            5,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
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
            5,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
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
            5,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false),
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SSubvectorLoopBegin extends InstructionDef(
        "s_subvector_loop_begin",
        decoder(form(2)((operands, mods) => Instruction.SSubvectorLoopBegin(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            27,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            27,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            28,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "LABEL", 16, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            28,
            Vector(
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
  case object SSwapPcI64 extends InstructionDef(
        "s_swap_pc_i64",
        decoder(form(2)((operands, mods) => Instruction.SSwapPcI64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            73,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            30,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOP1",
            30,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOP1",
            30,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOP1",
            30,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOP1",
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOP1",
            73,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOP1",
            73,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            73,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SREG", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true),
              OperandInfo(None, "PC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOP1",
            33,
            Vector(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP1",
            33,
            Vector(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP1",
            30,
            Vector(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP1",
            30,
            Vector(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("branch"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object STrap extends InstructionDef(
        "s_trap",
        decoder(form(1)((operands, mods) => Instruction.STrap(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            16,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            18,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            16,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            16,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            16,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("TRAP"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            18,
            Vector.empty,
            Some("other"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            18,
            Vector.empty,
            Some("other"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            18,
            Vector.empty,
            Some("other"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            18,
            Vector.empty,
            Some("other"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object STruncF16 extends InstructionDef(
        "s_trunc_f16",
        decoder(form(2)((operands, mods) => Instruction.STruncF16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            109,
            Vector(
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
            109,
            Vector(
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
            109,
            Vector(
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
            109,
            Vector(
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
            109,
            Vector(
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
            109,
            Vector(
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
            109,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            98,
            Vector(
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
            98,
            Vector(
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
            98,
            Vector(
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
            98,
            Vector(
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
            98,
            Vector(
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
            98,
            Vector(
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
            98,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object STtracedata extends InstructionDef(
        "s_ttracedata",
        decoder(form(0)((_, mods) => Instruction.STtracedata(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            22,
            Vector(
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            22,
            Vector(
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            22,
            Vector(
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            22,
            Vector(
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            58,
            Vector(
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            22,
            Vector(
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            22,
            Vector(
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            58,
            Vector(
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            58,
            Vector(
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            58,
            Vector(
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            22,
            Vector(
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            22,
            Vector(
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            22,
            Vector(
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            22,
            Vector(
              OperandInfo(None, "uint", 32, true, false, true)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object STtracedataImm extends InstructionDef(
        "s_ttracedata_imm",
        decoder(form(1)((operands, mods) => Instruction.STtracedataImm(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            59,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            40,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            40,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            59,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            59,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            59,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("BRANCH"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SVersion extends InstructionDef(
        "s_version",
        decoder(form(1)((operands, mods) => Instruction.SVersion(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPK",
            1,
            Vector(
              OperandInfo(Some("SIMM16"), "VERSION", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            1,
            Vector(
              OperandInfo(Some("SIMM16"), "VERSION", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            1,
            Vector(
              OperandInfo(Some("SIMM16"), "VERSION", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            1,
            Vector(
              OperandInfo(Some("SIMM16"), "VERSION", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            1,
            Vector(
              OperandInfo(Some("SIMM16"), "VERSION", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPK",
            1,
            Vector(
              OperandInfo(Some("SIMM16"), "VERSION", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitAlu extends InstructionDef(
        "s_wait_alu",
        decoder(form(1)((operands, mods) => Instruction.SWaitAlu(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            8,
            Vector(
              OperandInfo(Some("SIMM16"), "WAIT_ALU", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            8,
            Vector(
              OperandInfo(Some("SIMM16"), "WAIT_ALU", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitAsynccnt extends InstructionDef(
        "s_wait_asynccnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitAsynccnt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            74,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitBvhcnt extends InstructionDef(
        "s_wait_bvhcnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitBvhcnt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            67,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitDscnt extends InstructionDef(
        "s_wait_dscnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitDscnt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            70,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            70,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitEvent extends InstructionDef(
        "s_wait_event",
        decoder(form(1)((operands, mods) => Instruction.SWaitEvent(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            11,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            11,
            Vector(
              OperandInfo(Some("SIMM16"), "WAIT_EVENT", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            11,
            Vector(
              OperandInfo(Some("SIMM16"), "WAIT_EVENT", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitExpcnt extends InstructionDef(
        "s_wait_expcnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitExpcnt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            68,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitIdle extends InstructionDef(
        "s_wait_idle",
        decoder(form(0)((_, mods) => Instruction.SWaitIdle(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            10,
            Vector.empty,
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            34,
            Vector.empty,
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            10,
            Vector.empty,
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            10,
            Vector.empty,
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            10,
            Vector.empty,
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitKmcnt extends InstructionDef(
        "s_wait_kmcnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitKmcnt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            71,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            71,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitLoadcnt extends InstructionDef(
        "s_wait_loadcnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitLoadcnt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            64,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            64,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitLoadcntDscnt extends InstructionDef(
        "s_wait_loadcnt_dscnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitLoadcntDscnt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            72,
            Vector(
              OperandInfo(Some("SIMM16"), "WAIT_MEM_DS", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            72,
            Vector(
              OperandInfo(Some("SIMM16"), "WAIT_MEM_DS", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitSamplecnt extends InstructionDef(
        "s_wait_samplecnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitSamplecnt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            66,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitStorecnt extends InstructionDef(
        "s_wait_storecnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitStorecnt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            65,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            65,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitStorecntDscnt extends InstructionDef(
        "s_wait_storecnt_dscnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitStorecntDscnt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            73,
            Vector(
              OperandInfo(Some("SIMM16"), "WAIT_MEM_DS", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            73,
            Vector(
              OperandInfo(Some("SIMM16"), "WAIT_MEM_DS", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitTensorcnt extends InstructionDef(
        "s_wait_tensorcnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitTensorcnt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            75,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitXcnt extends InstructionDef(
        "s_wait_xcnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitXcnt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            69,
            Vector(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitcnt extends InstructionDef(
        "s_waitcnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitcnt(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            12,
            Vector(
              OperandInfo(Some("SIMM16"), "WAITCNT", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            12,
            Vector(
              OperandInfo(Some("SIMM16"), "WAITCNT", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            12,
            Vector(
              OperandInfo(Some("SIMM16"), "WAITCNT", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            12,
            Vector(
              OperandInfo(Some("SIMM16"), "WAITCNT", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            12,
            Vector(
              OperandInfo(Some("SIMM16"), "WAITCNT", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            12,
            Vector(
              OperandInfo(Some("SIMM16"), "WAITCNT", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            9,
            Vector(
              OperandInfo(Some("SIMM16"), "WAITCNT", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            9,
            Vector(
              OperandInfo(Some("SIMM16"), "WAITCNT", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            12,
            Vector.empty,
            Some("waitcnt"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            12,
            Vector.empty,
            Some("waitcnt"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            12,
            Vector.empty,
            Some("waitcnt"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            12,
            Vector.empty,
            Some("waitcnt"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SWaitcntDepctr extends InstructionDef(
        "s_waitcnt_depctr",
        decoder(form(1)((operands, mods) => Instruction.SWaitcntDepctr(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            35,
            Vector(
              OperandInfo(Some("SIMM16"), "WAITCNT_DEPCTR", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            35,
            Vector(
              OperandInfo(Some("SIMM16"), "WAITCNT_DEPCTR", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            8,
            Vector(
              OperandInfo(Some("SIMM16"), "WAITCNT_DEPCTR", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            8,
            Vector(
              OperandInfo(Some("SIMM16"), "WAITCNT_DEPCTR", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWaitcntExpcnt extends InstructionDef(
        "s_waitcnt_expcnt",
        decoder(form(2)((operands, mods) => Instruction.SWaitcntExpcnt(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            25,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            25,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            26,
            Vector(
              OperandInfo(Some("SDST"), "SDST_NULL", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            26,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            26,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            26,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            27,
            Vector(
              OperandInfo(Some("SDST"), "SDST_NULL", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            27,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            24,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            24,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            25,
            Vector(
              OperandInfo(Some("SDST"), "SDST_NULL", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            25,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            23,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            23,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            24,
            Vector(
              OperandInfo(Some("SDST"), "SDST_NULL", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            24,
            Vector(
              OperandInfo(Some("SDST"), "SDST_NULL", 32, true, false, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SWakeup extends InstructionDef(
        "s_wakeup",
        decoder(form(0)((_, mods) => Instruction.SWakeup(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            3,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            3,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            3,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            3,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            52,
            Vector.empty,
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            3,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            3,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            52,
            Vector.empty,
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            52,
            Vector.empty,
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPP",
            52,
            Vector.empty,
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            3,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            3,
            Vector.empty,
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SWakeupBarrier extends InstructionDef(
        "s_wakeup_barrier",
        decoder(form(1)((operands, mods) => Instruction.SWakeupBarrier(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            87,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            6,
            Vector(
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
            6,
            Vector(
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
            6,
            Vector(
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
            6,
            Vector(
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
            6,
            Vector(
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
            6,
            Vector(
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
            6,
            Vector(
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
            6,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            9,
            Vector(
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
            9,
            Vector(
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
            9,
            Vector(
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
            9,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            9,
            Vector(
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
            9,
            Vector(
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
            6,
            Vector(
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
            6,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            7,
            Vector(
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
            7,
            Vector(
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
            7,
            Vector(
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
            7,
            Vector(
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
            7,
            Vector(
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
            7,
            Vector(
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
            7,
            Vector(
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
            7,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            10,
            Vector(
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
            10,
            Vector(
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
            10,
            Vector(
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
            10,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            10,
            Vector(
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
            10,
            Vector(
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
            7,
            Vector(
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
            7,
            Vector(
              OperandInfo(None, "uint", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "uint", 64, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SXnorB32 extends InstructionDef(
        "s_xnor_b32",
        decoder(form(3)((operands, mods) => Instruction.SXnorB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            32,
            Vector(
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
            28,
            Vector(
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
            28,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
  case object SXnorB64 extends InstructionDef(
        "s_xnor_b64",
        decoder(form(3)((operands, mods) => Instruction.SXnorB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            33,
            Vector(
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
            29,
            Vector(
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
            29,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
  case object SXnorSaveexecB32 extends InstructionDef(
        "s_xnor_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SXnorSaveexecB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            42,
            Vector(
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
            42,
            Vector(
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
            42,
            Vector(
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
            67,
            Vector(
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
            67,
            Vector(
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
            67,
            Vector(
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
            67,
            Vector(
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
            42,
            Vector(
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
            42,
            Vector(
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
            42,
            Vector(
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
            42,
            Vector(
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
            42,
            Vector(
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
            42,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            39,
            Vector(
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
            39,
            Vector(
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
            39,
            Vector(
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
            39,
            Vector(
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
            39,
            Vector(
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
            39,
            Vector(
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
            39,
            Vector(
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
            39,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            43,
            Vector(
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
            39,
            Vector(
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
            39,
            Vector(
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
  case object SXorB32 extends InstructionDef(
        "s_xor_b32",
        decoder(form(3)((operands, mods) => Instruction.SXorB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            18,
            Vector(
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
            18,
            Vector(
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
            18,
            Vector(
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
            18,
            Vector(
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
            18,
            Vector(
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
            18,
            Vector(
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
            18,
            Vector(
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
            18,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            26,
            Vector(
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
            18,
            Vector(
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
            18,
            Vector(
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
            16,
            Vector(
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
            16,
            Vector(
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
  case object SXorB64 extends InstructionDef(
        "s_xor_b64",
        decoder(form(3)((operands, mods) => Instruction.SXorB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            27,
            Vector(
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
            19,
            Vector(
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
            19,
            Vector(
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
            17,
            Vector(
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
            17,
            Vector(
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
  case object SXorSaveexecB32 extends InstructionDef(
        "s_xor_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SXorSaveexecB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            36,
            Vector(
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
            36,
            Vector(
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
            36,
            Vector(
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
            62,
            Vector(
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
            62,
            Vector(
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
            62,
            Vector(
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
            62,
            Vector(
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
            36,
            Vector(
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
            36,
            Vector(
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
            36,
            Vector(
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
            36,
            Vector(
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
            36,
            Vector(
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
            36,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            34,
            Vector(
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
            34,
            Vector(
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
            34,
            Vector(
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
            34,
            Vector(
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
            34,
            Vector(
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
            34,
            Vector(
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
            34,
            Vector(
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
            34,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            37,
            Vector(
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
            38,
            Vector(
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
            38,
            Vector(
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
            34,
            Vector(
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
            34,
            Vector(
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
  case object ScratchLoadB128 extends InstructionDef(
        "scratch_load_b128",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadB128_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchLoadB32 extends InstructionDef(
        "scratch_load_b32",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadB32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchLoadB64 extends InstructionDef(
        "scratch_load_b64",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadB64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchLoadB96 extends InstructionDef(
        "scratch_load_b96",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadB96_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadB96_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_SCRATCH",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_SCRATCH",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchLoadBlock extends InstructionDef(
        "scratch_load_block",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadBlock_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadBlock_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            83,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 1024, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 1024, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSCRATCH",
            83,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 1024, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 1024, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ScratchLoadD16B16 extends InstructionDef(
        "scratch_load_d16_b16",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadD16B16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadD16B16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
  case object ScratchLoadD16HiB16 extends InstructionDef(
        "scratch_load_d16_hi_b16",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadD16HiB16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadD16HiB16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
  case object ScratchLoadD16HiI8 extends InstructionDef(
        "scratch_load_d16_hi_i8",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadD16HiI8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadD16HiI8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
  case object ScratchLoadD16HiU8 extends InstructionDef(
        "scratch_load_d16_hi_u8",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadD16HiU8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadD16HiU8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
  case object ScratchLoadD16I8 extends InstructionDef(
        "scratch_load_d16_i8",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadD16I8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadD16I8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            31,
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
            31,
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
            31,
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
            31,
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
            31,
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
  case object ScratchLoadD16U8 extends InstructionDef(
        "scratch_load_d16_u8",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadD16U8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadD16U8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            30,
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
            30,
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
            30,
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
            30,
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
            30,
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
  case object ScratchLoadDword extends InstructionDef(
        "scratch_load_dword",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadDword_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadDword_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            20,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            20,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchLoadDwordx2 extends InstructionDef(
        "scratch_load_dwordx2",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadDwordx2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadDwordx2_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            13,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            13,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            21,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            21,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchLoadDwordx3 extends InstructionDef(
        "scratch_load_dwordx3",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadDwordx3_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadDwordx3_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            15,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            15,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            22,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            22,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchLoadDwordx4 extends InstructionDef(
        "scratch_load_dwordx4",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadDwordx4_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadDwordx4_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_SCRATCH",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_SCRATCH",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_SCRATCH",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_SCRATCH",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_SCRATCH",
            14,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_SCRATCH",
            14,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 32, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SCRATCH",
            23,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SCRATCH",
            23,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ScratchLoadI16 extends InstructionDef(
        "scratch_load_i16",
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadI16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VSCRATCH",
            19,
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
            19,
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
            19,
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
            19,
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
            19,
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

  val definitions: Vector[InstructionDef] = Vector(
    SScratchLoadDwordx2,
    SScratchLoadDwordx4,
    SScratchStoreDword,
    SScratchStoreDwordx2,
    SScratchStoreDwordx4,
    SSendmsg,
    SSendmsgRtnB32,
    SSendmsgRtnB64,
    SSendmsghalt,
    SSetGprIdxIdx,
    SSetGprIdxMode,
    SSetGprIdxOff,
    SSetGprIdxOn,
    SSetInstPrefetchDistance,
    SSetPcI64,
    SSetValuCoexecMode,
    SSetVgprMsb,
    SSethalt,
    SSetkill,
    SSetpcB64,
    SSetprio,
    SSetprioIncWg,
    SSetregB32,
    SSetregImm32B32,
    SSetvskip,
    SSextI32I16,
    SSextI32I8,
    SSleep,
    SSleepVar,
    SStoreDword,
    SStoreDwordx2,
    SStoreDwordx4,
    SSubCoCiU32,
    SSubCoI32,
    SSubCoU32,
    SSubF16,
    SSubF32,
    SSubI32,
    SSubNcU64,
    SSubU32,
    SSubbU32,
    SSubvectorLoopBegin,
    SSubvectorLoopEnd,
    SSwapPcI64,
    SSwappcB64,
    STrap,
    STruncF16,
    STruncF32,
    STtracedata,
    STtracedataImm,
    SVersion,
    SWaitAlu,
    SWaitAsynccnt,
    SWaitBvhcnt,
    SWaitDscnt,
    SWaitEvent,
    SWaitExpcnt,
    SWaitIdle,
    SWaitKmcnt,
    SWaitLoadcnt,
    SWaitLoadcntDscnt,
    SWaitSamplecnt,
    SWaitStorecnt,
    SWaitStorecntDscnt,
    SWaitTensorcnt,
    SWaitXcnt,
    SWaitcnt,
    SWaitcntDepctr,
    SWaitcntExpcnt,
    SWaitcntLgkmcnt,
    SWaitcntVmcnt,
    SWaitcntVscnt,
    SWakeup,
    SWakeupBarrier,
    SWqmB32,
    SWqmB64,
    SXnorB32,
    SXnorB64,
    SXnorSaveexecB32,
    SXnorSaveexecB64,
    SXorB32,
    SXorB64,
    SXorSaveexecB32,
    SXorSaveexecB64,
    ScratchLoadB128,
    ScratchLoadB32,
    ScratchLoadB64,
    ScratchLoadB96,
    ScratchLoadBlock,
    ScratchLoadD16B16,
    ScratchLoadD16HiB16,
    ScratchLoadD16HiI8,
    ScratchLoadD16HiU8,
    ScratchLoadD16I8,
    ScratchLoadD16U8,
    ScratchLoadDword,
    ScratchLoadDwordx2,
    ScratchLoadDwordx3,
    ScratchLoadDwordx4,
    ScratchLoadI16
  )
}
