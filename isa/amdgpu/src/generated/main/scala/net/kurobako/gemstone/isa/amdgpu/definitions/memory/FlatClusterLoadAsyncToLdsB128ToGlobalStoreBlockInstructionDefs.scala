// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object FlatClusterLoadAsyncToLdsB128ToGlobalStoreBlockInstructionDefs {
  case object ClusterLoadAsyncToLdsB128 extends InstructionDef(
        "cluster_load_async_to_lds_b128",
        decoder(form(3)((operands, mods) =>
          Instruction.ClusterLoadAsyncToLdsB128(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            109,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            107,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            108,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            106,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            105,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            103,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            104,
            Col(
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
  case object FlatAtomicAdd extends InstructionDef(
        "flat_atomic_add",
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAdd_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAdd_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAdd_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            66,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            66,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            66,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            66,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            50,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            50,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            50,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            50,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            66,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            66,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "FLAT",
            77,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            77,
            Col(
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
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            86,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            86,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            86,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            86,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "FLAT",
            79,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            79,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            79,
            Col(
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
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            85,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            53,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            53,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            53,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            53,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            67,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            67,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            67,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            67,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            98,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            98,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            98,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            98,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            82,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            82,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            82,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            82,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            98,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            98,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            72,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            72,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            72,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            72,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            57,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            57,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            57,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            57,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            72,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            72,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            60,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            60,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            60,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            60,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            73,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            73,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            73,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            73,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            104,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            104,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            104,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            104,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            89,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            89,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            89,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            89,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            104,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            104,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            65,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            65,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            65,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            65,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            49,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            49,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            49,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            49,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            65,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            65,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            52,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            52,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            52,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            52,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            66,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            66,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            66,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            66,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            80,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            80,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            97,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            97,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            97,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            97,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            81,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            81,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            81,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            81,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            97,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            97,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            80,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            80,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            76,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            76,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            76,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            76,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            61,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            61,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            61,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            61,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            76,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            76,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            64,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            64,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            64,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            64,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            77,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            77,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            77,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            77,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            108,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            108,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            108,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            108,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            93,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            93,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            93,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            93,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            108,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            108,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "FLAT",
            62,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            62,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            62,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            62,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "FLAT",
            94,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            94,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            94,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            94,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "FLAT",
            64,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            64,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            64,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            64,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "FLAT",
            96,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            96,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            96,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            96,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "FLAT",
            63,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            63,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            63,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            63,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "FLAT",
            95,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            95,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            95,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            95,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            75,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            75,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            75,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            75,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            60,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            60,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            60,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            60,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            75,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            75,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            63,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            63,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            63,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            63,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            76,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            76,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            76,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            76,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            107,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            107,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            107,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            107,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            92,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            92,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            92,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            92,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            107,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            107,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            82,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            82,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "FLAT",
            81,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            81,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            81,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            58,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            58,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            58,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            58,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            71,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            71,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            71,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            71,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            82,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            82,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            92,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            59,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            59,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            59,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            59,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            72,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            72,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            72,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            72,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            81,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            81,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "FLAT",
            80,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            80,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            80,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            56,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            56,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            56,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            56,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            69,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            69,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            69,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            69,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            81,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            81,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            91,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            57,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            57,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            57,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            57,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            70,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            70,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            70,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            70,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            73,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            73,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            73,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            73,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            58,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            58,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            58,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            58,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            73,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            73,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            61,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            61,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            61,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            61,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            74,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            74,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            74,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            74,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            105,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            105,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            105,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            105,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            90,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            90,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            90,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            90,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            105,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            105,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "FLAT",
            82,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            82,
            Col(
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
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            90,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            90,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "FLAT",
            78,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            78,
            Col(
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
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            89,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            89,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            70,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            70,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            70,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            70,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            55,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            55,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            55,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            55,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            70,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            70,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            102,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            102,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            102,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            102,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            87,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            87,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            87,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            87,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            102,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            102,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            68,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            68,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            68,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            68,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            53,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            53,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            53,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            53,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            68,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            68,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            100,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            100,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            100,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            100,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            85,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            85,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            85,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            85,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            100,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            100,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            67,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            67,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            67,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            67,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            51,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            51,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            51,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            51,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            67,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            67,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            55,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            55,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            54,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            54,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            54,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            54,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            68,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            68,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            68,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            68,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            99,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            99,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            99,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            99,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            83,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            83,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            83,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            83,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            99,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            99,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            64,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            64,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            64,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            64,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            48,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            48,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            48,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            48,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            64,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            64,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            51,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            51,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            51,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            51,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            65,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            65,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            65,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            65,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            96,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            96,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            96,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            96,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            80,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            80,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            80,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            80,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            96,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            96,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            71,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            71,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            71,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            71,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            56,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            56,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            56,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            56,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            71,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            71,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            103,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            103,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            103,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            103,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            88,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            88,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            88,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            88,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            103,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            103,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            69,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            69,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            69,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            69,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            54,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            54,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            54,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            54,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            69,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            69,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            101,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            101,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            101,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            101,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            86,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            86,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            86,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            86,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            101,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            101,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            74,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            74,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            74,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            74,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            59,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            59,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            59,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            59,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            74,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            74,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            62,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            62,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            62,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            62,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            75,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT",
            75,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            75,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VFLAT",
            75,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            106,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            106,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            106,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            106,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            91,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT",
            91,
            Col(
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
            Family.GFX6,
            "gfx6",
            "FLAT",
            91,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            91,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            106,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            106,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            23,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            20,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            21,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            21,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            21,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            21,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            22,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            22,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            22,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            22,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            32,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            32,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            32,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            32,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            35,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            35,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            35,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            35,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            34,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            33,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            31,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            31,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            31,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            31,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            30,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            20,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            20,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            20,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            20,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            12,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            12,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            12,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            12,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            20,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            20,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadDwordx2 extends InstructionDef(
        "flat_load_dwordx2",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadDwordx2(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            21,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            21,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            21,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            21,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            13,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            13,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            13,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            13,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            21,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            21,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadDwordx3 extends InstructionDef(
        "flat_load_dwordx3",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadDwordx3(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            22,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            22,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            22,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            22,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            15,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            15,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            15,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            22,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            22,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadDwordx4 extends InstructionDef(
        "flat_load_dwordx4",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadDwordx4(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            23,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            23,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            23,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            23,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            14,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            14,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            14,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            14,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            23,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            23,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            19,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            19,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            19,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            19,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            17,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            17,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            17,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            17,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            114,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            112,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            113,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            17,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            17,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            17,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            17,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            9,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            9,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            9,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            9,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            17,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            17,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadSbyteD16 extends InstructionDef(
        "flat_load_sbyte_d16",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadSbyteD16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            34,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            34,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            34,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            34,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            34,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            34,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadSbyteD16Hi extends InstructionDef(
        "flat_load_sbyte_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadSbyteD16Hi(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            35,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            35,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            35,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            35,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            35,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            35,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            35,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            35,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadShortD16 extends InstructionDef(
        "flat_load_short_d16",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadShortD16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            36,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            36,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            36,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            36,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            36,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            36,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            36,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            36,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadShortD16Hi extends InstructionDef(
        "flat_load_short_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadShortD16Hi(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            37,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            37,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            37,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            37,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            37,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            37,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            37,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            37,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadSshort extends InstructionDef(
        "flat_load_sshort",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadSshort(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            19,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            19,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            19,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            19,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            11,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            11,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            11,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            11,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            19,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            19,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            18,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            18,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            18,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            18,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            16,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            16,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            16,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            16,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            16,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            16,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            16,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            16,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            8,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            8,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            8,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            8,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            16,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            16,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadUbyteD16 extends InstructionDef(
        "flat_load_ubyte_d16",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadUbyteD16(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            32,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            32,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            32,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            32,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            32,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            32,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            32,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            32,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadUbyteD16Hi extends InstructionDef(
        "flat_load_ubyte_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadUbyteD16Hi(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            33,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            33,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            33,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            33,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            33,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            33,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatLoadUshort extends InstructionDef(
        "flat_load_ushort",
        decoder(form(2)((operands, mods) => Instruction.FlatLoadUshort(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            18,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            18,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            18,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            18,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            10,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            10,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            10,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            10,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            18,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            18,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            93,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            29,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            29,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            29,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            29,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            25,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            25,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            25,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            25,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            26,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            26,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            26,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            26,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            27,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            27,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            27,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            27,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            24,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            24,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            24,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            24,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            28,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            28,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            28,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            28,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            24,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            24,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            24,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            24,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            24,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            24,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            24,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            24,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            24,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            24,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatStoreByteD16Hi extends InstructionDef(
        "flat_store_byte_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreByteD16Hi(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            25,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            25,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            25,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            25,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            25,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            25,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            25,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            25,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            37,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            37,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            37,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            37,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VFLAT",
            36,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT",
            36,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT",
            36,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VFLAT",
            36,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            28,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            28,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            28,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            28,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            28,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            28,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            28,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            28,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            28,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            28,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatStoreDwordx2 extends InstructionDef(
        "flat_store_dwordx2",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreDwordx2(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            29,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            29,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            29,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            29,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            29,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            29,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            29,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            29,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            29,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            29,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatStoreDwordx3 extends InstructionDef(
        "flat_store_dwordx3",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreDwordx3(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            30,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            30,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            30,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            30,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            31,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            31,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            31,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            31,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            30,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            30,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatStoreDwordx4 extends InstructionDef(
        "flat_store_dwordx4",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreDwordx4(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            31,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            31,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            31,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            31,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            30,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            30,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            30,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            30,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            31,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            31,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatStoreShort extends InstructionDef(
        "flat_store_short",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreShort(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            26,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            26,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            26,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            26,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            26,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            26,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX6,
            "gfx6",
            "FLAT",
            26,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX7,
            "gfx7",
            "FLAT",
            26,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            26,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            26,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object FlatStoreShortD16Hi extends InstructionDef(
        "flat_store_short_d16_hi",
        decoder(form(2)((operands, mods) => Instruction.FlatStoreShortD16Hi(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT",
            27,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT",
            27,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT",
            27,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT",
            27,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT",
            27,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT",
            27,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true),
              OperandInfo(None, "FLAT_SCRATCH", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX8,
            "gfx8",
            "FLAT",
            27,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "FLAT",
            27,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            66,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            66,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            66,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            66,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            50,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            50,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            66,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            66,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            77,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            77,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            77,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            77,
            Col(
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
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            86,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            86,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            86,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            86,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            86,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            79,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            79,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            79,
            Col(
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
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            85,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            53,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            53,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            53,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            53,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            53,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            67,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            67,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            67,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            67,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            67,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            98,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            98,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            98,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            98,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            82,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            82,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            98,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            98,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            72,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            72,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            72,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            72,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            57,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            57,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            72,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            72,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            60,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            60,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            60,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            60,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            60,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            73,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            73,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            73,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            73,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            73,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            104,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            104,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            104,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            104,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            89,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            89,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            104,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            104,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            65,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            65,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            65,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            65,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            49,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            49,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            65,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            65,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            52,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            52,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            52,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            52,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            52,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            66,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            66,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            66,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            66,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            66,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            80,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            80,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            80,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            97,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            97,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            97,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            97,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            81,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            81,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            97,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            97,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            80,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            80,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            52,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            55,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            55,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            55,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            76,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            76,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            76,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            76,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            61,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            61,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            76,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            76,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            64,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            64,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            64,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            64,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            64,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            77,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            77,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            77,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            77,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            77,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            108,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            108,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            108,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            108,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            93,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            93,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            108,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            108,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            62,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            62,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            94,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            94,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            64,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            64,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            96,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            96,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            63,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            63,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            95,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            95,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            75,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            75,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            75,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            75,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            60,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            60,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            75,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            75,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            63,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            63,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            63,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            63,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            63,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            76,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            76,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            76,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            76,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            76,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            107,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            107,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            107,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            107,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            92,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            92,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            107,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            107,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            82,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            82,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            82,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            81,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            81,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            81,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            58,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            58,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            58,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            58,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            58,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            71,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            71,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            71,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            71,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            71,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            82,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            82,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            92,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            59,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            59,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            59,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            59,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            59,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            72,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            72,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            72,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            72,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            72,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            81,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            81,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            81,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            80,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            80,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            80,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            56,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            56,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            56,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            56,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            56,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            69,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            69,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            69,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            69,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            69,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            81,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            81,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            91,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            57,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            57,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            57,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            57,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            57,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            70,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            70,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            70,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            70,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            70,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            73,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            73,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            73,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            73,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            58,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            58,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            73,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            73,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            61,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            61,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            61,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            61,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            61,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            74,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            74,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            74,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            74,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            74,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            105,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            105,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            105,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            105,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            90,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            90,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            105,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            105,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            115,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            82,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            82,
            Col(
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
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            90,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            90,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            78,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            78,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            78,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            78,
            Col(
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
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            89,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            89,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            70,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            70,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            70,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            70,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            55,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            55,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            70,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            70,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            102,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            102,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            102,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            102,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            87,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            87,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            102,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            102,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            68,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            68,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            68,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            68,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            53,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            53,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            68,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            68,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            100,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            100,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            100,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            100,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            85,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            85,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            100,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            100,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            67,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            67,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            67,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            67,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            51,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            51,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            67,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            67,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            55,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            55,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            54,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            54,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            54,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            54,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            54,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            68,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            68,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            68,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            68,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            68,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            99,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            99,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            99,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            99,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            83,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            83,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            99,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            99,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            64,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            64,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            64,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            64,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            48,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            48,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            64,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            64,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            51,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            51,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            51,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            51,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            51,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            65,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            65,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            65,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            65,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            65,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            96,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            96,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            96,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            96,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            80,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            80,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            96,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            96,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            71,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            71,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            71,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            71,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            56,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            56,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            71,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            71,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            103,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            103,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            103,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            103,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            88,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            88,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            103,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            103,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            69,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            69,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            69,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            69,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            54,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            54,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            69,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            69,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            101,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            101,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            101,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            101,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            86,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            86,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            101,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            101,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            74,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            74,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            74,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            74,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            59,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            59,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            74,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            74,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            62,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            62,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            62,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            62,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            62,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            75,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            75,
            Col(
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
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            75,
            Col(
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
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            75,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            75,
            Col(
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
  case object GlobalAtomicXorX2 extends InstructionDef(
        "global_atomic_xor_x2",
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicXorX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicXorX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            106,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            106,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            106,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            106,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            91,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            91,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            106,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            106,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalInv extends InstructionDef(
        "global_inv",
        decoder(form(0)((_, mods) => Instruction.GlobalInv(mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            43,
            Col.empty,
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            43,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            40,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            98,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            96,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            97,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            95,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            23,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            23,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            20,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            20,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            21,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            21,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            21,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            21,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            21,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            22,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            22,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            22,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            22,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            22,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            83,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            83,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            32,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            32,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            32,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            32,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            32,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            35,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            35,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            35,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            35,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            35,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            34,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            34,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            33,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            33,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            31,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            31,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            31,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            31,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            31,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            30,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            30,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            20,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            20,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            20,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            20,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            12,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            12,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            20,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            20,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            22,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            21,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            21,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            21,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            21,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            13,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            13,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            21,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            21,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            22,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            22,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            22,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            22,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            15,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            15,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            22,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            22,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            23,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            23,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            23,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            23,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            14,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            14,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            23,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            23,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            19,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            19,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            19,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            19,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            19,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            17,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            17,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            17,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            17,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            17,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            42,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            42,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            126,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            125,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            39,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            39,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            41,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            41,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            38,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            38,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            40,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "SDST_M0", 32, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            40,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            114,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            112,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            113,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            17,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            17,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            17,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            17,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            9,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            9,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            17,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            17,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            34,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            34,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            34,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            34,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            34,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            34,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            34,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            34,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            35,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            35,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            35,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            35,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            35,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            35,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            35,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            35,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            36,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            36,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            36,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            36,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            36,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            36,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            36,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            36,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            37,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            37,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            37,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            37,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            37,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            37,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            37,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            37,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            19,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            19,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            19,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            19,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            11,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            11,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            19,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            19,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            87,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            115,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            116,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            88,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            87,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            88,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            18,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            18,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            18,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            18,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            18,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            16,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            16,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            16,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            16,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, true, false, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            16,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            16,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            16,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            16,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            16,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            8,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            8,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            16,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            16,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            32,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            32,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            32,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            32,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            32,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            32,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            32,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            32,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            33,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            33,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            33,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            33,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            33,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            33,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            33,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            33,
            Col.empty,
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
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "FLAT_GLBL",
            18,
            Col(
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
            Family.CDNA2,
            "cdna2",
            "FLAT_GLBL",
            18,
            Col(
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
            Family.CDNA3,
            "cdna3",
            "FLAT_GLBL",
            18,
            Col(
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
            Family.CDNA4,
            "cdna4",
            "FLAT_GLBL",
            18,
            Col(
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
            Family.GFX10,
            "rdna1",
            "FLAT_GLBL",
            10,
            Col(
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
            Family.GFX10,
            "rdna2",
            "FLAT_GLBL",
            10,
            Col(
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
            Family.GFX8,
            "gfx8",
            "GLOBAL",
            18,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Family.GFX9,
            "gfx9",
            "GLOBAL",
            18,
            Col.empty,
            Some("vmem"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object GlobalPrefetchB8 extends InstructionDef(
        "global_prefetch_b8",
        decoder(form(2)((operands, mods) => Instruction.GlobalPrefetchB8(operands(0), operands(1), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            93,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            41,
            Col(
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            41,
            Col(
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            41,
            Col(
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            41,
            Col(
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            41,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            102,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            100,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            101,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            99,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            29,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            29,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            29,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            29,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 128, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 128, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            29,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            25,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            25,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            25,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            25,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 16, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            25,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            26,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            26,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            26,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            26,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 32, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            26,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            27,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            27,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            27,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            27,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 64, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 64, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            27,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            24,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            24,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            24,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            24,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 32, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 8, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            24,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            28,
            Col(
              OperandInfo(Some("VADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("VSRC"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            28,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3",
            "FLAT_GLOBAL",
            28,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "FLAT_GLOBAL",
            28,
            Col(
              OperandInfo(Some("ADDR"), "VGPR", 64, true, false, false),
              OperandInfo(Some("DATA"), "VGPR", 96, true, false, false),
              OperandInfo(Some("SADDR"), "SREG", 64, true, false, false),
              OperandInfo(None, "GPUMEM", 96, false, true, true)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            28,
            Col(
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
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VGLOBAL",
            84,
            Col(
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
            Family.GFX12,
            "rdna4",
            "VGLOBAL",
            84,
            Col(
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

  val definitions: Col[InstructionDef] = Col(
    ClusterLoadAsyncToLdsB128,
    ClusterLoadAsyncToLdsB32,
    ClusterLoadAsyncToLdsB64,
    ClusterLoadAsyncToLdsB8,
    ClusterLoadB128,
    ClusterLoadB32,
    ClusterLoadB64,
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
    FlatLoadShortD16,
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
    GlobalAtomicXorB64,
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
    GlobalStoreBlock
  )
}
