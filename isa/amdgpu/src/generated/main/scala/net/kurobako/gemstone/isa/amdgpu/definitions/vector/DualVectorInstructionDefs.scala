// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object DualVectorInstructionDefs {
  case object VDualAddF32 extends InstructionDef(
        "v_dual_add_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualAddF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            4,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            4,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            4,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            4,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_X",
            4,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            4,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            4,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            4,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            4,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            4,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            4,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            4,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            4,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualAddF64 extends InstructionDef(
        "v_dual_add_f64",
        decoder(form(3)((operands, mods) => Instruction.VDualAddF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            33,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualAddNcU32 extends InstructionDef(
        "v_dual_add_nc_u32",
        decoder(form(3)((operands, mods) => Instruction.VDualAddNcU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            16,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            16,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            16,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            16,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            16,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            16,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            16,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            16,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualAndB32 extends InstructionDef(
        "v_dual_and_b32",
        decoder(form(3)((operands, mods) => Instruction.VDualAndB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            18,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            18,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            18,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            18,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualAshrrevI32 extends InstructionDef(
        "v_dual_ashrrev_i32",
        decoder(form(3)((operands, mods) => Instruction.VDualAshrrevI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            22,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            22,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            22,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            22,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualBitop2B32 extends InstructionDef(
        "v_dual_bitop2_b32",
        decoder(form(3)((operands, mods) => Instruction.VDualBitop2B32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            18,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualCndmaskB32 extends InstructionDef(
        "v_dual_cndmask_b32",
        decoder(
          form(3)((operands, mods) => Instruction.VDualCndmaskB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.VDualCndmaskB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            9,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRCX2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            9,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRCY2"), "SREG", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            9,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            9,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_X",
            9,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            9,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            9,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            9,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            9,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            9,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            9,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            9,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            9,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "VCC", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualDot2accF32Bf16 extends InstructionDef(
        "v_dual_dot2acc_f32_bf16",
        decoder(form(3)((operands, mods) =>
          Instruction.VDualDot2accF32Bf16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            13,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            13,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            13,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            13,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            13,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            13,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            13,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualDot2accF32F16 extends InstructionDef(
        "v_dual_dot2acc_f32_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VDualDot2accF32F16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            12,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            12,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            12,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            12,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            12,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            12,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            12,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualFmaF32 extends InstructionDef(
        "v_dual_fma_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VDualFmaF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            19,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRCX2"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            19,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRCY2"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualFmaF64 extends InstructionDef(
        "v_dual_fma_f64",
        decoder(form(4)((operands, mods) =>
          Instruction.VDualFmaF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            32,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRCX2"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualFmaakF32 extends InstructionDef(
        "v_dual_fmaak_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VDualFmaakF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            1,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            1,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            1,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            1,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            1,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualFmacF32 extends InstructionDef(
        "v_dual_fmac_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualFmacF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            0,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            0,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            0,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            0,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_X",
            0,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            0,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            0,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            0,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            0,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            0,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            0,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            0,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            0,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualFmamkF32 extends InstructionDef(
        "v_dual_fmamk_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VDualFmamkF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            2,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            2,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            2,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            2,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            2,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SIMM32", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualLshlrevB32 extends InstructionDef(
        "v_dual_lshlrev_b32",
        decoder(form(3)((operands, mods) => Instruction.VDualLshlrevB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            17,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            17,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            17,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            17,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            17,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            17,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            17,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            17,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualLshrrevB32 extends InstructionDef(
        "v_dual_lshrrev_b32",
        decoder(form(3)((operands, mods) => Instruction.VDualLshrrevB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            21,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            21,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            21,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            21,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualMaxF32 extends InstructionDef(
        "v_dual_max_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualMaxF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            10,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            10,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            10,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            10,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualMaxI32 extends InstructionDef(
        "v_dual_max_i32",
        decoder(form(3)((operands, mods) => Instruction.VDualMaxI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            23,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            23,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            23,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualMaxNumF32 extends InstructionDef(
        "v_dual_max_num_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualMaxNumF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            10,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            10,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            10,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            10,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_X",
            10,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            10,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            10,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            10,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            10,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualMaxNumF64 extends InstructionDef(
        "v_dual_max_num_f64",
        decoder(form(3)((operands, mods) => Instruction.VDualMaxNumF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            35,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualMinF32 extends InstructionDef(
        "v_dual_min_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualMinF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            11,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            11,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            11,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            11,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualMinI32 extends InstructionDef(
        "v_dual_min_i32",
        decoder(form(3)((operands, mods) => Instruction.VDualMinI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            24,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            24,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            24,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualMinNumF32 extends InstructionDef(
        "v_dual_min_num_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualMinNumF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            11,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            11,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            11,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            11,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_X",
            11,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            11,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            11,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            11,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            11,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualMinNumF64 extends InstructionDef(
        "v_dual_min_num_f64",
        decoder(form(3)((operands, mods) => Instruction.VDualMinNumF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            36,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualMovB32 extends InstructionDef(
        "v_dual_mov_b32",
        decoder(form(2)((operands, mods) => Instruction.VDualMovB32(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            8,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            8,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            8,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            8,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_X",
            8,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            8,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            8,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            8,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            8,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            8,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            8,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            8,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            8,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualMulDx9ZeroF32 extends InstructionDef(
        "v_dual_mul_dx9_zero_f32",
        decoder(form(3)((operands, mods) =>
          Instruction.VDualMulDx9ZeroF32(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            7,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            7,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            7,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            7,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_X",
            7,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            7,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            7,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            7,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            7,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            7,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            7,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            7,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            7,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualMulF32 extends InstructionDef(
        "v_dual_mul_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualMulF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            3,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            3,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            3,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            3,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_X",
            3,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            3,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            3,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            3,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            3,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            3,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            3,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            3,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            3,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualMulF64 extends InstructionDef(
        "v_dual_mul_f64",
        decoder(form(3)((operands, mods) => Instruction.VDualMulF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            34,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualSubF32 extends InstructionDef(
        "v_dual_sub_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualSubF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            5,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            5,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            5,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            5,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_X",
            5,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            5,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            5,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            5,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            5,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            5,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            5,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            5,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            5,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualSubNcU32 extends InstructionDef(
        "v_dual_sub_nc_u32",
        decoder(form(3)((operands, mods) => Instruction.VDualSubNcU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            20,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            20,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            20,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            20,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VDualSubrevF32 extends InstructionDef(
        "v_dual_subrev_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualSubrevF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            6,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPD3XY",
            6,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_X",
            6,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_INST_LITERAL_Y",
            6,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_X",
            6,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOPDXY_Y",
            6,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            6,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY",
            6,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOPDXY_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            6,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY",
            6,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOPDXY_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            6,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCX0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY",
            6,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRCY0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("VDSTX"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCX1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOPDXY_INST_LITERAL",
            6,
            Col(
              OperandInfo(Some("VDSTY"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRCY1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(
    VDualAddF32,
    VDualAddF64,
    VDualAddNcU32,
    VDualAndB32,
    VDualAshrrevI32,
    VDualBitop2B32,
    VDualCndmaskB32,
    VDualDot2accF32Bf16,
    VDualDot2accF32F16,
    VDualFmaF32,
    VDualFmaF64,
    VDualFmaakF32,
    VDualFmacF32,
    VDualFmamkF32,
    VDualLshlrevB32,
    VDualLshrrevB32,
    VDualMaxF32,
    VDualMaxI32,
    VDualMaxNumF32,
    VDualMaxNumF64,
    VDualMinF32,
    VDualMinI32,
    VDualMinNumF32,
    VDualMinNumF64,
    VDualMovB32,
    VDualMulDx9ZeroF32,
    VDualMulF32,
    VDualMulF64,
    VDualSubF32,
    VDualSubNcU32,
    VDualSubrevF32
  )
}
