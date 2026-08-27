// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

private[common] object InstructionDefs3 {
  case object DsMskorRtnB32 extends InstructionDef(
        "ds_mskor_rtn_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.DsMskorRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            44,
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            44,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
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
            44,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
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
            44,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
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
            44,
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
            Generation.GFX10,
            "rdna1",
            "DS",
            44,
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
            Generation.GFX10,
            "rdna2",
            "DS",
            44,
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
            44,
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
            44,
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
            44,
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
            Generation.GFX6,
            "gfx6",
            "DS",
            44,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            44,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            44,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            44,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsMskorRtnB64 extends InstructionDef(
        "ds_mskor_rtn_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.DsMskorRtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            108,
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            108,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
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
            108,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
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
            108,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
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
            108,
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
            Generation.GFX10,
            "rdna1",
            "DS",
            108,
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
            Generation.GFX10,
            "rdna2",
            "DS",
            108,
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
            108,
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
            108,
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
            108,
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
            Generation.GFX6,
            "gfx6",
            "DS",
            108,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            108,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            108,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            108,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsNop extends InstructionDef(
        "ds_nop",
        decoder(form(0)((_, mods) => Instruction.DsNop(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            20,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            20,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            20,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            20,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            20,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            20,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            20,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            20,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            20,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            20,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            20,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            20,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            20,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsOrB32 extends InstructionDef(
        "ds_or_b32",
        decoder(form(2)((operands, mods) => Instruction.DsOrB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            10,
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
            10,
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
            10,
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
            10,
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
            10,
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
            10,
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
            10,
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
            10,
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
            10,
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
            10,
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
            10,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            10,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            10,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            10,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsOrB64 extends InstructionDef(
        "ds_or_b64",
        decoder(form(2)((operands, mods) => Instruction.DsOrB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            74,
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
            74,
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
            74,
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
            74,
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
            74,
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
            74,
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
            74,
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
            74,
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
            74,
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
            74,
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
            74,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            74,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            74,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            74,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsOrRtnB32 extends InstructionDef(
        "ds_or_rtn_b32",
        decoder(form(3)((operands, mods) => Instruction.DsOrRtnB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            42,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            42,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            42,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsOrRtnB64 extends InstructionDef(
        "ds_or_rtn_b64",
        decoder(form(3)((operands, mods) => Instruction.DsOrRtnB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            106,
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
            106,
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
            106,
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
            106,
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
            106,
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
            106,
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
            106,
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
            106,
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
            106,
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
            106,
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
            106,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            106,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            106,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            106,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsOrSrc2B32 extends InstructionDef(
        "ds_or_src2_b32",
        decoder(form(1)((operands, mods) => Instruction.DsOrSrc2B32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            138,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            138,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            138,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            138,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsOrSrc2B64 extends InstructionDef(
        "ds_or_src2_b64",
        decoder(form(1)((operands, mods) => Instruction.DsOrSrc2B64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            202,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            202,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            202,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            202,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsOrderedCount extends InstructionDef(
        "ds_ordered_count",
        decoder(form(2)((operands, mods) => Instruction.DsOrderedCount(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            191,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
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
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
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
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            63,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            63,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            191,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            191,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsParamLoad extends InstructionDef(
        "ds_param_load",
        decoder(form(2)((operands, mods) => Instruction.DsParamLoad(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDSDIR",
            0,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ATTR"), "ATTR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsPermuteB32 extends InstructionDef(
        "ds_permute_b32",
        decoder(form(3)((operands, mods) => Instruction.DsPermuteB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            178,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            178,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            178,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            178,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            178,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            178,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            62,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            62,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsPkAddBf16 extends InstructionDef(
        "ds_pk_add_bf16",
        decoder(form(2)((operands, mods) => Instruction.DsPkAddBf16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            24,
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
            24,
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
            155,
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
            155,
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
  case object DsPkAddF16 extends InstructionDef(
        "ds_pk_add_f16",
        decoder(form(2)((operands, mods) => Instruction.DsPkAddF16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            23,
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
            23,
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
            154,
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
            154,
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
  case object DsPkAddRtnBf16 extends InstructionDef(
        "ds_pk_add_rtn_bf16",
        decoder(form(3)((operands, mods) => Instruction.DsPkAddRtnBf16(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            184,
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
            184,
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
            171,
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
            171,
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
  case object DsPkAddRtnF16 extends InstructionDef(
        "ds_pk_add_rtn_f16",
        decoder(form(3)((operands, mods) => Instruction.DsPkAddRtnF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            183,
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
            183,
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
            170,
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
            170,
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
  case object DsRead2B32 extends InstructionDef(
        "ds_read2_b32",
        decoder(form(2)((operands, mods) => Instruction.DsRead2B32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            55,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            55,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            55,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            55,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsRead2B64 extends InstructionDef(
        "ds_read2_b64",
        decoder(form(2)((operands, mods) => Instruction.DsRead2B64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            119,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            119,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            119,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            119,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            119,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            119,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            119,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsRead2st64B32 extends InstructionDef(
        "ds_read2st64_b32",
        decoder(form(2)((operands, mods) => Instruction.DsRead2st64B32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            56,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            56,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            56,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            56,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsRead2st64B64 extends InstructionDef(
        "ds_read2st64_b64",
        decoder(form(2)((operands, mods) => Instruction.DsRead2st64B64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            120,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            120,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            120,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            120,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            120,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            120,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            120,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadAddtidB32 extends InstructionDef(
        "ds_read_addtid_b32",
        decoder(form(1)((operands, mods) => Instruction.DsReadAddtidB32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            182,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            182,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            182,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            182,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX8,
            "gfx8",
            "DS",
            182,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            182,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadB128 extends InstructionDef(
        "ds_read_b128",
        decoder(form(2)((operands, mods) => Instruction.DsReadB128(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            255,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            255,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            255,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX7,
            "gfx7",
            "DS",
            255,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            255,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            255,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadB32 extends InstructionDef(
        "ds_read_b32",
        decoder(form(2)((operands, mods) => Instruction.DsReadB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            54,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            54,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            54,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            54,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadB64 extends InstructionDef(
        "ds_read_b64",
        decoder(form(2)((operands, mods) => Instruction.DsReadB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            118,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            118,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            118,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            118,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            118,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            118,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            118,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadB64TrB16 extends InstructionDef(
        "ds_read_b64_tr_b16",
        decoder(form(2)((operands, mods) => Instruction.DsReadB64TrB16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            227,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsReadB64TrB4 extends InstructionDef(
        "ds_read_b64_tr_b4",
        decoder(form(2)((operands, mods) => Instruction.DsReadB64TrB4(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            224,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsReadB64TrB8 extends InstructionDef(
        "ds_read_b64_tr_b8",
        decoder(form(2)((operands, mods) => Instruction.DsReadB64TrB8(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            226,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsReadB96 extends InstructionDef(
        "ds_read_b96",
        decoder(form(2)((operands, mods) => Instruction.DsReadB96(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            254,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            254,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            254,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX7,
            "gfx7",
            "DS",
            254,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            254,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            254,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadB96TrB6 extends InstructionDef(
        "ds_read_b96_tr_b6",
        decoder(form(2)((operands, mods) => Instruction.DsReadB96TrB6(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            225,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsReadI16 extends InstructionDef(
        "ds_read_i16",
        decoder(form(2)((operands, mods) => Instruction.DsReadI16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            59,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            59,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            59,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            59,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadI8 extends InstructionDef(
        "ds_read_i8",
        decoder(form(2)((operands, mods) => Instruction.DsReadI8(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            57,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            57,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            57,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            57,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadI8D16 extends InstructionDef(
        "ds_read_i8_d16",
        decoder(form(2)((operands, mods) => Instruction.DsReadI8D16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            88,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            88,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            88,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            88,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX9,
            "gfx9",
            "DS",
            88,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadI8D16Hi extends InstructionDef(
        "ds_read_i8_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.DsReadI8D16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            89,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            89,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            89,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            89,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX9,
            "gfx9",
            "DS",
            89,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadU16 extends InstructionDef(
        "ds_read_u16",
        decoder(form(2)((operands, mods) => Instruction.DsReadU16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            60,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            60,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            60,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            60,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadU16D16 extends InstructionDef(
        "ds_read_u16_d16",
        decoder(form(2)((operands, mods) => Instruction.DsReadU16D16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            90,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            90,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            90,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            90,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX9,
            "gfx9",
            "DS",
            90,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadU16D16Hi extends InstructionDef(
        "ds_read_u16_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.DsReadU16D16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            91,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            91,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            91,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            91,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX9,
            "gfx9",
            "DS",
            91,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadU8 extends InstructionDef(
        "ds_read_u8",
        decoder(form(2)((operands, mods) => Instruction.DsReadU8(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            58,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            58,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            58,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            58,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadU8D16 extends InstructionDef(
        "ds_read_u8_d16",
        decoder(form(2)((operands, mods) => Instruction.DsReadU8D16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX9,
            "gfx9",
            "DS",
            86,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsReadU8D16Hi extends InstructionDef(
        "ds_read_u8_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.DsReadU8D16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            87,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            87,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            87,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            87,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX9,
            "gfx9",
            "DS",
            87,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsRsubRtnU32 extends InstructionDef(
        "ds_rsub_rtn_u32",
        decoder(form(3)((operands, mods) => Instruction.DsRsubRtnU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            34,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            34,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            34,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsRsubRtnU64 extends InstructionDef(
        "ds_rsub_rtn_u64",
        decoder(form(3)((operands, mods) => Instruction.DsRsubRtnU64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            98,
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
            98,
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
            98,
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
            98,
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
            98,
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
            98,
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
            98,
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
            98,
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
            98,
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
            98,
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
            98,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            98,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            98,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            98,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsRsubSrc2U32 extends InstructionDef(
        "ds_rsub_src2_u32",
        decoder(form(1)((operands, mods) => Instruction.DsRsubSrc2U32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            130,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            130,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            130,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            130,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsRsubSrc2U64 extends InstructionDef(
        "ds_rsub_src2_u64",
        decoder(form(1)((operands, mods) => Instruction.DsRsubSrc2U64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            194,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            194,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            194,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            194,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsRsubU32 extends InstructionDef(
        "ds_rsub_u32",
        decoder(form(2)((operands, mods) => Instruction.DsRsubU32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            2,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            2,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            2,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsRsubU64 extends InstructionDef(
        "ds_rsub_u64",
        decoder(form(2)((operands, mods) => Instruction.DsRsubU64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            66,
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
            66,
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
            66,
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
            66,
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
            66,
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
            66,
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
            66,
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
            66,
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
            66,
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
            66,
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
            66,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            66,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            66,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            66,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsStore2addrB32 extends InstructionDef(
        "ds_store_2addr_b32",
        decoder(form(3)((operands, mods) => Instruction.DsStore2addrB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            14,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            14,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            14,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            14,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsStore2addrB64 extends InstructionDef(
        "ds_store_2addr_b64",
        decoder(form(3)((operands, mods) => Instruction.DsStore2addrB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            78,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            78,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            78,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            78,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsStore2addrStride64B32 extends InstructionDef(
        "ds_store_2addr_stride64_b32",
        decoder(form(3)((operands, mods) =>
          Instruction.DsStore2addrStride64B32(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            15,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            15,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            15,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            15,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsStore2addrStride64B64 extends InstructionDef(
        "ds_store_2addr_stride64_b64",
        decoder(form(3)((operands, mods) =>
          Instruction.DsStore2addrStride64B64(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            79,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            79,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            79,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            79,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsStoreAddtidB32 extends InstructionDef(
        "ds_store_addtid_b32",
        decoder(form(1)((operands, mods) => Instruction.DsStoreAddtidB32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            176,
            Vector(
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            176,
            Vector(
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            176,
            Vector(
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            176,
            Vector(
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsStoreB128 extends InstructionDef(
        "ds_store_b128",
        decoder(form(2)((operands, mods) => Instruction.DsStoreB128(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            223,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 128, true, false, false),
              OperandInfo(None, "DSMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            223,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 128, true, false, false),
              OperandInfo(None, "DSMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            223,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 128, true, false, false),
              OperandInfo(None, "DSMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            223,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 128, true, false, false),
              OperandInfo(None, "DSMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsStoreB16 extends InstructionDef(
        "ds_store_b16",
        decoder(form(2)((operands, mods) => Instruction.DsStoreB16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 16, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 16, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 16, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 16, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsStoreB16D16Hi extends InstructionDef(
        "ds_store_b16_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.DsStoreB16D16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            161,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            161,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            161,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            161,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsStoreB32 extends InstructionDef(
        "ds_store_b32",
        decoder(form(2)((operands, mods) => Instruction.DsStoreB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            13,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            13,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            13,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            13,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsStoreB64 extends InstructionDef(
        "ds_store_b64",
        decoder(form(2)((operands, mods) => Instruction.DsStoreB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            77,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            77,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            77,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            77,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsStoreB8 extends InstructionDef(
        "ds_store_b8",
        decoder(form(2)((operands, mods) => Instruction.DsStoreB8(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 8, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 8, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 8, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 8, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsStoreB8D16Hi extends InstructionDef(
        "ds_store_b8_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.DsStoreB8D16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            160,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            160,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            160,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            160,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsStoreB96 extends InstructionDef(
        "ds_store_b96",
        decoder(form(2)((operands, mods) => Instruction.DsStoreB96(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            222,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 96, true, false, false),
              OperandInfo(None, "DSMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            222,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 96, true, false, false),
              OperandInfo(None, "DSMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            222,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 96, true, false, false),
              OperandInfo(None, "DSMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            222,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 96, true, false, false),
              OperandInfo(None, "DSMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsStorexchg2addrRtnB32 extends InstructionDef(
        "ds_storexchg_2addr_rtn_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.DsStorexchg2addrRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            46,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
            46,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
            46,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
            46,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
  case object DsStorexchg2addrRtnB64 extends InstructionDef(
        "ds_storexchg_2addr_rtn_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.DsStorexchg2addrRtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            110,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
            110,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
            110,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
            110,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
  case object DsStorexchg2addrStride64RtnB32 extends InstructionDef(
        "ds_storexchg_2addr_stride64_rtn_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.DsStorexchg2addrStride64RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
  case object DsStorexchg2addrStride64RtnB64 extends InstructionDef(
        "ds_storexchg_2addr_stride64_rtn_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.DsStorexchg2addrStride64RtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            111,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
            111,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
            111,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
            111,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
  case object DsStorexchgRtnB32 extends InstructionDef(
        "ds_storexchg_rtn_b32",
        decoder(form(3)((operands, mods) =>
          Instruction.DsStorexchgRtnB32(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            45,
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
            45,
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
            45,
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
            45,
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
  case object DsStorexchgRtnB64 extends InstructionDef(
        "ds_storexchg_rtn_b64",
        decoder(form(3)((operands, mods) =>
          Instruction.DsStorexchgRtnB64(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            109,
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
            109,
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
            109,
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
            109,
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
  case object DsSubClampRtnU32 extends InstructionDef(
        "ds_sub_clamp_rtn_u32",
        decoder(form(3)((operands, mods) => Instruction.DsSubClampRtnU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            169,
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
            169,
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
  case object DsSubClampU32 extends InstructionDef(
        "ds_sub_clamp_u32",
        decoder(form(2)((operands, mods) => Instruction.DsSubClampU32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            153,
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
            153,
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
  case object DsSubGsRegRtn extends InstructionDef(
        "ds_sub_gs_reg_rtn",
        decoder(form(2)((operands, mods) => Instruction.DsSubGsRegRtn(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            123,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            123,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsSubRtnU32 extends InstructionDef(
        "ds_sub_rtn_u32",
        decoder(form(3)((operands, mods) => Instruction.DsSubRtnU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
            33,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            33,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            33,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            33,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsSubRtnU64 extends InstructionDef(
        "ds_sub_rtn_u64",
        decoder(form(3)((operands, mods) => Instruction.DsSubRtnU64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            97,
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
            97,
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
            97,
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
            97,
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
            97,
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
            97,
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
            97,
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
            97,
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
            97,
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
            97,
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
            97,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            97,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            97,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            97,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsSubSrc2U32 extends InstructionDef(
        "ds_sub_src2_u32",
        decoder(form(1)((operands, mods) => Instruction.DsSubSrc2U32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            129,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            129,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            129,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            129,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsSubSrc2U64 extends InstructionDef(
        "ds_sub_src2_u64",
        decoder(form(1)((operands, mods) => Instruction.DsSubSrc2U64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            193,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            193,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            193,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            193,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsSubU32 extends InstructionDef(
        "ds_sub_u32",
        decoder(form(2)((operands, mods) => Instruction.DsSubU32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            1,
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
            1,
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
            1,
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
            1,
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
            1,
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
            1,
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
            1,
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
            1,
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
            1,
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
            1,
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
            1,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            1,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            1,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            1,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsSubU64 extends InstructionDef(
        "ds_sub_u64",
        decoder(form(2)((operands, mods) => Instruction.DsSubU64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            65,
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
            65,
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
            65,
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
            65,
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
            65,
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
            65,
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
            65,
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
            65,
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
            65,
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
            65,
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
            65,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            65,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            65,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            65,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsSwizzleB32 extends InstructionDef(
        "ds_swizzle_b32",
        decoder(form(2)((operands, mods) => Instruction.DsSwizzleB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "DS",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            53,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            53,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            61,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            61,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWrapRtnB32 extends InstructionDef(
        "ds_wrap_rtn_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.DsWrapRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            52,
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            52,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
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
            52,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
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
            52,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
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
            Generation.GFX10,
            "rdna1",
            "DS",
            52,
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
            Generation.GFX10,
            "rdna2",
            "DS",
            52,
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
            52,
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
            52,
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
            Generation.GFX7,
            "gfx7",
            "DS",
            52,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            52,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            52,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWrite2B32 extends InstructionDef(
        "ds_write2_b32",
        decoder(form(3)((operands, mods) => Instruction.DsWrite2B32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            14,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            14,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            14,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            14,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            14,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            14,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            14,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            14,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            14,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            14,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWrite2B64 extends InstructionDef(
        "ds_write2_b64",
        decoder(form(3)((operands, mods) => Instruction.DsWrite2B64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            78,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            78,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            78,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            78,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            78,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            78,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            78,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            78,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            78,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            78,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWrite2st64B32 extends InstructionDef(
        "ds_write2st64_b32",
        decoder(form(3)((operands, mods) => Instruction.DsWrite2st64B32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            15,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            15,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            15,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            15,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            15,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            15,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            15,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            15,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            15,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            15,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWrite2st64B64 extends InstructionDef(
        "ds_write2st64_b64",
        decoder(form(3)((operands, mods) => Instruction.DsWrite2st64B64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            79,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            79,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            79,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            79,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            79,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            79,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            79,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            79,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            79,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            79,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWriteAddtidB32 extends InstructionDef(
        "ds_write_addtid_b32",
        decoder(form(1)((operands, mods) => Instruction.DsWriteAddtidB32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            29,
            Vector(
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            29,
            Vector(
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            29,
            Vector(
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            29,
            Vector(
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            176,
            Vector(
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            176,
            Vector(
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            29,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            29,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWriteB128 extends InstructionDef(
        "ds_write_b128",
        decoder(form(2)((operands, mods) => Instruction.DsWriteB128(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            223,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 128, true, false, false),
              OperandInfo(None, "DSMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            223,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(None, "DSMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            223,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(None, "DSMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            223,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(None, "DSMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            223,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 128, true, false, false),
              OperandInfo(None, "DSMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            223,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 128, true, false, false),
              OperandInfo(None, "DSMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            223,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            223,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            223,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWriteB16 extends InstructionDef(
        "ds_write_b16",
        decoder(form(2)((operands, mods) => Instruction.DsWriteB16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 16, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 16, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 16, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 16, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 16, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 16, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            31,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            31,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            31,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            31,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWriteB16D16Hi extends InstructionDef(
        "ds_write_b16_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.DsWriteB16D16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            85,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            85,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            85,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            85,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            161,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            161,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            85,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWriteB32 extends InstructionDef(
        "ds_write_b32",
        decoder(form(2)((operands, mods) => Instruction.DsWriteB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            13,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            13,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            13,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            13,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            13,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            13,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            13,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            13,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            13,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            13,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWriteB64 extends InstructionDef(
        "ds_write_b64",
        decoder(form(2)((operands, mods) => Instruction.DsWriteB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            77,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            77,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            77,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            77,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            77,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            77,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            77,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            77,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            77,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            77,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWriteB8 extends InstructionDef(
        "ds_write_b8",
        decoder(form(2)((operands, mods) => Instruction.DsWriteB8(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 8, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 8, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 8, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 8, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 8, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 8, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            30,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            30,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            30,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            30,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWriteB8D16Hi extends InstructionDef(
        "ds_write_b8_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.DsWriteB8D16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            84,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            84,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            84,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            84,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            160,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            160,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            84,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWriteB96 extends InstructionDef(
        "ds_write_b96",
        decoder(form(2)((operands, mods) => Instruction.DsWriteB96(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            222,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 96, true, false, false),
              OperandInfo(None, "DSMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            222,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 96, true, false, false),
              OperandInfo(None, "DSMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "DS",
            222,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 96, true, false, false),
              OperandInfo(None, "DSMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "DS",
            222,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR_OR_ACCVGPR", 96, true, false, false),
              OperandInfo(None, "DSMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "DS",
            222,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 96, true, false, false),
              OperandInfo(None, "DSMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "DS",
            222,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 96, true, false, false),
              OperandInfo(None, "DSMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            222,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            222,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            222,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWriteSrc2B32 extends InstructionDef(
        "ds_write_src2_b32",
        decoder(form(1)((operands, mods) => Instruction.DsWriteSrc2B32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            141,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            141,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            141,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            141,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWriteSrc2B64 extends InstructionDef(
        "ds_write_src2_b64",
        decoder(form(1)((operands, mods) => Instruction.DsWriteSrc2B64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            205,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            205,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            205,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            205,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWrxchg2RtnB32 extends InstructionDef(
        "ds_wrxchg2_rtn_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.DsWrxchg2RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            46,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
            46,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
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
            46,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
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
            46,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
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
            Generation.GFX10,
            "rdna1",
            "DS",
            46,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
            46,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
            46,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            46,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            46,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            46,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWrxchg2RtnB64 extends InstructionDef(
        "ds_wrxchg2_rtn_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.DsWrxchg2RtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            110,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
            110,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
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
            110,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
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
            110,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
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
            Generation.GFX10,
            "rdna1",
            "DS",
            110,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
            110,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
            110,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            110,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            110,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            110,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWrxchg2st64RtnB32 extends InstructionDef(
        "ds_wrxchg2st64_rtn_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.DsWrxchg2st64RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
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
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
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
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
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
            Generation.GFX10,
            "rdna1",
            "DS",
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
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
            47,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            47,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            47,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            47,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWrxchg2st64RtnB64 extends InstructionDef(
        "ds_wrxchg2st64_rtn_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.DsWrxchg2st64RtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            111,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
            111,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
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
            111,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
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
            111,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
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
            Generation.GFX10,
            "rdna1",
            "DS",
            111,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
            111,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
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
            111,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            111,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            111,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            111,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWrxchgRtnB32 extends InstructionDef(
        "ds_wrxchg_rtn_b32",
        decoder(form(3)((operands, mods) => Instruction.DsWrxchgRtnB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            45,
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
            45,
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
            45,
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
            45,
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
            45,
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
            45,
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
            45,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            45,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            45,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            45,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsWrxchgRtnB64 extends InstructionDef(
        "ds_wrxchg_rtn_b64",
        decoder(form(3)((operands, mods) => Instruction.DsWrxchgRtnB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            109,
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
            109,
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
            109,
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
            109,
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
            109,
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
            109,
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
            109,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            109,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            109,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            109,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsXorB32 extends InstructionDef(
        "ds_xor_b32",
        decoder(form(2)((operands, mods) => Instruction.DsXorB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            11,
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
            11,
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
            11,
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
            11,
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
            11,
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
            11,
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
            11,
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
            11,
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
            11,
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
            11,
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
            11,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            11,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            11,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            11,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsXorB64 extends InstructionDef(
        "ds_xor_b64",
        decoder(form(2)((operands, mods) => Instruction.DsXorB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            75,
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
            75,
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
            75,
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
            75,
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
            75,
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
            75,
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
            75,
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
            75,
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
            75,
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
            75,
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
            75,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            75,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            75,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            75,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsXorRtnB32 extends InstructionDef(
        "ds_xor_rtn_b32",
        decoder(form(3)((operands, mods) => Instruction.DsXorRtnB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            43,
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
            43,
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
            43,
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
            43,
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
            43,
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
            43,
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
            43,
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
            43,
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
            43,
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
            43,
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
            43,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            43,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            43,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            43,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsXorRtnB64 extends InstructionDef(
        "ds_xor_rtn_b64",
        decoder(form(3)((operands, mods) => Instruction.DsXorRtnB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            107,
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
            107,
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
            107,
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
            107,
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
            107,
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
            107,
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
            107,
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
            107,
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
            107,
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
            107,
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
            107,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            107,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            107,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            107,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )

  val definitions: Vector[InstructionDef] = Vector(
    DsMskorRtnB32,
    DsMskorRtnB64,
    DsNop,
    DsOrB32,
    DsOrB64,
    DsOrRtnB32,
    DsOrRtnB64,
    DsOrSrc2B32,
    DsOrSrc2B64,
    DsOrderedCount,
    DsParamLoad,
    DsPermuteB32,
    DsPkAddBf16,
    DsPkAddF16,
    DsPkAddRtnBf16,
    DsPkAddRtnF16,
    DsRead2B32,
    DsRead2B64,
    DsRead2st64B32,
    DsRead2st64B64,
    DsReadAddtidB32,
    DsReadB128,
    DsReadB32,
    DsReadB64,
    DsReadB64TrB16,
    DsReadB64TrB4,
    DsReadB64TrB8,
    DsReadB96,
    DsReadB96TrB6,
    DsReadI16,
    DsReadI8,
    DsReadI8D16,
    DsReadI8D16Hi,
    DsReadU16,
    DsReadU16D16,
    DsReadU16D16Hi,
    DsReadU8,
    DsReadU8D16,
    DsReadU8D16Hi,
    DsRsubRtnU32,
    DsRsubRtnU64,
    DsRsubSrc2U32,
    DsRsubSrc2U64,
    DsRsubU32,
    DsRsubU64,
    DsStore2addrB32,
    DsStore2addrB64,
    DsStore2addrStride64B32,
    DsStore2addrStride64B64,
    DsStoreAddtidB32,
    DsStoreB128,
    DsStoreB16,
    DsStoreB16D16Hi,
    DsStoreB32,
    DsStoreB64,
    DsStoreB8,
    DsStoreB8D16Hi,
    DsStoreB96,
    DsStorexchg2addrRtnB32,
    DsStorexchg2addrRtnB64,
    DsStorexchg2addrStride64RtnB32,
    DsStorexchg2addrStride64RtnB64,
    DsStorexchgRtnB32,
    DsStorexchgRtnB64,
    DsSubClampRtnU32,
    DsSubClampU32,
    DsSubGsRegRtn,
    DsSubRtnU32,
    DsSubRtnU64,
    DsSubSrc2U32,
    DsSubSrc2U64,
    DsSubU32,
    DsSubU64,
    DsSwizzleB32,
    DsWrapRtnB32,
    DsWrite2B32,
    DsWrite2B64,
    DsWrite2st64B32,
    DsWrite2st64B64,
    DsWriteAddtidB32,
    DsWriteB128,
    DsWriteB16,
    DsWriteB16D16Hi,
    DsWriteB32,
    DsWriteB64,
    DsWriteB8,
    DsWriteB8D16Hi,
    DsWriteB96,
    DsWriteSrc2B32,
    DsWriteSrc2B64,
    DsWrxchg2RtnB32,
    DsWrxchg2RtnB64,
    DsWrxchg2st64RtnB32,
    DsWrxchg2st64RtnB64,
    DsWrxchgRtnB32,
    DsWrxchgRtnB64,
    DsXorB32,
    DsXorB64,
    DsXorRtnB32,
    DsXorRtnB64
  )
}
