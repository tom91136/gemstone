// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object ImageAtomicAddToSampleLzOInstructionDefs {
  case object ImageAtomicAdd extends InstructionDef(
        "image_atomic_add",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicAdd(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            12,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            12,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            12,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            12,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            17,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            17,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            18,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            18,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicAddFlt extends InstructionDef(
        "image_atomic_add_flt",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicAddFlt(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            131,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicAddUint extends InstructionDef(
        "image_atomic_add_uint",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicAddUint(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            12,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicAnd extends InstructionDef(
        "image_atomic_and",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicAnd(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            24,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            24,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            24,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            24,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicCmpswap extends InstructionDef(
        "image_atomic_cmpswap",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicCmpswap(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            11,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            16,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            16,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            17,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            17,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicDec extends InstructionDef(
        "image_atomic_dec",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicDec(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            28,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            28,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            28,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            28,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicDecUint extends InstructionDef(
        "image_atomic_dec_uint",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicDecUint(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicFcmpswap extends InstructionDef(
        "image_atomic_fcmpswap",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicFcmpswap(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            29,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            29,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            29,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            29,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            29,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            29,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            29,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            29,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            29,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            29,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicFmax extends InstructionDef(
        "image_atomic_fmax",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicFmax(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            31,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            31,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            31,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            31,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            31,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            31,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            31,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            31,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            31,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            31,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicFmin extends InstructionDef(
        "image_atomic_fmin",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicFmin(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            30,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            30,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            30,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            30,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            30,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            30,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            30,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            30,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            30,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            30,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicInc extends InstructionDef(
        "image_atomic_inc",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicInc(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            27,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            27,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            27,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            27,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicIncUint extends InstructionDef(
        "image_atomic_inc_uint",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicIncUint(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicMaxFlt extends InstructionDef(
        "image_atomic_max_flt",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicMaxFlt(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            133,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicMaxInt extends InstructionDef(
        "image_atomic_max_int",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicMaxInt(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicMaxUint extends InstructionDef(
        "image_atomic_max_uint",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicMaxUint(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicMinFlt extends InstructionDef(
        "image_atomic_min_flt",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicMinFlt(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            132,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicMinInt extends InstructionDef(
        "image_atomic_min_int",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicMinInt(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicMinUint extends InstructionDef(
        "image_atomic_min_uint",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicMinUint(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            15,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicOr extends InstructionDef(
        "image_atomic_or",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicOr(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            19,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            19,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            19,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            19,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            19,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            25,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            25,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            25,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            25,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicPkAddBf16 extends InstructionDef(
        "image_atomic_pk_add_bf16",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicPkAddBf16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            135,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicPkAddF16 extends InstructionDef(
        "image_atomic_pk_add_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicPkAddF16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            134,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicRsub extends InstructionDef(
        "image_atomic_rsub",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicRsub(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            19,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicSmax extends InstructionDef(
        "image_atomic_smax",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicSmax(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            22,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            22,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            22,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            22,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            22,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicSmin extends InstructionDef(
        "image_atomic_smin",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicSmin(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            20,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            20,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            20,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            20,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicSub extends InstructionDef(
        "image_atomic_sub",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicSub(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            19,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            19,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            18,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            13,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            13,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            13,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            13,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            18,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            18,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            19,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            19,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicSubUint extends InstructionDef(
        "image_atomic_sub_uint",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicSubUint(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            13,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicSwap extends InstructionDef(
        "image_atomic_swap",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicSwap(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            16,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            15,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            15,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            15,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            15,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            15,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            15,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            15,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            15,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            10,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            15,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            15,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            16,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            16,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicUmax extends InstructionDef(
        "image_atomic_umax",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicUmax(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            17,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            23,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            23,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            23,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            23,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicUmin extends InstructionDef(
        "image_atomic_umin",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicUmin(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            21,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            15,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            15,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            15,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            15,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            21,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            21,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            21,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            21,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicXor extends InstructionDef(
        "image_atomic_xor",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicXor(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            20,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            26,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            26,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            26,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            26,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageBvh64IntersectRay extends InstructionDef(
        "image_bvh64_intersect_ray",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageBvh64IntersectRay(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            231,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            231,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            231,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            231,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            26,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 384, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageBvh8IntersectRay extends InstructionDef(
        "image_bvh8_intersect_ray",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageBvh8IntersectRay(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            129,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 320, false, true, false),
              OperandInfo(None, "VGPR", 352, false, true, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageBvhDualIntersectRay extends InstructionDef(
        "image_bvh_dual_intersect_ray",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageBvhDualIntersectRay(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            128,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 320, false, true, false),
              OperandInfo(None, "VGPR", 384, false, true, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageBvhIntersectRay extends InstructionDef(
        "image_bvh_intersect_ray",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageBvhIntersectRay(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            230,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            230,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            230,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            230,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            25,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 352, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageGather4 extends InstructionDef(
        "image_gather4",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            64,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            64,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            64,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            64,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            64,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            64,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            64,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            64,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            64,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            47,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            47,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            47,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            47,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            47,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 96, true, false, false),
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
            64,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            64,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            64,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            64,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4B extends InstructionDef(
        "image_gather4_b",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4B(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            69,
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
            69,
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
            69,
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
            69,
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
            69,
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
            69,
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
            69,
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
            69,
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
            69,
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
            49,
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
            49,
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
            49,
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
            49,
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
            49,
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
            69,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            69,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            69,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            69,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4BCl extends InstructionDef(
        "image_gather4_b_cl",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4BCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            97,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            97,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            97,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            97,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            97,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            70,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            70,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            70,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            70,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4BClO extends InstructionDef(
        "image_gather4_b_cl_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4BClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            86,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            86,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            86,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            86,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4BO extends InstructionDef(
        "image_gather4_b_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4BO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            85,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            85,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            85,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            85,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            85,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            85,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            85,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            85,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            85,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            85,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            85,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            85,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            85,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4C extends InstructionDef(
        "image_gather4_c",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4C(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            72,
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
            72,
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
            72,
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
            72,
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
            72,
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
            72,
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
            72,
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
            72,
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
            72,
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
            51,
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
            51,
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
            51,
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
            51,
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
            51,
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
            72,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            72,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            72,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            72,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4CB extends InstructionDef(
        "image_gather4_c_b",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CB(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            77,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            77,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            77,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            77,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            77,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            77,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            77,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            77,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            77,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            100,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            100,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            100,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            100,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            100,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            77,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            77,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            77,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            77,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4CBCl extends InstructionDef(
        "image_gather4_c_b_cl",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CBCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            78,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            78,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            78,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            78,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            78,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            78,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            78,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            78,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            78,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            101,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            101,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            101,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            101,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            101,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 192, true, false, false),
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
            78,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            78,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            78,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            78,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4CBClO extends InstructionDef(
        "image_gather4_c_b_cl_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CBClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            94,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            94,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            94,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            94,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            94,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            94,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            94,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            94,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            94,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            94,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            94,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            94,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            94,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4CBO extends InstructionDef(
        "image_gather4_c_b_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CBO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            93,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            93,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            93,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            93,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            93,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            93,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            93,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            93,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            93,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            93,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            93,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            93,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            93,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4CCl extends InstructionDef(
        "image_gather4_c_cl",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            98,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            98,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            98,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            98,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            98,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            73,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            73,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            73,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            73,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4CClO extends InstructionDef(
        "image_gather4_c_cl_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            89,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            89,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            89,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            89,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            89,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            89,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            89,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            89,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            89,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            89,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            89,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            89,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            89,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4CL_1 extends InstructionDef(
        "image_gather4_c_l",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CL_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            76,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            76,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            76,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            76,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            76,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            76,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            76,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            76,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            76,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            99,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            99,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            99,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            99,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            99,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            76,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            76,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            76,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            76,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4CLO_1 extends InstructionDef(
        "image_gather4_c_l_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CLO_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            92,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            92,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            92,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            92,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            92,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            92,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            92,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            92,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            92,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            92,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            92,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            92,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            92,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4CLz extends InstructionDef(
        "image_gather4_c_lz",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CLz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            79,
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
            79,
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
            79,
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
            79,
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
            79,
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
            79,
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
            79,
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
            79,
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
            79,
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
            52,
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
            52,
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
            52,
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
            52,
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
            52,
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
            79,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            79,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            79,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            79,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4CLzO extends InstructionDef(
        "image_gather4_c_lz_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CLzO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            95,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            95,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            95,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            95,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            95,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            95,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            95,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            95,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            95,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            55,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            55,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            55,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            55,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            55,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            95,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            95,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            95,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            95,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4CO extends InstructionDef(
        "image_gather4_c_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            88,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            88,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            88,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            88,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            88,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            88,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            88,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            88,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            88,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            88,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            88,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            88,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            88,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4Cl_2 extends InstructionDef(
        "image_gather4_cl",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4Cl_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            65,
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
            65,
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
            65,
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
            65,
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
            65,
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
            65,
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
            65,
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
            65,
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
            65,
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
            96,
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
            96,
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
            96,
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
            96,
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
            96,
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
            65,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            65,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            65,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            65,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4ClO_2 extends InstructionDef(
        "image_gather4_cl_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4ClO_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            81,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            81,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            81,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            81,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            81,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            81,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            81,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            81,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            81,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            81,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            81,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            81,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            81,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4L extends InstructionDef(
        "image_gather4_l",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4L(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            68,
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
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
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
            Family.GFX11,
            "rdna3_5",
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
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
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
            Family.GFX12,
            "rdna4",
            "VSAMPLE",
            48,
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
            68,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            68,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            68,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            68,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4LO extends InstructionDef(
        "image_gather4_l_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4LO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            84,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            84,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            84,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            84,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            84,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            84,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            84,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            84,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            84,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            84,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            84,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            84,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            84,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4Lz extends InstructionDef(
        "image_gather4_lz",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4Lz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 96, true, false, false),
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
            71,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            71,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            71,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            71,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4LzO extends InstructionDef(
        "image_gather4_lz_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4LzO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            87,
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
            54,
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
            54,
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
            54,
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
            54,
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
            54,
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
            87,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            87,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            87,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            87,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4O extends InstructionDef(
        "image_gather4_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4O(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            80,
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
            80,
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
            80,
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
            80,
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
            80,
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
            80,
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
            80,
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
            80,
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
            80,
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
            53,
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
            53,
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
            53,
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
            53,
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
            53,
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
            80,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            80,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            80,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            80,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGather4h extends InstructionDef(
        "image_gather4h",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4h(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            97,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            97,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            97,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            97,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            97,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            97,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            97,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            97,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            144,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            144,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            144,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            144,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            144,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 96, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageGather4hPck extends InstructionDef(
        "image_gather4h_pck",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4hPck(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            98,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            98,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            98,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            98,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            98,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            98,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            98,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            98,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageGather8hPck extends InstructionDef(
        "image_gather8h_pck",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather8hPck(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            99,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            99,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            99,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            99,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            99,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            99,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            99,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            99,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageGetLod extends InstructionDef(
        "image_get_lod",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGetLod(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            96,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            96,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            96,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            96,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            96,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            96,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            96,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            96,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            96,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 96, true, false, false),
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
            96,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            96,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            96,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            96,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageGetResinfo extends InstructionDef(
        "image_get_resinfo",
        decoder(form(3)((operands, mods) => Instruction.ImageGetResinfo(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA1",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA2",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG_NSA3",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA1",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA2",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "MIMG_NSA3",
            14,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "MIMG_NSA1",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VIMAGE",
            23,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 32, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            14,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            14,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            14,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            14,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageLoad extends InstructionDef(
        "image_load",
        decoder(form(3)((operands, mods) => Instruction.ImageLoad(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            0,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            0,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            0,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            0,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageLoadBy2 extends InstructionDef(
        "image_load_by2",
        decoder(form(3)((operands, mods) => Instruction.ImageLoadBy2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageLoadBy4 extends InstructionDef(
        "image_load_by4",
        decoder(form(3)((operands, mods) => Instruction.ImageLoadBy4(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            67,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            67,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            67,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            67,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            67,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            67,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            67,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            67,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageLoadMip extends InstructionDef(
        "image_load_mip",
        decoder(form(3)((operands, mods) => Instruction.ImageLoadMip(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            1,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            1,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            1,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            1,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageLoadMipBy2 extends InstructionDef(
        "image_load_mip_by2",
        decoder(form(3)((operands, mods) => Instruction.ImageLoadMipBy2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageLoadMipBy4 extends InstructionDef(
        "image_load_mip_by4",
        decoder(form(3)((operands, mods) => Instruction.ImageLoadMipBy4(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageLoadMipPck extends InstructionDef(
        "image_load_mip_pck",
        decoder(form(3)((operands, mods) => Instruction.ImageLoadMipPck(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            4,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            4,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            4,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            4,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageLoadMipPck2 extends InstructionDef(
        "image_load_mip_pck2",
        decoder(form(3)((operands, mods) => Instruction.ImageLoadMipPck2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            115,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            115,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            115,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            115,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            115,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            115,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            115,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            115,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageLoadMipPck4 extends InstructionDef(
        "image_load_mip_pck4",
        decoder(form(3)((operands, mods) => Instruction.ImageLoadMipPck4(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            116,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            116,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            116,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            116,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            116,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            116,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            116,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            116,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageLoadMipPckSgn extends InstructionDef(
        "image_load_mip_pck_sgn",
        decoder(form(3)((operands, mods) =>
          Instruction.ImageLoadMipPckSgn(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            5,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            5,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            5,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            5,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageLoadPck extends InstructionDef(
        "image_load_pck",
        decoder(form(3)((operands, mods) => Instruction.ImageLoadPck(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            2,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            2,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            2,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            2,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageLoadPck2 extends InstructionDef(
        "image_load_pck2",
        decoder(form(3)((operands, mods) => Instruction.ImageLoadPck2(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            112,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            112,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            112,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            112,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            112,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            112,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            112,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            112,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageLoadPck4 extends InstructionDef(
        "image_load_pck4",
        decoder(form(3)((operands, mods) => Instruction.ImageLoadPck4(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            113,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            113,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            113,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            113,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            113,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            113,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            113,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            113,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageLoadPckSgn extends InstructionDef(
        "image_load_pck_sgn",
        decoder(form(3)((operands, mods) => Instruction.ImageLoadPckSgn(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            3,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            3,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            3,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            3,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageMsaaLoad extends InstructionDef(
        "image_msaa_load",
        decoder(form(3)((operands, mods) => Instruction.ImageMsaaLoad(operands(0), operands(1), operands(2), mods))),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            128,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            128,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            128,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            128,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            128,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            128,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            128,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            128,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
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
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VSAMPLE",
            24,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 128, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSample extends InstructionDef(
        "image_sample",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSample(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "MIMG",
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            32,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            27,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 96, true, false, false),
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
            32,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            32,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            32,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            32,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleB extends InstructionDef(
        "image_sample_b",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleB(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
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
            Family.GFX10,
            "rdna1",
            "MIMG",
            37,
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
            37,
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
            37,
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
            37,
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
            37,
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
            37,
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
            37,
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
            37,
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
            30,
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
            30,
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
            30,
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
            30,
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
            30,
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
            37,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            37,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            37,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            37,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleBCl extends InstructionDef(
        "image_sample_b_cl",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleBCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            66,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            38,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            38,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            38,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            38,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleBClO extends InstructionDef(
        "image_sample_b_cl_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleBClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            54,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            54,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            54,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            54,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            54,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            54,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            54,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            54,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            54,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            72,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            72,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            72,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            72,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            72,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 192, true, false, false),
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
            54,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            54,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            54,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            54,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleBO extends InstructionDef(
        "image_sample_b_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleBO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            53,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            53,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            53,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            53,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            53,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            53,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            53,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            53,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            53,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            40,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            40,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            40,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            40,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            40,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            53,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            53,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            53,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            53,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleC extends InstructionDef(
        "image_sample_c",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleC(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            40,
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
            40,
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
            40,
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
            40,
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
            40,
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
            40,
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
            40,
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
            40,
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
            40,
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
            32,
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
            32,
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
            32,
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
            32,
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
            32,
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
            40,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            40,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            40,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            40,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCB extends InstructionDef(
        "image_sample_c_b",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCB(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            45,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            45,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            45,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            45,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCBCl extends InstructionDef(
        "image_sample_c_b_cl",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCBCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            69,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            69,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            69,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            69,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            69,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 192, true, false, false),
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
            46,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            46,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            46,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            46,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCBClO extends InstructionDef(
        "image_sample_c_b_cl_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCBClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            62,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            62,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            62,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            62,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            62,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            62,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            62,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            62,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            62,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            75,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 224, true, false, false),
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
            62,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            62,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            62,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            62,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCBO extends InstructionDef(
        "image_sample_c_b_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCBO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            61,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            61,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            61,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            61,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            61,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            61,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            61,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            61,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            61,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            45,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 192, true, false, false),
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
            61,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            61,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            61,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            61,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCCd extends InstructionDef(
        "image_sample_c_cd",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCd(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            106,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            106,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            106,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            106,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            106,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            106,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            106,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            106,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            106,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            106,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            106,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            106,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            106,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCCdCl extends InstructionDef(
        "image_sample_c_cd_cl",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            107,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            107,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            107,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            107,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            107,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            107,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            107,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            107,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            107,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            107,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            107,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            107,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            107,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCCdClG16 extends InstructionDef(
        "image_sample_c_cd_cl_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdClG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            235,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            235,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            235,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            235,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            235,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            235,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            235,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            235,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSampleCCdClO extends InstructionDef(
        "image_sample_c_cd_cl_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            111,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            111,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            111,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            111,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            111,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            111,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            111,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            111,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            111,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            111,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            111,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            111,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            111,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCCdClOG16 extends InstructionDef(
        "image_sample_c_cd_cl_o_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdClOG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            239,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            239,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            239,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            239,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            239,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            239,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            239,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            239,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSampleCCdG16 extends InstructionDef(
        "image_sample_c_cd_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            234,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            234,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            234,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            234,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            234,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            234,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            234,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            234,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSampleCCdO extends InstructionDef(
        "image_sample_c_cd_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            110,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            110,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            110,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            110,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            110,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            110,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            110,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            110,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            110,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            110,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            110,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            110,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            110,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCCdOG16 extends InstructionDef(
        "image_sample_c_cd_o_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdOG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            238,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            238,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            238,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            238,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            238,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            238,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            238,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            238,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSampleCCl extends InstructionDef(
        "image_sample_c_cl",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            41,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            41,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            41,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            41,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            41,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            41,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            41,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            41,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            41,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            67,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            67,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            67,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            67,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            67,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            41,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            41,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            41,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            41,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCClO extends InstructionDef(
        "image_sample_c_cl_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            57,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            57,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            57,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            57,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            57,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            57,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            57,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            57,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            57,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            73,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 192, true, false, false),
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
            57,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            57,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            57,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            57,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCD_1 extends InstructionDef(
        "image_sample_c_d",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCD_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            33,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            33,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            33,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            33,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            33,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 320, true, false, false),
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
            42,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            42,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            42,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            42,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCDCl_1 extends InstructionDef(
        "image_sample_c_d_cl",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDCl_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            68,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            68,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            68,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            68,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            68,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 352, true, false, false),
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
            43,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            43,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            43,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            43,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCDClG16_1 extends InstructionDef(
        "image_sample_c_d_cl_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDClG16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            171,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            171,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            171,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            171,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            171,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            171,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            171,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            171,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            84,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            84,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            84,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            84,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            84,
            Col(
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
  case object ImageSampleCDClO_1 extends InstructionDef(
        "image_sample_c_d_cl_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDClO_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            59,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            59,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            59,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            59,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            59,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            59,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            59,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            59,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            59,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 384, true, false, false),
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
            74,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 384, true, false, false),
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
            59,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            59,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            59,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            59,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCDClOG16_1 extends InstructionDef(
        "image_sample_c_d_cl_o_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDClOG16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            187,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            187,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            187,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            187,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            187,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            187,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            187,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            187,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            86,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 320, true, false, false),
              OperandInfo(Some("RSRC"), "SREG", 256, true, false, false),
              OperandInfo(Some("SAMP"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSampleCDG16_1 extends InstructionDef(
        "image_sample_c_d_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDG16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            170,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            170,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            170,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            170,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            170,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            170,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            170,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            170,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            58,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            58,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            58,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            58,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            58,
            Col(
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
  case object ImageSampleCDO_1 extends InstructionDef(
        "image_sample_c_d_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDO_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            58,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            58,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            58,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            58,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            58,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            58,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            58,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            58,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            58,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            43,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 352, true, false, false),
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
            58,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            58,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            58,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            58,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCDOG16_1 extends InstructionDef(
        "image_sample_c_d_o_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDOG16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            186,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            186,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            186,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            186,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            186,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            186,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            186,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            186,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            60,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            60,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            60,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            60,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            60,
            Col(
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
  case object ImageSampleCL_1 extends InstructionDef(
        "image_sample_c_l",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCL_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            44,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            44,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            44,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            44,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCLO_1 extends InstructionDef(
        "image_sample_c_l_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCLO_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            60,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            60,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            60,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            60,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            60,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            60,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            60,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            60,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            60,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 192, true, false, false),
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
            44,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 192, true, false, false),
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
            60,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            60,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            60,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            60,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCLz extends InstructionDef(
        "image_sample_c_lz",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCLz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            47,
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
            47,
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
            47,
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
            47,
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
            47,
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
            47,
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
            47,
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
            47,
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
            47,
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
            36,
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
            36,
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
            36,
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
            36,
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
            36,
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
            47,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            47,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            47,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            47,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCLzO extends InstructionDef(
        "image_sample_c_lz_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCLzO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            63,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            63,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            63,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            63,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            63,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            63,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            63,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            63,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            63,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            46,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            63,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            63,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            63,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            63,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCO extends InstructionDef(
        "image_sample_c_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            56,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            42,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            56,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            56,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            56,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            56,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCd_2 extends InstructionDef(
        "image_sample_cd",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCd_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            104,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            104,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            104,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            104,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            104,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            104,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            104,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            104,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            104,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            104,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            104,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            104,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            104,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCdCl_2 extends InstructionDef(
        "image_sample_cd_cl",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdCl_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            105,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            105,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            105,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            105,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            105,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            105,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            105,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            105,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            105,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            105,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            105,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            105,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            105,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCdClG16_2 extends InstructionDef(
        "image_sample_cd_cl_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdClG16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            233,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            233,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            233,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            233,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            233,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            233,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            233,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            233,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSampleCdClO_2 extends InstructionDef(
        "image_sample_cd_cl_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdClO_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            109,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            109,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            109,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            109,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            109,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            109,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            109,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            109,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            109,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            109,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            109,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            109,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            109,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCdClOG16_2 extends InstructionDef(
        "image_sample_cd_cl_o_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdClOG16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            237,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            237,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            237,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            237,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            237,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            237,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            237,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            237,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSampleCdG16_2 extends InstructionDef(
        "image_sample_cd_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdG16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            232,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            232,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            232,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            232,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            232,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            232,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            232,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            232,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSampleCdO_2 extends InstructionDef(
        "image_sample_cd_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdO_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            108,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            108,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            108,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            108,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            108,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            108,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            108,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            108,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            108,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "MIMG",
            108,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            108,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            108,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            108,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleCdOG16_2 extends InstructionDef(
        "image_sample_cd_o_g16",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdOG16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            236,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            236,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            236,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            236,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            236,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            236,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            236,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            236,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
              OperandInfo(Some("SRSRC"), "SREG_NONULL", 256, true, false, false),
              OperandInfo(Some("SSAMP"), "SREG_NONULL", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageSampleCl_2 extends InstructionDef(
        "image_sample_cl",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCl_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
            33,
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
            64,
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
            64,
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
            64,
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
            64,
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
            64,
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
            33,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            33,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            33,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            33,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleClO_2 extends InstructionDef(
        "image_sample_cl_o",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleClO_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            49,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            49,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            49,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            49,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            49,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            49,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            49,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            49,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            49,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            70,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            49,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            49,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            49,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            49,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageSampleD extends InstructionDef(
        "image_sample_d",
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleD(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            34,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            28,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 288, true, false, false),
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
            34,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            34,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            34,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            34,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            35,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            65,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            65,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            65,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            65,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            65,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 320, true, false, false),
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
            35,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            35,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            35,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            35,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            163,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            163,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            163,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            163,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            163,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            163,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            163,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            163,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            95,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            95,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            95,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            95,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            95,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            51,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            51,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            51,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            51,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            51,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            51,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            51,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            51,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            51,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 352, true, false, false),
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
            71,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 352, true, false, false),
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
            51,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            51,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            51,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            51,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            179,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            179,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            179,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            179,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            179,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            179,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            179,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            179,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            85,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            85,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            85,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            85,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 288, true, false, false),
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
            85,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            162,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            162,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            162,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            162,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            162,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            162,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            162,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            162,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            57,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            57,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            57,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            57,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 224, true, false, false),
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
            57,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            50,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 320, true, false, false),
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
            38,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 320, true, false, false),
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
            50,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            50,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            50,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            50,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "MIMG",
            178,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            178,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            178,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            178,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            178,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            178,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            178,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            178,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            59,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            59,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            59,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            59,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 256, true, false, false),
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
            59,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            36,
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
            36,
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
            36,
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
            36,
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
            36,
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
            36,
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
            36,
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
            36,
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
            36,
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
            29,
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
            29,
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
            29,
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
            29,
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
            29,
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
            36,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            36,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            36,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            36,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            52,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            52,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            52,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            52,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            52,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            52,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            52,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            52,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            52,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 160, true, false, false),
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
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 160, true, false, false),
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
            52,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            52,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            52,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            52,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            39,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            31,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            31,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            31,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            31,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 96, true, false, false),
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
            31,
            Col(
              OperandInfo(Some("VDATA"), "VGPR", 128, false, true, false),
              OperandInfo(None, "VGPR", 96, true, false, false),
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
            39,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            39,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            39,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            39,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "MIMG",
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            55,
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
            41,
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
            41,
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
            41,
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
            41,
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
            41,
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
            55,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "MIMG",
            55,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "MIMG",
            55,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "MIMG",
            55,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(
    ImageAtomicAdd,
    ImageAtomicAddFlt,
    ImageAtomicAddUint,
    ImageAtomicAnd,
    ImageAtomicCmpswap,
    ImageAtomicDec,
    ImageAtomicDecUint,
    ImageAtomicFcmpswap,
    ImageAtomicFmax,
    ImageAtomicFmin,
    ImageAtomicInc,
    ImageAtomicIncUint,
    ImageAtomicMaxFlt,
    ImageAtomicMaxInt,
    ImageAtomicMaxUint,
    ImageAtomicMinFlt,
    ImageAtomicMinInt,
    ImageAtomicMinUint,
    ImageAtomicOr,
    ImageAtomicPkAddBf16,
    ImageAtomicPkAddF16,
    ImageAtomicRsub,
    ImageAtomicSmax,
    ImageAtomicSmin,
    ImageAtomicSub,
    ImageAtomicSubUint,
    ImageAtomicSwap,
    ImageAtomicUmax,
    ImageAtomicUmin,
    ImageAtomicXor,
    ImageBvh64IntersectRay,
    ImageBvh8IntersectRay,
    ImageBvhDualIntersectRay,
    ImageBvhIntersectRay,
    ImageGather4,
    ImageGather4B,
    ImageGather4BCl,
    ImageGather4BClO,
    ImageGather4BO,
    ImageGather4C,
    ImageGather4CB,
    ImageGather4CBCl,
    ImageGather4CBClO,
    ImageGather4CBO,
    ImageGather4CCl,
    ImageGather4CClO,
    ImageGather4CL_1,
    ImageGather4CLO_1,
    ImageGather4CLz,
    ImageGather4CLzO,
    ImageGather4CO,
    ImageGather4Cl_2,
    ImageGather4ClO_2,
    ImageGather4L,
    ImageGather4LO,
    ImageGather4Lz,
    ImageGather4LzO,
    ImageGather4O,
    ImageGather4h,
    ImageGather4hPck,
    ImageGather8hPck,
    ImageGetLod,
    ImageGetResinfo,
    ImageLoad,
    ImageLoadBy2,
    ImageLoadBy4,
    ImageLoadMip,
    ImageLoadMipBy2,
    ImageLoadMipBy4,
    ImageLoadMipPck,
    ImageLoadMipPck2,
    ImageLoadMipPck4,
    ImageLoadMipPckSgn,
    ImageLoadPck,
    ImageLoadPck2,
    ImageLoadPck4,
    ImageLoadPckSgn,
    ImageMsaaLoad,
    ImageSample,
    ImageSampleB,
    ImageSampleBCl,
    ImageSampleBClO,
    ImageSampleBO,
    ImageSampleC,
    ImageSampleCB,
    ImageSampleCBCl,
    ImageSampleCBClO,
    ImageSampleCBO,
    ImageSampleCCd,
    ImageSampleCCdCl,
    ImageSampleCCdClG16,
    ImageSampleCCdClO,
    ImageSampleCCdClOG16,
    ImageSampleCCdG16,
    ImageSampleCCdO,
    ImageSampleCCdOG16,
    ImageSampleCCl,
    ImageSampleCClO,
    ImageSampleCD_1,
    ImageSampleCDCl_1,
    ImageSampleCDClG16_1,
    ImageSampleCDClO_1,
    ImageSampleCDClOG16_1,
    ImageSampleCDG16_1,
    ImageSampleCDO_1,
    ImageSampleCDOG16_1,
    ImageSampleCL_1,
    ImageSampleCLO_1,
    ImageSampleCLz,
    ImageSampleCLzO,
    ImageSampleCO,
    ImageSampleCd_2,
    ImageSampleCdCl_2,
    ImageSampleCdClG16_2,
    ImageSampleCdClO_2,
    ImageSampleCdClOG16_2,
    ImageSampleCdG16_2,
    ImageSampleCdO_2,
    ImageSampleCdOG16_2,
    ImageSampleCl_2,
    ImageSampleClO_2,
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
    ImageSampleLzO
  )
}
