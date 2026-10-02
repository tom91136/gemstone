package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object VideoMinMaxFixtures {
  val videoMinmaxSm35PtxSource: String =
    """.version 7.8
      |.target sm_35
      |.address_size 64
      |.visible .entry video_minmax(.param .u64 out, .param .u32 a, .param .u32 b, .param .u32 c) {
      |  .reg .b64 %p;
      |  .reg .b32 %r<15>;
      |  ld.param.u64 %p, [out];
      |  ld.param.u32 %r0, [a];
      |  ld.param.u32 %r1, [b];
      |  ld.param.u32 %r2, [c];
      |  vmin.s32.s32.s32.min %r3, %r0, %r1, %r2;
      |  vmin.s32.s32.s32.max %r4, %r0, %r1, %r2;
      |  vmin.s32.s32.s32.add %r5, %r0, %r1, %r2;
      |  vmax.s32.s32.s32.min %r6, %r0, %r1, %r2;
      |  vmax.s32.s32.s32.max %r7, %r0, %r1, %r2;
      |  vmax.s32.s32.s32.add %r8, %r0, %r1, %r2;
      |  vmin.u32.u32.u32.min %r9, %r0, %r1, %r2;
      |  vmin.u32.u32.u32.max %r10, %r0, %r1, %r2;
      |  vmin.u32.u32.u32.add %r11, %r0, %r1, %r2;
      |  vmax.u32.u32.u32.min %r12, %r0, %r1, %r2;
      |  vmax.u32.u32.u32.max %r13, %r0, %r1, %r2;
      |  vmax.u32.u32.u32.add %r14, %r0, %r1, %r2;
      |  st.global.u32 [%p+24], %r9;
      |  st.global.u32 [%p+28], %r10;
      |  st.global.u32 [%p+32], %r11;
      |  st.global.u32 [%p+36], %r12;
      |  st.global.u32 [%p+40], %r13;
      |  st.global.u32 [%p+44], %r14;
      |  st.global.u32 [%p], %r3;
      |  st.global.u32 [%p+4], %r4;
      |  st.global.u32 [%p+8], %r5;
      |  st.global.u32 [%p+12], %r6;
      |  st.global.u32 [%p+16], %r7;
      |  st.global.u32 [%p+20], %r8;
      |  ret;
      |}
      |""".stripMargin

  val videoMinmaxSm5060NestedCudaSource: String =
    """#include <cuda_runtime.h>
      |extern "C" __global__ void nested_min(const int* a, const int* b, const int* c, int* out) {
      |  const unsigned i = blockIdx.x * blockDim.x + threadIdx.x;
      |  out[i] = min(min(a[i], b[i]), c[i]);
      |}
      |extern "C" __global__ void nested_max(const int* a, const int* b, const int* c, int* out) {
      |  const unsigned i = blockIdx.x * blockDim.x + threadIdx.x;
      |  out[i] = max(max(a[i], b[i]), c[i]);
      |}
      |""".stripMargin

  val videoMinmaxSm35: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.video_minmax        --------------------------
      |	.section	.text.video_minmax,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=15"
      |	.align	64
      |        .global         video_minmax
      |        .type           video_minmax,@function
      |        .size           video_minmax,(.L_x_1 - video_minmax)
      |        .other          video_minmax,@"STO_CUDA_ENTRY STV_DEFAULT"
      |video_minmax:
      |.text.video_minmax:
      |        /*0008*/                   MOV R1, c[0x0][0x44];
      |        /*0010*/                   MOV R0, c[0x0][0x148];
      |        /*0018*/                   MOV R13, c[0x0][0x14c];
      |        /*0020*/                   MOV R14, c[0x0][0x150];
      |        /*0028*/                   MOV R2, c[0x0][0x140];
      |        /*0030*/                   MOV R3, c[0x0][0x144];
      |        /*0038*/                   VMNMX.MIN R4, R0, R13, R14;
      |        /*0048*/                   VMNMX.MAX R5, R0, R13, R14;
      |        /*0050*/                   ST.E [R2], R4;
      |        /*0058*/                   VMNMX.ACC R6, R0, R13, R14;
      |        /*0060*/                   VMNMX.MX.MIN R7, R0, R13, R14;
      |        /*0068*/                   VMNMX.MX.MAX R9, R0, R13, R14;
      |        /*0070*/                   ST.E [R2+0x4], R5;
      |        /*0078*/                   VMNMX.UD.U32.U32.MIN R11, R0, R13, R14;
      |        /*0088*/                   VMNMX.UD.U32.U32.MAX R12, R0, R13, R14;
      |        /*0090*/                   VMNMX.MX.ACC R4, R0, R13, R14;
      |        /*0098*/                   ST.E [R2+0x8], R6;
      |        /*00a0*/                   VMNMX.UD.U32.U32.ACC R5, R0, R13, R14;
      |        /*00a8*/                   ST.E [R2+0xc], R7;
      |        /*00b0*/                   VMNMX.UD.U32.U32.MX.MIN R6, R0, R13, R14;
      |        /*00b8*/                   ST.E [R2+0x10], R9;
      |        /*00c8*/                   VMNMX.UD.U32.U32.MX.MAX R7, R0, R13, R14;
      |        /*00d0*/                   VMNMX.UD.U32.U32.MX.ACC R9, R0, R13, R14;
      |        /*00d8*/                   ST.E [R2+0x18], R11;
      |        /*00e0*/                   ST.E [R2+0x1c], R12;
      |        /*00e8*/                   ST.E [R2+0x20], R5;
      |        /*00f0*/                   ST.E [R2+0x24], R6;
      |        /*00f8*/                   ST.E [R2+0x28], R7;
      |        /*0108*/                   ST.E [R2+0x2c], R9;
      |        /*0110*/                   ST.E [R2+0x14], R4;
      |        /*0118*/                   EXIT;
      |.L_x_0:
      |        /*0120*/                   BRA `(.L_x_0);
      |        /*0128*/                   NOP;
      |        /*0130*/                   NOP;
      |        /*0138*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val videoMinmaxSm5060NestedMaxSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.nested_max          --------------------------
      |	.section	.text.nested_max,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=11"
      |	.align	32
      |        .global         nested_max
      |        .type           nested_max,@function
      |        .size           nested_max,(.L_x_1 - nested_max)
      |        .other          nested_max,@"STO_CUDA_ENTRY STV_DEFAULT"
      |nested_max:
      |.text.nested_max:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R8, R0.reuse, 0x2 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1e ;
      |        /*0058*/                   IADD R2.CC, R8.reuse, c[0x0][0x140] ;
      |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x144] ;
      |        /*0070*/         {         IADD R4.CC, R8.reuse, c[0x0][0x148] ;
      |        /*0078*/                   LDG.E R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0.reuse, c[0x0][0x14c] ;
      |        /*0090*/         {         IADD R6.CC, R8, c[0x0][0x150] ;
      |        /*0098*/                   LDG.E R4, [R4]         }
      |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
      |        /*00b0*/                   LDG.E R7, [R6] ;
      |        /*00b8*/                   IADD R8.CC, R8, c[0x0][0x158] ;
      |        /*00c8*/                   IADD.X R9, R0, c[0x0][0x15c] ;
      |        /*00d0*/                   VMNMX.MX.MAX R10, R2, R4, R7 ;
      |        /*00d8*/                   STG.E [R8], R10 ;
      |        /*00e8*/                   EXIT ;
      |.L_x_0:
      |        /*00f0*/                   BRA `(.L_x_0) ;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val videoMinmaxSm5060NestedMaxSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.nested_max          --------------------------
      |	.section	.text.nested_max,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=11"
      |	.align	32
      |        .global         nested_max
      |        .type           nested_max,@function
      |        .size           nested_max,(.L_x_1 - nested_max)
      |        .other          nested_max,@"STO_CUDA_ENTRY STV_DEFAULT"
      |nested_max:
      |.text.nested_max:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R8, R0.reuse, 0x2 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1e ;
      |        /*0058*/                   IADD R2.CC, R8.reuse, c[0x0][0x140] ;
      |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x144] ;
      |        /*0070*/         {         IADD R4.CC, R8.reuse, c[0x0][0x148] ;
      |        /*0078*/                   LDG.E R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0.reuse, c[0x0][0x14c] ;
      |        /*0090*/         {         IADD R6.CC, R8, c[0x0][0x150] ;
      |        /*0098*/                   LDG.E R4, [R4]         }
      |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
      |        /*00b0*/                   LDG.E R7, [R6] ;
      |        /*00b8*/                   IADD R8.CC, R8, c[0x0][0x158] ;
      |        /*00c8*/                   IADD.X R9, R0, c[0x0][0x15c] ;
      |        /*00d0*/                   VMNMX.MX.MAX R10, R2, R4, R7 ;
      |        /*00d8*/                   STG.E [R8], R10 ;
      |        /*00e8*/                   EXIT ;
      |.L_x_0:
      |        /*00f0*/                   BRA `(.L_x_0) ;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val videoMinmaxSm5060NestedMinSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.nested_min          --------------------------
      |	.section	.text.nested_min,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=11"
      |	.align	32
      |        .global         nested_min
      |        .type           nested_min,@function
      |        .size           nested_min,(.L_x_1 - nested_min)
      |        .other          nested_min,@"STO_CUDA_ENTRY STV_DEFAULT"
      |nested_min:
      |.text.nested_min:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R8, R0.reuse, 0x2 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1e ;
      |        /*0058*/                   IADD R2.CC, R8.reuse, c[0x0][0x140] ;
      |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x144] ;
      |        /*0070*/         {         IADD R4.CC, R8.reuse, c[0x0][0x148] ;
      |        /*0078*/                   LDG.E R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0.reuse, c[0x0][0x14c] ;
      |        /*0090*/         {         IADD R6.CC, R8, c[0x0][0x150] ;
      |        /*0098*/                   LDG.E R4, [R4]         }
      |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
      |        /*00b0*/                   LDG.E R7, [R6] ;
      |        /*00b8*/                   IADD R8.CC, R8, c[0x0][0x158] ;
      |        /*00c8*/                   IADD.X R9, R0, c[0x0][0x15c] ;
      |        /*00d0*/                   VMNMX.MIN R10, R2, R4, R7 ;
      |        /*00d8*/                   STG.E [R8], R10 ;
      |        /*00e8*/                   EXIT ;
      |.L_x_0:
      |        /*00f0*/                   BRA `(.L_x_0) ;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val videoMinmaxSm5060NestedMinSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.nested_min          --------------------------
      |	.section	.text.nested_min,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=11"
      |	.align	32
      |        .global         nested_min
      |        .type           nested_min,@function
      |        .size           nested_min,(.L_x_1 - nested_min)
      |        .other          nested_min,@"STO_CUDA_ENTRY STV_DEFAULT"
      |nested_min:
      |.text.nested_min:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R0, SR_CTAID.X ;
      |        /*0018*/                   S2R R2, SR_TID.X ;
      |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
      |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
      |        /*0048*/                   SHL R8, R0.reuse, 0x2 ;
      |        /*0050*/                   SHR.U32 R0, R0, 0x1e ;
      |        /*0058*/                   IADD R2.CC, R8.reuse, c[0x0][0x140] ;
      |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x144] ;
      |        /*0070*/         {         IADD R4.CC, R8.reuse, c[0x0][0x148] ;
      |        /*0078*/                   LDG.E R2, [R2]         }
      |        /*0088*/                   IADD.X R5, R0.reuse, c[0x0][0x14c] ;
      |        /*0090*/         {         IADD R6.CC, R8, c[0x0][0x150] ;
      |        /*0098*/                   LDG.E R4, [R4]         }
      |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
      |        /*00b0*/                   LDG.E R7, [R6] ;
      |        /*00b8*/                   IADD R8.CC, R8, c[0x0][0x158] ;
      |        /*00c8*/                   IADD.X R9, R0, c[0x0][0x15c] ;
      |        /*00d0*/                   VMNMX.MIN R10, R2, R4, R7 ;
      |        /*00d8*/                   STG.E [R8], R10 ;
      |        /*00e8*/                   EXIT ;
      |.L_x_0:
      |        /*00f0*/                   BRA `(.L_x_0) ;
      |        /*00f8*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class VideoMinMaxFixtures extends NvidiaFixtureSuite {
  import VideoMinMaxFixtures.*

  reproduces("videoMinmaxSm35")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "input.ptx" -> videoMinmaxSm35PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_35 input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = videoMinmaxSm35
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("videoMinmaxSm5060NestedMaxSm60", 60, videoMinmaxSm5060NestedCudaSource, videoMinmaxSm5060NestedMaxSm60),
      ("videoMinmaxSm5060NestedMaxSm50", 50, videoMinmaxSm5060NestedCudaSource, videoMinmaxSm5060NestedMaxSm50)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("nested.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -ptx -arch=sm_$sm nested.cu -o nested-sm$sm.ptx",
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm nested.cu -o nested-sm$sm.cubin",
      s"symbol_index=$$(readelf -s nested-sm$sm.cubin | awk -v name=\"nested_max\" '$$NF == name { sub(\":\", \"\", $$1); print $$1; exit }')",
      "test -n \"$symbol_index\"",
      s"nvdisasm -c -ndf -fun \"$$symbol_index\" nested-sm$sm.cubin > nested_max-sm$sm.sass"
    ),
    output = s"nested_max-sm$sm.sass",
    expected = expected
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("videoMinmaxSm5060NestedMinSm60", 60, videoMinmaxSm5060NestedCudaSource, videoMinmaxSm5060NestedMinSm60),
      ("videoMinmaxSm5060NestedMinSm50", 50, videoMinmaxSm5060NestedCudaSource, videoMinmaxSm5060NestedMinSm50)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("nested.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -ptx -arch=sm_$sm nested.cu -o nested-sm$sm.ptx",
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm nested.cu -o nested-sm$sm.cubin",
      s"symbol_index=$$(readelf -s nested-sm$sm.cubin | awk -v name=\"nested_min\" '$$NF == name { sub(\":\", \"\", $$1); print $$1; exit }')",
      "test -n \"$symbol_index\"",
      s"nvdisasm -c -ndf -fun \"$$symbol_index\" nested-sm$sm.cubin > nested_min-sm$sm.sass"
    ),
    output = s"nested_min-sm$sm.sass",
    expected = expected
  )

}
