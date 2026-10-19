package net.kurobako.gemstone.isa.sass.fixtures

object MemoryFormFixtures {

  /** atomicAdd(&shared, 1u) with the result unused, which ptxas lowers to ATOMS.POPC.INC from sm_80. */
  val sharedCountersCuda: String =
    """extern "C" __global__ void shared_counters(unsigned *out) {
      |  __shared__ unsigned s[4];
      |  if (threadIdx.x < 4) s[threadIdx.x] = 0u;
      |  __syncthreads();
      |  atomicAdd(&s[threadIdx.x & 3u], 1u);
      |  __syncthreads();
      |  if (threadIdx.x < 4) out[threadIdx.x] = s[threadIdx.x];
      |}
      |""".stripMargin

  /** One thread arrives on an mbarrier and reads its two words after each step; before sm_90 the words are plain shared
    * memory.
    */
  val mbarrierWordsCuda: String =
    """#include <cstdint>
      |__device__ void words(unsigned *out, int &i, uint64_t *bar) {
      |  volatile unsigned *w = (volatile unsigned *)bar;
      |  out[i++] = w[0];
      |  out[i++] = w[1];
      |}
      |extern "C" __global__ void mbarrier_words(unsigned *out) {
      |  __shared__ alignas(8) uint64_t bar;
      |  unsigned b = (unsigned)__cvta_generic_to_shared(&bar);
      |  int i = 0;
      |  uint64_t t;
      |  asm volatile("mbarrier.init.shared.b64 [%0], %1;" ::"r"(b), "r"(3));
      |  words(out, i, &bar);
      |  for (int step = 0; step < 7; ++step) {
      |    asm volatile("mbarrier.arrive.shared.b64 %0, [%1];" : "=l"(t) : "r"(b));
      |    words(out, i, &bar);
      |  }
      |  asm volatile("mbarrier.arrive.noComplete.shared.b64 %0, [%1], %2;" : "=l"(t) : "r"(b), "r"(2));
      |  words(out, i, &bar);
      |  asm volatile("mbarrier.arrive_drop.shared.b64 %0, [%1];" : "=l"(t) : "r"(b));
      |  words(out, i, &bar);
      |  int pending;
      |  asm volatile("mbarrier.pending_count.b64 %0, %1;" : "=r"(pending) : "l"(t));
      |  out[i] = pending;
      |}
      |""".stripMargin

  /** Shared and global accesses whose base ptxas keeps in a uniform register. */
  val offsetsCuda: String =
    """extern "C" __global__ void uniform_offsets(int *out, int k) {
      |  extern __shared__ int dyn[];
      |  int t = threadIdx.x;
      |  int *base = dyn + k;
      |  base[t] = t * 3;
      |  __syncthreads();
      |  atomicMax(&base[(t * 5) & 127], t);
      |  __syncthreads();
      |  out[t] = base[(t + 7) & 127];
      |}
      |extern "C" __global__ void u32_offsets(const unsigned char *__restrict__ in, unsigned char *__restrict__ out, unsigned k) {
      |  unsigned t = threadIdx.x;
      |  out[t * 3u + k] = in[t * 5u + k] + 1;
      |}
      |""".stripMargin

  /** Dynamically indexed local memory, which sm_120 addresses through a uniform register. */
  val localOffsetsCuda: String =
    """extern "C" __global__ void local_offsets(int *out, int k) {
      |  int a[32];
      |  for (int i = 0; i < 32; ++i) a[i] = i * k;
      |  int s = 0;
      |  for (int i = 0; i < k; ++i) s += a[(threadIdx.x + i * k) & 31];
      |  a[(threadIdx.x * k) & 31] = s;
      |  out[threadIdx.x] = a[(threadIdx.x + k) & 31];
      |}
      |""".stripMargin

  /** ld.global.v8.b32 and st.global.v8.b32, the 256-bit accesses of sm_100. */
  val wideCuda: String =
    """extern "C" __global__ void wide(const int4 *p, int *q) {
      |  int t = threadIdx.x;
      |  int r[8];
      |  asm volatile("ld.global.v8.b32 {%0,%1,%2,%3,%4,%5,%6,%7}, [%8];"
      |               : "=r"(r[0]), "=r"(r[1]), "=r"(r[2]), "=r"(r[3]), "=r"(r[4]), "=r"(r[5]), "=r"(r[6]), "=r"(r[7])
      |               : "l"(p + 2 * t));
      |  q[t] = r[0] * 3 + r[4] * 5 + r[1] * 7;
      |  asm volatile("st.global.v8.b32 [%0], {%1,%2,%3,%4,%5,%6,%7,%8};" ::"l"(q + 64 + 8 * t), "r"(t), "r"(t + 1), "r"(t + 2),
      |               "r"(t + 3), "r"(t * 5), "r"(t * 5 + 1), "r"(t * 5 + 2), "r"(t * 5 + 3));
      |}
      |""".stripMargin

  /** Shared atomics, which sm_35 lowers to LDSLK and STSCUL retry loops. */
  val keplerSharedAtomicsCuda: String =
    """extern "C" __global__ void kepler_shared_atomics(int *out) {
      |  __shared__ int s[4];
      |  __shared__ unsigned long long d;
      |  if (threadIdx.x < 4) s[threadIdx.x] = 0;
      |  if (threadIdx.x == 0) d = 0xffffff00ull;
      |  __syncthreads();
      |  atomicAdd(&s[0], (int)threadIdx.x);
      |  atomicMax(&s[1], (int)(threadIdx.x ^ 21u));
      |  atomicAdd(&s[2 + (threadIdx.x & 1)], 1);
      |  atomicAdd(&d, (unsigned long long)threadIdx.x);
      |  __syncthreads();
      |  if (threadIdx.x < 4) out[threadIdx.x] = s[threadIdx.x];
      |  if (threadIdx.x == 0) {
      |    out[4] = (int)d;
      |    out[5] = (int)(d >> 32);
      |  }
      |}
      |""".stripMargin

  val listings: Map[String, String] = Map(
    "sharedCountersSm89" ->
      """//--------------------- .text.shared_counters     --------------------------
        |	.section	.text.shared_counters,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         shared_counters
        |        .type           shared_counters,@function
        |        .size           shared_counters,(.L_x_1 - shared_counters)
        |        .other          shared_counters,@"STO_CUDA_ENTRY STV_DEFAULT"
        |shared_counters:
        |.text.shared_counters:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   ISETP.GT.U32.AND P0, PT, R4.reuse, 0x3, PT ;
        |        /*0030*/                   SHF.L.U32 R0, R4, 0x2, RZ ;
        |        /*0040*/                   LOP3.LUT R0, R0, 0xc, RZ, 0xc0, !PT ;
        |        /*0050*/              @!P0 STS [R4.X4], RZ ;
        |        /*0060*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0070*/                   ATOMS.POPC.INC.32 RZ, [R0+URZ] ;
        |        /*0080*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0090*/               @P0 EXIT ;
        |        /*00a0*/                   LDS R5, [R4.X4] ;
        |        /*00b0*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x4 ;
        |        /*00c0*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*00d0*/                   IMAD.WIDE.U32 R2, R4, R3, c[0x0][0x160] ;
        |        /*00e0*/                   STG.E [R2.64], R5 ;
        |        /*00f0*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "mbarrierWordsSm89" ->
      """//--------------------- .text.mbarrier_words      --------------------------
        |	.section	.text.mbarrier_words,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         mbarrier_words
        |        .type           mbarrier_words,@function
        |        .size           mbarrier_words,(.L_x_1 - mbarrier_words)
        |        .other          mbarrier_words,@"STO_CUDA_ENTRY STV_DEFAULT"
        |mbarrier_words:
        |.text.mbarrier_words:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   IMAD.MOV.U32 R4, RZ, RZ, 0x3ffffffd ;
        |        /*0020*/                   MOV R5, 0x7ffffffd ;
        |        /*0030*/                   IMAD.MOV.U32 R2, RZ, RZ, c[0x0][0x160] ;
        |        /*0040*/                   MOV R3, c[0x0][0x164] ;
        |        /*0050*/                   ULDC.64 UR6, c[0x0][0x118] ;
        |        /*0060*/                   STS.64 [RZ], R4 ;
        |        /*0070*/                   LDS R7, [RZ] ;
        |        /*0080*/                   LDS R9, [0x4] ;
        |        /*0090*/                   STG.E [R2.64], R7 ;
        |        /*00a0*/                   STG.E [R2.64+0x4], R9 ;
        |        /*00b0*/                   MEMBAR.ALL.CTA ;
        |        /*00c0*/                   ATOMS.ARRIVE.64 RZ, [URZ] ;
        |        /*00d0*/                   LDS R5, [RZ] ;
        |        /*00e0*/                   LDS R7, [0x4] ;
        |        /*00f0*/                   STG.E [R2.64+0x8], R5 ;
        |        /*0100*/                   STG.E [R2.64+0xc], R7 ;
        |        /*0110*/                   MEMBAR.ALL.CTA ;
        |        /*0120*/                   ATOMS.ARRIVE.64 RZ, [URZ] ;
        |        /*0130*/                   LDS R5, [RZ] ;
        |        /*0140*/                   LDS R7, [0x4] ;
        |        /*0150*/                   STG.E [R2.64+0x10], R5 ;
        |        /*0160*/                   STG.E [R2.64+0x14], R7 ;
        |        /*0170*/                   MEMBAR.ALL.CTA ;
        |        /*0180*/                   ATOMS.ARRIVE.64 RZ, [URZ] ;
        |        /*0190*/                   LDS R5, [RZ] ;
        |        /*01a0*/                   LDS R7, [0x4] ;
        |        /*01b0*/                   STG.E [R2.64+0x18], R5 ;
        |        /*01c0*/                   STG.E [R2.64+0x1c], R7 ;
        |        /*01d0*/                   MEMBAR.ALL.CTA ;
        |        /*01e0*/                   ATOMS.ARRIVE.64 RZ, [URZ] ;
        |        /*01f0*/                   LDS R5, [RZ] ;
        |        /*0200*/                   LDS R7, [0x4] ;
        |        /*0210*/                   STG.E [R2.64+0x20], R5 ;
        |        /*0220*/                   STG.E [R2.64+0x24], R7 ;
        |        /*0230*/                   MEMBAR.ALL.CTA ;
        |        /*0240*/                   ATOMS.ARRIVE.64 RZ, [URZ] ;
        |        /*0250*/                   LDS R5, [RZ] ;
        |        /*0260*/                   LDS R7, [0x4] ;
        |        /*0270*/                   STG.E [R2.64+0x28], R5 ;
        |        /*0280*/                   STG.E [R2.64+0x2c], R7 ;
        |        /*0290*/                   MEMBAR.ALL.CTA ;
        |        /*02a0*/                   ATOMS.ARRIVE.64 RZ, [URZ] ;
        |        /*02b0*/                   LDS R5, [RZ] ;
        |        /*02c0*/                   LDS R7, [0x4] ;
        |        /*02d0*/                   STG.E [R2.64+0x30], R5 ;
        |        /*02e0*/                   STG.E [R2.64+0x34], R7 ;
        |        /*02f0*/                   MEMBAR.ALL.CTA ;
        |        /*0300*/                   ATOMS.ARRIVE.64 RZ, [URZ] ;
        |        /*0310*/                   LDS R5, [RZ] ;
        |        /*0320*/                   LDS R7, [0x4] ;
        |        /*0330*/                   S2R R4, SR_LANEID ;
        |        /*0340*/                   STG.E [R2.64+0x38], R5 ;
        |        /*0350*/                   STG.E [R2.64+0x3c], R7 ;
        |        /*0360*/                   MEMBAR.ALL.CTA ;
        |        /*0370*/                   VOTEU.ANY UR4, UPT, PT ;
        |        /*0380*/                   POPC R0, UR4 ;
        |        /*0390*/                   UFLO.U32 UR4, UR4 ;
        |        /*03a0*/                   ISETP.EQ.U32.AND P0, PT, R4, UR4, PT ;
        |        /*03b0*/                   IMAD.SHL.U32 R0, R0, 0x2, RZ ;
        |        /*03c0*/               @P0 ATOMS.ADD RZ, [0x4], R0 ;
        |        /*03d0*/                   LDS R5, [RZ] ;
        |        /*03e0*/                   LDS R7, [0x4] ;
        |        /*03f0*/                   STG.E [R2.64+0x40], R5 ;
        |        /*0400*/                   STG.E [R2.64+0x44], R7 ;
        |        /*0410*/                   MEMBAR.ALL.CTA ;
        |        /*0420*/                   ATOMS.POPC.INC.32 RZ, [URZ] ;
        |        /*0430*/                   ATOMS.ARRIVE.64 R4, [URZ] ;
        |        /*0440*/                   LDS R7, [RZ] ;
        |        /*0450*/                   LDS R9, [0x4] ;
        |        /*0460*/                   LOP3.LUT R5, R5, 0x7fffffff, RZ, 0xc0, !PT ;
        |        /*0470*/                   IADD3 R5, -R5, -0x80000000, RZ ;
        |        /*0480*/                   STG.E [R2.64+0x50], R5 ;
        |        /*0490*/                   STG.E [R2.64+0x48], R7 ;
        |        /*04a0*/                   STG.E [R2.64+0x4c], R9 ;
        |        /*04b0*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "sharedOffsetsSm100" ->
      """//--------------------- .text.uniform_offsets     --------------------------
        |	.section	.text.uniform_offsets,"ax",@progbits
        |	.align	128
        |        .global         uniform_offsets
        |        .type           uniform_offsets,@function
        |        .size           uniform_offsets,(.L_x_1 - uniform_offsets)
        |        .other          uniform_offsets,@"STO_CUDA_ENTRY STV_DEFAULT"
        |uniform_offsets:
        |.text.uniform_offsets:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2UR UR5, SR_CgaCtaId ;
        |        /*0020*/                   S2R R9, SR_TID.X ;
        |        /*0030*/                   LDCU UR6, c[0x0][0x388] ;
        |        /*0040*/                   UMOV UR4, 0x400 ;
        |        /*0050*/                   ULEA UR4, UR5, UR4, 0x18 ;
        |        /*0060*/                   ULEA UR4, UR6, UR4, 0x2 ;
        |        /*0070*/                   IMAD R0, R9, 0x3, RZ ;
        |        /*0080*/                   LEA R5, R9.reuse, UR4, 0x2 ;
        |        /*0090*/                   IMAD R2, R9.reuse, 0x14, RZ ;
        |        /*00a0*/                   LEA R3, R9, 0x1c, 0x2 ;
        |        /*00b0*/                   STS [R5], R0 ;
        |        /*00c0*/                   LOP3.LUT R4, R2, 0x1fc, RZ, 0xc0, !PT ;
        |        /*00d0*/                   LOP3.LUT R6, R3, 0x1fc, RZ, 0xc0, !PT ;
        |        /*00e0*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*00f0*/                   LDC.64 R2, c[0x0][0x380] ;
        |        /*0100*/                   ATOMS.MAX.S32 RZ, [R4+UR4], R9 ;
        |        /*0110*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0120*/                   IMAD.WIDE.U32 R2, R9, 0x4, R2 ;
        |        /*0130*/                   LDS R7, [R6+UR4] ;
        |        /*0140*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0150*/                   STG.E desc[UR4][R2.64], R7 ;
        |        /*0160*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin,
    "u32OffsetsSm75" ->
      """//--------------------- .text.u32_offsets         --------------------------
        |	.section	.text.u32_offsets,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	128
        |        .global         u32_offsets
        |        .type           u32_offsets,@function
        |        .size           u32_offsets,(.L_x_1 - u32_offsets)
        |        .other          u32_offsets,@"STO_CUDA_ENTRY STV_DEFAULT"
        |u32_offsets:
        |.text.u32_offsets:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R3, SR_TID.X ;
        |        /*0020*/                   MOV R0, 0x5 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x160] ;
        |        /*0040*/                   IMAD R0, R3, R0, c[0x0][0x170] ;
        |        /*0050*/                   LDG.E.U8.CONSTANT.SYS R2, [R0.U32+UR4] ;
        |        /*0060*/                   IMAD R3, R3, -0x2, R0 ;
        |        /*0070*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0080*/                   IADD3 R2, R2, 0x1, RZ ;
        |        /*0090*/                   STG.E.U8.SYS [R3.U32+UR4], R2 ;
        |        /*00a0*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "localOffsetsSm120" ->
      """//--------------------- .text.local_offsets       --------------------------
        |	.section	.text.local_offsets,"ax",@progbits
        |	.align	128
        |        .global         local_offsets
        |        .type           local_offsets,@function
        |        .size           local_offsets,(.L_x_7 - local_offsets)
        |        .other          local_offsets,@"STO_CUDA_ENTRY STV_DEFAULT"
        |local_offsets:
        |.text.local_offsets:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   LDC R5, c[0x0][0x388] ;
        |        /*0020*/                   IADD R1, R1, -0x80 ;
        |        /*0030*/                   HFMA2 R4, -RZ, RZ, 0, 0 ;
        |        /*0040*/                   LDCU.64 UR6, c[0x0][0x358] ;
        |        /*0050*/                   MOV R0, RZ ;
        |        /*0060*/                   R2UR UR5, R1 ;
        |        /*0070*/                   IMAD R8, R5.reuse, 0x14, RZ ;
        |        /*0080*/                   ISETP.GE.AND P0, PT, R5.reuse, 0x1, PT ;
        |        /*0090*/                   IMAD R9, R5.reuse, 0x15, RZ ;
        |        /*00a0*/                   IMAD R10, R5.reuse, 0x16, RZ ;
        |        /*00b0*/                   IMAD R11, R5.reuse, 0x17, RZ ;
        |        /*00c0*/                   IMAD R12, R5.reuse, 0x18, RZ ;
        |        /*00d0*/                   IMAD R13, R5.reuse, 0x19, RZ ;
        |        /*00e0*/                   STL.128 [R1+0x50], R8 ;
        |        /*00f0*/                   IMAD R14, R5, 0x1a, RZ ;
        |        /*0100*/                   IMAD R15, R5.reuse, 0x1b, RZ ;
        |        /*0110*/                   IMAD R24, R5.reuse, 0x1c, RZ ;
        |        /*0120*/                   IMAD R25, R5.reuse, 0x1d, RZ ;
        |        /*0130*/                   STL.128 [R1+0x60], R12 ;
        |        /*0140*/                   IMAD R26, R5.reuse, 0x1e, RZ ;
        |        /*0150*/                   IMAD R27, R5.reuse, 0x1f, RZ ;
        |        /*0160*/                   IMAD.SHL.U32 R20, R5, 0x10, RZ ;
        |        /*0170*/                   IMAD R21, R5.reuse, 0x11, RZ ;
        |        /*0180*/                   STL.128 [R1+0x70], R24 ;
        |        /*0190*/                   IMAD R22, R5.reuse, 0x12, RZ ;
        |        /*01a0*/                   IMAD R23, R5.reuse, 0x13, RZ ;
        |        /*01b0*/                   IMAD.SHL.U32 R6, R5.reuse, 0x2, RZ ;
        |        /*01c0*/                   IMAD R7, R5.reuse, 0x3, RZ ;
        |        /*01d0*/                   STL.128 [R1+0x40], R20 ;
        |        /*01e0*/                   IMAD.SHL.U32 R16, R5, 0x4, RZ ;
        |        /*01f0*/                   IMAD R17, R5.reuse, 0x5, RZ ;
        |        /*0200*/                   STL.128 [R1], R4 ;
        |        /*0210*/                   IMAD R18, R5.reuse, 0x6, RZ ;
        |        /*0220*/                   IMAD R19, R5.reuse, 0x7, RZ ;
        |        /*0230*/                   IMAD.SHL.U32 R12, R5.reuse, 0x8, RZ ;
        |        /*0240*/                   IMAD R13, R5.reuse, 0x9, RZ ;
        |        /*0250*/                   STL.128 [R1+0x10], R16 ;
        |        /*0260*/                   IMAD R14, R5, 0xa, RZ ;
        |        /*0270*/                   IMAD R15, R5.reuse, 0xb, RZ ;
        |        /*0280*/                   IMAD R8, R5.reuse, 0xc, RZ ;
        |        /*0290*/                   IMAD R9, R5.reuse, 0xd, RZ ;
        |        /*02a0*/                   STL.128 [R1+0x20], R12 ;
        |        /*02b0*/                   IMAD R10, R5.reuse, 0xe, RZ ;
        |        /*02c0*/                   IMAD R11, R5, 0xf, RZ ;
        |        /*02d0*/                   STL.128 [R1+0x30], R8 ;
        |        /*02e0*/              @!P0 BRA `(.L_x_0) ;
        |        /*02f0*/                   S2R R2, SR_TID.X ;
        |        /*0300*/                   ISETP.GE.U32.AND P0, PT, R5.reuse, 0x10, PT ;
        |        /*0310*/                   UMOV UR4, URZ ;
        |        /*0320*/                   LOP3.LUT R22, R5, 0xf, RZ, 0xc0, !PT ;
        |        /*0330*/                   MOV R0, RZ ;
        |        /*0340*/              @!P0 BRA `(.L_x_1) ;
        |        /*0350*/                   LOP3.LUT R4, R5, 0x7ffffff0, RZ, 0xc0, !PT ;
        |        /*0360*/                   UMOV UR4, URZ ;
        |        /*0370*/                   IADD R35, R7, R2.reuse ;
        |        /*0380*/                   IADD R3, R16, R2.reuse ;
        |        /*0390*/                   IADD R17, R17, R2.reuse ;
        |        /*03a0*/                   IADD R21, R18, R2.reuse ;
        |        /*03b0*/                   IADD R19, R19, R2.reuse ;
        |        /*03c0*/                   IADD R23, R12, R2.reuse ;
        |        /*03d0*/                   IADD R13, R13, R2.reuse ;
        |        /*03e0*/                   IADD R25, R14, R2.reuse ;
        |        /*03f0*/                   IADD R15, R15, R2.reuse ;
        |        /*0400*/                   IADD R27, R8, R2.reuse ;
        |        /*0410*/                   IADD R9, R9, R2.reuse ;
        |        /*0420*/                   IADD R29, R10, R2.reuse ;
        |        /*0430*/                   IADD R11, R11, R2.reuse ;
        |        /*0440*/                   IADD R31, R6, R2.reuse ;
        |        /*0450*/                   IADD R7, R2, R5 ;
        |        /*0460*/                   MOV R33, R2 ;
        |        /*0470*/                   IADD R4, -R4, RZ ;
        |.L_x_2:
        |        /*0480*/                   IMAD.SHL.U32 R6, R33, 0x4, RZ ;
        |        /*0490*/                   IMAD.SHL.U32 R8, R7, 0x4, RZ ;
        |        /*04a0*/                   IMAD.SHL.U32 R10, R31, 0x4, RZ ;
        |        /*04b0*/                   LOP3.LUT R30, R6, 0x7c, RZ, 0xc0, !PT ;
        |        /*04c0*/                   IMAD.SHL.U32 R12, R35, 0x4, RZ ;
        |        /*04d0*/                   LOP3.LUT R28, R8, 0x7c, RZ, 0xc0, !PT ;
        |        /*04e0*/                   LOP3.LUT R26, R10, 0x7c, RZ, 0xc0, !PT ;
        |        /*04f0*/                   LDL R37, [R30+UR5] ;
        |        /*0500*/                   LOP3.LUT R24, R12, 0x7c, RZ, 0xc0, !PT ;
        |        /*0510*/                   LDL R6, [R28+UR5] ;
        |        /*0520*/                   LDL R8, [R26+UR5] ;
        |        /*0530*/                   LDL R10, [R24+UR5] ;
        |        /*0540*/                   IMAD.SHL.U32 R14, R21, 0x4, RZ ;
        |        /*0550*/                   IMAD.SHL.U32 R16, R19, 0x4, RZ ;
        |        /*0560*/                   IMAD.SHL.U32 R18, R23, 0x4, RZ ;
        |        /*0570*/                   LOP3.LUT R14, R14, 0x7c, RZ, 0xc0, !PT ;
        |        /*0580*/                   IMAD.SHL.U32 R12, R13, 0x4, RZ ;
        |        /*0590*/                   LOP3.LUT R16, R16, 0x7c, RZ, 0xc0, !PT ;
        |        /*05a0*/                   LOP3.LUT R18, R18, 0x7c, RZ, 0xc0, !PT ;
        |        /*05b0*/                   LOP3.LUT R12, R12, 0x7c, RZ, 0xc0, !PT ;
        |        /*05c0*/                   LDL R14, [R14+UR5] ;
        |        /*05d0*/                   IMAD.SHL.U32 R24, R27, 0x4, RZ ;
        |        /*05e0*/                   IMAD.SHL.U32 R30, R9, 0x4, RZ ;
        |        /*05f0*/                   LDL R16, [R16+UR5] ;
        |        /*0600*/                   IMAD.SHL.U32 R26, R29, 0x4, RZ ;
        |        /*0610*/                   LOP3.LUT R24, R24, 0x7c, RZ, 0xc0, !PT ;
        |        /*0620*/                   IMAD.SHL.U32 R28, R11, 0x4, RZ ;
        |        /*0630*/                   LDL R18, [R18+UR5] ;
        |        /*0640*/                   LOP3.LUT R30, R30, 0x7c, RZ, 0xc0, !PT ;
        |        /*0650*/                   LDL R12, [R12+UR5] ;
        |        /*0660*/                   LOP3.LUT R32, R26, 0x7c, RZ, 0xc0, !PT ;
        |        /*0670*/                   LOP3.LUT R26, R28, 0x7c, RZ, 0xc0, !PT ;
        |        /*0680*/                   LDL R24, [R24+UR5] ;
        |        /*0690*/                   LDL R30, [R30+UR5] ;
        |        /*06a0*/                   LDL R28, [R32+UR5] ;
        |        /*06b0*/                   LDL R26, [R26+UR5] ;
        |        /*06c0*/                   IADD3 R0, PT, PT, R6, R37, R0 ;
        |        /*06d0*/                   IMAD.SHL.U32 R37, R3, 0x4, RZ ;
        |        /*06e0*/                   IMAD.SHL.U32 R6, R17, 0x4, RZ ;
        |        /*06f0*/                   LOP3.LUT R37, R37, 0x7c, RZ, 0xc0, !PT ;
        |        /*0700*/                   LOP3.LUT R6, R6, 0x7c, RZ, 0xc0, !PT ;
        |        /*0710*/                   IADD3 R0, PT, PT, R10, R8, R0 ;
        |        /*0720*/                   IMAD.SHL.U32 R8, R25, 0x4, RZ ;
        |        /*0730*/                   LDL R37, [R37+UR5] ;
        |        /*0740*/                   IMAD.SHL.U32 R10, R15, 0x4, RZ ;
        |        /*0750*/                   LDL R6, [R6+UR5] ;
        |        /*0760*/                   LOP3.LUT R8, R8, 0x7c, RZ, 0xc0, !PT ;
        |        /*0770*/                   LOP3.LUT R10, R10, 0x7c, RZ, 0xc0, !PT ;
        |        /*0780*/                   LDL R8, [R8+UR5] ;
        |        /*0790*/                   LDL R10, [R10+UR5] ;
        |        /*07a0*/                   IADD R4, R4, 0x10 ;
        |        /*07b0*/                   ISETP.NE.U32.AND P0, PT, R4, RZ, PT ;
        |        /*07c0*/                   UIADD3 UR4, UPT, UPT, UR4, 0x10, URZ ;
        |        /*07d0*/                   IADD R7, R20.reuse, R7 ;
        |        /*07e0*/                   IADD R31, R20.reuse, R31 ;
        |        /*07f0*/                   IADD R35, R20.reuse, R35 ;
        |        /*0800*/                   IADD R3, R20.reuse, R3 ;
        |        /*0810*/                   IADD R17, R20.reuse, R17 ;
        |        /*0820*/                   IADD R21, R20.reuse, R21 ;
        |        /*0830*/                   IADD R19, R20.reuse, R19 ;
        |        /*0840*/                   IADD R23, R20.reuse, R23 ;
        |        /*0850*/                   IADD R13, R20.reuse, R13 ;
        |        /*0860*/                   IADD R25, R20.reuse, R25 ;
        |        /*0870*/                   IADD R15, R20.reuse, R15 ;
        |        /*0880*/                   IADD R27, R20.reuse, R27 ;
        |        /*0890*/                   IADD R9, R20.reuse, R9 ;
        |        /*08a0*/                   IADD R29, R20.reuse, R29 ;
        |        /*08b0*/                   IADD R11, R20.reuse, R11 ;
        |        /*08c0*/                   IADD R33, R20, R33 ;
        |        /*08d0*/                   IADD3 R37, PT, PT, R6, R37, R0 ;
        |        /*08e0*/                   IADD3 R37, PT, PT, R16, R14, R37 ;
        |        /*08f0*/                   IADD3 R37, PT, PT, R12, R18, R37 ;
        |        /*0900*/                   IADD3 R37, PT, PT, R10, R8, R37 ;
        |        /*0910*/                   IADD3 R37, PT, PT, R30, R24, R37 ;
        |        /*0920*/                   IADD3 R0, PT, PT, R26, R28, R37 ;
        |        /*0930*/               @P0 BRA `(.L_x_2) ;
        |        /*0940*/                   ISETP.NE.U32.AND P0, PT, R22, RZ, PT ;
        |        /*0950*/              @!P0 BRA `(.L_x_0) ;
        |.L_x_1:
        |        /*0960*/                   ISETP.GE.U32.AND P0, PT, R22, 0x8, PT ;
        |        /*0970*/                   LOP3.LUT R16, R5, 0x7, RZ, 0xc0, !PT ;
        |        /*0980*/              @!P0 BRA `(.L_x_3) ;
        |        /*0990*/                   IMAD R4, R5, UR4, R2 ;
        |        /*09a0*/                   IADD R6, R4.reuse, R5 ;
        |        /*09b0*/                   IMAD.SHL.U32 R4, R4, 0x4, RZ ;
        |        /*09c0*/                   IADD R8, R6.reuse, R5 ;
        |        /*09d0*/                   IMAD.SHL.U32 R6, R6, 0x4, RZ ;
        |        /*09e0*/                   LOP3.LUT R3, R4, 0x7c, RZ, 0xc0, !PT ;
        |        /*09f0*/                   IADD R10, R8.reuse, R5.reuse ;
        |        /*0a00*/                   IMAD.SHL.U32 R8, R8, 0x4, RZ ;
        |        /*0a10*/                   LOP3.LUT R4, R6, 0x7c, RZ, 0xc0, !PT ;
        |        /*0a20*/                   LDL R3, [R3+UR5] ;
        |        /*0a30*/                   IADD R12, R10, R5.reuse ;
        |        /*0a40*/                   LOP3.LUT R6, R8, 0x7c, RZ, 0xc0, !PT ;
        |        /*0a50*/                   LDL R4, [R4+UR5] ;
        |        /*0a60*/                   IADD R14, R12, R5.reuse ;
        |        /*0a70*/                   IMAD.SHL.U32 R10, R10, 0x4, RZ ;
        |        /*0a80*/                   LDL R6, [R6+UR5] ;
        |        /*0a90*/                   IADD R8, R14, R5 ;
        |        /*0aa0*/                   IMAD.SHL.U32 R12, R12, 0x4, RZ ;
        |        /*0ab0*/                   LOP3.LUT R15, R10, 0x7c, RZ, 0xc0, !PT ;
        |        /*0ac0*/                   IMAD.SHL.U32 R14, R14, 0x4, RZ ;
        |        /*0ad0*/                   IADD R7, R8.reuse, R5 ;
        |        /*0ae0*/                   IMAD.SHL.U32 R9, R8, 0x4, RZ ;
        |        /*0af0*/                   LOP3.LUT R13, R12, 0x7c, RZ, 0xc0, !PT ;
        |        /*0b00*/                   IMAD.SHL.U32 R10, R7, 0x4, RZ ;
        |        /*0b10*/                   LOP3.LUT R14, R14, 0x7c, RZ, 0xc0, !PT ;
        |        /*0b20*/                   LDL R7, [R15+UR5] ;
        |        /*0b30*/                   LOP3.LUT R12, R9, 0x7c, RZ, 0xc0, !PT ;
        |        /*0b40*/                   LOP3.LUT R11, R10, 0x7c, RZ, 0xc0, !PT ;
        |        /*0b50*/                   LDL R8, [R13+UR5] ;
        |        /*0b60*/                   LDL R9, [R14+UR5] ;
        |        /*0b70*/                   LDL R10, [R12+UR5] ;
        |        /*0b80*/                   LDL R11, [R11+UR5] ;
        |        /*0b90*/                   ISETP.NE.U32.AND P0, PT, R16, RZ, PT ;
        |        /*0ba0*/                   IADD3 R3, PT, PT, R4, R3, R0 ;
        |        /*0bb0*/                   IADD3 R3, PT, PT, R7, R6, R3 ;
        |        /*0bc0*/                   IADD3 R3, PT, PT, R9, R8, R3 ;
        |        /*0bd0*/                   IADD3 R0, PT, PT, R11, R10, R3 ;
        |        /*0be0*/              @!P0 BRA `(.L_x_0) ;
        |        /*0bf0*/                   UIADD3 UR4, UPT, UPT, UR4, 0x8, URZ ;
        |.L_x_3:
        |        /*0c00*/                   ISETP.GE.U32.AND P0, PT, R16, 0x4, PT ;
        |        /*0c10*/                   LOP3.LUT R3, R5, 0x3, RZ, 0xc0, !PT ;
        |        /*0c20*/              @!P0 BRA `(.L_x_4) ;
        |        /*0c30*/                   IMAD R4, R5, UR4, R2 ;
        |        /*0c40*/                   IADD R6, R4.reuse, R5 ;
        |        /*0c50*/                   IMAD.SHL.U32 R4, R4, 0x4, RZ ;
        |        /*0c60*/                   IADD R8, R6.reuse, R5 ;
        |        /*0c70*/                   IMAD.SHL.U32 R6, R6, 0x4, RZ ;
        |        /*0c80*/                   LOP3.LUT R4, R4, 0x7c, RZ, 0xc0, !PT ;
        |        /*0c90*/                   IADD R7, R8, R5 ;
        |        /*0ca0*/                   LOP3.LUT R6, R6, 0x7c, RZ, 0xc0, !PT ;
        |        /*0cb0*/                   IMAD.SHL.U32 R8, R8, 0x4, RZ ;
        |        /*0cc0*/                   IMAD.SHL.U32 R9, R7, 0x4, RZ ;
        |        /*0cd0*/                   LOP3.LUT R8, R8, 0x7c, RZ, 0xc0, !PT ;
        |        /*0ce0*/                   LDL R7, [R4+UR5] ;
        |        /*0cf0*/                   LOP3.LUT R10, R9, 0x7c, RZ, 0xc0, !PT ;
        |        /*0d00*/                   LDL R6, [R6+UR5] ;
        |        /*0d10*/                   LDL R9, [R8+UR5] ;
        |        /*0d20*/                   LDL R10, [R10+UR5] ;
        |        /*0d30*/                   ISETP.NE.U32.AND P0, PT, R3, RZ, PT ;
        |        /*0d40*/                   IADD3 R0, PT, PT, R6, R7, R0 ;
        |        /*0d50*/                   IADD3 R0, PT, PT, R10, R9, R0 ;
        |        /*0d60*/              @!P0 BRA `(.L_x_0) ;
        |        /*0d70*/                   UIADD3 UR4, UPT, UPT, UR4, 0x4, URZ ;
        |.L_x_4:
        |        /*0d80*/                   IMAD R2, R5, UR4, R2 ;
        |        /*0d90*/                   IADD R3, -R3, RZ ;
        |.L_x_5:
        |        /*0da0*/                   IMAD.SHL.U32 R4, R2, 0x4, RZ ;
        |        /*0db0*/                   LOP3.LUT R4, R4, 0x7c, RZ, 0xc0, !PT ;
        |        /*0dc0*/                   LDL R7, [R4+UR5] ;
        |        /*0dd0*/                   IADD R3, R3, 0x1 ;
        |        /*0de0*/                   IADD R2, R2, R5 ;
        |        /*0df0*/                   ISETP.NE.U32.AND P0, PT, R3, RZ, PT ;
        |        /*0e00*/                   IADD R0, R7, R0 ;
        |        /*0e10*/               @P0 BRA `(.L_x_5) ;
        |.L_x_0:
        |        /*0e20*/                   S2R R4, SR_TID.X ;
        |        /*0e30*/                   IADD R3, R4.reuse, R5.reuse ;
        |        /*0e40*/                   IMAD R2, R4, R5, RZ ;
        |        /*0e50*/                   IMAD.SHL.U32 R2, R2, 0x4, RZ ;
        |        /*0e60*/                   IMAD.SHL.U32 R3, R3, 0x4, RZ ;
        |        /*0e70*/                   LOP3.LUT R7, R2, 0x7c, RZ, 0xc0, !PT ;
        |        /*0e80*/                   LOP3.LUT R5, R3, 0x7c, RZ, 0xc0, !PT ;
        |        /*0e90*/                   LDC.64 R2, c[0x0][0x380] ;
        |        /*0ea0*/                   STL [R7+UR5], R0 ;
        |        /*0eb0*/                   LDL R5, [R5+UR5] ;
        |        /*0ec0*/                   IMAD.WIDE.U32 R2, R4, 0x4, R2 ;
        |        /*0ed0*/                   STG.E desc[UR6][R2.64], R5 ;
        |        /*0ee0*/                   EXIT ;
        |.L_x_7:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "wideSm100" ->
      """//--------------------- .text.wide                --------------------------
        |	.section	.text.wide,"ax",@progbits
        |	.align	128
        |        .global         wide
        |        .type           wide,@function
        |        .size           wide,(.L_x_1 - wide)
        |        .other          wide,@"STO_CUDA_ENTRY STV_DEFAULT"
        |wide:
        |.text.wide:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x380] ;
        |        /*0030*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0040*/                   LDC.64 R12, c[0x0][0x388] ;
        |        /*0050*/                   SHF.L.U32 R5, R4, 0x1, RZ ;
        |        /*0060*/                   IMAD.WIDE.U32 R2, R5, 0x10, R2 ;
        |        /*0070*/                   LDG.E.ENL2.256 R0, R2, desc[UR4][R2.64], 0x13 ;
        |        /*0080*/                   SHF.L.U32 R15, R4.reuse, 0x3, RZ ;
        |        /*0090*/                   IMAD R8, R4.reuse, 0x5, RZ ;
        |        /*00a0*/                   IADD3 R6, PT, PT, R4.reuse, 0x2, RZ ;
        |        /*00b0*/                   IADD3 R7, PT, PT, R4, 0x3, RZ ;
        |        /*00c0*/                   IMAD.WIDE.U32 R14, R15, 0x4, R12.reuse ;
        |        /*00d0*/                   IADD3 R9, PT, PT, R8.reuse, 0x1, RZ ;
        |        /*00e0*/                   IADD3 R10, PT, PT, R8, 0x2, RZ ;
        |        /*00f0*/                   IMAD.WIDE.U32 R12, R4, 0x4, R12 ;
        |        /*0100*/                   IADD3 R11, PT, PT, R8, 0x3, RZ ;
        |        /*0110*/                   IMAD R5, R0, 0x5, RZ ;
        |        /*0120*/                   IMAD R0, R2, 0x3, R5 ;
        |        /*0130*/                   IADD3 R5, PT, PT, R4, 0x1, RZ ;
        |        /*0140*/                   IMAD R17, R3, 0x7, R0 ;
        |        /*0150*/                   STG.E desc[UR4][R12.64], R17 ;
        |        /*0160*/                   STG.E.ENL2.256 desc[UR4][R14.64+0x100], R4, R8 ;
        |        /*0170*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "keplerSharedAtomicsSm35" ->
      """//--------------------- .text.kepler_shared_atomics --------------------------
        |	.section	.text.kepler_shared_atomics,"ax",@progbits
        |	.sectionflags	@"SHF_BARRIERS=1"
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	64
        |        .global         kepler_shared_atomics
        |        .type           kepler_shared_atomics,@function
        |        .size           kepler_shared_atomics,(.L_x_13 - kepler_shared_atomics)
        |        .other          kepler_shared_atomics,@"STO_CUDA_ENTRY STV_DEFAULT"
        |kepler_shared_atomics:
        |.text.kepler_shared_atomics:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   S2R R0, SR_TID.X;
        |        /*0018*/                   VOTE.ANY R3, PT, PT;
        |        /*0020*/                   SHF.L R2, RZ, 0x2, R0;
        |        /*0028*/                   ISETP.NE.AND P2, PT, R0, RZ, PT;
        |        /*0030*/                   ISETP.GT.U32.AND P1, PT, R0, 0x3, PT;
        |        /*0038*/                   ISETP.EQ.U32.AND P3, PT, R3, -0x1, PT;
        |        /*0048*/              @!P2 MOV32I R4, 0xffffff00;
        |        /*0050*/              @!P2 MOV32I R5, 0x0;
        |        /*0058*/              @!P1 STS [R2], RZ;
        |        /*0060*/              @!P2 STS.64 [0x10], R4;
        |        /*0068*/                   BAR.SYNC 0x0;
        |        /*0070*/               @P3 BRA `(.L_x_0);
        |        /*0078*/                   PSETP.AND.AND P0, PT, !PT, PT, PT;
        |        /*0088*/                   SSY `(.L_x_0);
        |.L_x_1:
        |        /*0090*/                   LDSLK P4, R3, [RZ];
        |        /*0098*/               @P4 IADD R3, R0, R3;
        |        /*00a0*/               @P4 STSCUL P0, [RZ], R3;
        |        /*00a8*/              @!P0 BRA `(.L_x_1);
        |        /*00b0*/                   NOP.S;
        |.L_x_0:
        |        /*00b8*/              @!P3 BRA `(.L_x_2);
        |        /*00c8*/                   SHFL.BFLY PT, R3, R0, 0x1, 0x1f;
        |        /*00d0*/                   S2R R8, SR_LANEID;
        |        /*00d8*/                   SSY `(.L_x_2);
        |        /*00e0*/                   ISETP.EQ.U32.AND P0, PT, R8, RZ, PT;
        |        /*00e8*/                   IADD R3, R0, R3;
        |        /*00f0*/                   SHFL.BFLY PT, R4, R3, 0x2, 0x1f;
        |        /*00f8*/                   IADD R4, R3, R4;
        |        /*0108*/                   SHFL.BFLY PT, R5, R4, 0x4, 0x1f;
        |        /*0110*/                   IADD R5, R4, R5;
        |        /*0118*/                   SHFL.BFLY PT, R6, R5, 0x8, 0x1f;
        |        /*0120*/                   IADD R6, R5, R6;
        |        /*0128*/                   SHFL.BFLY PT, R7, R6, 0x10, 0x1f;
        |        /*0130*/                   IADD R7, R6, R7;
        |        /*0138*/              @!P0 NOP.S;
        |        /*0148*/                   PSETP.AND.AND P0, PT, !PT, PT, PT;
        |.L_x_3:
        |        /*0150*/                   LDSLK P3, R3, [RZ];
        |        /*0158*/               @P3 IADD R3, R7, R3;
        |        /*0160*/               @P3 STSCUL P0, [RZ], R3;
        |        /*0168*/              @!P0 BRA `(.L_x_3);
        |        /*0170*/                   NOP.S;
        |.L_x_2:
        |        /*0178*/                   VOTE.ANY R3, PT, PT;
        |        /*0188*/                   ISETP.EQ.U32.AND P3, PT, R3, -0x1, PT;
        |        /*0190*/                   LOP32I.XOR R3, R0, 0x15;
        |        /*0198*/               @P3 BRA `(.L_x_4);
        |        /*01a0*/                   PSETP.AND.AND P0, PT, !PT, PT, PT;
        |        /*01a8*/                   SSY `(.L_x_4);
        |.L_x_5:
        |        /*01b0*/                   LDSLK P4, R4, [0x4];
        |        /*01b8*/               @P4 IMNMX R4, R3, R4, !PT;
        |        /*01c8*/               @P4 STSCUL P0, [0x4], R4;
        |        /*01d0*/              @!P0 BRA `(.L_x_5);
        |        /*01d8*/                   NOP.S;
        |.L_x_4:
        |        /*01e0*/              @!P3 BRA `(.L_x_6);
        |        /*01e8*/                   SHFL.BFLY PT, R4, R3, 0x1, 0x1f;
        |        /*01f0*/                   S2R R9, SR_LANEID;
        |        /*01f8*/                   SSY `(.L_x_6);
        |        /*0208*/                   ISETP.EQ.U32.AND P0, PT, R9, RZ, PT;
        |        /*0210*/                   IMNMX R4, R3, R4, !PT;
        |        /*0218*/                   SHFL.BFLY PT, R5, R4, 0x2, 0x1f;
        |        /*0220*/                   IMNMX R5, R4, R5, !PT;
        |        /*0228*/                   SHFL.BFLY PT, R6, R5, 0x4, 0x1f;
        |        /*0230*/                   IMNMX R6, R5, R6, !PT;
        |        /*0238*/                   SHFL.BFLY PT, R7, R6, 0x8, 0x1f;
        |        /*0248*/                   IMNMX R7, R6, R7, !PT;
        |        /*0250*/                   SHFL.BFLY PT, R8, R7, 0x10, 0x1f;
        |        /*0258*/                   IMNMX R8, R7, R8, !PT;
        |        /*0260*/              @!P0 NOP.S;
        |        /*0268*/                   PSETP.AND.AND P0, PT, !PT, PT, PT;
        |.L_x_7:
        |        /*0270*/                   LDSLK P3, R3, [0x4];
        |        /*0278*/               @P3 IMNMX R3, R8, R3, !PT;
        |        /*0288*/               @P3 STSCUL P0, [0x4], R3;
        |        /*0290*/              @!P0 BRA `(.L_x_7);
        |        /*0298*/                   NOP.S;
        |.L_x_6:
        |        /*02a0*/                   PSETP.AND.AND P0, PT, !PT, PT, PT;
        |        /*02a8*/                   LOP32I.AND R3, R2, 0x4;
        |        /*02b0*/                   SSY `(.L_x_8);
        |.L_x_9:
        |        /*02b8*/                   LDSLK P3, R4, [R3+0x8];
        |        /*02c8*/               @P3 IADD32I R4, R4, 0x1;
        |        /*02d0*/               @P3 STSCUL P0, [R3+0x8], R4;
        |        /*02d8*/              @!P0 BRA `(.L_x_9);
        |        /*02e0*/                   PSETP.AND.AND.S P3, PT, !PT, PT, PT;
        |.L_x_8:
        |        /*02e8*/                   SSY `(.L_x_10);
        |.L_x_11:
        |        /*02f0*/                   LDSLK.64 P0, R4, [0x10];
        |        /*02f8*/               @P0 IADD R4.CC, R0, R4;
        |        /*0308*/               @P0 IADD.X R5, RZ, R5;
        |        /*0310*/               @P0 STSCUL.64 P3, [0x10], R4;
        |        /*0318*/              @!P3 BRA `(.L_x_11);
        |        /*0320*/                   NOP.S;
        |.L_x_10:
        |        /*0328*/                   BAR.SYNC 0x0;
        |        /*0330*/              @!P1 ISCADD R4.CC, R0, c[0x0][0x140], 0x2;
        |        /*0338*/                   MOV32I R3, 0x4;
        |        /*0348*/              @!P1 IMAD.U32.U32.HI.X R5, R0, R3, c[0x0][0x144];
        |        /*0350*/              @!P1 LDS R2, [R2];
        |        /*0358*/              @!P1 ST.E [R4], R2;
        |        /*0360*/               @P2 EXIT;
        |        /*0368*/                   LDS.64 R2, [0x10];
        |        /*0370*/                   MOV R4, c[0x0][0x140];
        |        /*0378*/                   MOV R5, c[0x0][0x144];
        |        /*0388*/                   ST.E [R4+0x10], R2;
        |        /*0390*/                   ST.E [R4+0x14], R3;
        |        /*0398*/                   EXIT;
        |.L_x_13:
        |""".stripMargin
  )
}
