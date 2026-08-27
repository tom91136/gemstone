// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

private[common] object InstructionDefs8 {
  case object ImageSampleD extends InstructionDef(
        "image_sample_d",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleD(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            34,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            34,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            34,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            34,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            34,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            34,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            34,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            34,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            34,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            28,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            28,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            28,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            28,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSAMPLE",
            28,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 288, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MIMG",
            34,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            34,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            34,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            34,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleDCl extends InstructionDef(
        "image_sample_d_cl",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            35,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            35,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            35,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            35,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            35,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            35,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            35,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            35,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            35,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            65,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            65,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            65,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            65,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSAMPLE",
            65,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 320, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MIMG",
            35,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            35,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            35,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            35,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleDClG16 extends InstructionDef(
        "image_sample_d_cl_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDClG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            163,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            163,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            163,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            163,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            163,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            163,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            163,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            163,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            95,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            95,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            95,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            95,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSAMPLE",
            95,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 256, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSampleDClO extends InstructionDef(
        "image_sample_d_cl_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            51,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            71,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            71,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            71,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            71,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSAMPLE",
            71,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 352, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MIMG",
            51,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            51,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            51,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            51,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleDClOG16 extends InstructionDef(
        "image_sample_d_cl_o_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDClOG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            179,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            179,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            179,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            179,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            179,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            179,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            179,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            179,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            85,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            85,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            85,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            85,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSAMPLE",
            85,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 288, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSampleDG16 extends InstructionDef(
        "image_sample_d_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            162,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            162,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            162,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            162,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            162,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            162,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            162,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            162,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            57,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            57,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            57,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            57,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSAMPLE",
            57,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 224, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSampleDO extends InstructionDef(
        "image_sample_d_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            50,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            50,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            50,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            50,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            50,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            50,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            50,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            50,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            50,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            38,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            38,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            38,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            38,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSAMPLE",
            38,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 320, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MIMG",
            50,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            50,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            50,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            50,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleDOG16 extends InstructionDef(
        "image_sample_d_o_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDOG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            178,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            178,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            178,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            178,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            178,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            178,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            178,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            178,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            59,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            59,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            59,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            59,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSAMPLE",
            59,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 256, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSampleL extends InstructionDef(
        "image_sample_l",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleL(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            36,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            36,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            36,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            36,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            36,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            36,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            36,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            36,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            36,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            29,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            29,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            29,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            29,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSAMPLE",
            29,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MIMG",
            36,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            36,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            36,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            36,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleLO extends InstructionDef(
        "image_sample_l_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleLO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            52,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSAMPLE",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MIMG",
            52,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            52,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            52,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            52,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleLz extends InstructionDef(
        "image_sample_lz",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleLz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            39,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            31,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            31,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            31,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            31,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSAMPLE",
            31,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 96, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MIMG",
            39,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            39,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            39,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            39,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleLzO extends InstructionDef(
        "image_sample_lz_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleLzO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            55,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            41,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            41,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            41,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            41,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSAMPLE",
            41,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MIMG",
            55,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            55,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            55,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            55,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleO extends InstructionDef(
        "image_sample_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            48,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            48,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            48,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            48,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            48,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            48,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            48,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            48,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            48,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            37,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            37,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            37,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            37,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VSAMPLE",
            37,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MIMG",
            48,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            48,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            48,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            48,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageStore extends InstructionDef(
        "image_store",
        decoder(form(3)((operands, mods) => Instruction.ImageStore(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MIMG",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            6,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            6,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            6,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            6,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            6,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MIMG",
            8,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            8,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            8,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            8,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageStoreBy2 extends InstructionDef(
        "image_store_by2",
        decoder(form(3)((operands, mods) => Instruction.ImageStoreBy2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            82,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageStoreBy4 extends InstructionDef(
        "image_store_by4",
        decoder(form(3)((operands, mods) => Instruction.ImageStoreBy4(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            83,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            83,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            83,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            83,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            83,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            83,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            83,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            83,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageStoreMip extends InstructionDef(
        "image_store_mip",
        decoder(form(3)((operands, mods) => Instruction.ImageStoreMip(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MIMG",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            7,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            7,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            7,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            7,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            7,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MIMG",
            9,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            9,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            9,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            9,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageStoreMipBy2 extends InstructionDef(
        "image_store_mip_by2",
        decoder(form(3)((operands, mods) => Instruction.ImageStoreMipBy2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            90,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            90,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            90,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            90,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            90,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            90,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            90,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            90,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageStoreMipBy4 extends InstructionDef(
        "image_store_mip_by4",
        decoder(form(3)((operands, mods) => Instruction.ImageStoreMipBy4(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            91,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            91,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            91,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            91,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            91,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            91,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            91,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            91,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageStoreMipPck extends InstructionDef(
        "image_store_mip_pck",
        decoder(form(3)((operands, mods) => Instruction.ImageStoreMipPck(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MIMG",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            11,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            9,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MIMG",
            11,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            11,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            11,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            11,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageStoreMipPck2 extends InstructionDef(
        "image_store_mip_pck2",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageStoreMipPck2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            121,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            121,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            121,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            121,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            121,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            121,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            121,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            121,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageStoreMipPck4 extends InstructionDef(
        "image_store_mip_pck4",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageStoreMipPck4(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            122,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            122,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            122,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            122,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            122,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            122,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            122,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            122,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageStorePck extends InstructionDef(
        "image_store_pck",
        decoder(form(3)((operands, mods) => Instruction.ImageStorePck(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "MIMG",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            10,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            8,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "MIMG",
            10,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            10,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            10,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            10,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageStorePck2 extends InstructionDef(
        "image_store_pck2",
        decoder(form(3)((operands, mods) => Instruction.ImageStorePck2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            118,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            118,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            118,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            118,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            118,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            118,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            118,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            118,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageStorePck4 extends InstructionDef(
        "image_store_pck4",
        decoder(form(3)((operands, mods) => Instruction.ImageStorePck4(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            119,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            119,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            119,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            119,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG",
            119,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            119,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            119,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            119,
            Vector(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object LdsDirectLoad extends InstructionDef(
        "lds_direct_load",
        decoder(form(1)((operands, mods) => Instruction.LdsDirectLoad(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "LDSDIR",
            1,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "LDSDIR",
            1,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object LdsParamLoad extends InstructionDef(
        "lds_param_load",
        decoder(form(2)((operands, mods) => Instruction.LdsParamLoad(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX11,
            "rdna3",
            "LDSDIR",
            0,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ATTR"), "ATTR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "LDSDIR",
            0,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ATTR"), "ATTR", 32, true, false, false),
              OperandInfo(None, "DSMEM", 32, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAbsI32 extends InstructionDef(
        "s_abs_i32",
        decoder(form(2)((operands, mods) => Instruction.SAbsI32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            48,
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
            48,
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
            48,
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
            48,
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
            48,
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
            48,
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
            48,
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
            48,
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
            21,
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
            21,
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
            21,
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
            52,
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
            52,
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
            52,
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
            52,
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
            21,
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
            21,
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
            21,
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
            21,
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
            21,
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
            21,
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
            52,
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
            52,
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
            48,
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
            48,
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
  case object SAbsdiffI32 extends InstructionDef(
        "s_absdiff_i32",
        decoder(form(3)((operands, mods) => Instruction.SAbsdiffI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            42,
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
            6,
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
            6,
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
            6,
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
            6,
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
            6,
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
            6,
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
            6,
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
            44,
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
            44,
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
            44,
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
            44,
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
            44,
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
            44,
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
            44,
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
            44,
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
            6,
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
            6,
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
            6,
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
            6,
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
            6,
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
            6,
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
            6,
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
            6,
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
            6,
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
            6,
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
            6,
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
            6,
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
            44,
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
            44,
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
            42,
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
            42,
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
  case object SAddCoCiU32 extends InstructionDef(
        "s_add_co_ci_u32",
        decoder(form(3)((operands, mods) => Instruction.SAddCoCiU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
  case object SAddCoI32 extends InstructionDef(
        "s_add_co_i32",
        decoder(form(3)((operands, mods) => Instruction.SAddCoI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
  case object SAddCoU32 extends InstructionDef(
        "s_add_co_u32",
        decoder(form(3)((operands, mods) => Instruction.SAddCoU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
  case object SAddF16 extends InstructionDef(
        "s_add_f16",
        decoder(form(3)((operands, mods) => Instruction.SAddF16(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
            73,
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
  case object SAddF32 extends InstructionDef(
        "s_add_f32",
        decoder(form(3)((operands, mods) => Instruction.SAddF32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
  case object SAddI32 extends InstructionDef(
        "s_add_i32",
        decoder(form(3)((operands, mods) => Instruction.SAddI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
            2,
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
  case object SAddNcU64 extends InstructionDef(
        "s_add_nc_u64",
        decoder(form(3)((operands, mods) => Instruction.SAddNcU64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            83,
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
            83,
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
            83,
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
            83,
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
            83,
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
            83,
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
            83,
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
            83,
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
            83,
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
            83,
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
            83,
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
  case object SAddPcI64 extends InstructionDef(
        "s_add_pc_i64",
        decoder(form(1)((operands, mods) => Instruction.SAddPcI64(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            75,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL",
            75,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1_INST_LITERAL64",
            75,
            Vector(
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
              OperandInfo(None, "PC", 64, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAddU32 extends InstructionDef(
        "s_add_u32",
        decoder(form(3)((operands, mods) => Instruction.SAddU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
            0,
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
  case object SAddcU32 extends InstructionDef(
        "s_addc_u32",
        decoder(form(3)((operands, mods) => Instruction.SAddcU32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
            4,
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
  case object SAddkCoI32 extends InstructionDef(
        "s_addk_co_i32",
        decoder(form(2)((operands, mods) => Instruction.SAddkCoI32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPK",
            15,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOPK",
            15,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAddkI32 extends InstructionDef(
        "s_addk_i32",
        decoder(form(2)((operands, mods) => Instruction.SAddkI32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPK",
            14,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPK",
            14,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPK",
            14,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPK",
            14,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPK",
            15,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPK",
            15,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPK",
            15,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPK",
            15,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 32, false, true, false),
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPK",
            15,
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
            "SOPK",
            15,
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
            "SOPK",
            14,
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
            "SOPK",
            14,
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
  case object SAllocVgpr extends InstructionDef(
        "s_alloc_vgpr",
        decoder(form(1)((operands, mods) => Instruction.SAllocVgpr(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            83,
            Vector(
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
            83,
            Vector(
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
            83,
            Vector(
              OperandInfo(Some("LITERAL64"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SOP1",
            83,
            Vector(
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
            83,
            Vector(
              OperandInfo(Some("LITERAL"), "SSRC", 32, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndB32 extends InstructionDef(
        "s_and_b32",
        decoder(form(3)((operands, mods) => Instruction.SAndB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            22,
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
            22,
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
            22,
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
            22,
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
            22,
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
            22,
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
            22,
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
            14,
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
            14,
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
            14,
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
            14,
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
            14,
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
            14,
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
            14,
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
            14,
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
            22,
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
            22,
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
            22,
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
            22,
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
            22,
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
            22,
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
            22,
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
            22,
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
            22,
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
            22,
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
            22,
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
            22,
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
            14,
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
            14,
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
            12,
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
            12,
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
  case object SAndB64 extends InstructionDef(
        "s_and_b64",
        decoder(form(3)((operands, mods) => Instruction.SAndB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            13,
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
            13,
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
            13,
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
            13,
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
            13,
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
            13,
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
            13,
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
            13,
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
            13,
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
            13,
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
            13,
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
            13,
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
            13,
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
            13,
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
            13,
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
            13,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            15,
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
            15,
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
            15,
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
            15,
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
            15,
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
            15,
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
            15,
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
            15,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            23,
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
            15,
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
            15,
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
            13,
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
            13,
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
  case object SAndNot0SaveexecB32 extends InstructionDef(
        "s_and_not0_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndNot0SaveexecB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            44,
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
            44,
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
            44,
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
            Generation.GFX11,
            "rdna3",
            "SOP1",
            44,
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
            44,
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
            44,
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
            44,
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
            44,
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
            44,
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
  case object SAndNot0SaveexecB64 extends InstructionDef(
        "s_and_not0_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndNot0SaveexecB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            45,
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
            45,
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
            45,
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
            Generation.GFX11,
            "rdna3",
            "SOP1",
            45,
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
            45,
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
            45,
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
            45,
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
            45,
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
            45,
            Vector(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndNot0WrexecB32 extends InstructionDef(
        "s_and_not0_wrexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndNot0WrexecB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            52,
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
            52,
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
            52,
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
            Generation.GFX11,
            "rdna3",
            "SOP1",
            52,
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
            52,
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
            52,
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
            52,
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
            52,
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
            52,
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
  case object SAndNot0WrexecB64 extends InstructionDef(
        "s_and_not0_wrexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndNot0WrexecB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            53,
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
            53,
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
            53,
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
            Generation.GFX11,
            "rdna3",
            "SOP1",
            53,
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
            53,
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
            53,
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
            53,
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
            53,
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
            53,
            Vector(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndNot1B32 extends InstructionDef(
        "s_and_not1_b32",
        decoder(form(3)((operands, mods) => Instruction.SAndNot1B32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            Generation.GFX11,
            "rdna3",
            "SOP2",
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
  case object SAndNot1B64 extends InstructionDef(
        "s_and_not1_b64",
        decoder(form(3)((operands, mods) => Instruction.SAndNot1B64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP2",
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            Generation.GFX11,
            "rdna3",
            "SOP2",
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndNot1SaveexecB32 extends InstructionDef(
        "s_and_not1_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndNot1SaveexecB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            48,
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
            48,
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
            48,
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
            Generation.GFX11,
            "rdna3",
            "SOP1",
            48,
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
            48,
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
            48,
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
            48,
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
            48,
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
            48,
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
  case object SAndNot1SaveexecB64 extends InstructionDef(
        "s_and_not1_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndNot1SaveexecB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            49,
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
            49,
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
            49,
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
            Generation.GFX11,
            "rdna3",
            "SOP1",
            49,
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
            49,
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
            49,
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
            49,
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
            49,
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
            49,
            Vector(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndNot1WrexecB32 extends InstructionDef(
        "s_and_not1_wrexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndNot1WrexecB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            54,
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
            54,
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
            54,
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
            Generation.GFX11,
            "rdna3",
            "SOP1",
            54,
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
            54,
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
            54,
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
            54,
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
            54,
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
            54,
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
  case object SAndNot1WrexecB64 extends InstructionDef(
        "s_and_not1_wrexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndNot1WrexecB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            55,
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
            55,
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
            55,
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
            Generation.GFX11,
            "rdna3",
            "SOP1",
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
            Vector(
              OperandInfo(Some("SDST"), "SREG", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndSaveexecB32 extends InstructionDef(
        "s_and_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndSaveexecB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            32,
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
            32,
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
            32,
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
            60,
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
            60,
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
            60,
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
            60,
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
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
  case object SAndSaveexecB64 extends InstructionDef(
        "s_and_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndSaveexecB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
            33,
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
            33,
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
            33,
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
            36,
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
            36,
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
            36,
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
            36,
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
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
            36,
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
            36,
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
            32,
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
            32,
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
  case object SAndn1SaveexecB32 extends InstructionDef(
        "s_andn1_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndn1SaveexecB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            68,
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
            68,
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
            68,
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
            68,
            Vector(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndn1SaveexecB64 extends InstructionDef(
        "s_andn1_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndn1SaveexecB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            51,
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
            51,
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
            51,
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
            51,
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
            51,
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
            51,
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
            51,
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
            51,
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
            Generation.GFX10,
            "rdna1",
            "SOP1",
            55,
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
            55,
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
            55,
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
            55,
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
            Generation.GFX9,
            "gfx9",
            "SOP1",
            51,
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
  case object SAndn1WrexecB32 extends InstructionDef(
        "s_andn1_wrexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndn1WrexecB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            70,
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
            70,
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
            70,
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
            70,
            Vector(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndn1WrexecB64 extends InstructionDef(
        "s_andn1_wrexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndn1WrexecB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            53,
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
            53,
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
            53,
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
            53,
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
            53,
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
            53,
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
            53,
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
            53,
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
            Generation.GFX10,
            "rdna1",
            "SOP1",
            57,
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
            57,
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
            57,
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
            57,
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
            Generation.GFX9,
            "gfx9",
            "SOP1",
            53,
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
  case object SAndn2B32 extends InstructionDef(
        "s_andn2_b32",
        decoder(form(3)((operands, mods) => Instruction.SAndn2B32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            18,
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
            18,
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
            18,
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
            18,
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
            18,
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
            18,
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
            18,
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
            18,
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
            18,
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
            18,
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
            18,
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
            18,
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
            18,
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
            18,
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
            18,
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
            18,
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
            20,
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
            20,
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
            20,
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
            20,
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
            20,
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
            20,
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
            20,
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
            20,
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
            Generation.GFX6,
            "gfx6",
            "SOP2",
            20,
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
            20,
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
            Generation.GFX9,
            "gfx9",
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
          )
        ))
      )
  case object SAndn2B64 extends InstructionDef(
        "s_andn2_b64",
        decoder(form(3)((operands, mods) => Instruction.SAndn2B64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            19,
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
            19,
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
            19,
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
            19,
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
            19,
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
            19,
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
            19,
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
            19,
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
            19,
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
            19,
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
            19,
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
            19,
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
            19,
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
            19,
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
            19,
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
            19,
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
            Generation.GFX10,
            "rdna1",
            "SOP2",
            21,
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
            21,
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
            21,
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
            21,
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
            21,
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
            21,
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
            21,
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
            21,
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
            Generation.GFX6,
            "gfx6",
            "SOP2",
            21,
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
            21,
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
            Generation.GFX9,
            "gfx9",
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
          )
        ))
      )
  case object SAndn2SaveexecB32 extends InstructionDef(
        "s_andn2_saveexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndn2SaveexecB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            63,
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
            63,
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
            63,
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
            63,
            Vector(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndn2SaveexecB64 extends InstructionDef(
        "s_andn2_saveexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndn2SaveexecB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            35,
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
            Generation.GFX10,
            "rdna1",
            "SOP1",
            39,
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
            39,
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
            39,
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
            39,
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
            Generation.GFX6,
            "gfx6",
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
            Generation.GFX7,
            "gfx7",
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
            Generation.GFX8,
            "gfx8",
            "SOP1",
            35,
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
            35,
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
  case object SAndn2WrexecB32 extends InstructionDef(
        "s_andn2_wrexec_b32",
        decoder(form(2)((operands, mods) => Instruction.SAndn2WrexecB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOP1",
            71,
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
            71,
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
            71,
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
            71,
            Vector(
              OperandInfo(Some("SDST"), "SREG", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 32, true, false, false),
              OperandInfo(None, "SDST_EXEC", 64, false, true, true),
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true),
              OperandInfo(None, "SDST_EXEC", 64, true, false, true)
            ),
            Some("SALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SAndn2WrexecB64 extends InstructionDef(
        "s_andn2_wrexec_b64",
        decoder(form(2)((operands, mods) => Instruction.SAndn2WrexecB64(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP1",
            54,
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
            54,
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
            54,
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
            54,
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
            54,
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
            54,
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
            54,
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
            54,
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
            Generation.GFX10,
            "rdna1",
            "SOP1",
            58,
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
            58,
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
            58,
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
            58,
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
            Generation.GFX9,
            "gfx9",
            "SOP1",
            54,
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
  case object SAshrI32 extends InstructionDef(
        "s_ashr_i32",
        decoder(form(3)((operands, mods) => Instruction.SAshrI32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA1,
            "cdna1",
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
            Generation.CDNA2,
            "cdna2",
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
            Generation.CDNA2,
            "cdna2",
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
            Generation.CDNA2,
            "cdna2",
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
            Generation.CDNA2,
            "cdna2",
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
            Generation.CDNA3,
            "cdna3",
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
            Generation.CDNA3,
            "cdna3",
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
            Generation.CDNA3,
            "cdna3",
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
            Generation.CDNA3,
            "cdna3",
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
            Generation.CDNA4,
            "cdna4",
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
            Generation.CDNA4,
            "cdna4",
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
            Generation.CDNA4,
            "cdna4",
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
            Generation.CDNA4,
            "cdna4",
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
            "SOP2",
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            34,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            12,
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
            34,
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
            34,
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
            32,
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
            32,
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
  case object SAshrI64 extends InstructionDef(
        "s_ashr_i64",
        decoder(form(3)((operands, mods) => Instruction.SAshrI64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOP2",
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            33,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL64"), "SSRC", 64, true, false, false),
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
            35,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
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
            35,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
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
            35,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
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
            35,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
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
            35,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
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
            35,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
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
            35,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC_NOLDS", 64, true, false, false),
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
            35,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC_NOLDS", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("SSRC0"), "SSRC", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("SDST"), "SDST", 64, false, true, false),
              OperandInfo(Some("LITERAL"), "SSRC", 64, true, false, false),
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
            35,
            Vector(
              OperandInfo(None, "int", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOP2",
            35,
            Vector(
              OperandInfo(None, "int", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOP2",
            33,
            Vector(
              OperandInfo(None, "int", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOP2",
            33,
            Vector(
              OperandInfo(None, "int", 64, false, true, false),
              OperandInfo(None, "uint", 32, false, true, true),
              OperandInfo(None, "int", 64, true, false, false),
              OperandInfo(None, "uint", 32, true, false, false)
            ),
            Some("salu"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtcProbe extends InstructionDef(
        "s_atc_probe",
        decoder(form(3)((operands, mods) => Instruction.SAtcProbe(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            38,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            38,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            38,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            38,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            34,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SMEM",
            38,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SMEM",
            34,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            34,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            38,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            38,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtcProbeBuffer extends InstructionDef(
        "s_atc_probe_buffer",
        decoder(form(3)((operands, mods) => Instruction.SAtcProbeBuffer(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            39,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            39,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            39,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            39,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SMEM",
            35,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SMEM",
            39,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG_NONULL", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SMEM",
            35,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "SMEM",
            35,
            Vector(
              OperandInfo(Some("SDATA"), "SIMM8", 8, true, false, false),
              OperandInfo(Some("SBASE"), "SREG", 128, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SMEM",
            39,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            39,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicAdd extends InstructionDef(
        "s_atomic_add",
        decoder(form(3)((operands, mods) => Instruction.SAtomicAdd(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            130,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            130,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            130,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            130,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            130,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicAddX2 extends InstructionDef(
        "s_atomic_add_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicAddX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            162,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            162,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            162,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            162,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            162,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicAnd extends InstructionDef(
        "s_atomic_and",
        decoder(form(3)((operands, mods) => Instruction.SAtomicAnd(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            136,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            136,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            136,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            136,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            136,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicAndX2 extends InstructionDef(
        "s_atomic_and_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicAndX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            168,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            168,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            168,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            168,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            168,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicCmpswap extends InstructionDef(
        "s_atomic_cmpswap",
        decoder(form(3)((operands, mods) => Instruction.SAtomicCmpswap(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            129,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            129,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            129,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            129,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            129,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicCmpswapX2 extends InstructionDef(
        "s_atomic_cmpswap_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicCmpswapX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            161,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            161,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            161,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            161,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 128, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            161,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicDec extends InstructionDef(
        "s_atomic_dec",
        decoder(form(3)((operands, mods) => Instruction.SAtomicDec(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            140,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            140,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            140,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            140,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            140,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicDecX2 extends InstructionDef(
        "s_atomic_dec_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicDecX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            172,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            172,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            172,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            172,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            172,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicInc extends InstructionDef(
        "s_atomic_inc",
        decoder(form(3)((operands, mods) => Instruction.SAtomicInc(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            139,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            139,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            139,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            139,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            139,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicIncX2 extends InstructionDef(
        "s_atomic_inc_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicIncX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            171,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            171,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            171,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            171,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            171,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicOr extends InstructionDef(
        "s_atomic_or",
        decoder(form(3)((operands, mods) => Instruction.SAtomicOr(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            137,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            137,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            137,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            137,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            137,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicOrX2 extends InstructionDef(
        "s_atomic_or_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicOrX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            169,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            169,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            169,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            169,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            169,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSmax extends InstructionDef(
        "s_atomic_smax",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSmax(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            134,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            134,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            134,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            134,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            134,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSmaxX2 extends InstructionDef(
        "s_atomic_smax_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSmaxX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            166,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            166,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            166,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            166,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            166,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSmin extends InstructionDef(
        "s_atomic_smin",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSmin(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            132,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            132,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            132,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            132,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            132,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSminX2 extends InstructionDef(
        "s_atomic_smin_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSminX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            164,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            164,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            164,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            164,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            164,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSub extends InstructionDef(
        "s_atomic_sub",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSub(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            131,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            131,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            131,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            131,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            131,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSubX2 extends InstructionDef(
        "s_atomic_sub_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSubX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            163,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            163,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            163,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            163,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            163,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSwap extends InstructionDef(
        "s_atomic_swap",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSwap(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            128,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            128,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            128,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            128,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            128,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicSwapX2 extends InstructionDef(
        "s_atomic_swap_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicSwapX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            160,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            160,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            160,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            160,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            160,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicUmax extends InstructionDef(
        "s_atomic_umax",
        decoder(form(3)((operands, mods) => Instruction.SAtomicUmax(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            135,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            135,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            135,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            135,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            135,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicUmaxX2 extends InstructionDef(
        "s_atomic_umax_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicUmaxX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            167,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            167,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            167,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            167,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            167,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicUmin extends InstructionDef(
        "s_atomic_umin",
        decoder(form(3)((operands, mods) => Instruction.SAtomicUmin(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            133,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            133,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            133,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            133,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            133,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicUminX2 extends InstructionDef(
        "s_atomic_umin_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicUminX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            165,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            165,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            165,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            165,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            165,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicXor extends InstructionDef(
        "s_atomic_xor",
        decoder(form(3)((operands, mods) => Instruction.SAtomicXor(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            138,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            138,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            138,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            138,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 32, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            138,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SAtomicXorX2 extends InstructionDef(
        "s_atomic_xor_x2",
        decoder(form(3)((operands, mods) => Instruction.SAtomicXorX2(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SMEM",
            170,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SMEM",
            170,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SMEM",
            170,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SMEM",
            170,
            Vector(
              OperandInfo(Some("SDATA"), "SREG", 64, false, true, false),
              OperandInfo(Some("SBASE"), "SREG", 64, true, false, false),
              OperandInfo(Some("SOFFSET"), "SMEM_OFFSET", 32, true, false, false)
            ),
            Some("SMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SMEM",
            170,
            Vector.empty,
            Some("smem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBarrier extends InstructionDef(
        "s_barrier",
        decoder(form(0)((_, mods) => Instruction.SBarrier(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "SOPP",
            10,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "SOPP",
            10,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "SOPP",
            10,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "SOPP",
            10,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "SOPP",
            10,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "SOPP",
            10,
            Vector.empty,
            Some("SALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "SOPP",
            61,
            Vector.empty,
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "SOPP",
            61,
            Vector.empty,
            Some("BRANCH"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "SOPP",
            10,
            Vector.empty,
            Some("barrier"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "SOPP",
            10,
            Vector.empty,
            Some("barrier"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "SOPP",
            10,
            Vector.empty,
            Some("barrier"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "SOPP",
            10,
            Vector.empty,
            Some("barrier"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object SBarrierInit extends InstructionDef(
        "s_barrier_init",
        decoder(form(1)((operands, mods) => Instruction.SBarrierInit(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            81,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC_BARRIER_ID", 32, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBarrierJoin extends InstructionDef(
        "s_barrier_join",
        decoder(form(1)((operands, mods) => Instruction.SBarrierJoin(operands(0), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOP1",
            82,
            Vector(
              OperandInfo(Some("SSRC0"), "SSRC_BARRIER_ID", 32, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object SBarrierLeave extends InstructionDef(
        "s_barrier_leave",
        decoder(form(0)((_, mods) => Instruction.SBarrierLeave(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "SOPP",
            21,
            Vector(
              OperandInfo(None, "SSRC_SPECIAL_SCC", 1, false, true, true)
            ),
            Some("MESSAGE"),
            MetadataSource.AMDXML
          )
        ))
      )

  val definitions: Vector[InstructionDef] = Vector(
    ImageSampleD,
    ImageSampleDCl,
    ImageSampleDClG16,
    ImageSampleDClO,
    ImageSampleDClOG16,
    ImageSampleDG16,
    ImageSampleDO,
    ImageSampleDOG16,
    ImageSampleL,
    ImageSampleLO,
    ImageSampleLz,
    ImageSampleLzO,
    ImageSampleO,
    ImageStore,
    ImageStoreBy2,
    ImageStoreBy4,
    ImageStoreMip,
    ImageStoreMipBy2,
    ImageStoreMipBy4,
    ImageStoreMipPck,
    ImageStoreMipPck2,
    ImageStoreMipPck4,
    ImageStorePck,
    ImageStorePck2,
    ImageStorePck4,
    LdsDirectLoad,
    LdsParamLoad,
    SAbsI32,
    SAbsdiffI32,
    SAddCoCiU32,
    SAddCoI32,
    SAddCoU32,
    SAddF16,
    SAddF32,
    SAddI32,
    SAddNcU64,
    SAddPcI64,
    SAddU32,
    SAddcU32,
    SAddkCoI32,
    SAddkI32,
    SAllocVgpr,
    SAndB32,
    SAndB64,
    SAndNot0SaveexecB32,
    SAndNot0SaveexecB64,
    SAndNot0WrexecB32,
    SAndNot0WrexecB64,
    SAndNot1B32,
    SAndNot1B64,
    SAndNot1SaveexecB32,
    SAndNot1SaveexecB64,
    SAndNot1WrexecB32,
    SAndNot1WrexecB64,
    SAndSaveexecB32,
    SAndSaveexecB64,
    SAndn1SaveexecB32,
    SAndn1SaveexecB64,
    SAndn1WrexecB32,
    SAndn1WrexecB64,
    SAndn2B32,
    SAndn2B64,
    SAndn2SaveexecB32,
    SAndn2SaveexecB64,
    SAndn2WrexecB32,
    SAndn2WrexecB64,
    SAshrI32,
    SAshrI64,
    SAtcProbe,
    SAtcProbeBuffer,
    SAtomicAdd,
    SAtomicAddX2,
    SAtomicAnd,
    SAtomicAndX2,
    SAtomicCmpswap,
    SAtomicCmpswapX2,
    SAtomicDec,
    SAtomicDecX2,
    SAtomicInc,
    SAtomicIncX2,
    SAtomicOr,
    SAtomicOrX2,
    SAtomicSmax,
    SAtomicSmaxX2,
    SAtomicSmin,
    SAtomicSminX2,
    SAtomicSub,
    SAtomicSubX2,
    SAtomicSwap,
    SAtomicSwapX2,
    SAtomicUmax,
    SAtomicUmaxX2,
    SAtomicUmin,
    SAtomicUminX2,
    SAtomicXor,
    SAtomicXorX2,
    SBarrier,
    SBarrierInit,
    SBarrierJoin,
    SBarrierLeave
  )
}
