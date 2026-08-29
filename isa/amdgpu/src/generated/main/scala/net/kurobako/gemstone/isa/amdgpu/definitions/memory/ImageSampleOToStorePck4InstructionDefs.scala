// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object ImageSampleOToStorePck4InstructionDefs {
  case object ImageSampleO extends InstructionDef(
        "image_sample_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            48,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            48,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            48,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            48,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            48,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            48,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            48,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            48,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            48,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VSAMPLE",
            37,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            48,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            48,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            48,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            48,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageStore extends InstructionDef(
        "image_store",
        decoder(form(3)((operands, mods) => Instruction.ImageStore(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            6,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            6,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            6,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            6,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            6,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            8,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            8,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            8,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            8,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageStoreBy2 extends InstructionDef(
        "image_store_by2",
        decoder(form(3)((operands, mods) => Instruction.ImageStoreBy2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            82,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            82,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            82,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            82,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            82,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            82,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            82,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            82,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            83,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            83,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            83,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            83,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            83,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            83,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            83,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            83,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            7,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            7,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            7,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            7,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            7,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            9,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            9,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            9,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            9,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageStoreMipBy2 extends InstructionDef(
        "image_store_mip_by2",
        decoder(form(3)((operands, mods) => Instruction.ImageStoreMipBy2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            90,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            90,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            90,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            90,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            90,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            90,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            90,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            90,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            91,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            91,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            91,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            91,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            91,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            91,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            91,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            91,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            9,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            11,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            11,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            11,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            11,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            121,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            121,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            121,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            121,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            121,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            121,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            121,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            121,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            122,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            122,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            122,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            122,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            122,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            122,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            122,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            122,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            8,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            10,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            10,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            10,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            10,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageStorePck2 extends InstructionDef(
        "image_store_pck2",
        decoder(form(3)((operands, mods) => Instruction.ImageStorePck2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            118,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            118,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            118,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            118,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            118,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            118,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            118,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            118,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            119,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            119,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            119,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            119,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            119,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            119,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            119,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            119,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(
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
    ImageStorePck4
  )
}
