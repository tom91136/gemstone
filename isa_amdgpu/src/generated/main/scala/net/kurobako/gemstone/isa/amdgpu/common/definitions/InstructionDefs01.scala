// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

private[common] object InstructionDefs1 {
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            38,
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
            38,
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
            38,
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
            38,
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
            38,
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
            38,
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
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            38,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
            128,
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
            128,
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
            "MUBUF",
            8,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            8,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
            129,
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
            "MUBUF",
            129,
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
            "MUBUF",
            9,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            9,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
            130,
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
            "MUBUF",
            130,
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
            "MUBUF",
            10,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            10,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
            131,
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
            "MUBUF",
            131,
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
            "MUBUF",
            11,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            11,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            0,
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
            "MUBUF",
            0,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            0,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            0,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            0,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            1,
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
            "MUBUF",
            1,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            1,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            1,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            1,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            2,
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
            "MUBUF",
            2,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            2,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            2,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            2,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            3,
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
            "MUBUF",
            3,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            3,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            3,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            3,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            19,
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
            19,
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
            19,
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
            19,
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
  case object BufferLoadI8 extends InstructionDef(
        "buffer_load_i8",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadI8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadI8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            17,
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
            17,
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
            17,
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
            17,
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
  case object BufferLoadSbyte extends InstructionDef(
        "buffer_load_sbyte",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadSbyte_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadSbyte_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            17,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            17,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            17,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            17,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            9,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            9,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            9,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            9,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            17,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            17,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            34,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            34,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            34,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            34,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            34,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            34,
            Vector(
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
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            34,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            35,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            35,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            35,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            35,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            35,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            35,
            Vector(
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
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            35,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            36,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            36,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            36,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            36,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            36,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            36,
            Vector(
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
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            36,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            37,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            37,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            37,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            37,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            37,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            37,
            Vector(
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
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            37,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            19,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            19,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            19,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            19,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            11,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            11,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            11,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            11,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            19,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            19,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            18,
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
            18,
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
            18,
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
            18,
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
  case object BufferLoadU8 extends InstructionDef(
        "buffer_load_u8",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadU8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadU8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            16,
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
            16,
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
            16,
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
            16,
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
  case object BufferLoadUbyte extends InstructionDef(
        "buffer_load_ubyte",
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadUbyte_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadUbyte_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            16,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            16,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            16,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            16,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            8,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            8,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            8,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            8,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            16,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            16,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            32,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            32,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            32,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            32,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            32,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            32,
            Vector(
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
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            32,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            33,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            33,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            33,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            33,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            33,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            33,
            Vector(
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
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            33,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            18,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            18,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            18,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            18,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            10,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            10,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            10,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            10,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            18,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            18,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            29,
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
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            29,
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
            "MUBUF",
            29,
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
            29,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            25,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            25,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            25,
            Vector(
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
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            25,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            26,
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
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            26,
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
            "MUBUF",
            26,
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
            26,
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
  case object BufferStoreB64 extends InstructionDef(
        "buffer_store_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            27,
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
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            27,
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
            "MUBUF",
            27,
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
            27,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            24,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            24,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            24,
            Vector(
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
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            24,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            28,
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
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            28,
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
            "MUBUF",
            28,
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
            28,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            24,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            24,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            24,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            24,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            24,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            24,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            24,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            25,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            25,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            25,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            25,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            25,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            25,
            Vector(
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
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            25,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
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
            "MUBUF",
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
            12,
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
  case object BufferStoreD16FormatXy extends InstructionDef(
        "buffer_store_d16_format_xy",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16FormatXy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
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
            "MUBUF",
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
            13,
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
  case object BufferStoreD16FormatXyz extends InstructionDef(
        "buffer_store_d16_format_xyz",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16FormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
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
            "MUBUF",
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
            14,
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
  case object BufferStoreD16FormatXyzw extends InstructionDef(
        "buffer_store_d16_format_xyzw",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16FormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
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
            "MUBUF",
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
            15,
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
  case object BufferStoreD16HiB16 extends InstructionDef(
        "buffer_store_d16_hi_b16",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16HiB16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            37,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            37,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            37,
            Vector(
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
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            37,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VBUFFER",
            36,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            36,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MUBUF",
            36,
            Vector(
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
            Generation.GFX12,
            "rdna4",
            "VBUFFER",
            36,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MUBUF",
            39,
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
            "MUBUF",
            39,
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
            39,
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
  case object BufferStoreDword extends InstructionDef(
        "buffer_store_dword",
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreDword(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            28,
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
            "MUBUF",
            28,
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
            "MUBUF",
            28,
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
            "MUBUF",
            28,
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
            "MUBUF",
            28,
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
            "MUBUF",
            28,
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
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            28,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            29,
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
            "MUBUF",
            29,
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
            "MUBUF",
            29,
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
            "MUBUF",
            29,
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
            "MUBUF",
            29,
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
            "MUBUF",
            29,
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
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            29,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            29,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            29,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            29,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            30,
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
            "MUBUF",
            30,
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
            "MUBUF",
            30,
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
            "MUBUF",
            30,
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
            "MUBUF",
            31,
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
            "MUBUF",
            31,
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
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            31,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            30,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            30,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            31,
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
            "MUBUF",
            31,
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
            "MUBUF",
            31,
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
            "MUBUF",
            31,
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
            "MUBUF",
            30,
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
            "MUBUF",
            30,
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
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            30,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            30,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            31,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            31,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            39,
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
            "MUBUF",
            39,
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
            "MUBUF",
            39,
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
            "MUBUF",
            39,
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
            "MUBUF",
            39,
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
            "MUBUF",
            39,
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
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            39,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
            132,
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
            "MUBUF",
            132,
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
            "MUBUF",
            12,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            12,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
            133,
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
            "MUBUF",
            133,
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
            "MUBUF",
            13,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            13,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
            134,
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
            "MUBUF",
            134,
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
            "MUBUF",
            14,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            14,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
            135,
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
            "MUBUF",
            135,
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
            "MUBUF",
            15,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            15,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            4,
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
            "MUBUF",
            4,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            4,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            4,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            4,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            5,
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
            "MUBUF",
            5,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            5,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            5,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            5,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            6,
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
            "MUBUF",
            6,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            6,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            6,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            6,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            "MUBUF",
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
            7,
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
            "MUBUF",
            7,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            7,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            7,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            7,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferStoreLdsDword extends InstructionDef(
        "buffer_store_lds_dword",
        decoder(form(2)((operands, mods) => Instruction.BufferStoreLdsDword(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            61,
            Vector(
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            61,
            Vector(
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SSRC_NOLIT", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            61,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            61,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            26,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            26,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            26,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            26,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            26,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            26,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            26,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            26,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            26,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            26,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            27,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            27,
            Vector(
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
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            27,
            Vector(
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
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            27,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MUBUF",
            27,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MUBUF",
            27,
            Vector(
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
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            27,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferWbinvl1 extends InstructionDef(
        "buffer_wbinvl1",
        decoder(form(0)((_, mods) => Instruction.BufferWbinvl1(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            62,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            62,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            113,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            113,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            62,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            62,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferWbinvl1Vol extends InstructionDef(
        "buffer_wbinvl1_vol",
        decoder(form(0)((_, mods) => Instruction.BufferWbinvl1Vol(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MUBUF",
            63,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            63,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MUBUF",
            112,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MUBUF",
            112,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MUBUF",
            63,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MUBUF",
            63,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object BufferWbl2 extends InstructionDef(
        "buffer_wbl2",
        decoder(form(0)((_, mods) => Instruction.BufferWbl2(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MUBUF",
            40,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "MUBUF",
            40,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "MUBUF",
            40,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ClusterLoadAsyncToLdsB128 extends InstructionDef(
        "cluster_load_async_to_lds_b128",
        decoder(form(3)((operands, mods) =>
          Instruction.ClusterLoadAsyncToLdsB128(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            109,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "DSMEM", 128, false, true, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ClusterLoadAsyncToLdsB32 extends InstructionDef(
        "cluster_load_async_to_lds_b32",
        decoder(form(3)((operands, mods) =>
          Instruction.ClusterLoadAsyncToLdsB32(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            107,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ClusterLoadAsyncToLdsB64 extends InstructionDef(
        "cluster_load_async_to_lds_b64",
        decoder(form(3)((operands, mods) =>
          Instruction.ClusterLoadAsyncToLdsB64(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            108,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ClusterLoadAsyncToLdsB8 extends InstructionDef(
        "cluster_load_async_to_lds_b8",
        decoder(form(3)((operands, mods) =>
          Instruction.ClusterLoadAsyncToLdsB8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            106,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ClusterLoadB128 extends InstructionDef(
        "cluster_load_b128",
        decoder(form(3)((operands, mods) => Instruction.ClusterLoadB128(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            105,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ClusterLoadB32 extends InstructionDef(
        "cluster_load_b32",
        decoder(form(3)((operands, mods) => Instruction.ClusterLoadB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            103,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ClusterLoadB64 extends InstructionDef(
        "cluster_load_b64",
        decoder(form(3)((operands, mods) => Instruction.ClusterLoadB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            104,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsAddF32 extends InstructionDef(
        "ds_add_f32",
        decoder(form(2)((operands, mods) => Instruction.DsAddF32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            21,
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
            21,
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
            21,
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
            21,
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
            21,
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
            21,
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
            21,
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
            21,
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
            21,
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
            21,
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
            Generation.GFX8,
            "gfx8",
            "DS",
            21,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            21,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAddF64 extends InstructionDef(
        "ds_add_f64",
        decoder(form(2)((operands, mods) => Instruction.DsAddF64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            92,
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
            92,
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
            92,
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
            84,
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
  case object DsAddGsRegRtn extends InstructionDef(
        "ds_add_gs_reg_rtn",
        decoder(form(2)((operands, mods) => Instruction.DsAddGsRegRtn(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            122,
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
            122,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsAddRtnF32 extends InstructionDef(
        "ds_add_rtn_f32",
        decoder(form(3)((operands, mods) => Instruction.DsAddRtnF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            53,
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
            53,
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
            53,
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
            53,
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
            121,
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
            85,
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
            85,
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
            121,
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
            121,
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
            121,
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
            Generation.GFX8,
            "gfx8",
            "DS",
            53,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            53,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAddRtnF64 extends InstructionDef(
        "ds_add_rtn_f64",
        decoder(form(3)((operands, mods) => Instruction.DsAddRtnF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "DS",
            124,
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
            124,
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
            124,
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
            116,
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
  case object DsAddRtnU32 extends InstructionDef(
        "ds_add_rtn_u32",
        decoder(form(3)((operands, mods) => Instruction.DsAddRtnU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
            32,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            32,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            32,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            32,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAddRtnU64 extends InstructionDef(
        "ds_add_rtn_u64",
        decoder(form(3)((operands, mods) => Instruction.DsAddRtnU64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            96,
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
            96,
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
            96,
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
            96,
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
            96,
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
            96,
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
            96,
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
            96,
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
            96,
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
            96,
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
            96,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            96,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            96,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            96,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAddSrc2F32 extends InstructionDef(
        "ds_add_src2_f32",
        decoder(form(1)((operands, mods) => Instruction.DsAddSrc2F32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            149,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            149,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAddSrc2U32 extends InstructionDef(
        "ds_add_src2_u32",
        decoder(form(1)((operands, mods) => Instruction.DsAddSrc2U32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            128,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            128,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            128,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            128,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAddSrc2U64 extends InstructionDef(
        "ds_add_src2_u64",
        decoder(form(1)((operands, mods) => Instruction.DsAddSrc2U64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            192,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            192,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            192,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            192,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAddU32 extends InstructionDef(
        "ds_add_u32",
        decoder(form(2)((operands, mods) => Instruction.DsAddU32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            0,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            0,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            0,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAddU64 extends InstructionDef(
        "ds_add_u64",
        decoder(form(2)((operands, mods) => Instruction.DsAddU64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            64,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            64,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            64,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAndB32 extends InstructionDef(
        "ds_and_b32",
        decoder(form(2)((operands, mods) => Instruction.DsAndB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            9,
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
            9,
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
            9,
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
            9,
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
            9,
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
            9,
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
            9,
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
            9,
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
            9,
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
            9,
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
            9,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            9,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            9,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            9,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAndB64 extends InstructionDef(
        "ds_and_b64",
        decoder(form(2)((operands, mods) => Instruction.DsAndB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            73,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            73,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            73,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAndRtnB32 extends InstructionDef(
        "ds_and_rtn_b32",
        decoder(form(3)((operands, mods) => Instruction.DsAndRtnB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            41,
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
            41,
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
            41,
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
            41,
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
            41,
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
            41,
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
            41,
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
            41,
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
            41,
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
            41,
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
            41,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            41,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            41,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            41,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAndRtnB64 extends InstructionDef(
        "ds_and_rtn_b64",
        decoder(form(3)((operands, mods) => Instruction.DsAndRtnB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            105,
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
            105,
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
            105,
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
            105,
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
            105,
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
            105,
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
            105,
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
            105,
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
            105,
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
            105,
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
            105,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            105,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            105,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            105,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAndSrc2B32 extends InstructionDef(
        "ds_and_src2_b32",
        decoder(form(1)((operands, mods) => Instruction.DsAndSrc2B32(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            137,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            137,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            137,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            137,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAndSrc2B64 extends InstructionDef(
        "ds_and_src2_b64",
        decoder(form(1)((operands, mods) => Instruction.DsAndSrc2B64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX6,
            "gfx6",
            "DS",
            201,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            201,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            201,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            201,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAppend extends InstructionDef(
        "ds_append",
        decoder(form(1)((operands, mods) => Instruction.DsAppend(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            190,
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
            190,
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
            190,
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
            190,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
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
            62,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            62,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            190,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            190,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsAtomicAsyncBarrierArriveB64 extends InstructionDef(
        "ds_atomic_async_barrier_arrive_b64",
        decoder(form(1)((operands, mods) => Instruction.DsAtomicAsyncBarrierArriveB64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            86,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsAtomicBarrierArriveRtnB64 extends InstructionDef(
        "ds_atomic_barrier_arrive_rtn_b64",
        decoder(form(3)((operands, mods) =>
          Instruction.DsAtomicBarrierArriveRtnB64(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            117,
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
  case object DsBpermuteB32 extends InstructionDef(
        "ds_bpermute_b32",
        decoder(form(3)((operands, mods) => Instruction.DsBpermuteB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            63,
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
            63,
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
            63,
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
            63,
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
            179,
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
            179,
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
            179,
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
            179,
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
            179,
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
            179,
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
            63,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            63,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsBpermuteFiB32 extends InstructionDef(
        "ds_bpermute_fi_b32",
        decoder(form(3)((operands, mods) => Instruction.DsBpermuteFiB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            205,
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
            205,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsBvhStackPush4Pop1RtnB32 extends InstructionDef(
        "ds_bvh_stack_push4_pop1_rtn_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.DsBvhStackPush4Pop1RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            224,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, false, true, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsBvhStackPush8Pop1RtnB32 extends InstructionDef(
        "ds_bvh_stack_push8_pop1_rtn_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.DsBvhStackPush8Pop1RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            225,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, false, true, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsBvhStackPush8Pop2RtnB64 extends InstructionDef(
        "ds_bvh_stack_push8_pop2_rtn_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.DsBvhStackPush8Pop2RtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VDS",
            226,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, false, true, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsBvhStackRtnB32 extends InstructionDef(
        "ds_bvh_stack_rtn_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.DsBvhStackRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "DS",
            173,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, false, true, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 128, true, false, false),
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
            173,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 32, false, true, false),
              OperandInfo(Some("DATA0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("DATA1"), "VGPR", 128, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object DsCmpstB32 extends InstructionDef(
        "ds_cmpst_b32",
        decoder(form(3)((operands, mods) => Instruction.DsCmpstB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "DS",
            16,
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
            16,
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
            16,
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
            16,
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
            Generation.GFX10,
            "rdna1",
            "DS",
            16,
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
            16,
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
            16,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            16,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            16,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            16,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsCmpstB64 extends InstructionDef(
        "ds_cmpst_b64",
        decoder(form(3)((operands, mods) => Instruction.DsCmpstB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            80,
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
            80,
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
            80,
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
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            80,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            80,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            80,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            80,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsCmpstF32 extends InstructionDef(
        "ds_cmpst_f32",
        decoder(form(3)((operands, mods) => Instruction.DsCmpstF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            17,
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
            17,
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
            17,
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
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            17,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            17,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            17,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            17,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsCmpstF64 extends InstructionDef(
        "ds_cmpst_f64",
        decoder(form(3)((operands, mods) => Instruction.DsCmpstF64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            81,
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
            81,
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
            81,
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
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            81,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            81,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            81,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            81,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsCmpstRtnB32 extends InstructionDef(
        "ds_cmpst_rtn_b32",
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            48,
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
            48,
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
            48,
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            48,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            48,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            48,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            48,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsCmpstRtnB64 extends InstructionDef(
        "ds_cmpst_rtn_b64",
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstRtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            112,
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
            112,
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
            112,
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
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            112,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            112,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            112,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            112,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsCmpstRtnF32 extends InstructionDef(
        "ds_cmpst_rtn_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstRtnF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            49,
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
            49,
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
            49,
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            49,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            49,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            49,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            49,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsCmpstRtnF64 extends InstructionDef(
        "ds_cmpst_rtn_f64",
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstRtnF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
            "DS",
            113,
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
            113,
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
            113,
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
            Generation.GFX10,
            "rdna1",
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
            Generation.GFX10,
            "rdna2",
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
            Generation.GFX6,
            "gfx6",
            "DS",
            113,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "DS",
            113,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "DS",
            113,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "DS",
            113,
            Vector.empty,
            Some("ds"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object DsCmpstoreB32 extends InstructionDef(
        "ds_cmpstore_b32",
        decoder(form(3)((operands, mods) => Instruction.DsCmpstoreB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VDS",
            16,
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
            16,
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
            16,
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
            16,
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

  val definitions: Vector[InstructionDef] = Vector(
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
    ClusterLoadAsyncToLdsB128,
    ClusterLoadAsyncToLdsB32,
    ClusterLoadAsyncToLdsB64,
    ClusterLoadAsyncToLdsB8,
    ClusterLoadB128,
    ClusterLoadB32,
    ClusterLoadB64,
    DsAddF32,
    DsAddF64,
    DsAddGsRegRtn,
    DsAddRtnF32,
    DsAddRtnF64,
    DsAddRtnU32,
    DsAddRtnU64,
    DsAddSrc2F32,
    DsAddSrc2U32,
    DsAddSrc2U64,
    DsAddU32,
    DsAddU64,
    DsAndB32,
    DsAndB64,
    DsAndRtnB32,
    DsAndRtnB64,
    DsAndSrc2B32,
    DsAndSrc2B64,
    DsAppend,
    DsAtomicAsyncBarrierArriveB64,
    DsAtomicBarrierArriveRtnB64,
    DsBpermuteB32,
    DsBpermuteFiB32,
    DsBvhStackPush4Pop1RtnB32,
    DsBvhStackPush8Pop1RtnB32,
    DsBvhStackPush8Pop2RtnB64,
    DsBvhStackRtnB32,
    DsCmpstB32,
    DsCmpstB64,
    DsCmpstF32,
    DsCmpstF64,
    DsCmpstRtnB32,
    DsCmpstRtnB64,
    DsCmpstRtnF32,
    DsCmpstRtnF64,
    DsCmpstoreB32
  )
}
