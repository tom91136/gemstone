package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object CallFixtures {
  val callsSm35CudaSource: String =
    """__device__ __noinline__ unsigned inner(unsigned x) { return 3 * x + 1; }
      |__device__ __noinline__ unsigned outer(unsigned x) { return inner(x) + inner(x + 1); }
      |
      |extern "C" __global__ void calls(unsigned *output) {
      |  const unsigned lane = threadIdx.x;
      |  unsigned value = lane;
      |  if ((lane & 1) == 0) value = outer(value);
      |  output[blockIdx.x * blockDim.x + lane] = value;
      |}
      |""".stripMargin

  val indirectCallSm35CudaSource: String =
    """__device__ __noinline__ unsigned twice(unsigned x) { return 2 * x; }
      |__device__ __noinline__ unsigned thrice(unsigned x) { return 3 * x; }
      |
      |extern "C" __global__ void indirect_call(unsigned *output) {
      |  const unsigned lane = threadIdx.x;
      |  unsigned (*function)(unsigned) = (lane & 1) ? twice : thrice;
      |  output[blockIdx.x * blockDim.x + lane] = function(lane);
      |}
      |""".stripMargin

  val callsSm35: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.calls               --------------------------
      |	.section	.text.calls,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=5"
      |	.align	64
      |        .global         calls
      |        .type           calls,@function
      |        .size           calls,(.L_x_3 - calls)
      |        .other          calls,@"STO_CUDA_ENTRY STV_DEFAULT"
      |calls:
      |.text.calls:
      |        /*0008*/                   MOV R1, c[0x0][0x44];
      |        /*0010*/                   S2R R2, SR_TID.X;
      |        /*0018*/                   SSY `(.L_x_0);
      |        /*0020*/                   LOP32I.AND R0, R2, 0x1;
      |        /*0028*/                   MOV R4, R2;
      |        /*0030*/                   ISETP.NE.U32.AND P0, PT, R0, 0x1, PT;
      |        /*0038*/              @!P0 NOP.S;
      |        /*0048*/                   CAL `($calls$_Z5outerj);
      |        /*0050*/                   NOP.S;
      |.L_x_0:
      |        /*0058*/                   S2R R3, SR_CTAID.X;
      |        /*0060*/                   IMAD R0, R3, c[0x0][0x28], R2;
      |        /*0068*/                   MOV32I R3, 0x4;
      |        /*0070*/                   ISCADD R2.CC, R0, c[0x0][0x140], 0x2;
      |        /*0078*/                   IMAD.U32.U32.HI.X R3, R0, R3, c[0x0][0x144];
      |        /*0088*/                   ST.E [R2], R4;
      |        /*0090*/                   EXIT;
      |        .type           $calls$_Z5innerj,@function
      |        .size           $calls$_Z5innerj,($calls$_Z5outerj - $calls$_Z5innerj)
      |$calls$_Z5innerj:
      |        /*0098*/                   MOV32I R4, 0x1;
      |        /*00a0*/                   IMAD R4, R3, 0x3, R4;
      |        /*00a8*/                   RET;
      |        .type           $calls$_Z5outerj,@function
      |        .size           $calls$_Z5outerj,(.L_x_3 - $calls$_Z5outerj)
      |$calls$_Z5outerj:
      |        /*00b0*/                   MOV R3, R2;
      |        /*00b8*/                   CAL `($calls$_Z5innerj);
      |        /*00c8*/                   MOV R0, R4;
      |        /*00d0*/                   IADD32I R3, R2, 0x1;
      |        /*00d8*/                   CAL `($calls$_Z5innerj);
      |        /*00e0*/                   IADD R4, R4, R0;
      |        /*00e8*/                   RET;
      |.L_x_1:
      |        /*00f0*/                   BRA `(.L_x_1);
      |        /*00f8*/                   NOP;
      |.L_x_3:
      |""".stripMargin

  val callsSm50: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.calls               --------------------------
      |	.section	.text.calls,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=5"
      |	.align	32
      |        .global         calls
      |        .type           calls,@function
      |        .size           calls,(.L_x_7 - calls)
      |        .other          calls,@"STO_CUDA_ENTRY STV_DEFAULT"
      |calls:
      |.text.calls:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R2, SR_TID.X ;
      |        /*0018*/                   SSY `(.L_x_0) ;
      |        /*0028*/                   LOP32I.AND R0, R2, 0x1 ;
      |        /*0030*/                   ISETP.NE.U32.AND P0, PT, R0, 0x1, PT ;
      |.L_x_2:
      |        /*0038*/         {         MOV R0, R2 ;
      |        /*0048*/              @!P0 SYNC         }
      |        /*0050*/                   CAL `($calls$_Z5outerj) ;
      |.L_x_4:
      |        /*0058*/                   SYNC ;
      |.L_x_0:
      |        /*0068*/                   S2R R3, SR_CTAID.X ;
      |        /*0070*/                   XMAD R2, R3.reuse, c[0x0] [0x8], R2 ;
      |        /*0078*/                   XMAD.MRG R4, R3.reuse, c[0x0] [0x8].H1, RZ ;
      |        /*0088*/                   XMAD.PSL.CBCC R2, R3.H1, R4.H1, R2 ;
      |        /*0090*/                   SHR.U32 R3, R2.reuse, 0x1e ;
      |        /*0098*/                   ISCADD R2.CC, R2, c[0x0][0x140], 0x2 ;
      |        /*00a8*/                   IADD.X R3, R3, c[0x0][0x144] ;
      |        /*00b0*/                   STG.E [R2], R0 ;
      |        /*00b8*/                   EXIT ;
      |        .type           $calls$_Z5innerj,@function
      |        .size           $calls$_Z5innerj,($calls$_Z5outerj - $calls$_Z5innerj)
      |$calls$_Z5innerj:
      |        /*00c8*/                   MOV32I R3, 0x1 ;
      |        /*00d0*/                   XMAD R3, R4.reuse, 0x3, R3 ;
      |        /*00d8*/         {         XMAD.PSL R3, R4.H1, 0x3, R3 ;
      |        /*00e8*/                   RET         }
      |        .type           $calls$_Z5outerj,@function
      |        .size           $calls$_Z5outerj,(.L_x_7 - $calls$_Z5outerj)
      |$calls$_Z5outerj:
      |        /*00f0*/                   MOV R4, R2 ;
      |        /*00f8*/                   CAL `($calls$_Z5innerj) ;
      |        /*0108*/                   MOV R0, R3 ;
      |        /*0110*/                   IADD32I R4, R2, 0x1 ;
      |        /*0118*/                   CAL `($calls$_Z5innerj) ;
      |        /*0128*/         {         IADD R0, R3, R0 ;
      |        /*0130*/                   RET         }
      |.L_x_1:
      |        /*0138*/                   BRA `(.L_x_1) ;
      |.L_x_7:
      |""".stripMargin

  val callsSm60: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.calls               --------------------------
      |	.section	.text.calls,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=5"
      |	.align	32
      |        .global         calls
      |        .type           calls,@function
      |        .size           calls,(.L_x_7 - calls)
      |        .other          calls,@"STO_CUDA_ENTRY STV_DEFAULT"
      |calls:
      |.text.calls:
      |        /*0008*/                   MOV R1, c[0x0][0x20] ;
      |        /*0010*/                   S2R R2, SR_TID.X ;
      |        /*0018*/                   SSY `(.L_x_0) ;
      |        /*0028*/                   LOP32I.AND R0, R2, 0x1 ;
      |        /*0030*/                   ISETP.NE.U32.AND P0, PT, R0, 0x1, PT ;
      |.L_x_2:
      |        /*0038*/         {         MOV R0, R2 ;
      |        /*0048*/              @!P0 SYNC         }
      |        /*0050*/                   CAL `($calls$_Z5outerj) ;
      |.L_x_4:
      |        /*0058*/                   SYNC ;
      |.L_x_0:
      |        /*0068*/                   S2R R3, SR_CTAID.X ;
      |        /*0070*/                   XMAD R2, R3, c[0x0] [0x8], R2 ;
      |        /*0078*/                   XMAD.MRG R4, R3, c[0x0] [0x8].H1, RZ ;
      |        /*0088*/                   XMAD.PSL.CBCC R2, R3.H1, R4.H1, R2 ;
      |        /*0090*/                   SHR.U32 R3, R2.reuse, 0x1e ;
      |        /*0098*/                   ISCADD R2.CC, R2, c[0x0][0x140], 0x2 ;
      |        /*00a8*/                   IADD.X R3, R3, c[0x0][0x144] ;
      |        /*00b0*/                   STG.E [R2], R0 ;
      |        /*00b8*/                   EXIT ;
      |        .type           $calls$_Z5innerj,@function
      |        .size           $calls$_Z5innerj,($calls$_Z5outerj - $calls$_Z5innerj)
      |$calls$_Z5innerj:
      |        /*00c8*/                   MOV32I R3, 0x1 ;
      |        /*00d0*/                   XMAD R3, R4.reuse, 0x3, R3 ;
      |        /*00d8*/         {         XMAD.PSL R3, R4.H1, 0x3, R3 ;
      |        /*00e8*/                   RET         }
      |        .type           $calls$_Z5outerj,@function
      |        .size           $calls$_Z5outerj,(.L_x_7 - $calls$_Z5outerj)
      |$calls$_Z5outerj:
      |        /*00f0*/                   MOV R4, R2 ;
      |        /*00f8*/                   CAL `($calls$_Z5innerj) ;
      |        /*0108*/                   MOV R0, R3 ;
      |        /*0110*/                   IADD32I R4, R2, 0x1 ;
      |        /*0118*/                   CAL `($calls$_Z5innerj) ;
      |        /*0128*/         {         IADD R0, R3, R0 ;
      |        /*0130*/                   RET         }
      |.L_x_1:
      |        /*0138*/                   BRA `(.L_x_1) ;
      |.L_x_7:
      |""".stripMargin

  val indirectCallSm35: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.indirect_call       --------------------------
      |	.section	.text.indirect_call,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=5"
      |	.align	64
      |        .global         indirect_call
      |        .type           indirect_call,@function
      |        .size           indirect_call,(.L_x_3 - indirect_call)
      |        .other          indirect_call,@"STO_CUDA_ENTRY STV_DEFAULT"
      |indirect_call:
      |.text.indirect_call:
      |        /*0008*/                   MOV R1, c[0x0][0x44];
      |        /*0010*/                   S2R R2, SR_TID.X;
      |        /*0018*/                   LOP32I.AND R0, R2, 0x1;
      |        /*0020*/                   MOV R4, R2;
      |        /*0028*/                   ISETP.NE.U32.AND P0, PT, R0, 0x1, PT;
      |        /*0030*/                   MOV32I R0, 0xa8;
      |        /*0038*/              @!P0 MOV32I R0, 0x98;
      |        /*0048*/                   PRET `(.L_x_0);
      |        /*0050*/                   BRX R0 -0x58;
      |.L_x_0:
      |        /*0058*/                   S2R R3, SR_CTAID.X;
      |        /*0060*/                   IMAD R0, R3, c[0x0][0x28], R2;
      |        /*0068*/                   MOV32I R3, 0x4;
      |        /*0070*/                   ISCADD R2.CC, R0, c[0x0][0x140], 0x2;
      |        /*0078*/                   IMAD.U32.U32.HI.X R3, R0, R3, c[0x0][0x144];
      |        /*0088*/                   ST.E [R2], R4;
      |        /*0090*/                   EXIT;
      |        .type           $indirect_call$_Z5twicej,@function
      |        .size           $indirect_call$_Z5twicej,($indirect_call$_Z6thricej - $indirect_call$_Z5twicej)
      |$indirect_call$_Z5twicej:
      |        /*0098*/                   SHF.L R4, RZ, 0x1, R4;
      |        /*00a0*/                   RET;
      |        .type           $indirect_call$_Z6thricej,@function
      |        .size           $indirect_call$_Z6thricej,(.L_x_3 - $indirect_call$_Z6thricej)
      |$indirect_call$_Z6thricej:
      |        /*00a8*/                   IMUL32I R4, R4, 0x3;
      |        /*00b0*/                   RET;
      |.L_x_1:
      |        /*00b8*/                   BRA `(.L_x_1);
      |.L_x_3:
      |""".stripMargin

}

class CallFixtures extends NvidiaFixtureSuite {
  import CallFixtures.*

  reproduces("callsSm35")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "calls-sm35.cu" -> callsSm35CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_35 calls-sm35.cu -o calls-sm35.cubin",
      "nvdisasm -c -ndf calls-sm35.cubin > calls-sm35.sass"
    ),
    output = "calls-sm35.sass",
    expected = callsSm35
  )

  reproduces("callsSm50")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "calls-sm35.cu" -> callsSm35CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_50 calls-sm35.cu -o calls-sm50.cubin",
      "nvdisasm -c -ndf calls-sm50.cubin > calls-sm50.sass"
    ),
    output = "calls-sm50.sass",
    expected = callsSm50
  )

  reproduces("callsSm60")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "calls-sm35.cu" -> callsSm35CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_60 calls-sm35.cu -o calls-sm60.cubin",
      "nvdisasm -c -ndf calls-sm60.cubin > calls-sm60.sass"
    ),
    output = "calls-sm60.sass",
    expected = callsSm60
  )

  reproduces("indirectCallSm35")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "indirect-call-sm35.cu" -> indirectCallSm35CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_35 indirect-call-sm35.cu -o indirect-call-sm35.cubin",
      "nvdisasm -c -ndf indirect-call-sm35.cubin > indirect-call-sm35.sass"
    ),
    output = "indirect-call-sm35.sass",
    expected = indirectCallSm35
  )

}
