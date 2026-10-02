package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object Iadd3Fixtures {
  val iadd3LegacyMulwidePtxSource: String =
    """.version 7.8
      |.target sm_50
      |.address_size 64
      |.visible .entry mulwide(.param .u64 pOut, .param .u64 pA, .param .u64 pB, .param .u32 pCount) {
      |  .reg .pred %p;
      |  .reg .b32 %r<8>;
      |  .reg .b64 %rd<8>;
      |  ld.param.u64 %rd0, [pOut];
      |  ld.param.u64 %rd1, [pA];
      |  ld.param.u64 %rd2, [pB];
      |  ld.param.u32 %r7, [pCount];
      |  mov.u32 %r0, %tid.x;
      |  setp.ge.u32 %p, %r0, %r7;
      |  @%p bra DONE;
      |  shl.b32 %r1, %r0, 2;
      |  cvt.u64.u32 %rd3, %r1;
      |  add.u64 %rd4, %rd1, %rd3;
      |  add.u64 %rd5, %rd2, %rd3;
      |  ld.global.u32 %r2, [%rd4];
      |  ld.global.u32 %r3, [%rd5];
      |  mul.wide.u32 %rd6, %r2, %r3;
      |  shl.b64 %rd7, %rd3, 1;
      |  add.u64 %rd7, %rd0, %rd7;
      |  st.global.u64 [%rd7], %rd6;
      |DONE:
      |  ret;
      |}
      |""".stripMargin

  val iadd3LegacyProbeConstantCudaSource: String =
    """#include <stdint.h>
      |
      |extern "C" __global__ void probe_constant(uint64_t *out, const uint64_t *a, const uint64_t *b, uint64_t c) {
      |  unsigned i = threadIdx.x;
      |  out[i] = a[i] + b[i] + c;
      |}
      |""".stripMargin

  val iadd3LegacyProbeMultipairCudaSource: String =
    """#include <stdint.h>
      |
      |extern "C" __global__ void probe_multi(uint64_t *out, const uint64_t *a, const uint64_t *b, const uint64_t *c) {
      |  unsigned i = threadIdx.x;
      |  uint64_t s0 = a[4 * i] + b[4 * i] + c[4 * i];
      |  uint64_t s1 = a[4 * i + 1] + b[4 * i + 1] + c[4 * i + 1];
      |  uint64_t s2 = a[4 * i + 2] + b[4 * i + 2] + c[4 * i + 2];
      |  uint64_t s3 = a[4 * i + 3] + b[4 * i + 3] + c[4 * i + 3];
      |  out[4 * i] = s0;
      |  out[4 * i + 1] = s1;
      |  out[4 * i + 2] = s2;
      |  out[4 * i + 3] = s3;
      |}
      |""".stripMargin

  val iadd3LegacyProbeCudaSource: String =
    """#include <stdint.h>
      |extern "C" __global__ void probe(uint64_t *out, const uint64_t *a, const uint64_t *b, const uint64_t *c) {
      |  unsigned i = threadIdx.x;
      |  out[i] = a[i] + b[i] + c[i];
      |}
      |""".stripMargin

  val iadd3NegativeProbeCudaSource: String =
    """#include <stdint.h>
      |
      |extern "C" __global__ void add_constant_sub(const uint64_t* a, const uint32_t* b, uint64_t* out) {
      |  const unsigned i = blockIdx.x * blockDim.x + threadIdx.x;
      |  out[i] = a[i] + 0x3f00ULL - static_cast<uint64_t>(b[i]);
      |}
      |
      |extern "C" __global__ void add_large_constant_sub(const uint64_t* a, const uint32_t* b, uint64_t* out) {
      |  const unsigned i = blockIdx.x * blockDim.x + threadIdx.x;
      |  out[i] = a[i] + 0xfc100ULL - static_cast<uint64_t>(b[i]);
      |}
      |
      |extern "C" __global__ void negative_constant_sub(const uint64_t* a, const uint64_t* b, uint64_t* out) {
      |  const unsigned i = blockIdx.x * blockDim.x + threadIdx.x;
      |  out[i] = a[i] - 0x3f00ULL - b[i];
      |}
      |
      |extern "C" __global__ void add_sub(const uint64_t* a, const uint64_t* b, const uint64_t* c, uint64_t* out) {
      |  const unsigned i = blockIdx.x * blockDim.x + threadIdx.x;
      |  out[i] = a[i] + b[i] - c[i];
      |}
      |""".stripMargin

  val eq64ExtendedSm35: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.eq64                --------------------------
      |	.section	.text.eq64,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	64
      |        .global         eq64
      |        .type           eq64,@function
      |        .size           eq64,(.L_x_1 - eq64)
      |        .other          eq64,@"STO_CUDA_ENTRY STV_DEFAULT"
      |eq64:
      |.text.eq64:
      |        /*0008*/                   MOV R1, c[0x0][0x44];
      |        /*0010*/                   MOV R0, c[0x0][0x148];
      |        /*0018*/                   MOV R5, c[0x0][0x14c];
      |        /*0020*/                   MOV R2, c[0x0][0x140];
      |        /*0028*/                   MOV R3, c[0x0][0x144];
      |        /*0030*/                   ISUB RZ.CC, R0, c[0x0][0x150];
      |        /*0038*/                   ISET.EQ.X.AND R4, R5, c[0x0][0x154], PT;
      |        /*0048*/                   ISET.NE.X.AND R5, R5, c[0x0][0x154], PT;
      |        /*0050*/                   LOP32I.AND R4, R4, 0x1;
      |        /*0058*/                   ISUB RZ.CC, c[0x0][0x148], RZ;
      |        /*0060*/                   LOP32I.AND R5, R5, 0x1;
      |        /*0068*/                   ST.E [R2], R4;
      |        /*0070*/                   ISET.EQ.X.AND R6, RZ, c[0x0][0x14c], PT;
      |        /*0078*/                   ST.E [R2+0x4], R5;
      |        /*0088*/                   ISUB RZ.CC, c[0x0][0x150], RZ;
      |        /*0090*/                   LOP32I.AND R6, R6, 0x1;
      |        /*0098*/                   ST.E [R2+0x8], R6;
      |        /*00a0*/                   ISET.EQ.X.AND R7, RZ, c[0x0][0x154], PT;
      |        /*00a8*/                   LOP32I.AND R7, R7, 0x1;
      |        /*00b0*/                   ST.E [R2+0xc], R7;
      |        /*00b8*/                   EXIT;
      |.L_x_0:
      |        /*00c0*/                   BRA `(.L_x_0);
      |        /*00c8*/                   NOP;
      |        /*00d0*/                   NOP;
      |        /*00d8*/                   NOP;
      |        /*00e0*/                   NOP;
      |        /*00e8*/                   NOP;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val eq64sExtendedSm35: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.eq64s               --------------------------
      |	.section	.text.eq64s,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=6"
      |	.align	64
      |        .global         eq64s
      |        .type           eq64s,@function
      |        .size           eq64s,(.L_x_1 - eq64s)
      |        .other          eq64s,@"STO_CUDA_ENTRY STV_DEFAULT"
      |eq64s:
      |.text.eq64s:
      |        /*0008*/                   MOV R1, c[0x0][0x44];
      |        /*0010*/                   MOV R0, c[0x0][0x148];
      |        /*0018*/                   MOV R5, c[0x0][0x14c];
      |        /*0020*/                   MOV R2, c[0x0][0x140];
      |        /*0028*/                   MOV R3, c[0x0][0x144];
      |        /*0030*/                   ISUB RZ.CC, R0, c[0x0][0x150];
      |        /*0038*/                   ISET.EQ.X.AND R4, R5, c[0x0][0x154], PT;
      |        /*0048*/                   ISET.NE.X.AND R5, R5, c[0x0][0x154], PT;
      |        /*0050*/                   LOP32I.AND R4, R4, 0x1;
      |        /*0058*/                   LOP32I.AND R5, R5, 0x1;
      |        /*0060*/                   ST.E [R2], R4;
      |        /*0068*/                   ST.E [R2+0x4], R5;
      |        /*0070*/                   EXIT;
      |.L_x_0:
      |        /*0078*/                   BRA `(.L_x_0);
      |.L_x_1:
      |""".stripMargin

  val iadd3LegacyMulwideSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.mulwide             --------------------------
      |	.section	.text.mulwide,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=14"
      |	.align	32
      |        .global         mulwide
      |        .type           mulwide,@function
      |        .size           mulwide,(.L_x_1 - mulwide)
      |        .other          mulwide,@"STO_CUDA_ENTRY STV_DEFAULT"
      |mulwide:
      |.text.mulwide:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_TID.X ;
      |        /*0018*/                   ISETP.GE.U32.AND P0, PT, R0, c[0x0][0x158], PT ;
      |        /*0028*/                   NOP ;
      |        /*0030*/                   NOP ;
      |        /*0038*/               @P0 EXIT ;
      |        /*0048*/                   SHL R0, R0, 0x2 ;
      |        /*0050*/                   IADD R2.CC, R0.reuse, c[0x0][0x148] ;
      |        /*0058*/                   IADD.X R3, RZ, c[0x0][0x14c] ;
      |        /*0068*/         {         IADD R4.CC, R0.reuse, c[0x0][0x150] ;
      |        /*0070*/                   LDG.E R2, [R2]         }
      |        /*0078*/                   IADD.X R5, RZ, c[0x0][0x154] ;
      |        /*0088*/                   LDG.E R4, [R4] ;
      |        /*0090*/                   LEA R8.CC, R0, c[0x0][0x140], 0x1 ;
      |        /*0098*/                   XMAD R9, R2, R4, RZ ;
      |        /*00a8*/                   XMAD R7, R2.reuse, R4.reuse, RZ ;
      |        /*00b0*/                   XMAD.MRG R12, R2.reuse, R4.H1.reuse, RZ ;
      |        /*00b8*/                   XMAD R10, R2.reuse, R4.H1.reuse, RZ ;
      |        /*00c8*/                   XMAD R11, R2.H1.reuse, R4.H1.reuse, RZ ;
      |        /*00d0*/                   XMAD.CHI R13, R2.H1, R4, R9 ;
      |        /*00d8*/                   LEA.HI.X R9, R0, c[0x0][0x144], RZ, 0x1 ;
      |        /*00e8*/                   XMAD.PSL.CBCC R6, R2.H1, R12.H1, R7 ;
      |        /*00f0*/                   IADD3.RS R7, R13, R10, R11 ;
      |        /*00f8*/                   STG.E.64 [R8], R6 ;
      |        /*0108*/                   EXIT ;
      |.L_x_0:
      |        /*0110*/                   BRA `(.L_x_0) ;
      |        /*0118*/                   NOP;
      |        /*0128*/                   NOP;
      |        /*0130*/                   NOP;
      |        /*0138*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iadd3LegacyMulwideSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.mulwide             --------------------------
      |	.section	.text.mulwide,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=14"
      |	.align	32
      |        .global         mulwide
      |        .type           mulwide,@function
      |        .size           mulwide,(.L_x_1 - mulwide)
      |        .other          mulwide,@"STO_CUDA_ENTRY STV_DEFAULT"
      |mulwide:
      |.text.mulwide:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_TID.X ;
      |        /*0018*/                   ISETP.GE.U32.AND P0, PT, R0, c[0x0][0x158], PT ;
      |        /*0028*/                   NOP ;
      |        /*0030*/                   NOP ;
      |        /*0038*/               @P0 EXIT ;
      |        /*0048*/                   SHL R0, R0, 0x2 ;
      |        /*0050*/                   IADD R2.CC, R0, c[0x0][0x148] ;
      |        /*0058*/                   IADD.X R3, RZ, c[0x0][0x14c] ;
      |        /*0068*/         {         IADD R4.CC, R0, c[0x0][0x150] ;
      |        /*0070*/                   LDG.E R2, [R2]         }
      |        /*0078*/                   IADD.X R5, RZ, c[0x0][0x154] ;
      |        /*0088*/                   LDG.E R4, [R4] ;
      |        /*0090*/                   LEA R8.CC, R0, c[0x0][0x140], 0x1 ;
      |        /*0098*/                   XMAD R9, R2, R4, RZ ;
      |        /*00a8*/                   XMAD R7, R2, R4, RZ ;
      |        /*00b0*/                   XMAD.MRG R12, R2, R4.H1, RZ ;
      |        /*00b8*/                   XMAD R10, R2, R4.H1, RZ ;
      |        /*00c8*/                   XMAD R11, R2.H1, R4.H1, RZ ;
      |        /*00d0*/                   XMAD.CHI R13, R2.H1, R4, R9 ;
      |        /*00d8*/                   LEA.HI.X R9, R0, c[0x0][0x144], RZ, 0x1 ;
      |        /*00e8*/                   XMAD.PSL.CBCC R6, R2.H1, R12.H1, R7 ;
      |        /*00f0*/                   IADD3.RS R7, R13, R10, R11 ;
      |        /*00f8*/                   STG.E.64 [R8], R6 ;
      |        /*0108*/                   EXIT ;
      |.L_x_0:
      |        /*0110*/                   BRA `(.L_x_0) ;
      |        /*0118*/                   NOP;
      |        /*0128*/                   NOP;
      |        /*0130*/                   NOP;
      |        /*0138*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iadd3LegacyProbeConstantSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe_constant      --------------------------
      |	.section	.text.probe_constant,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	32
      |        .global         probe_constant
      |        .type           probe_constant,@function
      |        .size           probe_constant,(.L_x_1 - probe_constant)
      |        .other          probe_constant,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe_constant:
      |.text.probe_constant:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_TID.X ;
      |        /*0018*/                   SHL R8, R0.reuse, 0x3 ;
      |        /*0028*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0030*/                   IADD R2.CC, R8.reuse, c[0x0][0x148] ;
      |        /*0038*/                   IADD.X R3, R0.reuse, c[0x0][0x14c] ;
      |        /*0048*/         {         IADD R4.CC, R8, c[0x0][0x150] ;
      |        /*0050*/                   LDG.E.64 R2, [R2]         }
      |        /*0058*/                   IADD.X R5, R0, c[0x0][0x154] ;
      |        /*0068*/                   LDG.E.64 R4, [R4] ;
      |        /*0070*/                   IADD R8.CC, R8, c[0x0][0x140] ;
      |        /*0078*/                   IADD.X R9, R0, c[0x0][0x144] ;
      |        /*0088*/                   IADD3 R6.CC, R4, c[0x0][0x158], R2 ;
      |        /*0090*/                   IADD3.X R7, R5, c[0x0][0x15c], R3 ;
      |        /*0098*/                   STG.E.64 [R8], R6 ;
      |        /*00a8*/                   EXIT ;
      |.L_x_0:
      |        /*00b0*/                   BRA `(.L_x_0) ;
      |        /*00b8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iadd3LegacyProbeConstantSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe_constant      --------------------------
      |	.section	.text.probe_constant,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	32
      |        .global         probe_constant
      |        .type           probe_constant,@function
      |        .size           probe_constant,(.L_x_1 - probe_constant)
      |        .other          probe_constant,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe_constant:
      |.text.probe_constant:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_TID.X ;
      |        /*0018*/                   SHL R8, R0.reuse, 0x3 ;
      |        /*0028*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0030*/                   IADD R2.CC, R8, c[0x0][0x148] ;
      |        /*0038*/                   IADD.X R3, R0, c[0x0][0x14c] ;
      |        /*0048*/         {         IADD R4.CC, R8, c[0x0][0x150] ;
      |        /*0050*/                   LDG.E.64 R2, [R2]         }
      |        /*0058*/                   IADD.X R5, R0, c[0x0][0x154] ;
      |        /*0068*/                   LDG.E.64 R4, [R4] ;
      |        /*0070*/                   IADD R8.CC, R8, c[0x0][0x140] ;
      |        /*0078*/                   IADD.X R9, R0, c[0x0][0x144] ;
      |        /*0088*/                   IADD3 R6.CC, R4, c[0x0][0x158], R2 ;
      |        /*0090*/                   IADD3.X R7, R5, c[0x0][0x15c], R3 ;
      |        /*0098*/                   STG.E.64 [R8], R6 ;
      |        /*00a8*/                   EXIT ;
      |.L_x_0:
      |        /*00b0*/                   BRA `(.L_x_0) ;
      |        /*00b8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iadd3LegacyProbeMultipairSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe_multi         --------------------------
      |	.section	.text.probe_multi,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=32"
      |	.align	32
      |        .global         probe_multi
      |        .type           probe_multi,@function
      |        .size           probe_multi,(.L_x_1 - probe_multi)
      |        .other          probe_multi,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe_multi:
      |.text.probe_multi:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_TID.X ;
      |        /*0018*/                   SHL R0, R0, 0x2 ;
      |        /*0028*/                   SHL R18, R0.reuse, 0x3 ;
      |        /*0030*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0038*/                   IADD R30.CC, R18.reuse, c[0x0][0x148] ;
      |        /*0048*/                   IADD.X R31, R0.reuse, c[0x0][0x14c] ;
      |        /*0050*/         {         IADD R8.CC, R18.reuse, c[0x0][0x150] ;
      |        /*0058*/                   LDG.E.64 R20, [R30]         }
      |        /*0068*/         {         IADD.X R9, R0.reuse, c[0x0][0x154] ;
      |        /*0070*/                   LDG.E.64 R6, [R30+0x8]         }
      |        /*0078*/         {         IADD R26.CC, R18, c[0x0][0x158] ;
      |        /*0088*/                   LDG.E.64 R4, [R30+0x10]         }
      |        /*0090*/         {         IADD.X R27, R0, c[0x0][0x15c] ;
      |        /*0098*/                   LDG.E.64 R22, [R8]         }
      |        /*00a8*/                   LDG.E.64 R14, [R8+0x8] ;
      |        /*00b0*/                   LDG.E.64 R24, [R26] ;
      |        /*00b8*/                   LDG.E.64 R16, [R26+0x8] ;
      |        /*00c8*/                   LDG.E.64 R12, [R8+0x10] ;
      |        /*00d0*/                   LDG.E.64 R10, [R26+0x10] ;
      |        /*00d8*/                   LDG.E.64 R2, [R30+0x18] ;
      |        /*00e8*/                   LDG.E.64 R8, [R8+0x18] ;
      |        /*00f0*/                   LDG.E.64 R28, [R26+0x18] ;
      |        /*00f8*/                   DEPBAR.LE SB5, 0x2 ;
      |        /*0108*/                   IADD3 R20.CC, R24, R22, R20 ;
      |        /*0110*/                   IADD3.X R21, R25, R23, R21 ;
      |        /*0118*/                   IADD3 R16.CC, R16, R14, R6 ;
      |        /*0128*/                   IADD3.X R17, R17, R15, R7 ;
      |        /*0130*/                   DEPBAR.LE SB5, 0x1 ;
      |        /*0138*/                   IADD3 R4.CC, R10, R12, R4 ;
      |        /*0148*/                   IADD3.X R5, R11, R13, R5 ;
      |        /*0150*/                   IADD R18.CC, R18, c[0x0][0x140] ;
      |        /*0158*/                   IADD.X R19, R0, c[0x0][0x144] ;
      |        /*0168*/         {         IADD3 R12.CC, R28, R8, R2 ;
      |        /*0170*/                   STG.E.64 [R18], R20         }
      |        /*0178*/         {         IADD3.X R13, R29, R9, R3 ;
      |        /*0188*/                   STG.E.64 [R18+0x8], R16         }
      |        /*0190*/                   STG.E.64 [R18+0x10], R4 ;
      |        /*0198*/                   STG.E.64 [R18+0x18], R12 ;
      |        /*01a8*/                   EXIT ;
      |.L_x_0:
      |        /*01b0*/                   BRA `(.L_x_0) ;
      |        /*01b8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iadd3LegacyProbeMultipairSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe_multi         --------------------------
      |	.section	.text.probe_multi,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=32"
      |	.align	32
      |        .global         probe_multi
      |        .type           probe_multi,@function
      |        .size           probe_multi,(.L_x_1 - probe_multi)
      |        .other          probe_multi,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe_multi:
      |.text.probe_multi:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_TID.X ;
      |        /*0018*/                   SHL R0, R0, 0x2 ;
      |        /*0028*/                   SHL R18, R0.reuse, 0x3 ;
      |        /*0030*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0038*/                   IADD R30.CC, R18, c[0x0][0x148] ;
      |        /*0048*/                   IADD.X R31, R0, c[0x0][0x14c] ;
      |        /*0050*/         {         IADD R8.CC, R18.reuse, c[0x0][0x150] ;
      |        /*0058*/                   LDG.E.64 R20, [R30]         }
      |        /*0068*/         {         IADD.X R9, R0.reuse, c[0x0][0x154] ;
      |        /*0070*/                   LDG.E.64 R6, [R30+0x8]         }
      |        /*0078*/         {         IADD R26.CC, R18, c[0x0][0x158] ;
      |        /*0088*/                   LDG.E.64 R4, [R30+0x10]         }
      |        /*0090*/         {         IADD.X R27, R0, c[0x0][0x15c] ;
      |        /*0098*/                   LDG.E.64 R22, [R8]         }
      |        /*00a8*/                   LDG.E.64 R24, [R26] ;
      |        /*00b0*/                   LDG.E.64 R14, [R8+0x8] ;
      |        /*00b8*/                   LDG.E.64 R16, [R26+0x8] ;
      |        /*00c8*/                   LDG.E.64 R12, [R8+0x10] ;
      |        /*00d0*/                   LDG.E.64 R10, [R26+0x10] ;
      |        /*00d8*/                   LDG.E.64 R2, [R30+0x18] ;
      |        /*00e8*/                   LDG.E.64 R8, [R8+0x18] ;
      |        /*00f0*/                   LDG.E.64 R28, [R26+0x18] ;
      |        /*00f8*/                   DEPBAR.LE SB5, 0x2 ;
      |        /*0108*/                   IADD3 R20.CC, R24, R22, R20 ;
      |        /*0110*/                   IADD3.X R21, R25, R23, R21 ;
      |        /*0118*/                   IADD3 R16.CC, R16, R14, R6 ;
      |        /*0128*/                   IADD3.X R17, R17, R15, R7 ;
      |        /*0130*/                   DEPBAR.LE SB5, 0x1 ;
      |        /*0138*/                   IADD3 R4.CC, R10, R12, R4 ;
      |        /*0148*/                   IADD3.X R5, R11, R13, R5 ;
      |        /*0150*/                   IADD R18.CC, R18, c[0x0][0x140] ;
      |        /*0158*/                   IADD.X R19, R0, c[0x0][0x144] ;
      |        /*0168*/         {         IADD3 R12.CC, R28, R8, R2 ;
      |        /*0170*/                   STG.E.64 [R18], R20         }
      |        /*0178*/         {         IADD3.X R13, R29, R9, R3 ;
      |        /*0188*/                   STG.E.64 [R18+0x8], R16         }
      |        /*0190*/                   STG.E.64 [R18+0x10], R4 ;
      |        /*0198*/                   STG.E.64 [R18+0x18], R12 ;
      |        /*01a8*/                   EXIT ;
      |.L_x_0:
      |        /*01b0*/                   BRA `(.L_x_0) ;
      |        /*01b8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iadd3LegacyProbeSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	32
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_TID.X ;
      |        /*0018*/                   SHL R10, R0.reuse, 0x3 ;
      |        /*0028*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0030*/                   IADD R2.CC, R10.reuse, c[0x0][0x148] ;
      |        /*0038*/                   IADD.X R3, R0.reuse, c[0x0][0x14c] ;
      |        /*0048*/         {         IADD R4.CC, R10, c[0x0][0x150] ;
      |        /*0050*/                   LDG.E.64 R2, [R2]         }
      |        /*0058*/                   IADD.X R5, R0, c[0x0][0x154] ;
      |        /*0068*/         {         IADD R6.CC, R10.reuse, c[0x0][0x158] ;
      |        /*0070*/                   LDG.E.64 R4, [R4]         }
      |        /*0078*/                   IADD.X R7, R0.reuse, c[0x0][0x15c] ;
      |        /*0088*/                   LDG.E.64 R6, [R6] ;
      |        /*0090*/                   IADD R10.CC, R10, c[0x0][0x140] ;
      |        /*0098*/                   IADD.X R11, R0, c[0x0][0x144] ;
      |        /*00a8*/                   IADD3 R8.CC, R6, R4, R2 ;
      |        /*00b0*/                   IADD3.X R9, R7, R5, R3 ;
      |        /*00b8*/                   STG.E.64 [R10], R8 ;
      |        /*00c8*/                   EXIT ;
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0) ;
      |        /*00d8*/                   NOP;
      |        /*00e8*/                   NOP;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iadd3LegacyProbeSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	32
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_TID.X ;
      |        /*0018*/                   SHL R10, R0.reuse, 0x3 ;
      |        /*0028*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0030*/                   IADD R2.CC, R10, c[0x0][0x148] ;
      |        /*0038*/                   IADD.X R3, R0, c[0x0][0x14c] ;
      |        /*0048*/         {         IADD R4.CC, R10, c[0x0][0x150] ;
      |        /*0050*/                   LDG.E.64 R2, [R2]         }
      |        /*0058*/                   IADD.X R5, R0, c[0x0][0x154] ;
      |        /*0068*/         {         IADD R6.CC, R10, c[0x0][0x158] ;
      |        /*0070*/                   LDG.E.64 R4, [R4]         }
      |        /*0078*/                   IADD.X R7, R0, c[0x0][0x15c] ;
      |        /*0088*/                   LDG.E.64 R6, [R6] ;
      |        /*0090*/                   IADD R10.CC, R10, c[0x0][0x140] ;
      |        /*0098*/                   IADD.X R11, R0, c[0x0][0x144] ;
      |        /*00a8*/                   IADD3 R8.CC, R6, R4, R2 ;
      |        /*00b0*/                   IADD3.X R9, R7, R5, R3 ;
      |        /*00b8*/                   STG.E.64 [R10], R8 ;
      |        /*00c8*/                   EXIT ;
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0) ;
      |        /*00d8*/                   NOP;
      |        /*00e8*/                   NOP;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iadd3NegativeAddConstantSubSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.add_constant_sub    --------------------------
      |	.section	.text.add_constant_sub,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	32
      |        .global         add_constant_sub
      |        .type           add_constant_sub,@function
      |        .size           add_constant_sub,(.L_x_1 - add_constant_sub)
      |        .other          add_constant_sub,@"STO_CUDA_ENTRY STV_DEFAULT"
      |add_constant_sub:
      |.text.add_constant_sub:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHR.U32 R2, R0.reuse, 0x1e ;
      |        /*0050*/                   ISCADD R4.CC, R0, c[0x0][0x148], 0x2 ;
      |        /*0058*/                   SHL R8, R0.reuse, 0x3 ;
      |        /*0068*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0070*/                   IADD.X R5, R2, c[0x0][0x14c] ;
      |        /*0078*/         {         IADD R2.CC, R8.reuse, c[0x0][0x140] ;
      |        /*0088*/                   LDG.E R4, [R4]         }
      |        /*0090*/                   IADD.X R3, R0, c[0x0][0x144] ;
      |        /*0098*/                   LDG.E.64 R2, [R2] ;
      |        /*00a8*/                   IADD R8.CC, R8, c[0x0][0x150] ;
      |        /*00b0*/                   IADD.X R9, R0, c[0x0][0x154] ;
      |        /*00b8*/                   IADD3 R6.CC, -R4, 0x3f00, R2 ;
      |        /*00c8*/                   IADD3.X R7, RZ, 0xfffff, R3 ;
      |        /*00d0*/                   STG.E.64 [R8], R6 ;
      |        /*00d8*/                   EXIT ;
      |.L_x_0:
      |        /*00e8*/                   BRA `(.L_x_0) ;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iadd3NegativeAddConstantSubSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.add_constant_sub    --------------------------
      |	.section	.text.add_constant_sub,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	32
      |        .global         add_constant_sub
      |        .type           add_constant_sub,@function
      |        .size           add_constant_sub,(.L_x_1 - add_constant_sub)
      |        .other          add_constant_sub,@"STO_CUDA_ENTRY STV_DEFAULT"
      |add_constant_sub:
      |.text.add_constant_sub:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHR.U32 R2, R0.reuse, 0x1e ;
      |        /*0050*/                   ISCADD R4.CC, R0.reuse, c[0x0][0x148], 0x2 ;
      |        /*0058*/                   SHL R8, R0.reuse, 0x3 ;
      |        /*0068*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0070*/                   IADD.X R5, R2, c[0x0][0x14c] ;
      |        /*0078*/         {         IADD R2.CC, R8, c[0x0][0x140] ;
      |        /*0088*/                   LDG.E R4, [R4]         }
      |        /*0090*/                   IADD.X R3, R0, c[0x0][0x144] ;
      |        /*0098*/                   LDG.E.64 R2, [R2] ;
      |        /*00a8*/                   IADD R8.CC, R8, c[0x0][0x150] ;
      |        /*00b0*/                   IADD.X R9, R0, c[0x0][0x154] ;
      |        /*00b8*/                   IADD3 R6.CC, -R4, 0x3f00, R2 ;
      |        /*00c8*/                   IADD3.X R7, RZ, 0xfffff, R3 ;
      |        /*00d0*/                   STG.E.64 [R8], R6 ;
      |        /*00d8*/                   EXIT ;
      |.L_x_0:
      |        /*00e8*/                   BRA `(.L_x_0) ;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iadd3NegativeAddLargeConstantSubSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.add_large_constant_sub --------------------------
      |	.section	.text.add_large_constant_sub,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	32
      |        .global         add_large_constant_sub
      |        .type           add_large_constant_sub,@function
      |        .size           add_large_constant_sub,(.L_x_1 - add_large_constant_sub)
      |        .other          add_large_constant_sub,@"STO_CUDA_ENTRY STV_DEFAULT"
      |add_large_constant_sub:
      |.text.add_large_constant_sub:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHR.U32 R2, R0.reuse, 0x1e ;
      |        /*0050*/                   ISCADD R4.CC, R0, c[0x0][0x148], 0x2 ;
      |        /*0058*/                   SHL R8, R0.reuse, 0x3 ;
      |        /*0068*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0070*/                   IADD.X R5, R2, c[0x0][0x14c] ;
      |        /*0078*/         {         IADD R2.CC, R8.reuse, c[0x0][0x140] ;
      |        /*0088*/                   LDG.E R4, [R4]         }
      |        /*0090*/                   IADD.X R3, R0, c[0x0][0x144] ;
      |        /*0098*/                   LDG.E.64 R2, [R2] ;
      |        /*00a8*/                   IADD R8.CC, R8, c[0x0][0x150] ;
      |        /*00b0*/                   IADD.X R9, R0, c[0x0][0x154] ;
      |        /*00b8*/                   IADD3 R6.CC, -R4, c[0x2][0x0], R2 ;
      |        /*00c8*/                   IADD3.X R7, RZ, 0xfffff, R3 ;
      |        /*00d0*/                   STG.E.64 [R8], R6 ;
      |        /*00d8*/                   EXIT ;
      |.L_x_0:
      |        /*00e8*/                   BRA `(.L_x_0) ;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iadd3NegativeAddLargeConstantSubSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.add_large_constant_sub --------------------------
      |	.section	.text.add_large_constant_sub,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	32
      |        .global         add_large_constant_sub
      |        .type           add_large_constant_sub,@function
      |        .size           add_large_constant_sub,(.L_x_1 - add_large_constant_sub)
      |        .other          add_large_constant_sub,@"STO_CUDA_ENTRY STV_DEFAULT"
      |add_large_constant_sub:
      |.text.add_large_constant_sub:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHR.U32 R2, R0.reuse, 0x1e ;
      |        /*0050*/                   ISCADD R4.CC, R0.reuse, c[0x0][0x148], 0x2 ;
      |        /*0058*/                   SHL R8, R0.reuse, 0x3 ;
      |        /*0068*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0070*/                   IADD.X R5, R2, c[0x0][0x14c] ;
      |        /*0078*/         {         IADD R2.CC, R8, c[0x0][0x140] ;
      |        /*0088*/                   LDG.E R4, [R4]         }
      |        /*0090*/                   IADD.X R3, R0, c[0x0][0x144] ;
      |        /*0098*/                   LDG.E.64 R2, [R2] ;
      |        /*00a8*/                   IADD R8.CC, R8, c[0x0][0x150] ;
      |        /*00b0*/                   IADD.X R9, R0, c[0x0][0x154] ;
      |        /*00b8*/                   IADD3 R6.CC, -R4, c[0x2][0x0], R2 ;
      |        /*00c8*/                   IADD3.X R7, RZ, 0xfffff, R3 ;
      |        /*00d0*/                   STG.E.64 [R8], R6 ;
      |        /*00d8*/                   EXIT ;
      |.L_x_0:
      |        /*00e8*/                   BRA `(.L_x_0) ;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iadd3NegativeAddSubSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.add_sub             --------------------------
      |	.section	.text.add_sub,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	32
      |        .global         add_sub
      |        .type           add_sub,@function
      |        .size           add_sub,(.L_x_1 - add_sub)
      |        .other          add_sub,@"STO_CUDA_ENTRY STV_DEFAULT"
      |add_sub:
      |.text.add_sub:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R10, R0.reuse, 0x3 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0058*/                   IADD R2.CC, R10.reuse, c[0x0][0x140] ;
      |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x144] ;
      |        /*0070*/         {         IADD R4.CC, R10.reuse, c[0x0][0x148] ;
      |        /*0078*/                   LDG.E.64 R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0.reuse, c[0x0][0x14c] ;
      |        /*0090*/         {         IADD R6.CC, R10, c[0x0][0x150] ;
      |        /*0098*/                   LDG.E.64 R4, [R4]         }
      |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
      |        /*00b0*/                   LDG.E.64 R6, [R6] ;
      |        /*00b8*/                   IADD R10.CC, R10, c[0x0][0x158] ;
      |        /*00c8*/                   IADD.X R11, R0, c[0x0][0x15c] ;
      |        /*00d0*/                   IADD3 R8.CC, -R6, R4, R2 ;
      |        /*00d8*/                   IADD3.X R9, -R7, R5, R3 ;
      |        /*00e8*/                   STG.E.64 [R10], R8 ;
      |        /*00f0*/                   EXIT ;
      |.L_x_0:
      |        /*00f8*/                   BRA `(.L_x_0) ;
      |.L_x_1:
      |""".stripMargin

  val iadd3NegativeAddSubSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.add_sub             --------------------------
      |	.section	.text.add_sub,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	32
      |        .global         add_sub
      |        .type           add_sub,@function
      |        .size           add_sub,(.L_x_1 - add_sub)
      |        .other          add_sub,@"STO_CUDA_ENTRY STV_DEFAULT"
      |add_sub:
      |.text.add_sub:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R10, R0.reuse, 0x3 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0058*/                   IADD R2.CC, R10, c[0x0][0x140] ;
      |        /*0068*/                   IADD.X R3, R0, c[0x0][0x144] ;
      |        /*0070*/         {         IADD R4.CC, R10, c[0x0][0x148] ;
      |        /*0078*/                   LDG.E.64 R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0, c[0x0][0x14c] ;
      |        /*0090*/         {         IADD R6.CC, R10, c[0x0][0x150] ;
      |        /*0098*/                   LDG.E.64 R4, [R4]         }
      |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
      |        /*00b0*/                   LDG.E.64 R6, [R6] ;
      |        /*00b8*/                   IADD R10.CC, R10, c[0x0][0x158] ;
      |        /*00c8*/                   IADD.X R11, R0, c[0x0][0x15c] ;
      |        /*00d0*/                   IADD3 R8.CC, -R6, R4, R2 ;
      |        /*00d8*/                   IADD3.X R9, -R7, R5, R3 ;
      |        /*00e8*/                   STG.E.64 [R10], R8 ;
      |        /*00f0*/                   EXIT ;
      |.L_x_0:
      |        /*00f8*/                   BRA `(.L_x_0) ;
      |.L_x_1:
      |""".stripMargin

  val iadd3NegativeNegativeConstantSubSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.negative_constant_sub --------------------------
      |	.section	.text.negative_constant_sub,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	32
      |        .global         negative_constant_sub
      |        .type           negative_constant_sub,@function
      |        .size           negative_constant_sub,(.L_x_1 - negative_constant_sub)
      |        .other          negative_constant_sub,@"STO_CUDA_ENTRY STV_DEFAULT"
      |negative_constant_sub:
      |.text.negative_constant_sub:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R8, R0.reuse, 0x3 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0058*/                   IADD R2.CC, R8.reuse, c[0x0][0x140] ;
      |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x144] ;
      |        /*0070*/         {         IADD R4.CC, R8.reuse, c[0x0][0x148] ;
      |        /*0078*/                   LDG.E.64 R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0, c[0x0][0x14c] ;
      |        /*0090*/                   LDG.E.64 R4, [R4] ;
      |        /*0098*/                   IADD R8.CC, R8, c[0x0][0x150] ;
      |        /*00a8*/                   IADD.X R9, R0, c[0x0][0x154] ;
      |        /*00b0*/                   IADD3 R6.CC, -R4, 0xfc100, R2 ;
      |        /*00b8*/                   IADD3.X R7, -R5, 0xfffff, R3 ;
      |        /*00c8*/                   STG.E.64 [R8], R6 ;
      |        /*00d0*/                   EXIT ;
      |.L_x_0:
      |        /*00d8*/                   BRA `(.L_x_0) ;
      |        /*00e8*/                   NOP;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val iadd3NegativeNegativeConstantSubSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.negative_constant_sub --------------------------
      |	.section	.text.negative_constant_sub,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	32
      |        .global         negative_constant_sub
      |        .type           negative_constant_sub,@function
      |        .size           negative_constant_sub,(.L_x_1 - negative_constant_sub)
      |        .other          negative_constant_sub,@"STO_CUDA_ENTRY STV_DEFAULT"
      |negative_constant_sub:
      |.text.negative_constant_sub:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R8, R0.reuse, 0x3 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1d ;
      |        /*0058*/                   IADD R2.CC, R8, c[0x0][0x140] ;
      |        /*0068*/                   IADD.X R3, R0, c[0x0][0x144] ;
      |        /*0070*/         {         IADD R4.CC, R8, c[0x0][0x148] ;
      |        /*0078*/                   LDG.E.64 R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0, c[0x0][0x14c] ;
      |        /*0090*/                   LDG.E.64 R4, [R4] ;
      |        /*0098*/                   IADD R8.CC, R8, c[0x0][0x150] ;
      |        /*00a8*/                   IADD.X R9, R0, c[0x0][0x154] ;
      |        /*00b0*/                   IADD3 R6.CC, -R4, 0xfc100, R2 ;
      |        /*00b8*/                   IADD3.X R7, -R5, 0xfffff, R3 ;
      |        /*00c8*/                   STG.E.64 [R8], R6 ;
      |        /*00d0*/                   EXIT ;
      |.L_x_0:
      |        /*00d8*/                   BRA `(.L_x_0) ;
      |        /*00e8*/                   NOP;
      |        /*00f0*/                   NOP;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val eq64ExtendedSm35CudaSource: String =
    """extern "C" __global__ void eq64(unsigned *out, unsigned long long a, unsigned long long b) {
      |  out[0] = a == b;
      |  out[1] = a != b;
      |  out[2] = a == 0;
      |  out[3] = b == 0;
      |}
      |""".stripMargin

  val eq64sExtendedSm35CudaSource: String =
    """extern "C" __global__ void eq64s(unsigned *out, long long a, long long b) {
      |  out[0] = a == b;
      |  out[1] = a != b;
      |}
      |""".stripMargin

}

class Iadd3Fixtures extends NvidiaFixtureSuite {
  import Iadd3Fixtures.*

  for (
    (name, sm, source, expected) <- Vec(
      ("iadd3LegacyMulwideSm50", 50, iadd3LegacyMulwidePtxSource, iadd3LegacyMulwideSm50),
      ("iadd3LegacyMulwideSm60", 60, iadd3LegacyMulwidePtxSource, iadd3LegacyMulwideSm60)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("input.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = expected
  )

  reproduces("eq64ExtendedSm35")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "eq64-extended.cu" -> eq64ExtendedSm35CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_35 eq64-extended.cu -o eq64-extended.cubin",
      "symbol_index=$(readelf -sW eq64-extended.cubin | awk -v name=\"eq64\" '$NF == name { sub(\":\", \"\", $1); print $1; exit }')",
      "test -n \"$symbol_index\"",
      "nvdisasm -c -ndf -fun \"$symbol_index\" eq64-extended.cubin > eq64-extended-sm35.sass"
    ),
    output = "eq64-extended-sm35.sass",
    expected = eq64ExtendedSm35
  )

  reproduces("eq64sExtendedSm35")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "eq64s-extended.cu" -> eq64sExtendedSm35CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_35 eq64s-extended.cu -o eq64s-extended.cubin",
      "symbol_index=$(readelf -sW eq64s-extended.cubin | awk -v name=\"eq64s\" '$NF == name { sub(\":\", \"\", $1); print $1; exit }')",
      "test -n \"$symbol_index\"",
      "nvdisasm -c -ndf -fun \"$symbol_index\" eq64s-extended.cubin > eq64s-extended-sm35.sass"
    ),
    output = "eq64s-extended-sm35.sass",
    expected = eq64sExtendedSm35
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("iadd3LegacyProbeConstantSm50", 50, iadd3LegacyProbeConstantCudaSource, iadd3LegacyProbeConstantSm50),
      ("iadd3LegacyProbeConstantSm60", 60, iadd3LegacyProbeConstantCudaSource, iadd3LegacyProbeConstantSm60)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe-constant.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm probe-constant.cu -o probe-constant-sm$sm.cubin",
      s"nvdisasm -c -ndf -fun 1 probe-constant-sm$sm.cubin > probe-constant-sm$sm.sass"
    ),
    output = s"probe-constant-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("iadd3LegacyProbeMultipairSm50", 50, iadd3LegacyProbeMultipairCudaSource, iadd3LegacyProbeMultipairSm50),
      ("iadd3LegacyProbeMultipairSm60", 60, iadd3LegacyProbeMultipairCudaSource, iadd3LegacyProbeMultipairSm60)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe-multipair.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm probe-multipair.cu -o probe-multipair-sm$sm.cubin",
      s"nvdisasm -c -ndf -fun 1 probe-multipair-sm$sm.cubin > probe-multipair-sm$sm.sass"
    ),
    output = s"probe-multipair-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("iadd3LegacyProbeSm50", 50, iadd3LegacyProbeCudaSource, iadd3LegacyProbeSm50),
      ("iadd3LegacyProbeSm60", 60, iadd3LegacyProbeCudaSource, iadd3LegacyProbeSm60)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm probe.cu -o probe-sm$sm.cubin",
      s"nvdisasm -c -ndf -fun 1 probe-sm$sm.cubin > probe-sm$sm.sass"
    ),
    output = s"probe-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("iadd3NegativeAddConstantSubSm50", 50, iadd3NegativeProbeCudaSource, iadd3NegativeAddConstantSubSm50),
      ("iadd3NegativeAddConstantSubSm60", 60, iadd3NegativeProbeCudaSource, iadd3NegativeAddConstantSubSm60)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -ptx -arch=sm_$sm probe.cu -o probe-sm$sm.ptx",
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm probe.cu -o probe-sm$sm.cubin",
      s"symbol_index=$$(readelf -sW probe-sm$sm.cubin | awk -v name=\"add_constant_sub\" '$$NF == name { sub(\":\", \"\", $$1); print $$1; exit }')",
      "test -n \"$symbol_index\"",
      s"nvdisasm -c -ndf -fun \"$$symbol_index\" probe-sm$sm.cubin > add_constant_sub-sm$sm.sass"
    ),
    output = s"add_constant_sub-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("iadd3NegativeAddLargeConstantSubSm50", 50, iadd3NegativeProbeCudaSource, iadd3NegativeAddLargeConstantSubSm50),
      ("iadd3NegativeAddLargeConstantSubSm60", 60, iadd3NegativeProbeCudaSource, iadd3NegativeAddLargeConstantSubSm60)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -ptx -arch=sm_$sm probe.cu -o probe-sm$sm.ptx",
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm probe.cu -o probe-sm$sm.cubin",
      s"symbol_index=$$(readelf -sW probe-sm$sm.cubin | awk -v name=\"add_large_constant_sub\" '$$NF == name { sub(\":\", \"\", $$1); print $$1; exit }')",
      "test -n \"$symbol_index\"",
      s"nvdisasm -c -ndf -fun \"$$symbol_index\" probe-sm$sm.cubin > add_large_constant_sub-sm$sm.sass"
    ),
    output = s"add_large_constant_sub-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("iadd3NegativeAddSubSm50", 50, iadd3NegativeProbeCudaSource, iadd3NegativeAddSubSm50),
      ("iadd3NegativeAddSubSm60", 60, iadd3NegativeProbeCudaSource, iadd3NegativeAddSubSm60)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -ptx -arch=sm_$sm probe.cu -o probe-sm$sm.ptx",
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm probe.cu -o probe-sm$sm.cubin",
      s"symbol_index=$$(readelf -sW probe-sm$sm.cubin | awk -v name=\"add_sub\" '$$NF == name { sub(\":\", \"\", $$1); print $$1; exit }')",
      "test -n \"$symbol_index\"",
      s"nvdisasm -c -ndf -fun \"$$symbol_index\" probe-sm$sm.cubin > add_sub-sm$sm.sass"
    ),
    output = s"add_sub-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("iadd3NegativeNegativeConstantSubSm50", 50, iadd3NegativeProbeCudaSource, iadd3NegativeNegativeConstantSubSm50),
      ("iadd3NegativeNegativeConstantSubSm60", 60, iadd3NegativeProbeCudaSource, iadd3NegativeNegativeConstantSubSm60)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("probe.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -ptx -arch=sm_$sm probe.cu -o probe-sm$sm.ptx",
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm probe.cu -o probe-sm$sm.cubin",
      s"symbol_index=$$(readelf -sW probe-sm$sm.cubin | awk -v name=\"negative_constant_sub\" '$$NF == name { sub(\":\", \"\", $$1); print $$1; exit }')",
      "test -n \"$symbol_index\"",
      s"nvdisasm -c -ndf -fun \"$$symbol_index\" probe-sm$sm.cubin > negative_constant_sub-sm$sm.sass"
    ),
    output = s"negative_constant_sub-sm$sm.sass",
    expected = expected
  )

}
