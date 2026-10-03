package net.kurobako.gemstone.isa.sass.fixtures

import java.nio.file.Files
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object SaxpyFixtures {
  val saxpyCudaSource: String =
    """#include <cuda_runtime.h>
      |extern "C" __global__ void saxpy(float *y, const float *x, float alpha, int n) {
      | int i=blockIdx.x*blockDim.x+threadIdx.x;
      | if(i<n) y[i]=alpha*x[i]+y[i];
      |}
      |""".stripMargin

  val mutationsMetadata: String =
    """{
      |  "probes": [
      |    {
      |      "id": "nop-unused-x-bit-127-set",
      |      "offset": 2848,
      |      "instruction_offset": "0x420",
      |      "base_instruction_hex": "18790000000000000000000000c00f00",
      |      "mutation": {
      |        "field": "nop__unused X (don't-care)",
      |        "bit": 127,
      |        "xor": 1
      |      },
      |      "expected": {
      |        "nvdisasm_exit_code": 0,
      |        "text": "NOP;"
      |      }
      |    },
      |    {
      |      "id": "nop-src-rel-sb-zero",
      |      "offset": 2848,
      |      "instruction_offset": "0x420",
      |      "base_instruction_hex": "18790000000000000000000000c00f00",
      |      "mutation": {
      |        "field": "src_rel_sb",
      |        "shift": 113,
      |        "width": 3,
      |        "value": 0
      |      },
      |      "expected": {
      |        "nvdisasm_exit_code": 0,
      |        "text": "NOP;"
      |      }
      |    },
      |    {
      |      "id": "nop-dst-wr-sb-zero",
      |      "offset": 2848,
      |      "instruction_offset": "0x420",
      |      "base_instruction_hex": "18790000000000000000000000c00f00",
      |      "mutation": {
      |        "field": "dst_wr_sb",
      |        "shift": 110,
      |        "width": 3,
      |        "value": 0
      |      },
      |      "expected": {
      |        "nvdisasm_exit_code": 0,
      |        "text": "NOP;"
      |      }
      |    },
      |    {
      |      "id": "exit-src-rel-sb-zero",
      |      "offset": 2816,
      |      "instruction_offset": "0x400",
      |      "base_instruction_hex": "4d790000000000000000800300de3f00",
      |      "mutation": {
      |        "field": "src_rel_sb",
      |        "shift": 113,
      |        "width": 3,
      |        "value": 0
      |      },
      |      "expected": {
      |        "nvdisasm_exit_code": 1,
      |        "diagnostic": "Illegal instruction found"
      |      }
      |    },
      |    {
      |      "id": "exit-dst-wr-sb-zero",
      |      "offset": 2816,
      |      "instruction_offset": "0x400",
      |      "base_instruction_hex": "4d790000000000000000800300de3f00",
      |      "mutation": {
      |        "field": "dst_wr_sb",
      |        "shift": 110,
      |        "width": 3,
      |        "value": 0
      |      },
      |      "expected": {
      |        "nvdisasm_exit_code": 1,
      |        "diagnostic": "Illegal instruction found"
      |      }
      |    },
      |    {
      |      "id": "mov-const-bank-address",
      |      "offset": 1792,
      |      "instruction_offset": "0x0",
      |      "base_instruction_hex": "027a0100000a0000000f000000de3f00",
      |      "class": "ConstBankAddress2",
      |      "mutations": [
      |        {
      |          "field": "bank",
      |          "shift": 54,
      |          "width": 5,
      |          "value": 3
      |        },
      |        {
      |          "field": "addr",
      |          "shift": 40,
      |          "width": 14,
      |          "value": 291
      |        }
      |      ],
      |      "expected": {
      |        "nvdisasm_exit_code": 0,
      |        "text": "MOV R1, c[0x3][0x48c]"
      |      }
      |    },
      |    {
      |      "id": "ldc-direct-byte-offset",
      |      "offset": 1856,
      |      "instruction_offset": "0x40",
      |      "base_instruction_hex": "827b020200580000000a0000001e3200",
      |      "class": "ConstBankAddress0",
      |      "mutations": [
      |        {
      |          "field": "addr",
      |          "shift": 38,
      |          "width": 16,
      |          "value": 4660
      |        }
      |      ],
      |      "expected": {
      |        "nvdisasm_exit_code": 0,
      |        "text": "LDC.64 R2, c[0x0][R2+0x1234]"
      |      }
      |    }
      |  ]
      |}
      |""".stripMargin

  val saxpySource: String =
    """.version 8.5
      |.target sm_89
      |.address_size 64
      |
      |.visible .entry saxpy(
      |    .param .u64 saxpy_param_0,
      |    .param .u64 saxpy_param_1,
      |    .param .f32 saxpy_param_2,
      |    .param .u32 saxpy_param_3
      |)
      |{
      |    .reg .pred %p<2>;
      |    .reg .b32 %r<5>;
      |    .reg .b64 %rd<5>;
      |    .reg .f32 %f<4>;
      |
      |    ld.param.u64 %rd1, [saxpy_param_0];
      |    ld.param.u64 %rd2, [saxpy_param_1];
      |    ld.param.f32 %f1, [saxpy_param_2];
      |    ld.param.u32 %r1, [saxpy_param_3];
      |    mov.u32 %r2, %tid.x;
      |    mov.u32 %r3, %ctaid.x;
      |    mov.u32 %r4, %ntid.x;
      |    mad.lo.u32 %r2, %r3, %r4, %r2;
      |    setp.ge.u32 %p1, %r2, %r1;
      |    @%p1 bra DONE;
      |    mul.wide.u32 %rd3, %r2, 4;
      |    add.s64 %rd4, %rd2, %rd3;
      |    ld.global.f32 %f2, [%rd4];
      |    add.s64 %rd4, %rd1, %rd3;
      |    ld.global.f32 %f3, [%rd4];
      |    fma.rn.f32 %f3, %f2, %f1, %f3;
      |    st.global.f32 [%rd4], %f3;
      |DONE:
      |    ret;
      |}
      |""".stripMargin

  val saxpy: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.saxpy               --------------------------
      |	.section	.text.saxpy,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=14"
      |	.align	128
      |        .global         saxpy
      |        .type           saxpy,@function
      |        .size           saxpy,(.L_x_2 - saxpy)
      |        .other          saxpy,@"STO_CUDA_ENTRY STV_DEFAULT"
      |saxpy:
      |.text.saxpy:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                 /* 0x00000a0000017a02 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0010*/                   MOV R6, c[0x0][0x118] ;                /* 0x0000460000067a02 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0020*/                   MOV R7, c[0x0][0x11c] ;                /* 0x0000470000077a02 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0030*/                   MOV R2, RZ ;                           /* 0x000000ff00027202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0040*/                   LDC.64 R2, c[0x0][R2+0x160] ;          /* 0x0000580002027b82 */
      |                                                                          /* 0x00321e0000000a00 */
      |        /*0050*/                   MOV R10, R2 ;                          /* 0x00000002000a7202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0060*/                   MOV R11, R3 ;                          /* 0x00000003000b7202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0070*/                   MOV R10, R10 ;                         /* 0x0000000a000a7202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0080*/                   MOV R11, R11 ;                         /* 0x0000000b000b7202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0090*/                   MOV R2, 0x8 ;                          /* 0x0000000800027802 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*00a0*/                   LDC.64 R2, c[0x0][R2+0x160] ;          /* 0x0000580002027b82 */
      |                                                                          /* 0x00321e0000000a00 */
      |        /*00b0*/                   MOV R8, R2 ;                           /* 0x0000000200087202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*00c0*/                   MOV R9, R3 ;                           /* 0x0000000300097202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*00d0*/                   MOV R8, R8 ;                           /* 0x0000000800087202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*00e0*/                   MOV R9, R9 ;                           /* 0x0000000900097202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*00f0*/                   MOV R0, 0x10 ;                         /* 0x0000001000007802 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0100*/                   LDC R0, c[0x0][R0+0x160] ;             /* 0x0000580000007b82 */
      |                                                                          /* 0x00321e0000000800 */
      |        /*0110*/                   MOV R0, R0 ;                           /* 0x0000000000007202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0120*/                   MOV R2, 0x14 ;                         /* 0x0000001400027802 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0130*/                   LDC R2, c[0x0][R2+0x160] ;             /* 0x0000580002027b82 */
      |                                                                          /* 0x00321e0000000800 */
      |        /*0140*/                   MOV R2, R2 ;                           /* 0x0000000200027202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0150*/                   S2R R3, SR_TID.X ;                     /* 0x0000000000037919 */
      |                                                                          /* 0x00321e0000002100 */
      |        /*0160*/                   MOV R3, R3 ;                           /* 0x0000000300037202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0170*/                   S2R R4, SR_CTAID.X ;                   /* 0x0000000000047919 */
      |                                                                          /* 0x00321e0000002500 */
      |        /*0180*/                   MOV R4, R4 ;                           /* 0x0000000400047202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0190*/                   MOV R5, c[0x0][0x0] ;                  /* 0x0000000000057a02 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*01a0*/                   IMAD.U32 R3, R4, R5, R3 ;              /* 0x0000000504037224 */
      |                                                                          /* 0x003fde00078e0003 */
      |        /*01b0*/                   ISETP.GE.U32.AND P0, PT, R3, R2, PT ;  /* 0x000000020300720c */
      |                                                                          /* 0x003fde0003f06070 */
      |        /*01c0*/                   MOV R10, R10 ;                         /* 0x0000000a000a7202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*01d0*/                   MOV R11, R11 ;                         /* 0x0000000b000b7202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*01e0*/                   MOV R4, R8 ;                           /* 0x0000000800047202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*01f0*/                   MOV R9, R9 ;                           /* 0x0000000900097202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0200*/                   MOV R0, R0 ;                           /* 0x0000000000007202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0210*/                   MOV R3, R3 ;                           /* 0x0000000300037202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0220*/               @P0 BRA `(.L_x_0) ;                        /* 0x000001d000000947 */
      |                                                                          /* 0x003fde0003800000 */
      |        /*0230*/                   IMAD.WIDE.U32 R2, R3, 0x4, RZ ;        /* 0x0000000403027825 */
      |                                                                          /* 0x003fde00078e00ff */
      |        /*0240*/                   MOV R5, R2 ;                           /* 0x0000000200057202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0250*/                   MOV R8, R3 ;                           /* 0x0000000300087202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0260*/                   IADD3 R2, P0, R4, R5, RZ ;             /* 0x0000000504027210 */
      |                                                                          /* 0x003fde0007f1e0ff */
      |        /*0270*/                   IADD3.X R3, R9, R8, RZ, P0, !PT ;      /* 0x0000000809037210 */
      |                                                                          /* 0x003fde00007fe4ff */
      |        /*0280*/                   MOV R2, R2 ;                           /* 0x0000000200027202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0290*/                   MOV R3, R3 ;                           /* 0x0000000300037202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*02a0*/                   MOV R2, R2 ;                           /* 0x0000000200027202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*02b0*/                   MOV R3, R3 ;                           /* 0x0000000300037202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*02c0*/                   R2UR UR4, R6 ;                         /* 0x00000000060473c2 */
      |                                                                          /* 0x00321e00000e0000 */
      |        /*02d0*/                   R2UR UR5, R7 ;                         /* 0x00000000070573c2 */
      |                                                                          /* 0x00321e00000e0000 */
      |        /*02e0*/                   LDG.E R4, [R2.64] ;                    /* 0x0000000402047981 */
      |                                                                          /* 0x00321e000c1e1900 */
      |        /*02f0*/                   IADD3 R5, P0, R10, R5, RZ ;            /* 0x000000050a057210 */
      |                                                                          /* 0x003fde0007f1e0ff */
      |        /*0300*/                   IADD3.X R8, R11, R8, RZ, P0, !PT ;     /* 0x000000080b087210 */
      |                                                                          /* 0x003fde00007fe4ff */
      |        /*0310*/                   MOV R2, R5 ;                           /* 0x0000000500027202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0320*/                   MOV R3, R8 ;                           /* 0x0000000800037202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0330*/                   MOV R2, R2 ;                           /* 0x0000000200027202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0340*/                   MOV R3, R3 ;                           /* 0x0000000300037202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*0350*/                   R2UR UR4, R6 ;                         /* 0x00000000060473c2 */
      |                                                                          /* 0x00321e00000e0000 */
      |        /*0360*/                   R2UR UR5, R7 ;                         /* 0x00000000070573c2 */
      |                                                                          /* 0x00321e00000e0000 */
      |        /*0370*/                   LDG.E R2, [R2.64] ;                    /* 0x0000000402027981 */
      |                                                                          /* 0x00321e000c1e1900 */
      |        /*0380*/                   FFMA R0, R4, R0, R2 ;                  /* 0x0000000004007223 */
      |                                                                          /* 0x003fde0000000002 */
      |        /*0390*/                   MOV R2, R5 ;                           /* 0x0000000500027202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*03a0*/                   MOV R3, R8 ;                           /* 0x0000000800037202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*03b0*/                   MOV R2, R2 ;                           /* 0x0000000200027202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*03c0*/                   MOV R3, R3 ;                           /* 0x0000000300037202 */
      |                                                                          /* 0x003fde0000000f00 */
      |        /*03d0*/                   R2UR UR4, R6 ;                         /* 0x00000000060473c2 */
      |                                                                          /* 0x00321e00000e0000 */
      |        /*03e0*/                   R2UR UR5, R7 ;                         /* 0x00000000070573c2 */
      |                                                                          /* 0x00321e00000e0000 */
      |        /*03f0*/                   STG.E [R2.64], R0 ;                    /* 0x0000000002007986 */
      |                                                                          /* 0x0033de000c101904 */
      |.L_x_0:
      |        /*0400*/                   EXIT ;                                 /* 0x000000000000794d */
      |                                                                          /* 0x003fde0003800000 */
      |.L_x_1:
      |        /*0410*/                   BRA `(.L_x_1);                         /* 0xfffffff000007947 */
      |                                                                          /* 0x000fc0000383ffff */
      |        /*0420*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |        /*0430*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |        /*0440*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |        /*0450*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |        /*0460*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |        /*0470*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |        /*0480*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |        /*0490*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |        /*04a0*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |        /*04b0*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |        /*04c0*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |        /*04d0*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |        /*04e0*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |        /*04f0*/                   NOP;                                   /* 0x0000000000007918 */
      |                                                                          /* 0x000fc00000000000 */
      |.L_x_2:
      |""".stripMargin

  val listings: Map[Int, String] = Vec(
    Vec(35) ->
      """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.saxpy               --------------------------
        |	.section	.text.saxpy,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	64
        |        .global         saxpy
        |        .type           saxpy,@function
        |        .size           saxpy,(.L_x_1 - saxpy)
        |        .other          saxpy,@"STO_CUDA_ENTRY STV_DEFAULT"
        |saxpy:
        |.text.saxpy:
        |                                                                                /* 0x0880b8b0a0a08cc0 */
        |        /*0008*/                   MOV R1, c[0x0][0x44];                        /* 0x64c03c00089c0006 */
        |        /*0010*/                   S2R R0, SR_CTAID.X;                          /* 0x86400000129c0002 */
        |        /*0018*/                   S2R R3, SR_TID.X;                            /* 0x86400000109c000e */
        |        /*0020*/                   IMAD R0, R0, c[0x0][0x28], R3;               /* 0x51080c00051c0002 */
        |        /*0028*/                   ISETP.GE.AND P0, PT, R0, c[0x0][0x154], PT;  /* 0x5b681c002a9c001e */
        |        /*0030*/               @P0 EXIT;                                        /* 0x180000000000003c */
        |        /*0038*/                   ISCADD R4.CC, R0, c[0x0][0x148], 0x2;        /* 0x60c40800291c0012 */
        |                                                                                /* 0x08a0fca0b010a0ac */
        |        /*0048*/                   MOV32I R3, 0x4;                              /* 0x74000000021fc00e */
        |        /*0050*/                   IMAD.HI.X R5, R0, R3, c[0x0][0x14c];         /* 0x93180c00299c0016 */
        |        /*0058*/                   ISCADD R2.CC, R0, c[0x0][0x140], 0x2;        /* 0x60c40800281c000a */
        |        /*0060*/                   LD.E R4, [R4];                               /* 0xc4800000001c1010 */
        |        /*0068*/                   IMAD.HI.X R3, R0, R3, c[0x0][0x144];         /* 0x93180c00289c000e */
        |        /*0070*/                   LD.E R0, [R2];                               /* 0xc4800000001c0800 */
        |        /*0078*/                   FFMA R6, R4, c[0x0][0x150], R0;              /* 0x4c0000002a1c101a */
        |                                                                                /* 0x080000000000b810 */
        |        /*0088*/                   ST.E [R2], R6;                               /* 0xe4800000001c0818 */
        |        /*0090*/                   EXIT;                                        /* 0x18000000001c003c */
        |.L_x_0:
        |        /*0098*/                   BRA `(.L_x_0);                               /* 0x12007ffffc1c003c */
        |        /*00a0*/                   NOP;                                         /* 0x85800000001c3c02 */
        |        /*00a8*/                   NOP;                                         /* 0x85800000001c3c02 */
        |        /*00b0*/                   NOP;                                         /* 0x85800000001c3c02 */
        |        /*00b8*/                   NOP;                                         /* 0x85800000001c3c02 */
        |.L_x_1:
        |""".stripMargin,
    Vec(50) ->
      """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM50 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM50)"
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.saxpy               --------------------------
        |	.section	.text.saxpy,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	32
        |        .global         saxpy
        |        .type           saxpy,@function
        |        .size           saxpy,(.L_x_1 - saxpy)
        |        .other          saxpy,@"STO_CUDA_ENTRY STV_DEFAULT"
        |saxpy:
        |.text.saxpy:
        |                                                                                 /* 0x001cfc00e22007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;                        /* 0x4c98078000870001 */
        |        /*0010*/                   S2R R0, SR_CTAID.X ;                          /* 0xf0c8000002570000 */
        |        /*0018*/                   S2R R2, SR_TID.X ;                            /* 0xf0c8000002170002 */
        |                                                                                 /* 0x001fd842fec20ff1 */
        |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;  /* 0x4f107f8000270003 */
        |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;         /* 0x4e00010000270002 */
        |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;          /* 0x5b30011800370000 */
        |                                                                                 /* 0x001ff400fd4007ed */
        |        /*0048*/                   ISETP.GE.AND P0, PT, R0, c[0x0][0x154], PT ;  /* 0x4b6d038005570007 */
        |        /*0050*/                   NOP ;                                         /* 0x50b0000000070f00 */
        |        /*0058*/               @P0 EXIT ;                                        /* 0xe30000000000000f */
        |                                                                                 /* 0x081fd800fea207f1 */
        |        /*0068*/                   SHL R2, R0.reuse, 0x2 ;                       /* 0x3848000000270002 */
        |        /*0070*/                   SHR R0, R0, 0x1e ;                            /* 0x3829000001e70000 */
        |        /*0078*/                   IADD R4.CC, R2.reuse, c[0x0][0x148] ;         /* 0x4c10800005270204 */
        |                                                                                 /* 0x001fd800fe0207f2 */
        |        /*0088*/                   IADD.X R5, R0.reuse, c[0x0][0x14c] ;          /* 0x4c10080005370005 */
        |        /*0090*/         {         IADD R2.CC, R2, c[0x0][0x140] ;               /* 0x4c10800005070202 */
        |        /*0098*/                   LDG.E R4, [R4]         }
        |                                                                                 /* 0xeed4200000070404 */
        |                                                                                 /* 0x041fc800f6a007e2 */
        |        /*00a8*/                   IADD.X R3, R0, c[0x0][0x144] ;                /* 0x4c10080005170003 */
        |        /*00b0*/                   LDG.E R6, [R2] ;                              /* 0xeed4200000070206 */
        |        /*00b8*/                   FFMA R0, R4, c[0x0][0x150], R6 ;              /* 0x4980030005470400 */
        |                                                                                 /* 0x001f9000fde007f1 */
        |        /*00c8*/                   STG.E [R2], R0 ;                              /* 0xeedc200000070200 */
        |        /*00d0*/                   NOP ;                                         /* 0x50b0000000070f00 */
        |        /*00d8*/                   NOP ;                                         /* 0x50b0000000070f00 */
        |                                                                                 /* 0x001f8000ffe007ff */
        |        /*00e8*/                   EXIT ;                                        /* 0xe30000000007000f */
        |.L_x_0:
        |        /*00f0*/                   BRA `(.L_x_0) ;                               /* 0xe2400fffff87000f */
        |        /*00f8*/                   NOP;                                          /* 0x50b0000000070f00 */
        |.L_x_1:
        |""".stripMargin,
    Vec(60) ->
      """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM60 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM60)"
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.saxpy               --------------------------
        |	.section	.text.saxpy,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	32
        |        .global         saxpy
        |        .type           saxpy,@function
        |        .size           saxpy,(.L_x_1 - saxpy)
        |        .other          saxpy,@"STO_CUDA_ENTRY STV_DEFAULT"
        |saxpy:
        |.text.saxpy:
        |                                                                                 /* 0x001cfc00e22007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;                        /* 0x4c98078000870001 */
        |        /*0010*/                   S2R R0, SR_CTAID.X ;                          /* 0xf0c8000002570000 */
        |        /*0018*/                   S2R R2, SR_TID.X ;                            /* 0xf0c8000002170002 */
        |                                                                                 /* 0x001fd842fec20ff1 */
        |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;  /* 0x4f107f8000270003 */
        |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;         /* 0x4e00010000270002 */
        |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;          /* 0x5b30011800370000 */
        |                                                                                 /* 0x001ff400fd4007ed */
        |        /*0048*/                   ISETP.GE.AND P0, PT, R0, c[0x0][0x154], PT ;  /* 0x4b6d038005570007 */
        |        /*0050*/                   NOP ;                                         /* 0x50b0000000070f00 */
        |        /*0058*/               @P0 EXIT ;                                        /* 0xe30000000000000f */
        |                                                                                 /* 0x001f9800fca207f1 */
        |        /*0068*/                   SHL R2, R0.reuse, 0x2 ;                       /* 0x3848000000270002 */
        |        /*0070*/                   SHR R0, R0, 0x1e ;                            /* 0x3829000001e70000 */
        |        /*0078*/                   IADD R4.CC, R2, c[0x0][0x148] ;               /* 0x4c10800005270204 */
        |                                                                                 /* 0x001fd800fe0007e2 */
        |        /*0088*/                   IADD.X R5, R0, c[0x0][0x14c] ;                /* 0x4c10080005370005 */
        |        /*0090*/         {         IADD R2.CC, R2, c[0x0][0x140] ;               /* 0x4c10800005070202 */
        |        /*0098*/                   LDG.E R4, [R4]         }
        |                                                                                 /* 0xeed4200000070404 */
        |                                                                                 /* 0x041f8800f6a007e2 */
        |        /*00a8*/                   IADD.X R3, R0, c[0x0][0x144] ;                /* 0x4c10080005170003 */
        |        /*00b0*/                   LDG.E R6, [R2] ;                              /* 0xeed4200000070206 */
        |        /*00b8*/                   FFMA R0, R4, c[0x0][0x150], R6 ;              /* 0x4980030005470400 */
        |                                                                                 /* 0x001f9000fde007f1 */
        |        /*00c8*/                   STG.E [R2], R0 ;                              /* 0xeedc200000070200 */
        |        /*00d0*/                   NOP ;                                         /* 0x50b0000000070f00 */
        |        /*00d8*/                   NOP ;                                         /* 0x50b0000000070f00 */
        |                                                                                 /* 0x001f8000ffe007ff */
        |        /*00e8*/                   EXIT ;                                        /* 0xe30000000007000f */
        |.L_x_0:
        |        /*00f0*/                   BRA `(.L_x_0) ;                               /* 0xe2400fffff87000f */
        |        /*00f8*/                   NOP;                                          /* 0x50b0000000070f00 */
        |.L_x_1:
        |""".stripMargin,
    Vec(70) ->
      """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.saxpy               --------------------------
        |	.section	.text.saxpy,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         saxpy
        |        .type           saxpy,@function
        |        .size           saxpy,(.L_x_1 - saxpy)
        |        .other          saxpy,@"STO_CUDA_ENTRY STV_DEFAULT"
        |saxpy:
        |.text.saxpy:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                        /* 0x00000a0000017a02 */
        |                                                                                 /* 0x000fc40000000f00 */
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                 /* 0x000000fffffff389 */
        |                                                                                 /* 0x000fe200000e00ff */
        |        /*0020*/                   S2R R4, SR_CTAID.X ;                          /* 0x0000000000047919 */
        |                                                                                 /* 0x000e280000002500 */
        |        /*0030*/                   S2R R3, SR_TID.X ;                            /* 0x0000000000037919 */
        |                                                                                 /* 0x000e240000002100 */
        |        /*0040*/                   IMAD R4, R4, c[0x0][0x0], R3 ;                /* 0x0000000004047a24 */
        |                                                                                 /* 0x001fca00078e0203 */
        |        /*0050*/                   ISETP.GE.AND P0, PT, R4, c[0x0][0x174], PT ;  /* 0x00005d0004007a0c */
        |                                                                                 /* 0x000fd80003f06270 */
        |        /*0060*/               @P0 EXIT ;                                        /* 0x000000000000094d */
        |                                                                                 /* 0x000fea0003800000 */
        |        /*0070*/                   MOV R5, 0x4 ;                                 /* 0x0000000400057802 */
        |                                                                                 /* 0x000fca0000000f00 */
        |        /*0080*/                   IMAD.WIDE R2, R4, R5, c[0x0][0x168] ;         /* 0x00005a0004027625 */
        |                                                                                 /* 0x000fc800078e0205 */
        |        /*0090*/                   IMAD.WIDE R4, R4, R5, c[0x0][0x160] ;         /* 0x0000580004047625 */
        |                                                                                 /* 0x000fc800078e0205 */
        |        /*00a0*/                   LDG.E.SYS R2, [R2] ;                          /* 0x0000000002027381 */
        |                                                                                 /* 0x000ea800001ee900 */
        |        /*00b0*/                   LDG.E.SYS R7, [R4] ;                          /* 0x0000000004077381 */
        |                                                                                 /* 0x000ea400001ee900 */
        |        /*00c0*/                   FFMA R7, R2, c[0x0][0x170], R7 ;              /* 0x00005c0002077a23 */
        |                                                                                 /* 0x004fd00000000007 */
        |        /*00d0*/                   STG.E.SYS [R4], R7 ;                          /* 0x0000000704007386 */
        |                                                                                 /* 0x000fe2000010e900 */
        |        /*00e0*/                   EXIT ;                                        /* 0x000000000000794d */
        |                                                                                 /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*00f0*/                   BRA `(.L_x_0);                                /* 0xfffffff000007947 */
        |                                                                                 /* 0x000fc0000383ffff */
        |.L_x_1:
        |""".stripMargin,
    Vec(75) -> """	.target	sm_75
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.saxpy               --------------------------
        |	.section	.text.saxpy,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         saxpy
        |        .type           saxpy,@function
        |        .size           saxpy,(.L_x_1 - saxpy)
        |        .other          saxpy,@"STO_CUDA_ENTRY STV_DEFAULT"
        |saxpy:
        |.text.saxpy:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_CTAID.X ;
        |        /*0020*/                   S2R R3, SR_TID.X ;
        |        /*0030*/                   IMAD R4, R4, c[0x0][0x0], R3 ;
        |        /*0040*/                   ISETP.GE.AND P0, PT, R4, c[0x0][0x174], PT ;
        |        /*0050*/               @P0 EXIT ;
        |        /*0060*/                   MOV R5, 0x4 ;
        |        /*0070*/                   IMAD.WIDE R2, R4, R5, c[0x0][0x168] ;
        |        /*0080*/                   IMAD.WIDE R4, R4, R5, c[0x0][0x160] ;
        |        /*0090*/                   LDG.E.SYS R2, [R2] ;
        |        /*00a0*/                   LDG.E.SYS R7, [R4] ;
        |        /*00b0*/                   FFMA R7, R2, c[0x0][0x170], R7 ;
        |        /*00c0*/                   STG.E.SYS [R4], R7 ;
        |        /*00d0*/                   EXIT ;
        |.L_x_0:
        |        /*00e0*/                   BRA `(.L_x_0);
        |        /*00f0*/                   NOP;
        |.L_x_1:
        |""".stripMargin,
    Vec(80) -> """	.target	sm_80
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.saxpy               --------------------------
        |	.section	.text.saxpy,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         saxpy
        |        .type           saxpy,@function
        |        .size           saxpy,(.L_x_1 - saxpy)
        |        .other          saxpy,@"STO_CUDA_ENTRY STV_DEFAULT"
        |saxpy:
        |.text.saxpy:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_CTAID.X ;
        |        /*0020*/                   S2R R3, SR_TID.X ;
        |        /*0030*/                   IMAD R4, R4, c[0x0][0x0], R3 ;
        |        /*0040*/                   ISETP.GE.AND P0, PT, R4, c[0x0][0x174], PT ;
        |        /*0050*/               @P0 EXIT ;
        |        /*0060*/                   HFMA2.MMA R5, -RZ, RZ, 0, 2.384185791015625e-07 ;
        |        /*0070*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0080*/                   IMAD.WIDE R2, R4, R5, c[0x0][0x168] ;
        |        /*0090*/                   IMAD.WIDE R4, R4, R5, c[0x0][0x160] ;
        |        /*00a0*/                   LDG.E R2, [R2.64] ;
        |        /*00b0*/                   LDG.E R7, [R4.64] ;
        |        /*00c0*/                   FFMA R7, R2, c[0x0][0x170], R7 ;
        |        /*00d0*/                   STG.E [R4.64], R7 ;
        |        /*00e0*/                   EXIT ;
        |.L_x_0:
        |        /*00f0*/                   BRA `(.L_x_0);
        |        /*0100*/                   NOP;
        |        /*0110*/                   NOP;
        |        /*0120*/                   NOP;
        |        /*0130*/                   NOP;
        |        /*0140*/                   NOP;
        |        /*0150*/                   NOP;
        |        /*0160*/                   NOP;
        |        /*0170*/                   NOP;
        |.L_x_1:
        |""".stripMargin,
    Vec(86, 89) -> """	.target	sm_86
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.saxpy               --------------------------
        |	.section	.text.saxpy,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         saxpy
        |        .type           saxpy,@function
        |        .size           saxpy,(.L_x_1 - saxpy)
        |        .other          saxpy,@"STO_CUDA_ENTRY STV_DEFAULT"
        |saxpy:
        |.text.saxpy:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_CTAID.X ;
        |        /*0020*/                   S2R R3, SR_TID.X ;
        |        /*0030*/                   IMAD R4, R4, c[0x0][0x0], R3 ;
        |        /*0040*/                   ISETP.GE.AND P0, PT, R4, c[0x0][0x174], PT ;
        |        /*0050*/               @P0 EXIT ;
        |        /*0060*/                   MOV R5, 0x4 ;
        |        /*0070*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0080*/                   IMAD.WIDE R2, R4, R5, c[0x0][0x168] ;
        |        /*0090*/                   IMAD.WIDE R4, R4, R5, c[0x0][0x160] ;
        |        /*00a0*/                   LDG.E R2, [R2.64] ;
        |        /*00b0*/                   LDG.E R7, [R4.64] ;
        |        /*00c0*/                   FFMA R7, R2, c[0x0][0x170], R7 ;
        |        /*00d0*/                   STG.E [R4.64], R7 ;
        |        /*00e0*/                   EXIT ;
        |.L_x_0:
        |        /*00f0*/                   BRA `(.L_x_0);
        |        /*0100*/                   NOP;
        |        /*0110*/                   NOP;
        |        /*0120*/                   NOP;
        |        /*0130*/                   NOP;
        |        /*0140*/                   NOP;
        |        /*0150*/                   NOP;
        |        /*0160*/                   NOP;
        |        /*0170*/                   NOP;
        |.L_x_1:
        |""".stripMargin,
    Vec(87) -> """	.target	sm_87
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.saxpy               --------------------------
        |	.section	.text.saxpy,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         saxpy
        |        .type           saxpy,@function
        |        .size           saxpy,(.L_x_2 - saxpy)
        |        .other          saxpy,@"STO_CUDA_ENTRY STV_DEFAULT"
        |saxpy:
        |.text.saxpy:
        |        /*0000*/                   ISETP.NE.U32.AND P0, PT, RZ, UR2, PT ;
        |        /*0010*/               @P0 BRA `(.L_x_0) ;
        |        /*0020*/                   BMOV.32 B0, 0xffffffff ;
        |        /*0030*/                   BMOV.32.CLEAR B1, B0 ;
        |        /*0040*/                   BMOV.32.CLEAR B2, B1 ;
        |        /*0050*/                   BMOV.32.CLEAR B3, B2 ;
        |        /*0060*/                   BMOV.32.CLEAR B4, B3 ;
        |        /*0070*/                   BMOV.32.CLEAR B5, B4 ;
        |        /*0080*/                   BMOV.32.CLEAR B6, B5 ;
        |        /*0090*/                   BMOV.32.CLEAR B7, B6 ;
        |        /*00a0*/                   BMOV.32.CLEAR B8, B7 ;
        |        /*00b0*/                   BMOV.32.CLEAR B9, B8 ;
        |        /*00c0*/                   BMOV.32.CLEAR B10, B9 ;
        |        /*00d0*/                   BMOV.32.CLEAR B11, B10 ;
        |        /*00e0*/                   BMOV.32.CLEAR B12, B11 ;
        |        /*00f0*/                   BMOV.32.CLEAR B13, B12 ;
        |        /*0100*/                   BMOV.32.CLEAR B14, B13 ;
        |        /*0110*/                   BMOV.32.CLEAR B15, B14 ;
        |        /*0120*/                   BMOV.32 B15, 0x0 ;
        |        /*0130*/                   UMOV UR2, 0x1 ;
        |.L_x_0:
        |        /*0140*/                   MOV R1, c[0x0][0x28] ;
        |        /*0150*/                   S2R R4, SR_CTAID.X ;
        |        /*0160*/                   S2R R3, SR_TID.X ;
        |        /*0170*/                   IMAD R4, R4, c[0x0][0x0], R3 ;
        |        /*0180*/                   ISETP.GE.AND P0, PT, R4, c[0x0][0x174], PT ;
        |        /*0190*/               @P0 EXIT ;
        |        /*01a0*/                   HFMA2.MMA R5, -RZ, RZ, 0, 2.384185791015625e-07 ;
        |        /*01b0*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*01c0*/                   IMAD.WIDE R2, R4, R5, c[0x0][0x168] ;
        |        /*01d0*/                   IMAD.WIDE R4, R4, R5, c[0x0][0x160] ;
        |        /*01e0*/                   LDG.E R2, [R2.64] ;
        |        /*01f0*/                   LDG.E R7, [R4.64] ;
        |        /*0200*/                   FFMA R7, R2, c[0x0][0x170], R7 ;
        |        /*0210*/                   STG.E [R4.64], R7 ;
        |        /*0220*/                   EXIT ;
        |.L_x_1:
        |        /*0230*/                   BRA `(.L_x_1);
        |        /*0240*/                   NOP;
        |        /*0250*/                   NOP;
        |        /*0260*/                   NOP;
        |        /*0270*/                   NOP;
        |        /*0280*/                   NOP;
        |        /*0290*/                   NOP;
        |        /*02a0*/                   NOP;
        |        /*02b0*/                   NOP;
        |        /*02c0*/                   NOP;
        |        /*02d0*/                   NOP;
        |        /*02e0*/                   NOP;
        |        /*02f0*/                   NOP;
        |.L_x_2:
        |""".stripMargin,
    Vec(90) -> """	.target	sm_90
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.saxpy               --------------------------
        |	.section	.text.saxpy,"ax",@progbits
        |	.align	128
        |        .global         saxpy
        |        .type           saxpy,@function
        |        .size           saxpy,(.L_x_1 - saxpy)
        |        .other          saxpy,@"STO_CUDA_ENTRY STV_DEFAULT"
        |saxpy:
        |.text.saxpy:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   S2UR UR4, SR_CTAID.X ;
        |        /*0030*/                   LDC R7, c[0x0][RZ] ;
        |        /*0040*/                   IMAD R7, R7, UR4, R0 ;
        |        /*0050*/                   ULDC UR4, c[0x0][0x224] ;
        |        /*0060*/                   ISETP.GE.AND P0, PT, R7, UR4, PT ;
        |        /*0070*/               @P0 EXIT ;
        |        /*0080*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0090*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*00a0*/                   ULDC UR6, c[0x0][0x220] ;
        |        /*00b0*/                   LDC.64 R4, c[0x0][0x210] ;
        |        /*00c0*/                   IMAD.WIDE R2, R7, 0x4, R2 ;
        |        /*00d0*/                   LDG.E R2, desc[UR4][R2.64] ;
        |        /*00e0*/                   IMAD.WIDE R4, R7, 0x4, R4 ;
        |        /*00f0*/                   LDG.E R7, desc[UR4][R4.64] ;
        |        /*0100*/                   FFMA R7, R2, UR6, R7 ;
        |        /*0110*/                   STG.E desc[UR4][R4.64], R7 ;
        |        /*0120*/                   EXIT ;
        |.L_x_0:
        |        /*0130*/                   BRA `(.L_x_0);
        |        /*0140*/                   NOP;
        |        /*0150*/                   NOP;
        |        /*0160*/                   NOP;
        |        /*0170*/                   NOP;
        |        /*0180*/                   NOP;
        |        /*0190*/                   NOP;
        |        /*01a0*/                   NOP;
        |        /*01b0*/                   NOP;
        |        /*01c0*/                   NOP;
        |        /*01d0*/                   NOP;
        |        /*01e0*/                   NOP;
        |        /*01f0*/                   NOP;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    Vec(100, 103, 120) -> """	.target	sm_100
        |
        |	.elftype	@"ET_EXEC"
        |
        |
        |//--------------------- .text.saxpy               --------------------------
        |	.section	.text.saxpy,"ax",@progbits
        |	.align	128
        |        .global         saxpy
        |        .type           saxpy,@function
        |        .size           saxpy,(.L_x_1 - saxpy)
        |        .other          saxpy,@"STO_CUDA_ENTRY STV_DEFAULT"
        |saxpy:
        |.text.saxpy:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   S2UR UR4, SR_CTAID.X ;
        |        /*0030*/                   LDCU UR5, c[0x0][0x394] ;
        |        /*0040*/                   LDC R7, c[0x0][0x360] ;
        |        /*0050*/                   IMAD R7, R7, UR4, R0 ;
        |        /*0060*/                   ISETP.GE.AND P0, PT, R7, UR5, PT ;
        |        /*0070*/               @P0 EXIT ;
        |        /*0080*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0090*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*00a0*/                   LDCU UR6, c[0x0][0x390] ;
        |        /*00b0*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*00c0*/                   IMAD.WIDE R2, R7, 0x4, R2 ;
        |        /*00d0*/                   LDG.E R2, desc[UR4][R2.64] ;
        |        /*00e0*/                   IMAD.WIDE R4, R7, 0x4, R4 ;
        |        /*00f0*/                   LDG.E R7, desc[UR4][R4.64] ;
        |        /*0100*/                   FFMA R7, R2, UR6, R7 ;
        |        /*0110*/                   STG.E desc[UR4][R4.64], R7 ;
        |        /*0120*/                   EXIT ;
        |.L_x_0:
        |        /*0130*/                   BRA `(.L_x_0);
        |        /*0140*/                   NOP;
        |        /*0150*/                   NOP;
        |        /*0160*/                   NOP;
        |        /*0170*/                   NOP;
        |        /*0180*/                   NOP;
        |        /*0190*/                   NOP;
        |        /*01a0*/                   NOP;
        |        /*01b0*/                   NOP;
        |        /*01c0*/                   NOP;
        |        /*01d0*/                   NOP;
        |        /*01e0*/                   NOP;
        |        /*01f0*/                   NOP;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin
  ).flatMap { (targets, text) =>
    targets.map(sm => sm -> text.replace(s".target\tsm_${targets.head}", s".target\tsm_$sm"))
  }.toMap

  val code: Map[Int, String] = Vec(
    Vec(35) -> """c08ca0a0b0b8800806009c08003cc06402009c12000040860e009c1000004086
        |02001c05000c08511e009c2a001c685b3c0000000000001812001c290008c460
        |aca010b0a0fca0080ec01f020000007416009c29000c18930a001c280008c460
        |10101c00000080c40e009c28000c189300081c00000080c41a101c2a0000004c
        |10b800000000000818081c00000080e43c001c00000000183c001cfcff7f0012
        |023c1c0000008085023c1c0000008085023c1c0000008085023c1c0000008085
        |""".stripMargin,
    Vec(50) -> """f60720e200fc1c00010087008007984c000057020000c8f0020017020000c8f0
        |f10fc2fe42d81f0003002700807f104f020027000001004e000037001801305b
        |ed0740fd00f41f000700570580036d4b000f07000000b0500f000000000000e3
        |f107a2fe00d81f0802002700000048380000e70100002938040227050080104c
        |f20702fe00d81f00050037050008104c020207050080104c040407000020d4ee
        |e207a0f600c81f04030017050008104c060207000020d4ee0004470500038049
        |f107e0fd00901f00000207000020dcee000f07000000b050000f07000000b050
        |ff07e0ff00801f000f000700000000e30f0087ffff0f40e2000f07000000b050
        |""".stripMargin,
    Vec(60) -> """f60720e200fc1c00010087008007984c000057020000c8f0020017020000c8f0
        |f10fc2fe42d81f0003002700807f104f020027000001004e000037001801305b
        |ed0740fd00f41f000700570580036d4b000f07000000b0500f000000000000e3
        |f107a2fc00981f0002002700000048380000e70100002938040227050080104c
        |e20700fe00d81f00050037050008104c020207050080104c040407000020d4ee
        |e207a0f600881f04030017050008104c060207000020d4ee0004470500038049
        |f107e0fd00901f00000207000020dcee000f07000000b050000f07000000b050
        |ff07e0ff00801f000f000700000000e30f0087ffff0f40e2000f07000000b050
        |""".stripMargin,
    Vec(70) -> """027a0100000a0000000f000000c40f0089f3ffffff000000ff000e0000e20f00
        |19790400000000000025000000280e0019790300000000000021000000240e00
        |247a04040000000003028e0700ca1f000c7a0004005d00007062f00300d80f00
        |4d090000000000000000800300ea0f000278050004000000000f000000ca0f00
        |25760204005a000005028e0700c80f00257604040058000005028e0700c80f00
        |817302020000000000e91e0000a80e00817307040000000000e91e0000a40e00
        |237a0702005c00000700000000d04f00867300040700000000e9100000e20f00
        |4d790000000000000000800300ea0f0047790000f0ffffffffff830300c00f00
        |""".stripMargin,
    Vec(75) -> """027a0100000a0000000f000000e40f0019790400000000000025000000280e00
        |19790300000000000021000000240e00247a04040000000003028e0700ca1f00
        |0c7a0004005d00007062f00300d80f004d090000000000000000800300ea0f00
        |0278050004000000000f000000ca0f0025760204005a000005028e0700c80f00
        |257604040058000005028e0700c80f00817302020000000000e91e0000a80e00
        |817307040000000000e91e0000a40e00237a0702005c00000700000000d04f00
        |867300040700000000e9100000e20f004d790000000000000000800300ea0f00
        |47790000f0ffffffffff830300c00f0018790000000000000000000000c00f00
        |""".stripMargin,
    Vec(80) -> """027a0100000a0000000f000000e40f0019790400000000000025000000280e00
        |19790300000000000021000000240e00247a04040000000003028e0700ca1f00
        |0c7a0004005d00007062f00300da0f004d090000000000000000800300ea0f00
        |357405ff04000000ff01000000e20f00b97a040000460000000a000000d20f00
        |25760204005a000005028e0700c80f00257604040058000005028e0700e40f00
        |817902020400000000191e0c00a80e00817907040400000000191e0c00a40e00
        |237a0702005c00000700000000ca4f0086790004070000000419100c00e20f00
        |4d790000000000000000800300ea0f0047790000f0ffffffffff830300c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |""".stripMargin,
    Vec(86, 89) -> """027a0100000a0000000f000000e40f0019790400000000000025000000280e00
        |19790300000000000021000000240e00247a04040000000003028e0700ca1f00
        |0c7a0004005d00007062f00300da0f004d090000000000000000800300ea0f00
        |0278050004000000000f000000e20f00b97a040000460000000a000000c80f00
        |25760204005a000005028e0700c80f00257604040058000005028e0700e40f00
        |817902020400000000191e0c00a80e00817907040400000000191e0c00a40e00
        |237a0702005c00000700000000ca4f0086790004070000000419100c00e20f00
        |4d790000000000000000800300ea0f0047790000f0ffffffffff830300c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |""".stripMargin,
    Vec(87) -> """0c7c00ff020000007050f00b00da0f0047090000200100000000800300ea0f00
        |56790000ffffffff0000000000e80f00557f0100000000000000100000e80f00
        |557f0201000000000000100000e80f00557f0302000000000000100000e80f00
        |557f0403000000000000100000e80f00557f0504000000000000100000e80f00
        |557f0605000000000000100000e80f00557f0706000000000000100000e80f00
        |557f0807000000000000100000e80f00557f0908000000000000100000e80f00
        |557f0a09000000000000100000e80f00557f0b0a000000000000100000e80f00
        |557f0c0b000000000000100000e80f00557f0d0c000000000000100000e80f00
        |557f0e0d000000000000100000e80f00557f0f0e000000000000100000e80f00
        |5679000f000000000000000000e80f0082780200010000000000000000e40f00
        |027a0100000a0000000f000000e40f0019790400000000000025000000280e00
        |19790300000000000021000000240e00247a04040000000003028e0700ca1f00
        |0c7a0004005d00007062f00300da0f004d090000000000000000800300ea0f00
        |357405ff04000000ff01000000e20f00b97a040000460000000a000000d20f00
        |25760204005a000005028e0700c80f00257604040058000005028e0700e40f00
        |817902020400000000191e0c00a80e00817907040400000000191e0c00a40e00
        |237a0702005c00000700000000ca4f0086790004070000000419100c00e20f00
        |4d790000000000000000800300ea0f0047790000f0ffffffffff830300c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |""".stripMargin,
    Vec(90) -> """827b01ff000a00000008000000e20f00197900000000000000210000002e0e00
        |c3790400000000000025000000300e00827b07ff000000000008000000240e00
        |247c07070400000000028e0f00e21f00b97a0400008900000008000000c80f00
        |0c7c0007040000007062f00b00da0f004d090000000000000000800300ea0f00
        |827b02ff00860000000a000000220e00b97a040000820000000a000000e20f00
        |b97a0600008800000008000000cc0f00827b04ff00840000000a000000620e00
        |257802070400000002028e0700cc1f00817902020400000000191e0c00a20e00
        |257804070400000004028e0700ca2f00817907040400000000191e0c00a40e00
        |237c0702060000000700000800ca4f0086790004070000000419100c00e20f00
        |4d790000000000000000800300ea0f004779fc00fcffffffffff830300c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |""".stripMargin,
    Vec(100, 103, 120) -> """827b01ff00df00000008000000e20f00197900000000000000210000002e0e00
        |c3790400000000000025000000220e00ac7705ff8072000000080008006e0e00
        |827b07ff00d800000008000000240e00247c07070400000000028e0f00ca1f00
        |0c7c0007050000007062f00b00da2f004d090000000000000000800300ea0f00
        |827b02ff00e20000000a000000220e00ac7704ff006b0000000a000800620e00
        |ac7706ff007200000008000800ac0e00827b04ff00e00000000a000000e20e00
        |257802070400000002028e0700cc1f00817902020400000000191e0c00a22e00
        |257804070400000004028e0700ca8f00817907040400000000191e0c00a40e00
        |237c0702060000000700000800ca4f0086790004070000000419100c00e20f00
        |4d790000000000000000800300ea0f004779fc00fcffffffffff830300c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |18790000000000000000000000c00f0018790000000000000000000000c00f00
        |""".stripMargin
  ).flatMap((targets, text) => targets.map(_ -> text)).toMap

  val parameters: Map[Int, (base: Int >= 0, registers: Int)] =
    Vec(35, 50, 60).map(_ -> (base = 320: Int >= 0, registers = 7)).toMap ++
      Vec(70, 75, 80, 86, 87, 89).map(_ -> (base = 352: Int >= 0, registers = 10)).toMap ++
      Map(90 -> (base = 528: Int >= 0, registers = 0)) ++
      Vec(100, 103, 120).map(_ -> (base = 896: Int >= 0, registers = 0)).toMap

}

class SaxpyFixtures extends NvidiaFixtureSuite {
  import SaxpyFixtures.*

  for ((sm, expectedCode) <- code.toVec.sortBy(_._1)) {
    val tools = if (sm <= 70) (ptxas = "11.8.89", nvdisasm = Some("11.8.86"))
    else (ptxas = "13.3.73", nvdisasm = Some("13.3.73"))
    val flags = if (sm <= 70) "-c -ndf -hex" else "-c -ndf"
    verifies(s"saxpySm$sm listing and code")(
      tools = tools,
      inputs = Vec("input.cu" -> saxpyCudaSource)
    ) { (directory, run) =>
      run(Vec(
        s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm input.cu -o kernel.cubin",
        s"nvdisasm $flags kernel.cubin > output.sass",
        "objcopy -I elf64-little --dump-section .text.saxpy=code.bin kernel.cubin /dev/null"
      ))
      assertEquals(Files.readString(directory.resolve("output.sass")), listings(sm))
      assertEquals(FixtureBytes.writeHex(Files.readAllBytes(directory.resolve("code.bin"))), expectedCode)
    }
  }

  reproduces("saxpy")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "saxpy.ptx" -> saxpySource
    ),
    script = Vec(
      "ptxas -arch=sm_89 -O0 saxpy.ptx -o saxpy.cubin",
      "nvdisasm -c -ndf -hex saxpy.cubin > saxpy.sass"
    ),
    output = "saxpy.sass",
    expected = saxpy
  )

  verifies("instruction mutations")(
    tools = (ptxas = "13.3.33", nvdisasm = Some("13.3.73")),
    inputs = Vec("saxpy.ptx" -> saxpySource)
  ) { (directory, run) =>
    run(Vec("ptxas -arch=sm_89 -O0 saxpy.ptx -o saxpy.cubin"))
    val base = Files.readAllBytes(directory.resolve("saxpy.cubin"))
    FixtureMutations.read(mutationsMetadata).foreach { probe =>
      val id = probe.id
      require(id.nonEmpty && id.forall(c => c.isLetterOrDigit || c == '-'), s"Invalid probe name: $id")
      val changed = FixtureMutations(base, probe).fold(fail(_), identity)
      val _       = Files.write(directory.resolve(s"$id.cubin"), changed)
      run(Vec(
        s"if nvdisasm -c -ndf -hex $id.cubin > $id.sass 2>&1; then echo 0 > $id.exit; else echo $$? > $id.exit; fi"
      ))
      assertEquals(
        Files.readString(directory.resolve(s"$id.exit")).trim.toInt,
        probe.expectedExitCode,
        id
      )
      val token   = probe.expectedOutput
      val listing = Files.readString(directory.resolve(s"$id.sass"))
      assert(listing.contains(token), s"$id: expected '$token' in NVIDIA output:\n$listing")
    }
  }
}
