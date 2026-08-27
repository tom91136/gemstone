// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

private[common] object InstructionDefs4 {
  case object DsXorSrc2B32 extends InstructionDef(
        "ds_xor_src2_b32",
        decoder(form(1)((operands, mods) => Instruction.DsXorSrc2B32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            139,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            139,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            139,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            139,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsXorSrc2B64 extends InstructionDef(
        "ds_xor_src2_b64",
        decoder(form(1)((operands, mods) => Instruction.DsXorSrc2B64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            203,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            203,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            203,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            203,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object Exp extends InstructionDef(
        "exp",
        decoder(form(5)((operands, mods) =>
          Instruction.Exp(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "EXP",
            0,
            Vector(
              OperandInfo(Some("TGT"), "TGT", 128, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC2"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC3"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("EXPORT"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "EXP",
            0,
            Vector(
              OperandInfo(Some("TGT"), "TGT", 128, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC2"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC3"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("EXPORT"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "EXP",
            0,
            Vector(
              OperandInfo(Some("TGT"), "TGT", 128, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC2"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC3"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("EXPORT"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "EXP",
            0,
            Vector(
              OperandInfo(Some("TGT"), "TGT", 128, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC2"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC3"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("EXPORT"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "EXP",
            0,
            Vector(
              OperandInfo(Some("TGT"), "TGT", 128, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC2"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC3"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("EXPORT"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "EXP",
            0,
            Vector.empty,
            Some("exp"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "EXP",
            0,
            Vector.empty,
            Some("exp"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "EXP",
            0,
            Vector.empty,
            Some("exp"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "EXP",
            0,
            Vector.empty,
            Some("exp"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object Export extends InstructionDef(
        "export",
        decoder(form(5)((operands, mods) =>
          Instruction.Export(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VEXPORT",
            0,
            Vector(
              OperandInfo(Some("TGT"), "TGT", 128, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC2"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC3"), "VGPR", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("EXPORT"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicAdd extends InstructionDef(
        "flat_atomic_add",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAdd_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAdd_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAdd_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            50,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            50,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            50,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            66,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            66,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicAddF32 extends InstructionDef(
        "flat_atomic_add_f32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAddF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAddF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAddF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicAddF64 extends InstructionDef(
        "flat_atomic_add_f64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAddF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAddF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAddF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT",
            79,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            79,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            79,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
  case object FlatAtomicAddU32 extends InstructionDef(
        "flat_atomic_add_u32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAddU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAddU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAddU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicAddU64 extends InstructionDef(
        "flat_atomic_add_u64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAddU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAddU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAddU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicAddX2 extends InstructionDef(
        "flat_atomic_add_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAddX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAddX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAddX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            98,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            98,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            98,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            98,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            82,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            82,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            98,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            98,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicAnd extends InstructionDef(
        "flat_atomic_and",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAnd_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAnd_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAnd_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            57,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            57,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            72,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            72,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicAndB32 extends InstructionDef(
        "flat_atomic_and_b32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAndB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAndB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAndB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicAndB64 extends InstructionDef(
        "flat_atomic_and_b64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAndB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAndB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAndB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicAndX2 extends InstructionDef(
        "flat_atomic_and_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAndX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAndX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAndX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            89,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            89,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            89,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            89,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            104,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            104,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicCmpswap extends InstructionDef(
        "flat_atomic_cmpswap",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicCmpswap_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicCmpswap_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicCmpswap_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            49,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            49,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            49,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            49,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            65,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            65,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicCmpswapB32 extends InstructionDef(
        "flat_atomic_cmpswap_b32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicCmpswapB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicCmpswapB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicCmpswapB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            52,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            52,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            52,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicCmpswapB64 extends InstructionDef(
        "flat_atomic_cmpswap_b64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicCmpswapB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicCmpswapB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicCmpswapB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            66,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicCmpswapF32 extends InstructionDef(
        "flat_atomic_cmpswap_f32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicCmpswapF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicCmpswapF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicCmpswapF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicCmpswapX2 extends InstructionDef(
        "flat_atomic_cmpswap_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicCmpswapX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicCmpswapX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicCmpswapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            97,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            97,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            97,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            97,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            81,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            81,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            97,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            97,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicCondSubU32 extends InstructionDef(
        "flat_atomic_cond_sub_u32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicCondSubU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicCondSubU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicCondSubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "VFLAT",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicDec extends InstructionDef(
        "flat_atomic_dec",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicDec_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicDec_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicDec_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            61,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            61,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            76,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            76,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicDecU32 extends InstructionDef(
        "flat_atomic_dec_u32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicDecU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicDecU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicDecU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicDecU64 extends InstructionDef(
        "flat_atomic_dec_u64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicDecU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicDecU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicDecU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            77,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicDecX2 extends InstructionDef(
        "flat_atomic_dec_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicDecX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicDecX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicDecX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            108,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            108,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            108,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            108,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            93,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            93,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            93,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            93,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            108,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            108,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicFcmpswap extends InstructionDef(
        "flat_atomic_fcmpswap",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicFcmpswap_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicFcmpswap_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicFcmpswap_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            62,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            62,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicFcmpswapX2 extends InstructionDef(
        "flat_atomic_fcmpswap_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicFcmpswapX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicFcmpswapX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicFcmpswapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT",
            94,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            94,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            94,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            94,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicFmax extends InstructionDef(
        "flat_atomic_fmax",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicFmax_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicFmax_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicFmax_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            64,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            64,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicFmaxX2 extends InstructionDef(
        "flat_atomic_fmax_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicFmaxX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicFmaxX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicFmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT",
            96,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            96,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            96,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            96,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicFmin extends InstructionDef(
        "flat_atomic_fmin",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicFmin_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicFmin_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicFmin_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            63,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            63,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicFminX2 extends InstructionDef(
        "flat_atomic_fmin_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicFminX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicFminX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicFminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT",
            95,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            95,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            95,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            95,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicInc extends InstructionDef(
        "flat_atomic_inc",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicInc_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicInc_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicInc_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            60,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            60,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            60,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            75,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            75,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicIncU32 extends InstructionDef(
        "flat_atomic_inc_u32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicIncU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicIncU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicIncU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            63,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicIncU64 extends InstructionDef(
        "flat_atomic_inc_u64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicIncU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicIncU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicIncU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            76,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicIncX2 extends InstructionDef(
        "flat_atomic_inc_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicIncX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicIncX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicIncX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            107,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            107,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            107,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            107,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            92,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            92,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            92,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            92,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            107,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            107,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicMaxF32 extends InstructionDef(
        "flat_atomic_max_f32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicMaxF64 extends InstructionDef(
        "flat_atomic_max_f64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicMaxI32 extends InstructionDef(
        "flat_atomic_max_i32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxI32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxI32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicMaxI64 extends InstructionDef(
        "flat_atomic_max_i64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxI64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxI64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicMaxNumF32 extends InstructionDef(
        "flat_atomic_max_num_f32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxNumF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxNumF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxNumF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "VFLAT",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicMaxNumF64 extends InstructionDef(
        "flat_atomic_max_num_f64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxNumF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxNumF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxNumF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
  case object FlatAtomicMaxU32 extends InstructionDef(
        "flat_atomic_max_u32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicMaxU64 extends InstructionDef(
        "flat_atomic_max_u64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            72,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicMinF32 extends InstructionDef(
        "flat_atomic_min_f32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicMinF64 extends InstructionDef(
        "flat_atomic_min_f64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicMinI32 extends InstructionDef(
        "flat_atomic_min_i32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinI32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinI32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicMinI64 extends InstructionDef(
        "flat_atomic_min_i64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinI64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinI64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicMinNumF32 extends InstructionDef(
        "flat_atomic_min_num_f32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinNumF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinNumF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinNumF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "VFLAT",
            81,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicMinNumF64 extends InstructionDef(
        "flat_atomic_min_num_f64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinNumF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinNumF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinNumF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
  case object FlatAtomicMinU32 extends InstructionDef(
        "flat_atomic_min_u32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            57,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicMinU64 extends InstructionDef(
        "flat_atomic_min_u64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicOr extends InstructionDef(
        "flat_atomic_or",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicOr_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicOr_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicOr_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            73,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            58,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            58,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            58,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            73,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            73,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicOrB32 extends InstructionDef(
        "flat_atomic_or_b32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicOrB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicOrB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicOrB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            61,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicOrB64 extends InstructionDef(
        "flat_atomic_or_b64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicOrB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicOrB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicOrB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicOrX2 extends InstructionDef(
        "flat_atomic_or_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicOrX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicOrX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicOrX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            105,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            105,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            105,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            105,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            90,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            90,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            90,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            90,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            105,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            105,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicPkAddBf16 extends InstructionDef(
        "flat_atomic_pk_add_bf16",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicPkAddBf16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicPkAddBf16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicPkAddBf16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            82,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "VFLAT",
            90,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicPkAddF16 extends InstructionDef(
        "flat_atomic_pk_add_f16",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicPkAddF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicPkAddF16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicPkAddF16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT",
            78,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            78,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "VFLAT",
            89,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicSmax extends InstructionDef(
        "flat_atomic_smax",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSmax_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSmax_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSmax_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            70,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            55,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            55,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            70,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            70,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicSmaxX2 extends InstructionDef(
        "flat_atomic_smax_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSmaxX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSmaxX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            102,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            87,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            87,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            87,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            87,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            102,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            102,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicSmin extends InstructionDef(
        "flat_atomic_smin",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSmin_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSmin_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSmin_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            53,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            53,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            53,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            68,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            68,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicSminX2 extends InstructionDef(
        "flat_atomic_smin_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSminX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSminX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            100,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            85,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            85,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            85,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            85,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            100,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            100,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicSub extends InstructionDef(
        "flat_atomic_sub",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSub_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSub_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSub_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            67,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            51,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            51,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            67,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            67,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicSubClampU32 extends InstructionDef(
        "flat_atomic_sub_clamp_u32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSubClampU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSubClampU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSubClampU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "VFLAT",
            55,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicSubU32 extends InstructionDef(
        "flat_atomic_sub_u32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSubU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSubU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicSubU64 extends InstructionDef(
        "flat_atomic_sub_u64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSubU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSubU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSubU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            68,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicSubX2 extends InstructionDef(
        "flat_atomic_sub_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSubX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSubX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSubX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            99,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            83,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            83,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            83,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            83,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            99,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            99,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicSwap extends InstructionDef(
        "flat_atomic_swap",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSwap_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSwap_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSwap_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            64,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            48,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            48,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            48,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            48,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            64,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            64,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicSwapB32 extends InstructionDef(
        "flat_atomic_swap_b32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSwapB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSwapB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSwapB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            51,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicSwapB64 extends InstructionDef(
        "flat_atomic_swap_b64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSwapB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSwapB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSwapB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            65,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicSwapX2 extends InstructionDef(
        "flat_atomic_swap_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSwapX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSwapX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSwapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            96,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            96,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            96,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            96,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            80,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            80,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            80,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            96,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            96,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicUmax extends InstructionDef(
        "flat_atomic_umax",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicUmax_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicUmax_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicUmax_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            71,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            56,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            56,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            56,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            71,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            71,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicUmaxX2 extends InstructionDef(
        "flat_atomic_umax_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicUmaxX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicUmaxX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicUmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            88,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            88,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            88,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            88,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            103,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            103,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicUmin extends InstructionDef(
        "flat_atomic_umin",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicUmin_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicUmin_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicUmin_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            69,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            54,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            54,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            54,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            69,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            69,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicUminX2 extends InstructionDef(
        "flat_atomic_umin_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicUminX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicUminX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicUminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            101,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            86,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            86,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            86,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            101,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            101,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicXor extends InstructionDef(
        "flat_atomic_xor",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicXor_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicXor_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicXor_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            74,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
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
            "FLAT",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            59,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            59,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            59,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            74,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            74,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatAtomicXorB32 extends InstructionDef(
        "flat_atomic_xor_b32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicXorB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicXorB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicXorB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            62,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicXorB64 extends InstructionDef(
        "flat_atomic_xor_b64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicXorB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicXorB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicXorB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
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
            "FLAT",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            75,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatAtomicXorX2 extends InstructionDef(
        "flat_atomic_xor_x2",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicXorX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicXorX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicXorX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            106,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            106,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            106,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            106,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
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
            "FLAT",
            91,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            91,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            91,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            91,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            106,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            106,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadB128 extends InstructionDef(
        "flat_load_b128",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadB128_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatLoadB32 extends InstructionDef(
        "flat_load_b32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadB32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatLoadB64 extends InstructionDef(
        "flat_load_b64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadB64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatLoadB96 extends InstructionDef(
        "flat_load_b96",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadB96_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadB96_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatLoadD16B16 extends InstructionDef(
        "flat_load_d16_b16",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadD16B16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadD16B16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            32,
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
            32,
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
            32,
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
            32,
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
  case object FlatLoadD16HiB16 extends InstructionDef(
        "flat_load_d16_hi_b16",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadD16HiB16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadD16HiB16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            35,
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
            35,
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
            35,
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
            35,
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
  case object FlatLoadD16HiI8 extends InstructionDef(
        "flat_load_d16_hi_i8",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadD16HiI8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadD16HiI8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            34,
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
            34,
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
            34,
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
            34,
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
  case object FlatLoadD16HiU8 extends InstructionDef(
        "flat_load_d16_hi_u8",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadD16HiU8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadD16HiU8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            33,
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
            Generation.GFX11,
            "rdna3_5",
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
            Generation.GFX12,
            "rdna4",
            "VFLAT",
            33,
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
  case object FlatLoadD16I8 extends InstructionDef(
        "flat_load_d16_i8",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadD16I8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadD16I8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            31,
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
            31,
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
            31,
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
            31,
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
  case object FlatLoadD16U8 extends InstructionDef(
        "flat_load_d16_u8",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadD16U8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadD16U8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            30,
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
            30,
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
            30,
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
            30,
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
  case object FlatLoadDword extends InstructionDef(
        "flat_load_dword",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadDword(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            12,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            12,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            20,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            20,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadDwordx2 extends InstructionDef(
        "flat_load_dwordx2",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadDwordx2(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            13,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            13,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            13,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            13,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            21,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            21,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadDwordx3 extends InstructionDef(
        "flat_load_dwordx3",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadDwordx3(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            15,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            15,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            15,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            15,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            22,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            22,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadDwordx4 extends InstructionDef(
        "flat_load_dwordx4",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadDwordx4(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
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
            "FLAT",
            14,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT",
            14,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "FLAT",
            14,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            14,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            23,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            23,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadI16 extends InstructionDef(
        "flat_load_i16",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadI16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            19,
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
            19,
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
            19,
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
            19,
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
  case object FlatLoadI8 extends InstructionDef(
        "flat_load_i8",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadI8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadI8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            17,
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
            17,
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
            17,
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
            17,
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
  case object FlatLoadMonitorB128 extends InstructionDef(
        "flat_load_monitor_b128",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadMonitorB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadMonitorB128_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            114,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatLoadMonitorB32 extends InstructionDef(
        "flat_load_monitor_b32",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadMonitorB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadMonitorB32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            112,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatLoadMonitorB64 extends InstructionDef(
        "flat_load_monitor_b64",
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadMonitorB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadMonitorB64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VFLAT",
            113,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object FlatLoadSbyte extends InstructionDef(
        "flat_load_sbyte",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadSbyte(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            17,
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
            17,
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
            17,
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
            17,
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
            9,
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
            9,
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
            9,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "FLAT",
            9,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "FLAT",
            17,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            17,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadSbyteD16 extends InstructionDef(
        "flat_load_sbyte_d16",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadSbyteD16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            34,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadSbyteD16Hi extends InstructionDef(
        "flat_load_sbyte_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadSbyteD16Hi(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            35,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadShortD16 extends InstructionDef(
        "flat_load_short_d16",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadShortD16(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT",
            36,
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
            36,
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
            36,
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
            36,
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
            36,
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
            36,
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
            36,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "FLAT",
            36,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )

  val definitions: Vector[InstructionDef] = Vector(
    DsXorSrc2B32,
    DsXorSrc2B64,
    Exp,
    Export,
    FlatAtomicAdd,
    FlatAtomicAddF32,
    FlatAtomicAddF64,
    FlatAtomicAddU32,
    FlatAtomicAddU64,
    FlatAtomicAddX2,
    FlatAtomicAnd,
    FlatAtomicAndB32,
    FlatAtomicAndB64,
    FlatAtomicAndX2,
    FlatAtomicCmpswap,
    FlatAtomicCmpswapB32,
    FlatAtomicCmpswapB64,
    FlatAtomicCmpswapF32,
    FlatAtomicCmpswapX2,
    FlatAtomicCondSubU32,
    FlatAtomicDec,
    FlatAtomicDecU32,
    FlatAtomicDecU64,
    FlatAtomicDecX2,
    FlatAtomicFcmpswap,
    FlatAtomicFcmpswapX2,
    FlatAtomicFmax,
    FlatAtomicFmaxX2,
    FlatAtomicFmin,
    FlatAtomicFminX2,
    FlatAtomicInc,
    FlatAtomicIncU32,
    FlatAtomicIncU64,
    FlatAtomicIncX2,
    FlatAtomicMaxF32,
    FlatAtomicMaxF64,
    FlatAtomicMaxI32,
    FlatAtomicMaxI64,
    FlatAtomicMaxNumF32,
    FlatAtomicMaxNumF64,
    FlatAtomicMaxU32,
    FlatAtomicMaxU64,
    FlatAtomicMinF32,
    FlatAtomicMinF64,
    FlatAtomicMinI32,
    FlatAtomicMinI64,
    FlatAtomicMinNumF32,
    FlatAtomicMinNumF64,
    FlatAtomicMinU32,
    FlatAtomicMinU64,
    FlatAtomicOr,
    FlatAtomicOrB32,
    FlatAtomicOrB64,
    FlatAtomicOrX2,
    FlatAtomicPkAddBf16,
    FlatAtomicPkAddF16,
    FlatAtomicSmax,
    FlatAtomicSmaxX2,
    FlatAtomicSmin,
    FlatAtomicSminX2,
    FlatAtomicSub,
    FlatAtomicSubClampU32,
    FlatAtomicSubU32,
    FlatAtomicSubU64,
    FlatAtomicSubX2,
    FlatAtomicSwap,
    FlatAtomicSwapB32,
    FlatAtomicSwapB64,
    FlatAtomicSwapX2,
    FlatAtomicUmax,
    FlatAtomicUmaxX2,
    FlatAtomicUmin,
    FlatAtomicUminX2,
    FlatAtomicXor,
    FlatAtomicXorB32,
    FlatAtomicXorB64,
    FlatAtomicXorX2,
    FlatLoadB128,
    FlatLoadB32,
    FlatLoadB64,
    FlatLoadB96,
    FlatLoadD16B16,
    FlatLoadD16HiB16,
    FlatLoadD16HiI8,
    FlatLoadD16HiU8,
    FlatLoadD16I8,
    FlatLoadD16U8,
    FlatLoadDword,
    FlatLoadDwordx2,
    FlatLoadDwordx3,
    FlatLoadDwordx4,
    FlatLoadI16,
    FlatLoadI8,
    FlatLoadMonitorB128,
    FlatLoadMonitorB32,
    FlatLoadMonitorB64,
    FlatLoadSbyte,
    FlatLoadSbyteD16,
    FlatLoadSbyteD16Hi,
    FlatLoadShortD16
  )
}
