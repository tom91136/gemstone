// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

private[common] object InstructionDefs5 {
  case object FlatLoadShortD16Hi extends InstructionDef(
        "flat_load_short_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadShortD16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            37,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            37,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadSshort extends InstructionDef(
        "flat_load_sshort",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadSshort(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            11,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            11,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            11,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            11,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            19,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            19,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadU16 extends InstructionDef(
        "flat_load_u16",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadU16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatLoadU8 extends InstructionDef(
        "flat_load_u8",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadU8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadU8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatLoadUbyte extends InstructionDef(
        "flat_load_ubyte",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadUbyte(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            8,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            8,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            8,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            8,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            16,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            16,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadUbyteD16 extends InstructionDef(
        "flat_load_ubyte_d16",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadUbyteD16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            32,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            32,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadUbyteD16Hi extends InstructionDef(
        "flat_load_ubyte_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadUbyteD16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            33,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            33,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadUshort extends InstructionDef(
        "flat_load_ushort",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadUshort(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            10,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            10,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            10,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            10,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            18,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            18,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatPrefetchB8 extends InstructionDef(
        "flat_prefetch_b8",
        decoder(
          form(1)((operands, mods) => Instruction.FlatPrefetchB8_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.FlatPrefetchB8_2(operands(0), operands(1), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            93,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatStoreB128 extends InstructionDef(
        "flat_store_b128",
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreB128_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            29,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            29,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatStoreB16 extends InstructionDef(
        "flat_store_b16",
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreB16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreB16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            25,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            25,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatStoreB32 extends InstructionDef(
        "flat_store_b32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreB32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            26,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            26,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatStoreB64 extends InstructionDef(
        "flat_store_b64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreB64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            27,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            27,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatStoreB8 extends InstructionDef(
        "flat_store_b8",
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreB8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreB8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            24,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            24,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatStoreB96 extends InstructionDef(
        "flat_store_b96",
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreB96_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreB96_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            28,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            28,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 96, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatStoreByte extends InstructionDef(
        "flat_store_byte",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreByte(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
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
            "FLAT",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatStoreByteD16Hi extends InstructionDef(
        "flat_store_byte_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreByteD16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
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
            "FLAT",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            25,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            25,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatStoreD16HiB16 extends InstructionDef(
        "flat_store_d16_hi_b16",
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreD16HiB16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreD16HiB16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            37,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            37,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            37,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            37,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatStoreD16HiB8 extends InstructionDef(
        "flat_store_d16_hi_b8",
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreD16HiB8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreD16HiB8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            36,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            36,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            36,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            36,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatStoreDword extends InstructionDef(
        "flat_store_dword",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreDword(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
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
            "FLAT",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatStoreDwordx2 extends InstructionDef(
        "flat_store_dwordx2",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreDwordx2(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
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
            "FLAT",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
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
            "FLAT",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
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
            "FLAT",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
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
            "FLAT",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            29,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            29,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            29,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            29,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatStoreDwordx3 extends InstructionDef(
        "flat_store_dwordx3",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreDwordx3(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
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
            "FLAT",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 96, true, false, false),
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
            "FLAT",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 96, true, false, false),
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
            "FLAT",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 96, true, false, false),
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
            "FLAT",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            31,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            31,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            30,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            30,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatStoreDwordx4 extends InstructionDef(
        "flat_store_dwordx4",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreDwordx4(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
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
            "FLAT",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
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
            "FLAT",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
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
            "FLAT",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
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
            "FLAT",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            30,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            30,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            31,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            31,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatStoreShort extends InstructionDef(
        "flat_store_short",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreShort(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
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
            "FLAT",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            26,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            26,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            26,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            26,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatStoreShortD16Hi extends InstructionDef(
        "flat_store_short_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreShortD16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
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
            "FLAT",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
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
            "FLAT",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            27,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            27,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicAdd extends InstructionDef(
        "global_atomic_add",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAdd_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAdd_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            66,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            66,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicAddF32 extends InstructionDef(
        "global_atomic_add_f32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAddF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAddF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicAddF64 extends InstructionDef(
        "global_atomic_add_f64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAddF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAddF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            79,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            79,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            79,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            85,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicAddU32 extends InstructionDef(
        "global_atomic_add_u32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAddU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAddU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicAddU64 extends InstructionDef(
        "global_atomic_add_u64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAddU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAddU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicAddX2 extends InstructionDef(
        "global_atomic_add_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAddX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAddX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            98,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            98,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            98,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            98,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            98,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            98,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicAnd extends InstructionDef(
        "global_atomic_and",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAnd_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAnd_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            72,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            72,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicAndB32 extends InstructionDef(
        "global_atomic_and_b32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAndB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAndB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicAndB64 extends InstructionDef(
        "global_atomic_and_b64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAndB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAndB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicAndX2 extends InstructionDef(
        "global_atomic_and_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAndX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAndX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            89,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            89,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            104,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            104,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicCmpswap extends InstructionDef(
        "global_atomic_cmpswap",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicCmpswap_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCmpswap_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            49,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            49,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            65,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            65,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicCmpswapB32 extends InstructionDef(
        "global_atomic_cmpswap_b32",
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapB32_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            52,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            52,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            52,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            52,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            52,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicCmpswapB64 extends InstructionDef(
        "global_atomic_cmpswap_b64",
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapB64_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicCmpswapF32 extends InstructionDef(
        "global_atomic_cmpswap_f32",
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapF32_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicCmpswapX2 extends InstructionDef(
        "global_atomic_cmpswap_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicCmpswapX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            97,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            97,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            97,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            97,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            97,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            97,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicCondSubU32 extends InstructionDef(
        "global_atomic_cond_sub_u32",
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicCondSubU32_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCondSubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicCsub extends InstructionDef(
        "global_atomic_csub",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicCsub_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCsub_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            52,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicCsubU32 extends InstructionDef(
        "global_atomic_csub_u32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicCsubU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCsubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicDec extends InstructionDef(
        "global_atomic_dec",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicDec_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicDec_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            76,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            76,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicDecU32 extends InstructionDef(
        "global_atomic_dec_u32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicDecU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicDecU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicDecU64 extends InstructionDef(
        "global_atomic_dec_u64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicDecU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicDecU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicDecX2 extends InstructionDef(
        "global_atomic_dec_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicDecX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicDecX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            108,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            108,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            108,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            108,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            93,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            93,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            108,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            108,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicFcmpswap extends InstructionDef(
        "global_atomic_fcmpswap",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicFcmpswap_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicFcmpswap_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicFcmpswapX2 extends InstructionDef(
        "global_atomic_fcmpswap_x2",
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicFcmpswapX2_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicFcmpswapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            94,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            94,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicFmax extends InstructionDef(
        "global_atomic_fmax",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicFmax_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicFmax_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicFmaxX2 extends InstructionDef(
        "global_atomic_fmax_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicFmaxX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicFmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            96,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            96,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicFmin extends InstructionDef(
        "global_atomic_fmin",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicFmin_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicFmin_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicFminX2 extends InstructionDef(
        "global_atomic_fmin_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicFminX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicFminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            95,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            95,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicInc extends InstructionDef(
        "global_atomic_inc",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicInc_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicInc_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            75,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            75,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicIncU32 extends InstructionDef(
        "global_atomic_inc_u32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicIncU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicIncU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicIncU64 extends InstructionDef(
        "global_atomic_inc_u64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicIncU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicIncU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicIncX2 extends InstructionDef(
        "global_atomic_inc_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicIncX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicIncX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            107,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            107,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            107,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            107,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            92,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            92,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            107,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            107,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicMaxF32 extends InstructionDef(
        "global_atomic_max_f32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMaxF64 extends InstructionDef(
        "global_atomic_max_f64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMaxI32 extends InstructionDef(
        "global_atomic_max_i32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxI32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxI32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMaxI64 extends InstructionDef(
        "global_atomic_max_i64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxI64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxI64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMaxNumF32 extends InstructionDef(
        "global_atomic_max_num_f32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxNumF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxNumF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMaxNumF64 extends InstructionDef(
        "global_atomic_max_num_f64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxNumF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxNumF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            92,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMaxU32 extends InstructionDef(
        "global_atomic_max_u32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMaxU64 extends InstructionDef(
        "global_atomic_max_u64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMinF32 extends InstructionDef(
        "global_atomic_min_f32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMinF64 extends InstructionDef(
        "global_atomic_min_f64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMinI32 extends InstructionDef(
        "global_atomic_min_i32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinI32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinI32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMinI64 extends InstructionDef(
        "global_atomic_min_i64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinI64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinI64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMinNumF32 extends InstructionDef(
        "global_atomic_min_num_f32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinNumF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinNumF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMinNumF64 extends InstructionDef(
        "global_atomic_min_num_f64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinNumF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinNumF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            91,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMinU32 extends InstructionDef(
        "global_atomic_min_u32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicMinU64 extends InstructionDef(
        "global_atomic_min_u64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicOr extends InstructionDef(
        "global_atomic_or",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicOr_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicOr_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            73,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            73,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicOrB32 extends InstructionDef(
        "global_atomic_or_b32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicOrB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicOrB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicOrB64 extends InstructionDef(
        "global_atomic_or_b64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicOrB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicOrB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicOrX2 extends InstructionDef(
        "global_atomic_or_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicOrX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicOrX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            105,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            105,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            105,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            105,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            90,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            90,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            105,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            105,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicOrderedAddB64 extends InstructionDef(
        "global_atomic_ordered_add_b64",
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicOrderedAddB64_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicOrderedAddB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            115,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicPkAddBf16 extends InstructionDef(
        "global_atomic_pk_add_bf16",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicPkAddBf16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicPkAddBf16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            90,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            90,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicPkAddF16 extends InstructionDef(
        "global_atomic_pk_add_f16",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicPkAddF16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicPkAddF16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            78,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            78,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            78,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            78,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            89,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            89,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicSmax extends InstructionDef(
        "global_atomic_smax",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSmax_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSmax_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            70,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            70,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicSmaxX2 extends InstructionDef(
        "global_atomic_smax_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSmaxX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            87,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            87,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            102,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            102,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicSmin extends InstructionDef(
        "global_atomic_smin",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSmin_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSmin_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            68,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            68,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicSminX2 extends InstructionDef(
        "global_atomic_smin_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSminX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            85,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            85,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            100,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            100,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicSub extends InstructionDef(
        "global_atomic_sub",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSub_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSub_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            67,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            67,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicSubClampU32 extends InstructionDef(
        "global_atomic_sub_clamp_u32",
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicSubClampU32_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSubClampU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicSubU32 extends InstructionDef(
        "global_atomic_sub_u32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSubU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicSubU64 extends InstructionDef(
        "global_atomic_sub_u64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSubU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSubU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicSubX2 extends InstructionDef(
        "global_atomic_sub_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSubX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSubX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            83,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            83,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            99,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            99,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicSwap extends InstructionDef(
        "global_atomic_swap",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSwap_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSwap_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            48,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            48,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            64,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            64,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicSwapB32 extends InstructionDef(
        "global_atomic_swap_b32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSwapB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSwapB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicSwapB64 extends InstructionDef(
        "global_atomic_swap_b64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSwapB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSwapB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicSwapX2 extends InstructionDef(
        "global_atomic_swap_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSwapX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSwapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            96,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            96,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            96,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            96,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            96,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            96,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicUmax extends InstructionDef(
        "global_atomic_umax",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicUmax_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicUmax_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            71,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            71,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicUmaxX2 extends InstructionDef(
        "global_atomic_umax_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicUmaxX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicUmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            88,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            88,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            103,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            103,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicUmin extends InstructionDef(
        "global_atomic_umin",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicUmin_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicUmin_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            69,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            69,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicUminX2 extends InstructionDef(
        "global_atomic_umin_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicUminX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicUminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            101,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            101,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicXor extends InstructionDef(
        "global_atomic_xor",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicXor_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicXor_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            74,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            74,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalAtomicXorB32 extends InstructionDef(
        "global_atomic_xor_b32",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicXorB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicXorB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalAtomicXorB64 extends InstructionDef(
        "global_atomic_xor_b64",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicXorB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicXorB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )

  val definitions: Vector[InstructionDef] = Vector(
    FlatLoadShortD16Hi,
    FlatLoadSshort,
    FlatLoadU16,
    FlatLoadU8,
    FlatLoadUbyte,
    FlatLoadUbyteD16,
    FlatLoadUbyteD16Hi,
    FlatLoadUshort,
    FlatPrefetchB8,
    FlatStoreB128,
    FlatStoreB16,
    FlatStoreB32,
    FlatStoreB64,
    FlatStoreB8,
    FlatStoreB96,
    FlatStoreByte,
    FlatStoreByteD16Hi,
    FlatStoreD16HiB16,
    FlatStoreD16HiB8,
    FlatStoreDword,
    FlatStoreDwordx2,
    FlatStoreDwordx3,
    FlatStoreDwordx4,
    FlatStoreShort,
    FlatStoreShortD16Hi,
    GlobalAtomicAdd,
    GlobalAtomicAddF32,
    GlobalAtomicAddF64,
    GlobalAtomicAddU32,
    GlobalAtomicAddU64,
    GlobalAtomicAddX2,
    GlobalAtomicAnd,
    GlobalAtomicAndB32,
    GlobalAtomicAndB64,
    GlobalAtomicAndX2,
    GlobalAtomicCmpswap,
    GlobalAtomicCmpswapB32,
    GlobalAtomicCmpswapB64,
    GlobalAtomicCmpswapF32,
    GlobalAtomicCmpswapX2,
    GlobalAtomicCondSubU32,
    GlobalAtomicCsub,
    GlobalAtomicCsubU32,
    GlobalAtomicDec,
    GlobalAtomicDecU32,
    GlobalAtomicDecU64,
    GlobalAtomicDecX2,
    GlobalAtomicFcmpswap,
    GlobalAtomicFcmpswapX2,
    GlobalAtomicFmax,
    GlobalAtomicFmaxX2,
    GlobalAtomicFmin,
    GlobalAtomicFminX2,
    GlobalAtomicInc,
    GlobalAtomicIncU32,
    GlobalAtomicIncU64,
    GlobalAtomicIncX2,
    GlobalAtomicMaxF32,
    GlobalAtomicMaxF64,
    GlobalAtomicMaxI32,
    GlobalAtomicMaxI64,
    GlobalAtomicMaxNumF32,
    GlobalAtomicMaxNumF64,
    GlobalAtomicMaxU32,
    GlobalAtomicMaxU64,
    GlobalAtomicMinF32,
    GlobalAtomicMinF64,
    GlobalAtomicMinI32,
    GlobalAtomicMinI64,
    GlobalAtomicMinNumF32,
    GlobalAtomicMinNumF64,
    GlobalAtomicMinU32,
    GlobalAtomicMinU64,
    GlobalAtomicOr,
    GlobalAtomicOrB32,
    GlobalAtomicOrB64,
    GlobalAtomicOrX2,
    GlobalAtomicOrderedAddB64,
    GlobalAtomicPkAddBf16,
    GlobalAtomicPkAddF16,
    GlobalAtomicSmax,
    GlobalAtomicSmaxX2,
    GlobalAtomicSmin,
    GlobalAtomicSminX2,
    GlobalAtomicSub,
    GlobalAtomicSubClampU32,
    GlobalAtomicSubU32,
    GlobalAtomicSubU64,
    GlobalAtomicSubX2,
    GlobalAtomicSwap,
    GlobalAtomicSwapB32,
    GlobalAtomicSwapB64,
    GlobalAtomicSwapX2,
    GlobalAtomicUmax,
    GlobalAtomicUmaxX2,
    GlobalAtomicUmin,
    GlobalAtomicUminX2,
    GlobalAtomicXor,
    GlobalAtomicXorB32,
    GlobalAtomicXorB64
  )
}
