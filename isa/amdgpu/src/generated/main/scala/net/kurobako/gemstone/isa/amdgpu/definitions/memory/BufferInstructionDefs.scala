// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object BufferInstructionDefs {
  case object BufferAtomicAdd extends InstructionDef(
        "buffer_atomic_add",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAdd(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            66,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            66,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            66,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            66,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            50,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            50,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            50,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            50,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            66,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            66,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            77,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            77,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            77,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            77,
            Col(
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
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            86,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            86,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            86,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            86,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            79,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            79,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            79,
            Col(
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
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            85,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            53,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            53,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            53,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            53,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            67,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            67,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            67,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            67,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            98,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            98,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            98,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            98,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            82,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            82,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            82,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            82,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            98,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            98,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            72,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            72,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            72,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            72,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            57,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            57,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            57,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            57,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            72,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            72,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            60,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            60,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            60,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            60,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            73,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            73,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            73,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            73,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            104,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            104,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            104,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            104,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            89,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            89,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            89,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            89,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            104,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            104,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            65,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            65,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            65,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            65,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            49,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            49,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            49,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            49,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            65,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            65,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            52,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            52,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            52,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            52,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            66,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            66,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            66,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            66,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            80,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            80,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            97,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            97,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            97,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            97,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            81,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            81,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            81,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            81,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            97,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            97,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            80,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            80,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            52,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            55,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            55,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            76,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            76,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            76,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            76,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            61,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            61,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            61,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            61,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            76,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            76,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            64,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            64,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            64,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            64,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            77,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            77,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            77,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            77,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            108,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            108,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            108,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            108,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            93,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            93,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            93,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            93,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            108,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            108,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            62,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            62,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            62,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            62,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            94,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            94,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            94,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            94,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            64,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            64,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            64,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            64,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            96,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            96,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            96,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            96,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            63,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            63,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            63,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            63,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            95,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            95,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            95,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            95,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            75,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            75,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            75,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            75,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            60,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            60,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            60,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            60,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            75,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            75,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            63,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            63,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            63,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            63,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            76,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            76,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            76,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            76,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            107,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            107,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            107,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            107,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            92,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            92,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            92,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            92,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            107,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            107,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            82,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            82,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            81,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            81,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            81,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            58,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            58,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            58,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            58,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            71,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            71,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            71,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            71,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            82,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            82,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            92,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            59,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            59,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            59,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            59,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            72,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            72,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            72,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            72,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            81,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            81,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            80,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            80,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            80,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            56,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            56,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            56,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            56,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            69,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            69,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            69,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            69,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            81,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            81,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            91,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            57,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            57,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            57,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            57,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            70,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            70,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            70,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            70,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            73,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            73,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            73,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            73,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            58,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            58,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            58,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            58,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            73,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            73,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            61,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            61,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            61,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            61,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            74,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            74,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            74,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            74,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            105,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            105,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            105,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            105,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            90,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            90,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            90,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            90,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            105,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            105,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            82,
            Col(
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
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            90,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            90,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            78,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            78,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            78,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            78,
            Col(
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
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            89,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            89,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX6,
            "gfx6",
            "MUBUF",
            52,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferAtomicRsubX2 extends InstructionDef(
        "buffer_atomic_rsub_x2",
        decoder(form(0)((_, mods) => Instruction.BufferAtomicRsubX2(mods))),
        Metadata(Col(
          Encoding(
            Family.GFX6,
            "gfx6",
            "MUBUF",
            84,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            70,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            70,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            70,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            70,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            55,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            55,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            55,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            55,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            70,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            70,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            102,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            102,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            102,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            102,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            87,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            87,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            87,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            87,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            102,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            102,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            68,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            68,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            68,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            68,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            53,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            53,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            53,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            53,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            68,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            68,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            100,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            100,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            100,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            100,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            85,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            85,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            85,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            85,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            100,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            100,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            67,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            67,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            67,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            67,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            51,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            51,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            51,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            51,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            67,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            67,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            55,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            55,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            54,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            54,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            54,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            54,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            68,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            68,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            68,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            68,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            99,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            99,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            99,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            99,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            83,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            83,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            83,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            83,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            99,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            99,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            64,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            64,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            64,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            64,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            48,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            48,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            48,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            48,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            64,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            64,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            51,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            51,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            51,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            51,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            65,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            65,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            65,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            65,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            96,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            96,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            96,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            96,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            80,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            80,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            80,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            80,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            96,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            96,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            71,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            71,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            71,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            71,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            56,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            56,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            56,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            56,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            71,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            71,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            103,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            103,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            103,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            103,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            88,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            88,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            88,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            88,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            103,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            103,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            69,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            69,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            69,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            69,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            54,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            54,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            54,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            54,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            69,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            69,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            101,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            101,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            101,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            101,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            86,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            86,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            86,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            86,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            101,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            101,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            74,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            74,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            74,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            74,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            59,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            59,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            59,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            59,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            74,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            74,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            62,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            62,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            62,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            62,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            75,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            75,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            75,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            75,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            106,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            106,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            106,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            106,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            91,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            91,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            91,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            91,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            106,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            106,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferGl0Inv extends InstructionDef(
        "buffer_gl0_inv",
        decoder(form(0)((_, mods) => Instruction.BufferGl0Inv(mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            113,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            113,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            43,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            43,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferGl1Inv extends InstructionDef(
        "buffer_gl1_inv",
        decoder(form(0)((_, mods) => Instruction.BufferGl1Inv(mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            114,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            114,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            44,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            44,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferInv extends InstructionDef(
        "buffer_inv",
        decoder(form(0)((_, mods) => Instruction.BufferInv(mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            41,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            41,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferInvl2 extends InstructionDef(
        "buffer_invl2",
        decoder(form(0)((_, mods) => Instruction.BufferInvl2(mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            41,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            23,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            23,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            23,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            23,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            20,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            20,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            20,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            20,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            21,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            21,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            21,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            21,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            22,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            22,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            22,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            22,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            32,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            32,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            32,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            32,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            8,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            8,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            8,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            9,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            9,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            9,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            10,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            10,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            10,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            11,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            11,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            11,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            35,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            35,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            35,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            35,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            38,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            38,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            38,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            34,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            34,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            34,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            34,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            33,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            33,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            33,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            33,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            31,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            31,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            31,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            31,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            30,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            30,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            30,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            30,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            20,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            20,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            20,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            20,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            12,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            12,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            12,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            12,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            20,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            20,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            21,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            21,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            21,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            21,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            13,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            13,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            13,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            13,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            21,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            21,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            22,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            22,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            22,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            22,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            15,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            15,
            Col(
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
            Family.GFX7,
            "gfx7",
            "MUBUF",
            15,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            22,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            22,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            23,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            23,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            23,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            23,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            14,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            14,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            14,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            14,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            23,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            23,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadFormatD16HiX extends InstructionDef(
        "buffer_load_format_d16_hi_x",
        decoder(
          form(3)((operands, mods) =>
            Instruction.BufferLoadFormatD16HiX_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatD16HiX_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            38,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            38,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            38,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            38,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            38,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            38,
            Col(
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
            Family.GFX9,
            "gfx9",
            "MUBUF",
            38,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadFormatD16X extends InstructionDef(
        "buffer_load_format_d16_x",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadFormatD16X_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatD16X_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            8,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            8,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            8,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            8,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            128,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            128,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MUBUF",
            8,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            8,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadFormatD16Xy extends InstructionDef(
        "buffer_load_format_d16_xy",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadFormatD16Xy_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatD16Xy_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            9,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            9,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            9,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            9,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            129,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            129,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MUBUF",
            9,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            9,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadFormatD16Xyz extends InstructionDef(
        "buffer_load_format_d16_xyz",
        decoder(
          form(3)((operands, mods) =>
            Instruction.BufferLoadFormatD16Xyz_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatD16Xyz_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            10,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            10,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            10,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            10,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            130,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            130,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MUBUF",
            10,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            10,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadFormatD16Xyzw extends InstructionDef(
        "buffer_load_format_d16_xyzw",
        decoder(
          form(3)((operands, mods) =>
            Instruction.BufferLoadFormatD16Xyzw_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatD16Xyzw_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            11,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            11,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            11,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            11,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            131,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            131,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MUBUF",
            11,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            11,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadFormatX extends InstructionDef(
        "buffer_load_format_x",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadFormatX_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatX_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            0,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            0,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            0,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            0,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            0,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            0,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            0,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            0,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            0,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            0,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            0,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            0,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            0,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadFormatXy extends InstructionDef(
        "buffer_load_format_xy",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadFormatXy_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatXy_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            1,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            1,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            1,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            1,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            1,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            1,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            1,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            1,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            1,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            1,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            1,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            1,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            1,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadFormatXyz extends InstructionDef(
        "buffer_load_format_xyz",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadFormatXyz_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatXyz_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            2,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            2,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            2,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            2,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            2,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            2,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            2,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            2,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            2,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            2,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            2,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            2,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            2,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadFormatXyzw extends InstructionDef(
        "buffer_load_format_xyzw",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadFormatXyzw_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatXyzw_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            3,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            3,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            3,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            3,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            3,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            3,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            3,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            3,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            3,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            3,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            3,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            3,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            3,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadI16 extends InstructionDef(
        "buffer_load_i16",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadI16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadI16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            19,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            19,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            19,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            19,
            Col(
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
  case object BufferLoadI8 extends InstructionDef(
        "buffer_load_i8",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadI8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadI8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            17,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            17,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            17,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            17,
            Col(
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
  case object BufferLoadSbyte extends InstructionDef(
        "buffer_load_sbyte",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadSbyte_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadSbyte_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MUBUF",
            9,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            9,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            17,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            17,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadSbyteD16 extends InstructionDef(
        "buffer_load_sbyte_d16",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadSbyteD16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadSbyteD16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            34,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadSbyteD16Hi extends InstructionDef(
        "buffer_load_sbyte_d16_hi",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadSbyteD16Hi_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadSbyteD16Hi_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            35,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadShortD16 extends InstructionDef(
        "buffer_load_short_d16",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadShortD16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadShortD16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            36,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            36,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            36,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            36,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            36,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            36,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            36,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadShortD16Hi extends InstructionDef(
        "buffer_load_short_d16_hi",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadShortD16Hi_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadShortD16Hi_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            37,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadSshort extends InstructionDef(
        "buffer_load_sshort",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadSshort_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadSshort_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            19,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            19,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            19,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            19,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MUBUF",
            11,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            11,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            19,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            19,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadU16 extends InstructionDef(
        "buffer_load_u16",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadU16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadU16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            18,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            18,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            18,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            18,
            Col(
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
  case object BufferLoadU8 extends InstructionDef(
        "buffer_load_u8",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadU8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadU8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            16,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            16,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            16,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            16,
            Col(
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
  case object BufferLoadUbyte extends InstructionDef(
        "buffer_load_ubyte",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadUbyte_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadUbyte_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MUBUF",
            8,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            8,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            16,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            16,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadUbyteD16 extends InstructionDef(
        "buffer_load_ubyte_d16",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadUbyteD16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadUbyteD16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            32,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadUbyteD16Hi extends InstructionDef(
        "buffer_load_ubyte_d16_hi",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadUbyteD16Hi_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadUbyteD16Hi_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            33,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            33,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            33,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            33,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            33,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            33,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            33,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferLoadUshort extends InstructionDef(
        "buffer_load_ushort",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadUshort_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadUshort_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MUBUF",
            10,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            10,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            18,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            18,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreB128 extends InstructionDef(
        "buffer_store_b128",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreB128(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            29,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            29,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            29,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            29,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
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
  case object BufferStoreB16 extends InstructionDef(
        "buffer_store_b16",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreB16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferStoreB32 extends InstructionDef(
        "buffer_store_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            26,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            26,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            26,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            26,
            Col(
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
  case object BufferStoreB64 extends InstructionDef(
        "buffer_store_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            27,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            27,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            27,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 64, true, false, false),
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
  case object BufferStoreB8 extends InstructionDef(
        "buffer_store_b8",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreB8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferStoreB96 extends InstructionDef(
        "buffer_store_b96",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreB96(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            28,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            28,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            28,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 96, true, false, false),
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
  case object BufferStoreByte extends InstructionDef(
        "buffer_store_byte",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreByte(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MUBUF",
            24,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            24,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            24,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            24,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreByteD16Hi extends InstructionDef(
        "buffer_store_byte_d16_hi",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreByteD16Hi(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            25,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreD16FormatX extends InstructionDef(
        "buffer_store_d16_format_x",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16FormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            12,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            12,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            12,
            Col(
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
  case object BufferStoreD16FormatXy extends InstructionDef(
        "buffer_store_d16_format_xy",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16FormatXy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            13,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            13,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            13,
            Col(
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
  case object BufferStoreD16FormatXyz extends InstructionDef(
        "buffer_store_d16_format_xyz",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16FormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            14,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            14,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            14,
            Col(
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
  case object BufferStoreD16FormatXyzw extends InstructionDef(
        "buffer_store_d16_format_xyzw",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16FormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            15,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            15,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            15,
            Col(
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
  case object BufferStoreD16HiB16 extends InstructionDef(
        "buffer_store_d16_hi_b16",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16HiB16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferStoreD16HiB8 extends InstructionDef(
        "buffer_store_d16_hi_b8",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16HiB8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VBUFFER",
            36,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            36,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            36,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            36,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object BufferStoreD16HiFormatX extends InstructionDef(
        "buffer_store_d16_hi_format_x",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16HiFormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MUBUF",
            39,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            39,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            39,
            Col(
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
  case object BufferStoreDword extends InstructionDef(
        "buffer_store_dword",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreDword(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            28,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            28,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            28,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            28,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            28,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            28,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            28,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            28,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            28,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            28,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreDwordx2 extends InstructionDef(
        "buffer_store_dwordx2",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreDwordx2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            29,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            29,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            29,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            29,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            29,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            29,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            29,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            29,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            29,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            29,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreDwordx3 extends InstructionDef(
        "buffer_store_dwordx3",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreDwordx3(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            30,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            30,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            30,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            30,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            31,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            31,
            Col(
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
            Family.GFX7,
            "gfx7",
            "MUBUF",
            31,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            30,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            30,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreDwordx4 extends InstructionDef(
        "buffer_store_dwordx4",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreDwordx4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            31,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            31,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            31,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            31,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            30,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            30,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            30,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            30,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            31,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            31,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreFormatD16HiX extends InstructionDef(
        "buffer_store_format_d16_hi_x",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatD16HiX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            39,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            39,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            39,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            39,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            39,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            39,
            Col(
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
            Family.GFX9,
            "gfx9",
            "MUBUF",
            39,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreFormatD16X extends InstructionDef(
        "buffer_store_format_d16_x",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatD16X(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            12,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            12,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            12,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            12,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            132,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            132,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MUBUF",
            12,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            12,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreFormatD16Xy extends InstructionDef(
        "buffer_store_format_d16_xy",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatD16Xy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            13,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            13,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            13,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            13,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            133,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            133,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MUBUF",
            13,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            13,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreFormatD16Xyz extends InstructionDef(
        "buffer_store_format_d16_xyz",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatD16Xyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            14,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            14,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            14,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            14,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            134,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            134,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MUBUF",
            14,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            14,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreFormatD16Xyzw extends InstructionDef(
        "buffer_store_format_d16_xyzw",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatD16Xyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            15,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            15,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            15,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            15,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            135,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            135,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MUBUF",
            15,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            15,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreFormatX extends InstructionDef(
        "buffer_store_format_x",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            4,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            4,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            4,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            4,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            4,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            4,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            4,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            4,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            4,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            4,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            4,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            4,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            4,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreFormatXy extends InstructionDef(
        "buffer_store_format_xy",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatXy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            5,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            5,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            5,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            5,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            5,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            5,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            5,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            5,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            5,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            5,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            5,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            5,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            5,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreFormatXyz extends InstructionDef(
        "buffer_store_format_xyz",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            6,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            6,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            6,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            6,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            6,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            6,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            6,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            6,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            6,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            6,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            6,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            6,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            6,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreFormatXyzw extends InstructionDef(
        "buffer_store_format_xyzw",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            7,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            7,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            7,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            7,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MUBUF",
            7,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MUBUF",
            7,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MUBUF",
            7,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MUBUF",
            7,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            7,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MUBUF",
            7,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            7,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            7,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            7,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreLdsDword extends InstructionDef(
        "buffer_store_lds_dword",
        decoder(form(2)((operands, mods) => Instruction.BufferStoreLdsDword(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            61,
            Col(
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            61,
            Col(
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            61,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            61,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreShort extends InstructionDef(
        "buffer_store_short",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreShort(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MUBUF",
            26,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            26,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            26,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            26,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreShortD16Hi extends InstructionDef(
        "buffer_store_short_d16_hi",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreShortD16Hi(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MUBUF",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MUBUF",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SREG_M0_INL", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            27,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferWbinvl1 extends InstructionDef(
        "buffer_wbinvl1",
        decoder(form(0)((_, mods) => Instruction.BufferWbinvl1(mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            62,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            62,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MUBUF",
            113,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            113,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            62,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            62,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferWbinvl1Vol extends InstructionDef(
        "buffer_wbinvl1_vol",
        decoder(form(0)((_, mods) => Instruction.BufferWbinvl1Vol(mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MUBUF",
            63,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            63,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MUBUF",
            112,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MUBUF",
            112,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MUBUF",
            63,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MUBUF",
            63,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferWbl2 extends InstructionDef(
        "buffer_wbl2",
        decoder(form(0)((_, mods) => Instruction.BufferWbl2(mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MUBUF",
            40,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA3,
            "cdna3",
            "MUBUF",
            40,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "MUBUF",
            40,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object TbufferLoadD16FormatX extends InstructionDef(
        "tbuffer_load_d16_format_x",
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadD16FormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MTBUF",
            8,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            8,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            136,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MTBUF",
            9,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            9,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            137,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MTBUF",
            10,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            10,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            138,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MTBUF",
            11,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            11,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            139,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            8,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            8,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            8,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            8,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            8,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            8,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MTBUF",
            8,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            8,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            9,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            9,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            9,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            9,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            9,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            9,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MTBUF",
            9,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            9,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            10,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            10,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            10,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            10,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            10,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            10,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MTBUF",
            10,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            10,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            11,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            11,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            11,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            11,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            11,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            11,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MTBUF",
            11,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            11,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            0,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            0,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            0,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            0,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            0,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            0,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MTBUF",
            0,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            0,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            128,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MTBUF",
            0,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MTBUF",
            0,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MTBUF",
            0,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            0,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            1,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            1,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            1,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            1,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            1,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            1,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MTBUF",
            1,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            1,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            129,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MTBUF",
            1,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MTBUF",
            1,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MTBUF",
            1,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            1,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            2,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            2,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            2,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            2,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            2,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            2,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MTBUF",
            2,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            2,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            130,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MTBUF",
            2,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MTBUF",
            2,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MTBUF",
            2,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            2,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            3,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            3,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            3,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            3,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            3,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            3,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MTBUF",
            3,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            3,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            131,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MTBUF",
            3,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MTBUF",
            3,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MTBUF",
            3,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            3,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MTBUF",
            12,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            12,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            140,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MTBUF",
            13,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            13,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            141,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MTBUF",
            14,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            14,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            142,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "MTBUF",
            15,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            15,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            143,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            12,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            12,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            12,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            12,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            12,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            12,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MTBUF",
            12,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            12,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            13,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            13,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            13,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            13,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            13,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            13,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MTBUF",
            13,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            13,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            14,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            14,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            14,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            14,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            14,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            14,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MTBUF",
            14,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            14,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            15,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            15,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            15,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            15,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            15,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            15,
            Col(
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
            Family.GFX8,
            "gfx8",
            "MTBUF",
            15,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            15,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            4,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            4,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            4,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            4,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            4,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            4,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MTBUF",
            4,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            4,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            132,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MTBUF",
            4,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MTBUF",
            4,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MTBUF",
            4,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            4,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            5,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            5,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            5,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            5,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            5,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            5,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MTBUF",
            5,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            5,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            133,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MTBUF",
            5,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MTBUF",
            5,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MTBUF",
            5,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            5,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            6,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            6,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            6,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            6,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            6,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            6,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MTBUF",
            6,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            6,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            134,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MTBUF",
            6,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MTBUF",
            6,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MTBUF",
            6,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            6,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MTBUF",
            7,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "MTBUF",
            7,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "MTBUF",
            7,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "MTBUF",
            7,
            Col(
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
            Family.GFX10,
            "rdna1",
            "MTBUF",
            7,
            Col(
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
            Family.GFX10,
            "rdna2",
            "MTBUF",
            7,
            Col(
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
            Family.GFX11,
            "rdna3",
            "MTBUF",
            7,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "MTBUF",
            7,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VBUFFER",
            135,
            Col(
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
            Family.GFX6,
            "gfx6",
            "MTBUF",
            7,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MTBUF",
            7,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MTBUF",
            7,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MTBUF",
            7,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(
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
    BufferLoadDwordx4,
    BufferLoadFormatD16HiX,
    BufferLoadFormatD16X,
    BufferLoadFormatD16Xy,
    BufferLoadFormatD16Xyz,
    BufferLoadFormatD16Xyzw,
    BufferLoadFormatX,
    BufferLoadFormatXy,
    BufferLoadFormatXyz,
    BufferLoadFormatXyzw,
    BufferLoadI16,
    BufferLoadI8,
    BufferLoadSbyte,
    BufferLoadSbyteD16,
    BufferLoadSbyteD16Hi,
    BufferLoadShortD16,
    BufferLoadShortD16Hi,
    BufferLoadSshort,
    BufferLoadU16,
    BufferLoadU8,
    BufferLoadUbyte,
    BufferLoadUbyteD16,
    BufferLoadUbyteD16Hi,
    BufferLoadUshort,
    BufferStoreB128,
    BufferStoreB16,
    BufferStoreB32,
    BufferStoreB64,
    BufferStoreB8,
    BufferStoreB96,
    BufferStoreByte,
    BufferStoreByteD16Hi,
    BufferStoreD16FormatX,
    BufferStoreD16FormatXy,
    BufferStoreD16FormatXyz,
    BufferStoreD16FormatXyzw,
    BufferStoreD16HiB16,
    BufferStoreD16HiB8,
    BufferStoreD16HiFormatX,
    BufferStoreDword,
    BufferStoreDwordx2,
    BufferStoreDwordx3,
    BufferStoreDwordx4,
    BufferStoreFormatD16HiX,
    BufferStoreFormatD16X,
    BufferStoreFormatD16Xy,
    BufferStoreFormatD16Xyz,
    BufferStoreFormatD16Xyzw,
    BufferStoreFormatX,
    BufferStoreFormatXy,
    BufferStoreFormatXyz,
    BufferStoreFormatXyzw,
    BufferStoreLdsDword,
    BufferStoreShort,
    BufferStoreShortD16Hi,
    BufferWbinvl1,
    BufferWbinvl1Vol,
    BufferWbl2,
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
    TbufferStoreFormatXyzw
  )
}
