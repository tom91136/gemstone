// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

private[common] object InstructionDefs0 {
  case object BufferAtomicAdd extends InstructionDef(
        "buffer_atomic_add",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAdd(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            66,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            66,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            66,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            66,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            50,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            50,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            50,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            50,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            66,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            66,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicAddF32 extends InstructionDef(
        "buffer_atomic_add_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAddF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            77,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            77,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            77,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            77,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            86,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            86,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            86,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            86,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicAddF64 extends InstructionDef(
        "buffer_atomic_add_f64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAddF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            79,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            79,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            79,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            85,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicAddU32 extends InstructionDef(
        "buffer_atomic_add_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAddU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            53,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            53,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            53,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            53,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicAddU64 extends InstructionDef(
        "buffer_atomic_add_u64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAddU64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            67,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            67,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            67,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            67,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicAddX2 extends InstructionDef(
        "buffer_atomic_add_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAddX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            98,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            98,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            98,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            98,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            82,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            82,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            98,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            98,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicAnd extends InstructionDef(
        "buffer_atomic_and",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAnd(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            72,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            72,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            72,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            72,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            57,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            57,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            57,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            57,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            72,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            72,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicAndB32 extends InstructionDef(
        "buffer_atomic_and_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAndB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            60,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            60,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            60,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            60,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicAndB64 extends InstructionDef(
        "buffer_atomic_and_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAndB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            73,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            73,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            73,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            73,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicAndX2 extends InstructionDef(
        "buffer_atomic_and_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAndX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            104,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            104,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            104,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            104,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            89,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            89,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            89,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            89,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            104,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            104,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicCmpswap extends InstructionDef(
        "buffer_atomic_cmpswap",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCmpswap(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            65,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            65,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            65,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            65,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            49,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            49,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            49,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            49,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            65,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            65,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicCmpswapB32 extends InstructionDef(
        "buffer_atomic_cmpswap_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCmpswapB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicCmpswapB64 extends InstructionDef(
        "buffer_atomic_cmpswap_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCmpswapB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            66,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            66,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            66,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            66,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicCmpswapF32 extends InstructionDef(
        "buffer_atomic_cmpswap_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCmpswapF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            80,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            80,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicCmpswapX2 extends InstructionDef(
        "buffer_atomic_cmpswap_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCmpswapX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            97,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            97,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            97,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            97,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            81,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            81,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            81,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            81,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            97,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            97,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicCondSubU32 extends InstructionDef(
        "buffer_atomic_cond_sub_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCondSubU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            80,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            80,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicCsub extends InstructionDef(
        "buffer_atomic_csub",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCsub(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicCsubU32 extends InstructionDef(
        "buffer_atomic_csub_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCsubU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicDec extends InstructionDef(
        "buffer_atomic_dec",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicDec(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            76,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            76,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            76,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            76,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            61,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            61,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            61,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            61,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            76,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            76,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicDecU32 extends InstructionDef(
        "buffer_atomic_dec_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicDecU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            64,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            64,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            64,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            64,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicDecU64 extends InstructionDef(
        "buffer_atomic_dec_u64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicDecU64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            77,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            77,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            77,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            77,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicDecX2 extends InstructionDef(
        "buffer_atomic_dec_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicDecX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            108,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            108,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            108,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            108,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            93,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            93,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            93,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            93,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            108,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            108,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicFcmpswap extends InstructionDef(
        "buffer_atomic_fcmpswap",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicFcmpswap(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            62,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            62,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            62,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            62,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicFcmpswapX2 extends InstructionDef(
        "buffer_atomic_fcmpswap_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicFcmpswapX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            94,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            94,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            94,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            94,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicFmax extends InstructionDef(
        "buffer_atomic_fmax",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicFmax(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            64,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            64,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            64,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            64,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicFmaxX2 extends InstructionDef(
        "buffer_atomic_fmax_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicFmaxX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            96,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            96,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            96,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            96,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicFmin extends InstructionDef(
        "buffer_atomic_fmin",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicFmin(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            63,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            63,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            63,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            63,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicFminX2 extends InstructionDef(
        "buffer_atomic_fmin_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicFminX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            95,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            95,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            95,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            95,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicInc extends InstructionDef(
        "buffer_atomic_inc",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicInc(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            75,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            75,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            75,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            75,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            60,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            60,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            60,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            60,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            75,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            75,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicIncU32 extends InstructionDef(
        "buffer_atomic_inc_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicIncU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            63,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            63,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            63,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            63,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicIncU64 extends InstructionDef(
        "buffer_atomic_inc_u64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicIncU64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            76,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            76,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            76,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            76,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicIncX2 extends InstructionDef(
        "buffer_atomic_inc_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicIncX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            107,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            107,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            107,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            107,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            92,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            92,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            92,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            92,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            107,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            107,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicMaxF32 extends InstructionDef(
        "buffer_atomic_max_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMaxF64 extends InstructionDef(
        "buffer_atomic_max_f64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            81,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            81,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            81,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMaxI32 extends InstructionDef(
        "buffer_atomic_max_i32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxI32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            58,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            58,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            58,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            58,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMaxI64 extends InstructionDef(
        "buffer_atomic_max_i64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxI64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            71,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            71,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            71,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            71,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMaxNumF32 extends InstructionDef(
        "buffer_atomic_max_num_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxNumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMaxNumF64 extends InstructionDef(
        "buffer_atomic_max_num_f64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxNumF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            92,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMaxU32 extends InstructionDef(
        "buffer_atomic_max_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            59,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            59,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            59,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            59,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMaxU64 extends InstructionDef(
        "buffer_atomic_max_u64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxU64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            72,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            72,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            72,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            72,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMinF32 extends InstructionDef(
        "buffer_atomic_min_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            81,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            81,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMinF64 extends InstructionDef(
        "buffer_atomic_min_f64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            80,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            80,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            80,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMinI32 extends InstructionDef(
        "buffer_atomic_min_i32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinI32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            56,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            56,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            56,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            56,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMinI64 extends InstructionDef(
        "buffer_atomic_min_i64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinI64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            69,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            69,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            69,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            69,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMinNumF32 extends InstructionDef(
        "buffer_atomic_min_num_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinNumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            81,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            81,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMinNumF64 extends InstructionDef(
        "buffer_atomic_min_num_f64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinNumF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            91,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMinU32 extends InstructionDef(
        "buffer_atomic_min_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            57,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            57,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            57,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            57,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicMinU64 extends InstructionDef(
        "buffer_atomic_min_u64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinU64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            70,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            70,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            70,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            70,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicOr extends InstructionDef(
        "buffer_atomic_or",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicOr(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            73,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            73,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            73,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            73,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            58,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            58,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            58,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            58,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            73,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            73,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicOrB32 extends InstructionDef(
        "buffer_atomic_or_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicOrB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            61,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            61,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            61,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            61,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicOrB64 extends InstructionDef(
        "buffer_atomic_or_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicOrB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            74,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            74,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            74,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            74,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicOrX2 extends InstructionDef(
        "buffer_atomic_or_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicOrX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            105,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            105,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            105,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            105,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            90,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            90,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            90,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            90,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            105,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            105,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicPkAddBf16 extends InstructionDef(
        "buffer_atomic_pk_add_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicPkAddBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            90,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            90,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicPkAddF16 extends InstructionDef(
        "buffer_atomic_pk_add_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicPkAddF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            78,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            78,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            78,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            78,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            89,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            89,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicRsub extends InstructionDef(
        "buffer_atomic_rsub",
        decoder(form(0)((_, mods) => Instruction.BufferAtomicRsub(mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            52,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicRsubX2 extends InstructionDef(
        "buffer_atomic_rsub_x2",
        decoder(form(0)((_, mods) => Instruction.BufferAtomicRsubX2(mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            84,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicSmax extends InstructionDef(
        "buffer_atomic_smax",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSmax(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            70,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            70,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            70,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            70,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            55,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            55,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            70,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            70,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicSmaxX2 extends InstructionDef(
        "buffer_atomic_smax_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSmaxX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            102,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            102,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            102,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            102,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            87,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            87,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            87,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            87,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            102,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            102,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicSmin extends InstructionDef(
        "buffer_atomic_smin",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSmin(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            68,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            68,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            68,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            68,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            53,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            53,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            53,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            53,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            68,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            68,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicSminX2 extends InstructionDef(
        "buffer_atomic_smin_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSminX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            100,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            100,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            100,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            100,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            85,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            85,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            85,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            85,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            100,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            100,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicSub extends InstructionDef(
        "buffer_atomic_sub",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSub(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            67,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            67,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            67,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            67,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            51,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            51,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            67,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            67,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicSubClampU32 extends InstructionDef(
        "buffer_atomic_sub_clamp_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSubClampU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicSubU32 extends InstructionDef(
        "buffer_atomic_sub_u32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSubU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            54,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            54,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            54,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            54,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicSubU64 extends InstructionDef(
        "buffer_atomic_sub_u64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSubU64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            68,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            68,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            68,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            68,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicSubX2 extends InstructionDef(
        "buffer_atomic_sub_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSubX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            99,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            99,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            99,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            99,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            83,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            83,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            83,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            83,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            99,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            99,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicSwap extends InstructionDef(
        "buffer_atomic_swap",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSwap(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            64,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            64,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            64,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            64,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            48,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            48,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            48,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            48,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            64,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            64,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicSwapB32 extends InstructionDef(
        "buffer_atomic_swap_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSwapB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicSwapB64 extends InstructionDef(
        "buffer_atomic_swap_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSwapB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            65,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            65,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            65,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            65,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicSwapX2 extends InstructionDef(
        "buffer_atomic_swap_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSwapX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            96,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            96,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            96,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            96,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            80,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            80,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            80,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            80,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            96,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            96,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicUmax extends InstructionDef(
        "buffer_atomic_umax",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicUmax(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            71,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            71,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            71,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            71,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            56,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            56,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            56,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            56,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            71,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            71,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicUmaxX2 extends InstructionDef(
        "buffer_atomic_umax_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicUmaxX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            103,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            103,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            103,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            103,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            88,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            88,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            88,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            88,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            103,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            103,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicUmin extends InstructionDef(
        "buffer_atomic_umin",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicUmin(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            69,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            69,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            69,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            69,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            54,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            54,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            54,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            54,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            69,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            69,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicUminX2 extends InstructionDef(
        "buffer_atomic_umin_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicUminX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            101,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            101,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            101,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            101,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            86,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            86,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            86,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            86,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            101,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            101,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicXor extends InstructionDef(
        "buffer_atomic_xor",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicXor(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            74,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            74,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            74,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            74,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            59,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            59,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            59,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            59,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            74,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            74,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicXorB32 extends InstructionDef(
        "buffer_atomic_xor_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicXorB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            62,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            62,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            62,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            62,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicXorB64 extends InstructionDef(
        "buffer_atomic_xor_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicXorB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            75,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            75,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            75,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            75,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferAtomicXorX2 extends InstructionDef(
        "buffer_atomic_xor_x2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicXorX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            106,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            106,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            106,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            106,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            91,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            91,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            91,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            91,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            106,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            106,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferGl0Inv extends InstructionDef(
        "buffer_gl0_inv",
        decoder(form(0)((_, mods) => Instruction.BufferGl0Inv(mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            113,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            113,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            43,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            43,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferGl1Inv extends InstructionDef(
        "buffer_gl1_inv",
        decoder(form(0)((_, mods) => Instruction.BufferGl1Inv(mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            114,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            114,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            44,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            44,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferInv extends InstructionDef(
        "buffer_inv",
        decoder(form(0)((_, mods) => Instruction.BufferInv(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            41,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            41,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferInvl2 extends InstructionDef(
        "buffer_invl2",
        decoder(form(0)((_, mods) => Instruction.BufferInvl2(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            41,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferLoadB128 extends InstructionDef(
        "buffer_load_b128",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadB128_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadB128_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            23,
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
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            23,
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
            "MUBUF",
            23,
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
            23,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
  case object BufferLoadB32 extends InstructionDef(
        "buffer_load_b32",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            20,
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
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            20,
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
            "MUBUF",
            20,
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
            20,
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
  case object BufferLoadB64 extends InstructionDef(
        "buffer_load_b64",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            21,
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
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            21,
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
            "MUBUF",
            21,
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
            21,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 64, false, true, false),
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
  case object BufferLoadB96 extends InstructionDef(
        "buffer_load_b96",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadB96_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadB96_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            22,
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
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            22,
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
            "MUBUF",
            22,
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
            22,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 96, false, true, false),
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
  case object BufferLoadD16B16 extends InstructionDef(
        "buffer_load_d16_b16",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16B16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16B16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            32,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            32,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            32,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            32,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferLoadD16FormatX extends InstructionDef(
        "buffer_load_d16_format_x",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16FormatX_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16FormatX_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
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
            "MUBUF",
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
            8,
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
  case object BufferLoadD16FormatXy extends InstructionDef(
        "buffer_load_d16_format_xy",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16FormatXy_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16FormatXy_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
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
            "MUBUF",
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
            9,
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
  case object BufferLoadD16FormatXyz extends InstructionDef(
        "buffer_load_d16_format_xyz",
        decoder(
          form(3)((operands, mods) =>
            Instruction.BufferLoadD16FormatXyz_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16FormatXyz_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
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
            "MUBUF",
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
            10,
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
  case object BufferLoadD16FormatXyzw extends InstructionDef(
        "buffer_load_d16_format_xyzw",
        decoder(
          form(3)((operands, mods) =>
            Instruction.BufferLoadD16FormatXyzw_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16FormatXyzw_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
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
            "MUBUF",
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
            11,
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
  case object BufferLoadD16HiB16 extends InstructionDef(
        "buffer_load_d16_hi_b16",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16HiB16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16HiB16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            35,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            35,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            35,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            35,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferLoadD16HiFormatX extends InstructionDef(
        "buffer_load_d16_hi_format_x",
        decoder(
          form(3)((operands, mods) =>
            Instruction.BufferLoadD16HiFormatX_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16HiFormatX_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            38,
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
            "MUBUF",
            38,
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
            38,
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
  case object BufferLoadD16HiI8 extends InstructionDef(
        "buffer_load_d16_hi_i8",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16HiI8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16HiI8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            34,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            34,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            34,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            34,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferLoadD16HiU8 extends InstructionDef(
        "buffer_load_d16_hi_u8",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16HiU8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16HiU8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            33,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            33,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            33,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            33,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferLoadD16I8 extends InstructionDef(
        "buffer_load_d16_i8",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16I8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16I8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            31,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            31,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            31,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            31,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferLoadD16U8 extends InstructionDef(
        "buffer_load_d16_u8",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16U8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16U8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            30,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            30,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            30,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            30,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferLoadDword extends InstructionDef(
        "buffer_load_dword",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadDword_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadDword_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            20,
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
            "MUBUF",
            20,
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
            "MUBUF",
            20,
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
            "MUBUF",
            20,
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
            "MUBUF",
            12,
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
            "MUBUF",
            12,
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
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            12,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            12,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            20,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            20,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadDwordx2 extends InstructionDef(
        "buffer_load_dwordx2",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadDwordx2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadDwordx2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            21,
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
            "MUBUF",
            21,
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
            "MUBUF",
            21,
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
            "MUBUF",
            21,
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
            "MUBUF",
            13,
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
            "MUBUF",
            13,
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
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            13,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            13,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            21,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            21,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadDwordx3 extends InstructionDef(
        "buffer_load_dwordx3",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadDwordx3_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadDwordx3_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            22,
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
            "MUBUF",
            22,
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
            "MUBUF",
            22,
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
            "MUBUF",
            22,
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
            "MUBUF",
            15,
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
            "MUBUF",
            15,
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
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            15,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            22,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            22,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadDwordx4 extends InstructionDef(
        "buffer_load_dwordx4",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadDwordx4_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadDwordx4_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            23,
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
            "MUBUF",
            23,
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
            "MUBUF",
            23,
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
            "MUBUF",
            23,
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
            "MUBUF",
            14,
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
            "MUBUF",
            14,
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
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            14,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            14,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            23,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            23,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )

  val definitions: Vector[InstructionDef] = Vector(
    BufferAtomicAdd,
    BufferAtomicAddF32,
    BufferAtomicAddF64,
    BufferAtomicAddU32,
    BufferAtomicAddU64,
    BufferAtomicAddX2,
    BufferAtomicAnd,
    BufferAtomicAndB32,
    BufferAtomicAndB64,
    BufferAtomicAndX2,
    BufferAtomicCmpswap,
    BufferAtomicCmpswapB32,
    BufferAtomicCmpswapB64,
    BufferAtomicCmpswapF32,
    BufferAtomicCmpswapX2,
    BufferAtomicCondSubU32,
    BufferAtomicCsub,
    BufferAtomicCsubU32,
    BufferAtomicDec,
    BufferAtomicDecU32,
    BufferAtomicDecU64,
    BufferAtomicDecX2,
    BufferAtomicFcmpswap,
    BufferAtomicFcmpswapX2,
    BufferAtomicFmax,
    BufferAtomicFmaxX2,
    BufferAtomicFmin,
    BufferAtomicFminX2,
    BufferAtomicInc,
    BufferAtomicIncU32,
    BufferAtomicIncU64,
    BufferAtomicIncX2,
    BufferAtomicMaxF32,
    BufferAtomicMaxF64,
    BufferAtomicMaxI32,
    BufferAtomicMaxI64,
    BufferAtomicMaxNumF32,
    BufferAtomicMaxNumF64,
    BufferAtomicMaxU32,
    BufferAtomicMaxU64,
    BufferAtomicMinF32,
    BufferAtomicMinF64,
    BufferAtomicMinI32,
    BufferAtomicMinI64,
    BufferAtomicMinNumF32,
    BufferAtomicMinNumF64,
    BufferAtomicMinU32,
    BufferAtomicMinU64,
    BufferAtomicOr,
    BufferAtomicOrB32,
    BufferAtomicOrB64,
    BufferAtomicOrX2,
    BufferAtomicPkAddBf16,
    BufferAtomicPkAddF16,
    BufferAtomicRsub,
    BufferAtomicRsubX2,
    BufferAtomicSmax,
    BufferAtomicSmaxX2,
    BufferAtomicSmin,
    BufferAtomicSminX2,
    BufferAtomicSub,
    BufferAtomicSubClampU32,
    BufferAtomicSubU32,
    BufferAtomicSubU64,
    BufferAtomicSubX2,
    BufferAtomicSwap,
    BufferAtomicSwapB32,
    BufferAtomicSwapB64,
    BufferAtomicSwapX2,
    BufferAtomicUmax,
    BufferAtomicUmaxX2,
    BufferAtomicUmin,
    BufferAtomicUminX2,
    BufferAtomicXor,
    BufferAtomicXorB32,
    BufferAtomicXorB64,
    BufferAtomicXorX2,
    BufferGl0Inv,
    BufferGl1Inv,
    BufferInv,
    BufferInvl2,
    BufferLoadB128,
    BufferLoadB32,
    BufferLoadB64,
    BufferLoadB96,
    BufferLoadD16B16,
    BufferLoadD16FormatX,
    BufferLoadD16FormatXy,
    BufferLoadD16FormatXyz,
    BufferLoadD16FormatXyzw,
    BufferLoadD16HiB16,
    BufferLoadD16HiFormatX,
    BufferLoadD16HiI8,
    BufferLoadD16HiU8,
    BufferLoadD16I8,
    BufferLoadD16U8,
    BufferLoadDword,
    BufferLoadDwordx2,
    BufferLoadDwordx3,
    BufferLoadDwordx4
  )
}
