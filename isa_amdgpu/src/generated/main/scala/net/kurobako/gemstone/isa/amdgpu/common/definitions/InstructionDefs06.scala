// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

private[common] object InstructionDefs6 {
  case object GlobalAtomicXorX2 extends InstructionDef(
        "global_atomic_xor_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicXorX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicXorX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            106,
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
            106,
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
            106,
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
            106,
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
            91,
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
            91,
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
            106,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            106,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalInv extends InstructionDef(
        "global_inv",
        decoder(form(0)((_, mods) => Instruction.GlobalInv(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            43,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            43,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadAddtidB32 extends InstructionDef(
        "global_load_addtid_b32",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadAddtidB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadAddtidB32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadAsyncToLdsB128 extends InstructionDef(
        "global_load_async_to_lds_b128",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) =>
            Instruction.GlobalLoadAsyncToLdsB128_3(operands(0), operands(1), operands(2), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            98,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "DSMEM", 128, false, true, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadAsyncToLdsB32 extends InstructionDef(
        "global_load_async_to_lds_b32",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) =>
            Instruction.GlobalLoadAsyncToLdsB32_3(operands(0), operands(1), operands(2), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            96,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "DSMEM", 32, false, true, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadAsyncToLdsB64 extends InstructionDef(
        "global_load_async_to_lds_b64",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) =>
            Instruction.GlobalLoadAsyncToLdsB64_3(operands(0), operands(1), operands(2), mods)
          )
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            97,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "DSMEM", 64, false, true, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadAsyncToLdsB8 extends InstructionDef(
        "global_load_async_to_lds_b8",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            95,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "DSMEM", 8, false, true, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadB128 extends InstructionDef(
        "global_load_b128",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadB128_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            23,
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
  case object GlobalLoadB32 extends InstructionDef(
        "global_load_b32",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadB32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            20,
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
  case object GlobalLoadB64 extends InstructionDef(
        "global_load_b64",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadB64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            21,
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
  case object GlobalLoadB96 extends InstructionDef(
        "global_load_b96",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadB96_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadB96_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadBlock extends InstructionDef(
        "global_load_block",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadBlock_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadBlock_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            83,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 1024, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 1024, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            83,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 1024, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 1024, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadD16B16 extends InstructionDef(
        "global_load_d16_b16",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadD16B16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadD16B16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadD16HiB16 extends InstructionDef(
        "global_load_d16_hi_b16",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadD16HiB16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadD16HiB16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadD16HiI8 extends InstructionDef(
        "global_load_d16_hi_i8",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadD16HiI8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadD16HiI8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadD16HiU8 extends InstructionDef(
        "global_load_d16_hi_u8",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadD16HiU8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadD16HiU8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadD16I8 extends InstructionDef(
        "global_load_d16_i8",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadD16I8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadD16I8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            31,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            31,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            31,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            31,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadD16U8 extends InstructionDef(
        "global_load_d16_u8",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadD16U8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadD16U8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            30,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            30,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            30,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            30,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadDword extends InstructionDef(
        "global_load_dword",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadDword_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadDword_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
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
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
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
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
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
            20,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
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
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
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
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
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
            20,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            20,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalLoadDwordAddtid extends InstructionDef(
        "global_load_dword_addtid",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadDwordAddtid_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadDwordAddtid_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadDwordx2 extends InstructionDef(
        "global_load_dwordx2",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadDwordx2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadDwordx2_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
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
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
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
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
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
            21,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
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
            13,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
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
            21,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            21,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalLoadDwordx3 extends InstructionDef(
        "global_load_dwordx3",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadDwordx3_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadDwordx3_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            22,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            15,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            15,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            22,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            22,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalLoadDwordx4 extends InstructionDef(
        "global_load_dwordx4",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadDwordx4_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadDwordx4_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            23,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            14,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            14,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            23,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            23,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalLoadI16 extends InstructionDef(
        "global_load_i16",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadI16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadI8 extends InstructionDef(
        "global_load_i8",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadI8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadI8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadLdsDword extends InstructionDef(
        "global_load_lds_dword",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsDword_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsDword_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            42,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            42,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadLdsDwordx3 extends InstructionDef(
        "global_load_lds_dwordx3",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsDwordx3_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsDwordx3_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            126,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadLdsDwordx4 extends InstructionDef(
        "global_load_lds_dwordx4",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsDwordx4_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsDwordx4_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            125,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadLdsSbyte extends InstructionDef(
        "global_load_lds_sbyte",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsSbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsSbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            39,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadLdsSshort extends InstructionDef(
        "global_load_lds_sshort",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsSshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsSshort_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            41,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            41,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadLdsUbyte extends InstructionDef(
        "global_load_lds_ubyte",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsUbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsUbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            38,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadLdsUshort extends InstructionDef(
        "global_load_lds_ushort",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsUshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsUshort_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            40,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadMonitorB128 extends InstructionDef(
        "global_load_monitor_b128",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadMonitorB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadMonitorB128_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
  case object GlobalLoadMonitorB32 extends InstructionDef(
        "global_load_monitor_b32",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadMonitorB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadMonitorB32_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
  case object GlobalLoadMonitorB64 extends InstructionDef(
        "global_load_monitor_b64",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadMonitorB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadMonitorB64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
  case object GlobalLoadSbyte extends InstructionDef(
        "global_load_sbyte",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadSbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadSbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            17,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            9,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            9,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            17,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            17,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalLoadSbyteD16 extends InstructionDef(
        "global_load_sbyte_d16",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadSbyteD16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadSbyteD16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            34,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            34,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            34,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalLoadSbyteD16Hi extends InstructionDef(
        "global_load_sbyte_d16_hi",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadSbyteD16Hi_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadSbyteD16Hi_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            35,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            35,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            35,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalLoadShortD16 extends InstructionDef(
        "global_load_short_d16",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadShortD16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadShortD16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            36,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            36,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            36,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalLoadShortD16Hi extends InstructionDef(
        "global_load_short_d16_hi",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadShortD16Hi_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadShortD16Hi_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            37,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            37,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            37,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalLoadSshort extends InstructionDef(
        "global_load_sshort",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadSshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadSshort_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            19,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            11,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            11,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            19,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            19,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalLoadTr16B128 extends InstructionDef(
        "global_load_tr16_b128",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadTr16B128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadTr16B128_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            87,
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
  case object GlobalLoadTr4B64 extends InstructionDef(
        "global_load_tr4_b64",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadTr4B64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadTr4B64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            115,
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
  case object GlobalLoadTr6B96 extends InstructionDef(
        "global_load_tr6_b96",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadTr6B96_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadTr6B96_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            116,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadTr8B64 extends InstructionDef(
        "global_load_tr8_b64",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadTr8B64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadTr8B64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            88,
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
  case object GlobalLoadTrB128 extends InstructionDef(
        "global_load_tr_b128",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadTrB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadTrB128_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            87,
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
  case object GlobalLoadTrB64 extends InstructionDef(
        "global_load_tr_b64",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadTrB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadTrB64_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            88,
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
  case object GlobalLoadU16 extends InstructionDef(
        "global_load_u16",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadU16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadU8 extends InstructionDef(
        "global_load_u8",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadU8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadU8_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalLoadUbyte extends InstructionDef(
        "global_load_ubyte",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadUbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadUbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            16,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            8,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            8,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            16,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            16,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalLoadUbyteD16 extends InstructionDef(
        "global_load_ubyte_d16",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadUbyteD16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadUbyteD16_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            32,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            32,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            32,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalLoadUbyteD16Hi extends InstructionDef(
        "global_load_ubyte_d16_hi",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadUbyteD16Hi_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadUbyteD16Hi_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            33,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            33,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            33,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalLoadUshort extends InstructionDef(
        "global_load_ushort",
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadUshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadUshort_3(operands(0), operands(1), operands(2), mods))
        ),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            18,
            Vector(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            10,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            10,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            18,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            18,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalPrefetchB8 extends InstructionDef(
        "global_prefetch_b8",
        decoder(form(2)((operands, mods) => Instruction.GlobalPrefetchB8(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
  case object GlobalStoreAddtidB32 extends InstructionDef(
        "global_store_addtid_b32",
        decoder(form(2)((operands, mods) => Instruction.GlobalStoreAddtidB32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            41,
            Vector(
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
            "FLAT_GLOBAL",
            41,
            Vector(
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            41,
            Vector(
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            41,
            Vector(
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            41,
            Vector(
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreAsyncFromLdsB128 extends InstructionDef(
        "global_store_async_from_lds_b128",
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreAsyncFromLdsB128(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            102,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "DSMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreAsyncFromLdsB32 extends InstructionDef(
        "global_store_async_from_lds_b32",
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreAsyncFromLdsB32(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            100,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "DSMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreAsyncFromLdsB64 extends InstructionDef(
        "global_store_async_from_lds_b64",
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreAsyncFromLdsB64(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            101,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "DSMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreAsyncFromLdsB8 extends InstructionDef(
        "global_store_async_from_lds_b8",
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreAsyncFromLdsB8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            99,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "DSMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreB128 extends InstructionDef(
        "global_store_b128",
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreB128(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            29,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreB16 extends InstructionDef(
        "global_store_b16",
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreB16(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            25,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreB32 extends InstructionDef(
        "global_store_b32",
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreB32(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            26,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreB64 extends InstructionDef(
        "global_store_b64",
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreB64(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            27,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreB8 extends InstructionDef(
        "global_store_b8",
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreB8(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            24,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreB96 extends InstructionDef(
        "global_store_b96",
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreB96(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            28,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreBlock extends InstructionDef(
        "global_store_block",
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreBlock(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            84,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 1024, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 1024, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            84,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 1024, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 1024, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreByte extends InstructionDef(
        "global_store_byte",
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreByte(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            24,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalStoreByteD16Hi extends InstructionDef(
        "global_store_byte_d16_hi",
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreByteD16Hi(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            25,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            25,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            25,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalStoreD16HiB16 extends InstructionDef(
        "global_store_d16_hi_b16",
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreD16HiB16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            37,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            37,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            37,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            37,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreD16HiB8 extends InstructionDef(
        "global_store_d16_hi_b8",
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreD16HiB8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
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
            "FLAT_GLOBAL",
            36,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            36,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            36,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            36,
            Vector(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreDword extends InstructionDef(
        "global_store_dword",
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreDword(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            28,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalStoreDwordAddtid extends InstructionDef(
        "global_store_dword_addtid",
        decoder(form(2)((operands, mods) => Instruction.GlobalStoreDwordAddtid(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            23,
            Vector(
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalStoreDwordx2 extends InstructionDef(
        "global_store_dwordx2",
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreDwordx2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            29,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            29,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            29,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalStoreDwordx3 extends InstructionDef(
        "global_store_dwordx3",
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreDwordx3(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            30,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            30,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalStoreDwordx4 extends InstructionDef(
        "global_store_dwordx4",
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreDwordx4(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            31,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            30,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            31,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            31,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalStoreShort extends InstructionDef(
        "global_store_short",
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreShort(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            26,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            26,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            26,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalStoreShortD16Hi extends InstructionDef(
        "global_store_short_d16_hi",
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreShortD16Hi(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "FLAT_GLBL",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "FLAT_GLBL",
            27,
            Vector(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "GLOBAL",
            27,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "GLOBAL",
            27,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalWb extends InstructionDef(
        "global_wb",
        decoder(form(0)((_, mods) => Instruction.GlobalWb(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            44,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            44,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object GlobalWbinv extends InstructionDef(
        "global_wbinv",
        decoder(form(0)((_, mods) => Instruction.GlobalWbinv(mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VGLOBAL",
            79,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VGLOBAL",
            79,
            Vector.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object ImageAtomicAdd extends InstructionDef(
        "image_atomic_add",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicAdd(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            18,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MIMG",
            18,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG",
            17,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            17,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            17,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            17,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG",
            17,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            17,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            17,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            17,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MIMG",
            12,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            12,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            12,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            12,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MIMG",
            17,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            17,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            18,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            18,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            131,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            12,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            24,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MIMG",
            24,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG",
            24,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            24,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            24,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            24,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG",
            24,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            24,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            24,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            24,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MIMG",
            18,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            18,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            18,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            18,
            Vector(
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
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            18,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MIMG",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            24,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            24,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            17,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MIMG",
            17,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG",
            16,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            16,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            16,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            16,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG",
            16,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            16,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            16,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            16,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MIMG",
            11,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            11,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            11,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            11,
            Vector(
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
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            11,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MIMG",
            16,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            16,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            17,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            17,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicDec extends InstructionDef(
        "image_atomic_dec",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicDec(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            28,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MIMG",
            28,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG",
            28,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            28,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            28,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            28,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG",
            28,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            28,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            28,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            28,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MIMG",
            22,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            22,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            22,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            22,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MIMG",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            28,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            28,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            22,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            29,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            29,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            29,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            29,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG",
            29,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            29,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            29,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            29,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MIMG",
            29,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            29,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicFmax extends InstructionDef(
        "image_atomic_fmax",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicFmax(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            31,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            31,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            31,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            31,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG",
            31,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            31,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            31,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            31,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MIMG",
            31,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            31,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicFmin extends InstructionDef(
        "image_atomic_fmin",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicFmin(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.GFX10,
            "rdna1",
            "MIMG",
            30,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            30,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            30,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            30,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG",
            30,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            30,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            30,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            30,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MIMG",
            30,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            30,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object ImageAtomicInc extends InstructionDef(
        "image_atomic_inc",
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicInc(operands(0), operands(1), operands(2), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            27,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MIMG",
            27,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG",
            27,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            27,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            27,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            27,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG",
            27,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            27,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            27,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            27,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MIMG",
            21,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            21,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            21,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            21,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MIMG",
            27,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            27,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            27,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            27,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            21,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            133,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            16,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            17,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            132,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            14,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            15,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "MIMG",
            25,
            Vector(
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
            Generation.CDNA2,
            "cdna2",
            "MIMG",
            25,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG",
            25,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA1",
            25,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA2",
            25,
            Vector(
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
            Generation.GFX10,
            "rdna1",
            "MIMG_NSA3",
            25,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG",
            25,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA1",
            25,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA2",
            25,
            Vector(
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
            Generation.GFX10,
            "rdna2",
            "MIMG_NSA3",
            25,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MIMG",
            19,
            Vector(
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
            Generation.GFX11,
            "rdna3",
            "MIMG_NSA1",
            19,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MIMG",
            19,
            Vector(
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
            Generation.GFX11,
            "rdna3_5",
            "MIMG_NSA1",
            19,
            Vector(
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
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            19,
            Vector(
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
            Generation.GFX6,
            "gfx6",
            "MIMG",
            25,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "MIMG",
            25,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "MIMG",
            25,
            Vector.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "MIMG",
            25,
            Vector.empty,
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
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            135,
            Vector(
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
        Metadata(Vector(
          Encoding(
            Generation.GFX12,
            "rdna4",
            "VIMAGE",
            134,
            Vector(
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

  val definitions: Vector[InstructionDef] = Vector(
    GlobalAtomicXorX2,
    GlobalInv,
    GlobalLoadAddtidB32,
    GlobalLoadAsyncToLdsB128,
    GlobalLoadAsyncToLdsB32,
    GlobalLoadAsyncToLdsB64,
    GlobalLoadAsyncToLdsB8,
    GlobalLoadB128,
    GlobalLoadB32,
    GlobalLoadB64,
    GlobalLoadB96,
    GlobalLoadBlock,
    GlobalLoadD16B16,
    GlobalLoadD16HiB16,
    GlobalLoadD16HiI8,
    GlobalLoadD16HiU8,
    GlobalLoadD16I8,
    GlobalLoadD16U8,
    GlobalLoadDword,
    GlobalLoadDwordAddtid,
    GlobalLoadDwordx2,
    GlobalLoadDwordx3,
    GlobalLoadDwordx4,
    GlobalLoadI16,
    GlobalLoadI8,
    GlobalLoadLdsDword,
    GlobalLoadLdsDwordx3,
    GlobalLoadLdsDwordx4,
    GlobalLoadLdsSbyte,
    GlobalLoadLdsSshort,
    GlobalLoadLdsUbyte,
    GlobalLoadLdsUshort,
    GlobalLoadMonitorB128,
    GlobalLoadMonitorB32,
    GlobalLoadMonitorB64,
    GlobalLoadSbyte,
    GlobalLoadSbyteD16,
    GlobalLoadSbyteD16Hi,
    GlobalLoadShortD16,
    GlobalLoadShortD16Hi,
    GlobalLoadSshort,
    GlobalLoadTr16B128,
    GlobalLoadTr4B64,
    GlobalLoadTr6B96,
    GlobalLoadTr8B64,
    GlobalLoadTrB128,
    GlobalLoadTrB64,
    GlobalLoadU16,
    GlobalLoadU8,
    GlobalLoadUbyte,
    GlobalLoadUbyteD16,
    GlobalLoadUbyteD16Hi,
    GlobalLoadUshort,
    GlobalPrefetchB8,
    GlobalStoreAddtidB32,
    GlobalStoreAsyncFromLdsB128,
    GlobalStoreAsyncFromLdsB32,
    GlobalStoreAsyncFromLdsB64,
    GlobalStoreAsyncFromLdsB8,
    GlobalStoreB128,
    GlobalStoreB16,
    GlobalStoreB32,
    GlobalStoreB64,
    GlobalStoreB8,
    GlobalStoreB96,
    GlobalStoreBlock,
    GlobalStoreByte,
    GlobalStoreByteD16Hi,
    GlobalStoreD16HiB16,
    GlobalStoreD16HiB8,
    GlobalStoreDword,
    GlobalStoreDwordAddtid,
    GlobalStoreDwordx2,
    GlobalStoreDwordx3,
    GlobalStoreDwordx4,
    GlobalStoreShort,
    GlobalStoreShortD16Hi,
    GlobalWb,
    GlobalWbinv,
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
    ImageAtomicPkAddF16
  )
}
