// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

private[common] object InstructionDefs2 {
  case object DsCmpstoreB64 extends InstructionDef(
        "ds_cmpstore_b64",
        decoder(form(3)((operands, mods) => Instruction.DsCmpstoreB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            80,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            80,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            80,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            80,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsCmpstoreF32 extends InstructionDef(
        "ds_cmpstore_f32",
        decoder(form(3)((operands, mods) => Instruction.DsCmpstoreF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            17,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            17,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsCmpstoreF64 extends InstructionDef(
        "ds_cmpstore_f64",
        decoder(form(3)((operands, mods) => Instruction.DsCmpstoreF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            81,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            81,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsCmpstoreRtnB32 extends InstructionDef(
        "ds_cmpstore_rtn_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstoreRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            48,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            48,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            48,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            48,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsCmpstoreRtnB64 extends InstructionDef(
        "ds_cmpstore_rtn_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstoreRtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            112,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            112,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            112,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            112,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsCmpstoreRtnF32 extends InstructionDef(
        "ds_cmpstore_rtn_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstoreRtnF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            49,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            49,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsCmpstoreRtnF64 extends InstructionDef(
        "ds_cmpstore_rtn_f64",
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstoreRtnF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            113,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            113,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsCondSubRtnU32 extends InstructionDef(
        "ds_cond_sub_rtn_u32",
        decoder(form(3)((operands, mods) => Instruction.DsCondSubRtnU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            168,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            168,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsCondSubU32 extends InstructionDef(
        "ds_cond_sub_u32",
        decoder(form(2)((operands, mods) => Instruction.DsCondSubU32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            152,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            152,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsCondxchg32RtnB128 extends InstructionDef(
        "ds_condxchg32_rtn_b128",
        decoder(form(0)((_, mods) => Instruction.DsCondxchg32RtnB128(mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            253,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            253,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsCondxchg32RtnB64 extends InstructionDef(
        "ds_condxchg32_rtn_b64",
        decoder(form(3)((operands, mods) =>
          Instruction.DsCondxchg32RtnB64(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            126,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            126,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            126,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            126,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            126,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            126,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            126,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            126,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            126,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            126,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            126,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            126,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            126,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsConsume extends InstructionDef(
        "ds_consume",
        decoder(form(1)((operands, mods) => Instruction.DsConsume(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            189,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            189,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            189,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            189,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            61,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            61,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            189,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            189,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsDecRtnU32 extends InstructionDef(
        "ds_dec_rtn_u32",
        decoder(form(3)((operands, mods) => Instruction.DsDecRtnU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            36,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            36,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            36,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            36,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsDecRtnU64 extends InstructionDef(
        "ds_dec_rtn_u64",
        decoder(form(3)((operands, mods) => Instruction.DsDecRtnU64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            100,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            100,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            100,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            100,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsDecSrc2U32 extends InstructionDef(
        "ds_dec_src2_u32",
        decoder(form(1)((operands, mods) => Instruction.DsDecSrc2U32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            132,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            132,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            132,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            132,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsDecSrc2U64 extends InstructionDef(
        "ds_dec_src2_u64",
        decoder(form(1)((operands, mods) => Instruction.DsDecSrc2U64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            196,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            196,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            196,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            196,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsDecU32 extends InstructionDef(
        "ds_dec_u32",
        decoder(form(2)((operands, mods) => Instruction.DsDecU32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            4,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            4,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            4,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            4,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            4,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            4,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            4,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            4,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            4,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            4,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            4,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            4,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            4,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            4,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsDecU64 extends InstructionDef(
        "ds_dec_u64",
        decoder(form(2)((operands, mods) => Instruction.DsDecU64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            68,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            68,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            68,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            68,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            68,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            68,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            68,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            68,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            68,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            68,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            68,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            68,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            68,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            68,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsDirectLoad extends InstructionDef(
        "ds_direct_load",
        decoder(form(1)((operands, mods) => Instruction.DsDirectLoad(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDSDIR",
            1,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsGwsBarrier extends InstructionDef(
        "ds_gws_barrier",
        decoder(form(1)((operands, mods) => Instruction.DsGwsBarrier(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            157,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            157,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            157,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            29,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            29,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            157,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            157,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsGwsInit extends InstructionDef(
        "ds_gws_init",
        decoder(form(1)((operands, mods) => Instruction.DsGwsInit(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            153,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            153,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            153,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            25,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            25,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            153,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            153,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsGwsSemaBr extends InstructionDef(
        "ds_gws_sema_br",
        decoder(form(1)((operands, mods) => Instruction.DsGwsSemaBr(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            155,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            155,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            155,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            27,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            27,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            155,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            155,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsGwsSemaP extends InstructionDef(
        "ds_gws_sema_p",
        decoder(form(0)((_, mods) => Instruction.DsGwsSemaP(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            156,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            156,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            156,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            28,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            28,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            28,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            28,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            28,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            28,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            156,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            156,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsGwsSemaReleaseAll extends InstructionDef(
        "ds_gws_sema_release_all",
        decoder(form(0)((_, mods) => Instruction.DsGwsSemaReleaseAll(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            152,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            152,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            152,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            24,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            24,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            24,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            24,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            24,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            152,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            152,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsGwsSemaV extends InstructionDef(
        "ds_gws_sema_v",
        decoder(form(0)((_, mods) => Instruction.DsGwsSemaV(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            154,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            154,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            154,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            26,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            26,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            26,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            26,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            26,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            26,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            154,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            154,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsIncRtnU32 extends InstructionDef(
        "ds_inc_rtn_u32",
        decoder(form(3)((operands, mods) => Instruction.DsIncRtnU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            35,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            35,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            35,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            35,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsIncRtnU64 extends InstructionDef(
        "ds_inc_rtn_u64",
        decoder(form(3)((operands, mods) => Instruction.DsIncRtnU64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            99,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            99,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            99,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            99,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsIncSrc2U32 extends InstructionDef(
        "ds_inc_src2_u32",
        decoder(form(1)((operands, mods) => Instruction.DsIncSrc2U32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            131,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            131,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            131,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            131,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsIncSrc2U64 extends InstructionDef(
        "ds_inc_src2_u64",
        decoder(form(1)((operands, mods) => Instruction.DsIncSrc2U64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            195,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            195,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            195,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            195,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsIncU32 extends InstructionDef(
        "ds_inc_u32",
        decoder(form(2)((operands, mods) => Instruction.DsIncU32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            3,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            3,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            3,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            3,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            3,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            3,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            3,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            3,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            3,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            3,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            3,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            3,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            3,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            3,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsIncU64 extends InstructionDef(
        "ds_inc_u64",
        decoder(form(2)((operands, mods) => Instruction.DsIncU64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            67,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            67,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            67,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            67,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            67,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            67,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            67,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            67,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            67,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            67,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            67,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            67,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            67,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            67,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsLoad2addrB32 extends InstructionDef(
        "ds_load_2addr_b32",
        decoder(form(2)((operands, mods) => Instruction.DsLoad2addrB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoad2addrB64 extends InstructionDef(
        "ds_load_2addr_b64",
        decoder(form(2)((operands, mods) => Instruction.DsLoad2addrB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            119,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            119,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            119,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            119,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoad2addrStride64B32 extends InstructionDef(
        "ds_load_2addr_stride64_b32",
        decoder(form(2)((operands, mods) => Instruction.DsLoad2addrStride64B32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoad2addrStride64B64 extends InstructionDef(
        "ds_load_2addr_stride64_b64",
        decoder(form(2)((operands, mods) => Instruction.DsLoad2addrStride64B64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            120,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            120,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            120,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            120,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadAddtidB32 extends InstructionDef(
        "ds_load_addtid_b32",
        decoder(form(1)((operands, mods) => Instruction.DsLoadAddtidB32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            177,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            177,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            177,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            177,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadB128 extends InstructionDef(
        "ds_load_b128",
        decoder(form(2)((operands, mods) => Instruction.DsLoadB128(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            255,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            255,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            255,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            255,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadB32 extends InstructionDef(
        "ds_load_b32",
        decoder(form(2)((operands, mods) => Instruction.DsLoadB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadB64 extends InstructionDef(
        "ds_load_b64",
        decoder(form(2)((operands, mods) => Instruction.DsLoadB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            118,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            118,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            118,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            118,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadB96 extends InstructionDef(
        "ds_load_b96",
        decoder(form(2)((operands, mods) => Instruction.DsLoadB96(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            254,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            254,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            254,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            254,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadI16 extends InstructionDef(
        "ds_load_i16",
        decoder(form(2)((operands, mods) => Instruction.DsLoadI16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadI8 extends InstructionDef(
        "ds_load_i8",
        decoder(form(2)((operands, mods) => Instruction.DsLoadI8(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadI8D16 extends InstructionDef(
        "ds_load_i8_d16",
        decoder(form(2)((operands, mods) => Instruction.DsLoadI8D16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            164,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            164,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            164,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            164,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadI8D16Hi extends InstructionDef(
        "ds_load_i8_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.DsLoadI8D16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            165,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            165,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            165,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            165,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadTr16B128 extends InstructionDef(
        "ds_load_tr16_b128",
        decoder(form(2)((operands, mods) => Instruction.DsLoadTr16B128(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            252,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadTr4B64 extends InstructionDef(
        "ds_load_tr4_b64",
        decoder(form(2)((operands, mods) => Instruction.DsLoadTr4B64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            250,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadTr6B96 extends InstructionDef(
        "ds_load_tr6_b96",
        decoder(form(2)((operands, mods) => Instruction.DsLoadTr6B96(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            251,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadTr8B64 extends InstructionDef(
        "ds_load_tr8_b64",
        decoder(form(2)((operands, mods) => Instruction.DsLoadTr8B64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            253,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadU16 extends InstructionDef(
        "ds_load_u16",
        decoder(form(2)((operands, mods) => Instruction.DsLoadU16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadU16D16 extends InstructionDef(
        "ds_load_u16_d16",
        decoder(form(2)((operands, mods) => Instruction.DsLoadU16D16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            166,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            166,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            166,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            166,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadU16D16Hi extends InstructionDef(
        "ds_load_u16_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.DsLoadU16D16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            167,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            167,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            167,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            167,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadU8 extends InstructionDef(
        "ds_load_u8",
        decoder(form(2)((operands, mods) => Instruction.DsLoadU8(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadU8D16 extends InstructionDef(
        "ds_load_u8_d16",
        decoder(form(2)((operands, mods) => Instruction.DsLoadU8D16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            162,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            162,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            162,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            162,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsLoadU8D16Hi extends InstructionDef(
        "ds_load_u8_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.DsLoadU8D16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            163,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            163,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            163,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            163,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsMaxF32 extends InstructionDef(
        "ds_max_f32",
        decoder(form(2)((operands, mods) => Instruction.DsMaxF32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            19,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            19,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            19,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            19,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            19,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            19,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            19,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            19,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            19,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            19,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            19,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            19,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxF64 extends InstructionDef(
        "ds_max_f64",
        decoder(form(2)((operands, mods) => Instruction.DsMaxF64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            83,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            83,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            83,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            83,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            83,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            83,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            83,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            83,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            83,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            83,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            83,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            83,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxI32 extends InstructionDef(
        "ds_max_i32",
        decoder(form(2)((operands, mods) => Instruction.DsMaxI32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            6,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            6,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            6,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            6,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            6,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            6,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            6,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            6,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            6,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            6,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            6,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            6,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            6,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            6,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxI64 extends InstructionDef(
        "ds_max_i64",
        decoder(form(2)((operands, mods) => Instruction.DsMaxI64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            70,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            70,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            70,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            70,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            70,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            70,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            70,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            70,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            70,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            70,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            70,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            70,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            70,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            70,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxNumF32 extends InstructionDef(
        "ds_max_num_f32",
        decoder(form(2)((operands, mods) => Instruction.DsMaxNumF32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            19,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            19,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsMaxNumF64 extends InstructionDef(
        "ds_max_num_f64",
        decoder(form(2)((operands, mods) => Instruction.DsMaxNumF64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            83,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            83,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsMaxNumRtnF32 extends InstructionDef(
        "ds_max_num_rtn_f32",
        decoder(form(3)((operands, mods) => Instruction.DsMaxNumRtnF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsMaxNumRtnF64 extends InstructionDef(
        "ds_max_num_rtn_f64",
        decoder(form(3)((operands, mods) => Instruction.DsMaxNumRtnF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            115,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            115,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsMaxRtnF32 extends InstructionDef(
        "ds_max_rtn_f32",
        decoder(form(3)((operands, mods) => Instruction.DsMaxRtnF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            51,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            51,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            51,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            51,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxRtnF64 extends InstructionDef(
        "ds_max_rtn_f64",
        decoder(form(3)((operands, mods) => Instruction.DsMaxRtnF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            115,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            115,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            115,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            115,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            115,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            115,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            115,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            115,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            115,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            115,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            115,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            115,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxRtnI32 extends InstructionDef(
        "ds_max_rtn_i32",
        decoder(form(3)((operands, mods) => Instruction.DsMaxRtnI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            38,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            38,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            38,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            38,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxRtnI64 extends InstructionDef(
        "ds_max_rtn_i64",
        decoder(form(3)((operands, mods) => Instruction.DsMaxRtnI64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            102,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            102,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            102,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            102,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxRtnU32 extends InstructionDef(
        "ds_max_rtn_u32",
        decoder(form(3)((operands, mods) => Instruction.DsMaxRtnU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            40,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            40,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            40,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            40,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxRtnU64 extends InstructionDef(
        "ds_max_rtn_u64",
        decoder(form(3)((operands, mods) => Instruction.DsMaxRtnU64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            104,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            104,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            104,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            104,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxSrc2F32 extends InstructionDef(
        "ds_max_src2_f32",
        decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2F32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            147,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            147,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            147,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            147,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxSrc2F64 extends InstructionDef(
        "ds_max_src2_f64",
        decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2F64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            211,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            211,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            211,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            211,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxSrc2I32 extends InstructionDef(
        "ds_max_src2_i32",
        decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2I32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            134,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            134,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            134,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            134,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxSrc2I64 extends InstructionDef(
        "ds_max_src2_i64",
        decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2I64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            198,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            198,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            198,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            198,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxSrc2U32 extends InstructionDef(
        "ds_max_src2_u32",
        decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2U32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            136,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            136,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            136,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            136,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxSrc2U64 extends InstructionDef(
        "ds_max_src2_u64",
        decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2U64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            200,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            200,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            200,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            200,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxU32 extends InstructionDef(
        "ds_max_u32",
        decoder(form(2)((operands, mods) => Instruction.DsMaxU32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            8,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            8,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            8,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            8,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            8,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            8,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            8,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            8,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            8,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            8,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            8,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            8,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            8,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            8,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMaxU64 extends InstructionDef(
        "ds_max_u64",
        decoder(form(2)((operands, mods) => Instruction.DsMaxU64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            72,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            72,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            72,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            72,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            72,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            72,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            72,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            72,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            72,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            72,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            72,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            72,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            72,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            72,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinF32 extends InstructionDef(
        "ds_min_f32",
        decoder(form(2)((operands, mods) => Instruction.DsMinF32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            18,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            18,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            18,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            18,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            18,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            18,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            18,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            18,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            18,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            18,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            18,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            18,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinF64 extends InstructionDef(
        "ds_min_f64",
        decoder(form(2)((operands, mods) => Instruction.DsMinF64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            82,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            82,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            82,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            82,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            82,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            82,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            82,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            82,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            82,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            82,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            82,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            82,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinI32 extends InstructionDef(
        "ds_min_i32",
        decoder(form(2)((operands, mods) => Instruction.DsMinI32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            5,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            5,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            5,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            5,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            5,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            5,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            5,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            5,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            5,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            5,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            5,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            5,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            5,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            5,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinI64 extends InstructionDef(
        "ds_min_i64",
        decoder(form(2)((operands, mods) => Instruction.DsMinI64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            69,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            69,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            69,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            69,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            69,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            69,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            69,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            69,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            69,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            69,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            69,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            69,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            69,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            69,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinNumF32 extends InstructionDef(
        "ds_min_num_f32",
        decoder(form(2)((operands, mods) => Instruction.DsMinNumF32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            18,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            18,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsMinNumF64 extends InstructionDef(
        "ds_min_num_f64",
        decoder(form(2)((operands, mods) => Instruction.DsMinNumF64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            82,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            82,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsMinNumRtnF32 extends InstructionDef(
        "ds_min_num_rtn_f32",
        decoder(form(3)((operands, mods) => Instruction.DsMinNumRtnF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsMinNumRtnF64 extends InstructionDef(
        "ds_min_num_rtn_f64",
        decoder(form(3)((operands, mods) => Instruction.DsMinNumRtnF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            114,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            114,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsMinRtnF32 extends InstructionDef(
        "ds_min_rtn_f32",
        decoder(form(3)((operands, mods) => Instruction.DsMinRtnF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            50,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            50,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            50,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            50,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinRtnF64 extends InstructionDef(
        "ds_min_rtn_f64",
        decoder(form(3)((operands, mods) => Instruction.DsMinRtnF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            114,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            114,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            114,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            114,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            114,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            114,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            114,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            114,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            114,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            114,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            114,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            114,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinRtnI32 extends InstructionDef(
        "ds_min_rtn_i32",
        decoder(form(3)((operands, mods) => Instruction.DsMinRtnI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            37,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            37,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            37,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            37,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinRtnI64 extends InstructionDef(
        "ds_min_rtn_i64",
        decoder(form(3)((operands, mods) => Instruction.DsMinRtnI64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            101,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            101,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            101,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            101,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinRtnU32 extends InstructionDef(
        "ds_min_rtn_u32",
        decoder(form(3)((operands, mods) => Instruction.DsMinRtnU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            39,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            39,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            39,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            39,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinRtnU64 extends InstructionDef(
        "ds_min_rtn_u64",
        decoder(form(3)((operands, mods) => Instruction.DsMinRtnU64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            103,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            103,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            103,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            103,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinSrc2F32 extends InstructionDef(
        "ds_min_src2_f32",
        decoder(form(1)((operands, mods) => Instruction.DsMinSrc2F32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            146,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            146,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            146,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            146,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinSrc2F64 extends InstructionDef(
        "ds_min_src2_f64",
        decoder(form(1)((operands, mods) => Instruction.DsMinSrc2F64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            210,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            210,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            210,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            210,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinSrc2I32 extends InstructionDef(
        "ds_min_src2_i32",
        decoder(form(1)((operands, mods) => Instruction.DsMinSrc2I32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            133,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            133,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            133,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            133,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinSrc2I64 extends InstructionDef(
        "ds_min_src2_i64",
        decoder(form(1)((operands, mods) => Instruction.DsMinSrc2I64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            197,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            197,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            197,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            197,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinSrc2U32 extends InstructionDef(
        "ds_min_src2_u32",
        decoder(form(1)((operands, mods) => Instruction.DsMinSrc2U32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            135,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            135,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            135,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            135,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinSrc2U64 extends InstructionDef(
        "ds_min_src2_u64",
        decoder(form(1)((operands, mods) => Instruction.DsMinSrc2U64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            199,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            199,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            199,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            199,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinU32 extends InstructionDef(
        "ds_min_u32",
        decoder(form(2)((operands, mods) => Instruction.DsMinU32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            7,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            7,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            7,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            7,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            7,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            7,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            7,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            7,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            7,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            7,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            7,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            7,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            7,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            7,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMinU64 extends InstructionDef(
        "ds_min_u64",
        decoder(form(2)((operands, mods) => Instruction.DsMinU64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            71,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            71,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            71,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            71,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            71,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            71,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            71,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            71,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            71,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            71,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            71,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            71,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            71,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            71,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMskorB32 extends InstructionDef(
        "ds_mskor_b32",
        decoder(form(3)((operands, mods) => Instruction.DsMskorB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            12,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            12,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            12,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            12,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            12,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            12,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            12,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            12,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            12,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            12,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            12,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            12,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            12,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            12,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMskorB64 extends InstructionDef(
        "ds_mskor_b64",
        decoder(form(3)((operands, mods) => Instruction.DsMskorB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            76,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            76,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            76,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            76,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            76,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            76,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            76,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            76,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            76,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            76,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            76,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            76,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            76,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            76,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )

  val definitions: Vector[InstructionDef] = Vector(
    DsCmpstoreB64,
    DsCmpstoreF32,
    DsCmpstoreF64,
    DsCmpstoreRtnB32,
    DsCmpstoreRtnB64,
    DsCmpstoreRtnF32,
    DsCmpstoreRtnF64,
    DsCondSubRtnU32,
    DsCondSubU32,
    DsCondxchg32RtnB128,
    DsCondxchg32RtnB64,
    DsConsume,
    DsDecRtnU32,
    DsDecRtnU64,
    DsDecSrc2U32,
    DsDecSrc2U64,
    DsDecU32,
    DsDecU64,
    DsDirectLoad,
    DsGwsBarrier,
    DsGwsInit,
    DsGwsSemaBr,
    DsGwsSemaP,
    DsGwsSemaReleaseAll,
    DsGwsSemaV,
    DsIncRtnU32,
    DsIncRtnU64,
    DsIncSrc2U32,
    DsIncSrc2U64,
    DsIncU32,
    DsIncU64,
    DsLoad2addrB32,
    DsLoad2addrB64,
    DsLoad2addrStride64B32,
    DsLoad2addrStride64B64,
    DsLoadAddtidB32,
    DsLoadB128,
    DsLoadB32,
    DsLoadB64,
    DsLoadB96,
    DsLoadI16,
    DsLoadI8,
    DsLoadI8D16,
    DsLoadI8D16Hi,
    DsLoadTr16B128,
    DsLoadTr4B64,
    DsLoadTr6B96,
    DsLoadTr8B64,
    DsLoadU16,
    DsLoadU16D16,
    DsLoadU16D16Hi,
    DsLoadU8,
    DsLoadU8D16,
    DsLoadU8D16Hi,
    DsMaxF32,
    DsMaxF64,
    DsMaxI32,
    DsMaxI64,
    DsMaxNumF32,
    DsMaxNumF64,
    DsMaxNumRtnF32,
    DsMaxNumRtnF64,
    DsMaxRtnF32,
    DsMaxRtnF64,
    DsMaxRtnI32,
    DsMaxRtnI64,
    DsMaxRtnU32,
    DsMaxRtnU64,
    DsMaxSrc2F32,
    DsMaxSrc2F64,
    DsMaxSrc2I32,
    DsMaxSrc2I64,
    DsMaxSrc2U32,
    DsMaxSrc2U64,
    DsMaxU32,
    DsMaxU64,
    DsMinF32,
    DsMinF64,
    DsMinI32,
    DsMinI64,
    DsMinNumF32,
    DsMinNumF64,
    DsMinNumRtnF32,
    DsMinNumRtnF64,
    DsMinRtnF32,
    DsMinRtnF64,
    DsMinRtnI32,
    DsMinRtnI64,
    DsMinRtnU32,
    DsMinRtnU64,
    DsMinSrc2F32,
    DsMinSrc2F64,
    DsMinSrc2I32,
    DsMinSrc2I64,
    DsMinSrc2U32,
    DsMinSrc2U64,
    DsMinU32,
    DsMinU64,
    DsMskorB32,
    DsMskorB64
  )
}
