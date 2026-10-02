package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object IscaddNegativeFixtures {
  val iscaddNegativeProbeCudaSource: String =
    """#include <stdint.h>
      |
      |extern "C" __global__ void index_scaled_minus_base(const uint32_t* index, const uint32_t* base, uint32_t* out) {
      |  const uint32_t i = blockIdx.x * blockDim.x + threadIdx.x;
      |  out[i] = base[i] - (index[i] << 8);
      |}
      |
      |extern "C" __global__ void index_scaled7_minus_base(const uint32_t* index, const uint32_t* base, uint32_t* out) {
      |  const uint32_t i = blockIdx.x * blockDim.x + threadIdx.x;
      |  out[i] = base[i] - (index[i] << 7);
      |}
      |
      |extern "C" __global__ void index_scaled9_minus_base(const uint32_t* index, const uint32_t* base, uint32_t* out) {
      |  const uint32_t i = blockIdx.x * blockDim.x + threadIdx.x;
      |  out[i] = base[i] - (index[i] << 9);
      |}
      |
      |extern "C" __global__ void guarded_index_scaled_minus_base(const uint32_t* index, const uint32_t* base, uint32_t* out) {
      |  const uint32_t i = blockIdx.x * blockDim.x + threadIdx.x;
      |  if ((index[i] & 1u) != 0u) out[i] = base[i] - (index[i] << 8);
      |  else out[i] = base[i];
      |}
      |""".stripMargin

  val iscaddNegativeGuardedIndexScaledMinusBaseSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.guarded_index_scaled_minus_base --------------------------
      |	.section	.text.guarded_index_scaled_minus_base,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	32
      |        .global         guarded_index_scaled_minus_base
      |        .type           guarded_index_scaled_minus_base,@function
      |        .size           guarded_index_scaled_minus_base,(.L_x_1 - guarded_index_scaled_minus_base)
      |        .other          guarded_index_scaled_minus_base,@"STO_CUDA_ENTRY STV_DEFAULT"
      |guarded_index_scaled_minus_base:
      |.text.guarded_index_scaled_minus_base:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R6, R0.reuse, 0x2 ;
      |        /*0050*/                   SHR.U32 R7, R0, 0x1e ;
      |        /*0058*/                   IADD R2.CC, R6.reuse, c[0x0][0x140] ;
      |        /*0068*/                   IADD.X R3, R7.reuse, c[0x0][0x144] ;
      |        /*0070*/                   LDG.E R2, [R2] ;
      |        /*0078*/                   IADD R4.CC, R6.reuse, c[0x0][0x148] ;
      |        /*0088*/                   IADD.X R5, R7, c[0x0][0x14c] ;
      |        /*0090*/                   LDG.E R4, [R4] ;
      |        /*0098*/                   IADD R6.CC, R6, c[0x0][0x150] ;
      |        /*00a8*/                   IADD.X R7, R7, c[0x0][0x154] ;
      |        /*00b0*/                   DEPBAR.LE SB5, 0x1 ;
      |        /*00b8*/                   LOP32I.AND R0, R2.reuse, 0x1 ;
      |        /*00c8*/                   SHL R8, R2, 0x8 ;
      |        /*00d0*/                   IADD R9, -R0, RZ ;
      |        /*00d8*/                   LOP.AND R9, R9, R8 ;
      |        /*00e8*/                   IADD R9, R4, -R9 ;
      |        /*00f0*/                   STG.E [R6], R9 ;
      |        /*00f8*/                   EXIT ;
      |.L_x_0:
      |        /*0108*/                   BRA `(.L_x_0) ;
      |        /*0110*/                   NOP;
      |        /*0118*/                   NOP;
      |        /*0128*/                   NOP;
      |        /*0130*/                   NOP;
      |        /*0138*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iscaddNegativeGuardedIndexScaledMinusBaseSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.guarded_index_scaled_minus_base --------------------------
      |	.section	.text.guarded_index_scaled_minus_base,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	32
      |        .global         guarded_index_scaled_minus_base
      |        .type           guarded_index_scaled_minus_base,@function
      |        .size           guarded_index_scaled_minus_base,(.L_x_1 - guarded_index_scaled_minus_base)
      |        .other          guarded_index_scaled_minus_base,@"STO_CUDA_ENTRY STV_DEFAULT"
      |guarded_index_scaled_minus_base:
      |.text.guarded_index_scaled_minus_base:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R6, R0.reuse, 0x2 ;
      |        /*0050*/                   SHR.U32 R7, R0, 0x1e ;
      |        /*0058*/                   IADD R2.CC, R6.reuse, c[0x0][0x140] ;
      |        /*0068*/                   IADD.X R3, R7.reuse, c[0x0][0x144] ;
      |        /*0070*/                   LDG.E R2, [R2] ;
      |        /*0078*/                   IADD R4.CC, R6.reuse, c[0x0][0x148] ;
      |        /*0088*/                   IADD.X R5, R7, c[0x0][0x14c] ;
      |        /*0090*/                   LDG.E R4, [R4] ;
      |        /*0098*/                   IADD R6.CC, R6, c[0x0][0x150] ;
      |        /*00a8*/                   IADD.X R7, R7, c[0x0][0x154] ;
      |        /*00b0*/                   DEPBAR.LE SB5, 0x1 ;
      |        /*00b8*/                   LOP32I.AND R0, R2.reuse, 0x1 ;
      |        /*00c8*/                   SHL R8, R2, 0x8 ;
      |        /*00d0*/                   IADD R9, -R0, RZ ;
      |        /*00d8*/                   LOP.AND R9, R9, R8 ;
      |        /*00e8*/                   IADD R9, R4, -R9 ;
      |        /*00f0*/                   STG.E [R6], R9 ;
      |        /*00f8*/                   EXIT ;
      |.L_x_0:
      |        /*0108*/                   BRA `(.L_x_0) ;
      |        /*0110*/                   NOP;
      |        /*0118*/                   NOP;
      |        /*0128*/                   NOP;
      |        /*0130*/                   NOP;
      |        /*0138*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iscaddNegativeIndexScaled7MinusBaseSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.index_scaled7_minus_base --------------------------
      |	.section	.text.index_scaled7_minus_base,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	32
      |        .global         index_scaled7_minus_base
      |        .type           index_scaled7_minus_base,@function
      |        .size           index_scaled7_minus_base,(.L_x_1 - index_scaled7_minus_base)
      |        .other          index_scaled7_minus_base,@"STO_CUDA_ENTRY STV_DEFAULT"
      |index_scaled7_minus_base:
      |.text.index_scaled7_minus_base:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R6, R0.reuse, 0x2 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1e ;
      |        /*0058*/                   IADD R2.CC, R6.reuse, c[0x0][0x148] ;
      |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x14c] ;
      |        /*0070*/         {         IADD R4.CC, R6.reuse, c[0x0][0x140] ;
      |        /*0078*/                   LDG.E R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0, c[0x0][0x144] ;
      |        /*0090*/                   LDG.E R4, [R4] ;
      |        /*0098*/                   IADD R6.CC, R6, c[0x0][0x150] ;
      |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
      |        /*00b0*/                   ISCADD R0, -R4, R2, 0x7 ;
      |        /*00b8*/                   STG.E [R6], R0 ;
      |        /*00c8*/                   EXIT ;
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0) ;
      |        /*00d8*/                   NOP;
      |        /*00e8*/                   NOP;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iscaddNegativeIndexScaled7MinusBaseSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.index_scaled7_minus_base --------------------------
      |	.section	.text.index_scaled7_minus_base,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	32
      |        .global         index_scaled7_minus_base
      |        .type           index_scaled7_minus_base,@function
      |        .size           index_scaled7_minus_base,(.L_x_1 - index_scaled7_minus_base)
      |        .other          index_scaled7_minus_base,@"STO_CUDA_ENTRY STV_DEFAULT"
      |index_scaled7_minus_base:
      |.text.index_scaled7_minus_base:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R6, R0.reuse, 0x2 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1e ;
      |        /*0058*/                   IADD R2.CC, R6.reuse, c[0x0][0x148] ;
      |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x14c] ;
      |        /*0070*/         {         IADD R4.CC, R6.reuse, c[0x0][0x140] ;
      |        /*0078*/                   LDG.E R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0, c[0x0][0x144] ;
      |        /*0090*/                   LDG.E R4, [R4] ;
      |        /*0098*/                   IADD R6.CC, R6, c[0x0][0x150] ;
      |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
      |        /*00b0*/                   ISCADD R0, -R4, R2, 0x7 ;
      |        /*00b8*/                   STG.E [R6], R0 ;
      |        /*00c8*/                   EXIT ;
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0) ;
      |        /*00d8*/                   NOP;
      |        /*00e8*/                   NOP;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iscaddNegativeIndexScaled9MinusBaseSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.index_scaled9_minus_base --------------------------
      |	.section	.text.index_scaled9_minus_base,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	32
      |        .global         index_scaled9_minus_base
      |        .type           index_scaled9_minus_base,@function
      |        .size           index_scaled9_minus_base,(.L_x_1 - index_scaled9_minus_base)
      |        .other          index_scaled9_minus_base,@"STO_CUDA_ENTRY STV_DEFAULT"
      |index_scaled9_minus_base:
      |.text.index_scaled9_minus_base:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R6, R0.reuse, 0x2 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1e ;
      |        /*0058*/                   IADD R2.CC, R6.reuse, c[0x0][0x148] ;
      |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x14c] ;
      |        /*0070*/         {         IADD R4.CC, R6.reuse, c[0x0][0x140] ;
      |        /*0078*/                   LDG.E R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0, c[0x0][0x144] ;
      |        /*0090*/                   LDG.E R4, [R4] ;
      |        /*0098*/                   IADD R6.CC, R6, c[0x0][0x150] ;
      |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
      |        /*00b0*/                   ISCADD R0, -R4, R2, 0x9 ;
      |        /*00b8*/                   STG.E [R6], R0 ;
      |        /*00c8*/                   EXIT ;
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0) ;
      |        /*00d8*/                   NOP;
      |        /*00e8*/                   NOP;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iscaddNegativeIndexScaled9MinusBaseSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.index_scaled9_minus_base --------------------------
      |	.section	.text.index_scaled9_minus_base,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	32
      |        .global         index_scaled9_minus_base
      |        .type           index_scaled9_minus_base,@function
      |        .size           index_scaled9_minus_base,(.L_x_1 - index_scaled9_minus_base)
      |        .other          index_scaled9_minus_base,@"STO_CUDA_ENTRY STV_DEFAULT"
      |index_scaled9_minus_base:
      |.text.index_scaled9_minus_base:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R6, R0.reuse, 0x2 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1e ;
      |        /*0058*/                   IADD R2.CC, R6.reuse, c[0x0][0x148] ;
      |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x14c] ;
      |        /*0070*/         {         IADD R4.CC, R6.reuse, c[0x0][0x140] ;
      |        /*0078*/                   LDG.E R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0, c[0x0][0x144] ;
      |        /*0090*/                   LDG.E R4, [R4] ;
      |        /*0098*/                   IADD R6.CC, R6, c[0x0][0x150] ;
      |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
      |        /*00b0*/                   ISCADD R0, -R4, R2, 0x9 ;
      |        /*00b8*/                   STG.E [R6], R0 ;
      |        /*00c8*/                   EXIT ;
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0) ;
      |        /*00d8*/                   NOP;
      |        /*00e8*/                   NOP;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iscaddNegativeIndexScaledMinusBaseSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.index_scaled_minus_base --------------------------
      |	.section	.text.index_scaled_minus_base,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	32
      |        .global         index_scaled_minus_base
      |        .type           index_scaled_minus_base,@function
      |        .size           index_scaled_minus_base,(.L_x_1 - index_scaled_minus_base)
      |        .other          index_scaled_minus_base,@"STO_CUDA_ENTRY STV_DEFAULT"
      |index_scaled_minus_base:
      |.text.index_scaled_minus_base:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R6, R0.reuse, 0x2 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1e ;
      |        /*0058*/                   IADD R2.CC, R6.reuse, c[0x0][0x148] ;
      |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x14c] ;
      |        /*0070*/         {         IADD R4.CC, R6.reuse, c[0x0][0x140] ;
      |        /*0078*/                   LDG.E R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0, c[0x0][0x144] ;
      |        /*0090*/                   LDG.E R4, [R4] ;
      |        /*0098*/                   IADD R6.CC, R6, c[0x0][0x150] ;
      |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
      |        /*00b0*/                   ISCADD R0, -R4, R2, 0x8 ;
      |        /*00b8*/                   STG.E [R6], R0 ;
      |        /*00c8*/                   EXIT ;
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0) ;
      |        /*00d8*/                   NOP;
      |        /*00e8*/                   NOP;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iscaddNegativeIndexScaledMinusBaseSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.index_scaled_minus_base --------------------------
      |	.section	.text.index_scaled_minus_base,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	32
      |        .global         index_scaled_minus_base
      |        .type           index_scaled_minus_base,@function
      |        .size           index_scaled_minus_base,(.L_x_1 - index_scaled_minus_base)
      |        .other          index_scaled_minus_base,@"STO_CUDA_ENTRY STV_DEFAULT"
      |index_scaled_minus_base:
      |.text.index_scaled_minus_base:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R6, R0.reuse, 0x2 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1e ;
      |        /*0058*/                   IADD R2.CC, R6.reuse, c[0x0][0x148] ;
      |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x14c] ;
      |        /*0070*/         {         IADD R4.CC, R6.reuse, c[0x0][0x140] ;
      |        /*0078*/                   LDG.E R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0, c[0x0][0x144] ;
      |        /*0090*/                   LDG.E R4, [R4] ;
      |        /*0098*/                   IADD R6.CC, R6, c[0x0][0x150] ;
      |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
      |        /*00b0*/                   ISCADD R0, -R4, R2, 0x8 ;
      |        /*00b8*/                   STG.E [R6], R0 ;
      |        /*00c8*/                   EXIT ;
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0) ;
      |        /*00d8*/                   NOP;
      |        /*00e8*/                   NOP;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class IscaddNegativeFixtures extends NvidiaFixtureSuite {
  import IscaddNegativeFixtures.*

  for (
    (name, sm, source, expected) <- Vec(
      (
        "iscaddNegativeGuardedIndexScaledMinusBaseSm50",
        50,
        iscaddNegativeProbeCudaSource,
        iscaddNegativeGuardedIndexScaledMinusBaseSm50
      ),
      (
        "iscaddNegativeGuardedIndexScaledMinusBaseSm60",
        60,
        iscaddNegativeProbeCudaSource,
        iscaddNegativeGuardedIndexScaledMinusBaseSm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -ptx -arch=sm_$sm probe.cu -o probe-sm$sm.ptx",
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm probe.cu -o probe-sm$sm.cubin",
      s"symbol_index=$$(readelf -sW probe-sm$sm.cubin | awk -v name=\"guarded_index_scaled_minus_base\" '$$NF == name { sub(\":\", \"\", $$1); print $$1; exit }')",
      "test -n \"$symbol_index\"",
      s"nvdisasm -c -ndf -fun \"$$symbol_index\" probe-sm$sm.cubin > guarded_index_scaled_minus_base-sm$sm.sass"
    ),
    output = s"guarded_index_scaled_minus_base-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "iscaddNegativeIndexScaled7MinusBaseSm50",
        50,
        iscaddNegativeProbeCudaSource,
        iscaddNegativeIndexScaled7MinusBaseSm50
      ),
      (
        "iscaddNegativeIndexScaled7MinusBaseSm60",
        60,
        iscaddNegativeProbeCudaSource,
        iscaddNegativeIndexScaled7MinusBaseSm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -ptx -arch=sm_$sm probe.cu -o probe-sm$sm.ptx",
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm probe.cu -o probe-sm$sm.cubin",
      s"symbol_index=$$(readelf -sW probe-sm$sm.cubin | awk -v name=\"index_scaled7_minus_base\" '$$NF == name { sub(\":\", \"\", $$1); print $$1; exit }')",
      "test -n \"$symbol_index\"",
      s"nvdisasm -c -ndf -fun \"$$symbol_index\" probe-sm$sm.cubin > index_scaled7_minus_base-sm$sm.sass"
    ),
    output = s"index_scaled7_minus_base-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "iscaddNegativeIndexScaled9MinusBaseSm50",
        50,
        iscaddNegativeProbeCudaSource,
        iscaddNegativeIndexScaled9MinusBaseSm50
      ),
      (
        "iscaddNegativeIndexScaled9MinusBaseSm60",
        60,
        iscaddNegativeProbeCudaSource,
        iscaddNegativeIndexScaled9MinusBaseSm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -ptx -arch=sm_$sm probe.cu -o probe-sm$sm.ptx",
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm probe.cu -o probe-sm$sm.cubin",
      s"symbol_index=$$(readelf -sW probe-sm$sm.cubin | awk -v name=\"index_scaled9_minus_base\" '$$NF == name { sub(\":\", \"\", $$1); print $$1; exit }')",
      "test -n \"$symbol_index\"",
      s"nvdisasm -c -ndf -fun \"$$symbol_index\" probe-sm$sm.cubin > index_scaled9_minus_base-sm$sm.sass"
    ),
    output = s"index_scaled9_minus_base-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      (
        "iscaddNegativeIndexScaledMinusBaseSm50",
        50,
        iscaddNegativeProbeCudaSource,
        iscaddNegativeIndexScaledMinusBaseSm50
      ),
      (
        "iscaddNegativeIndexScaledMinusBaseSm60",
        60,
        iscaddNegativeProbeCudaSource,
        iscaddNegativeIndexScaledMinusBaseSm60
      )
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -ptx -arch=sm_$sm probe.cu -o probe-sm$sm.ptx",
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm probe.cu -o probe-sm$sm.cubin",
      s"symbol_index=$$(readelf -sW probe-sm$sm.cubin | awk -v name=\"index_scaled_minus_base\" '$$NF == name { sub(\":\", \"\", $$1); print $$1; exit }')",
      "test -n \"$symbol_index\"",
      s"nvdisasm -c -ndf -fun \"$$symbol_index\" probe-sm$sm.cubin > index_scaled_minus_base-sm$sm.sass"
    ),
    output = s"index_scaled_minus_base-sm$sm.sass",
    expected = expected
  )

}
