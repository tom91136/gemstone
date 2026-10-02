package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object TensorFixtures {
  val conformanceUnifiedSrcCudaTensorCudaSource: String =
    """#include <stdexcept>
      |#include <string>
      |#include <vector>
      |
      |#include <mma.h>
      |
      |#include <catch2/catch_test_macros.hpp>
      |#include <cuda_fp16.h>
      |#include <cuda_runtime.h>
      |
      |#include "test_support.hpp"
      |
      |namespace cuda_tensor {
      |
      |void check_tensor(cudaError_t result) {
      |  if (result != cudaSuccess) throw std::runtime_error(std::string("CUDA tensor test: ") + cudaGetErrorString(result));
      |}
      |
      |__global__ void tensor_gemm(const half *a, const half *b, float *c) {
      |#if __CUDA_ARCH__ >= 700
      |  using namespace nvcuda;
      |  const unsigned tile = blockIdx.x * 2 + threadIdx.x / 32;
      |  wmma::fragment<wmma::matrix_a, 16, 16, 16, half, wmma::row_major> af;
      |  wmma::fragment<wmma::matrix_b, 16, 16, 16, half, wmma::col_major> bf;
      |  wmma::fragment<wmma::accumulator, 16, 16, 16, float> cf;
      |  wmma::load_matrix_sync(af, a + tile * 512, 32);
      |  wmma::load_matrix_sync(bf, b + tile * 512, 32);
      |  wmma::load_matrix_sync(cf, c + tile * 512, 32, wmma::mem_row_major);
      |  wmma::mma_sync(cf, af, bf, cf);
      |  for (int i = 0; i < cf.num_elements; ++i)
      |    cf.x[i] *= 2.0f;
      |  wmma::store_matrix_sync(c + tile * 512, cf, 32, wmma::mem_row_major);
      |#endif
      |}
      |
      |TEST_CASE("runtime/cuda/tensor-wmma-gemm", "[unified][runtime][tensor][compact]") {
      |  cudaDeviceProp properties{};
      |  check_tensor(cudaGetDeviceProperties(&properties, 0));
      |  if (properties.major < 7) SKIP("WMMA requires compute capability 7.0");
      |
      |  constexpr int tiles = 4;
      |  constexpr int stride = 32;
      |  constexpr int tile_size = 16 * stride;
      |  std::vector<half> a(tiles * tile_size, __float2half(0.0f));
      |  std::vector<half> b(tiles * tile_size, __float2half(0.0f));
      |  std::vector<float> c(tiles * tile_size, -1234.0f);
      |  for (int tile = 0; tile < tiles; ++tile)
      |    for (int row = 0; row < 16; ++row)
      |      for (int col = 0; col < 16; ++col) {
      |        a[tile * tile_size + row * stride + col] = __float2half(static_cast<float>((row + 3 * col + tile) % 7 - 3));
      |        b[tile * tile_size + col * stride + row] = __float2half(static_cast<float>((2 * row + col) % 5 - 2));
      |        c[tile * tile_size + row * stride + col] = static_cast<float>(row - col);
      |      }
      |  auto expected = c;
      |  for (int tile = 0; tile < tiles; ++tile)
      |    for (int row = 0; row < 16; ++row)
      |      for (int col = 0; col < 16; ++col) {
      |        auto &result = expected[tile * tile_size + row * stride + col];
      |        for (int k = 0; k < 16; ++k)
      |          result += __half2float(a[tile * tile_size + row * stride + k]) * __half2float(b[tile * tile_size + col * stride + k]);
      |        result *= 2.0f;
      |      }
      |
      |  unified::test_runtime::Cleanup cleanup;
      |  half *da = nullptr;
      |  half *db = nullptr;
      |  float *dc = nullptr;
      |  check_tensor(cudaMalloc(reinterpret_cast<void **>(&da), a.size() * sizeof(half)));
      |  cleanup.add([=] { cudaFree(da); });
      |  check_tensor(cudaMalloc(reinterpret_cast<void **>(&db), b.size() * sizeof(half)));
      |  cleanup.add([=] { cudaFree(db); });
      |  check_tensor(cudaMalloc(reinterpret_cast<void **>(&dc), c.size() * sizeof(float)));
      |  cleanup.add([=] { cudaFree(dc); });
      |  check_tensor(cudaMemcpy(da, a.data(), a.size() * sizeof(half), cudaMemcpyHostToDevice));
      |  check_tensor(cudaMemcpy(db, b.data(), b.size() * sizeof(half), cudaMemcpyHostToDevice));
      |  check_tensor(cudaMemcpy(dc, c.data(), c.size() * sizeof(float), cudaMemcpyHostToDevice));
      |  tensor_gemm<<<2, 64>>>(da, db, dc);
      |  check_tensor(cudaGetLastError());
      |  check_tensor(cudaDeviceSynchronize());
      |  check_tensor(cudaMemcpy(c.data(), dc, c.size() * sizeof(float), cudaMemcpyDeviceToHost));
      |  REQUIRE(c == expected);
      |}
      |
      |} // namespace cuda_tensor
      |""".stripMargin

  val tensorSm86: String =
    """	.target	sm_86
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text._ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf --------------------------
      |	.section	.text._ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=26"
      |	.align	128
      |        .global         _ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf
      |        .type           _ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf,@function
      |        .size           _ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf,(.L_x_1 - _ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf)
      |        .other          _ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf,@"STO_CUDA_ENTRY STV_DEFAULT"
      |_ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf:
      |.text._ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;
      |        /*0010*/                   S2R R0, SR_TID.X ;
      |        /*0020*/                   MOV R7, 0x2 ;
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0040*/                   MOV R13, 0x4 ;
      |        /*0050*/                   S2R R3, SR_CTAID.X ;
      |        /*0060*/                   S2R R5, SR_LANEID ;
      |        /*0070*/                   SHF.R.U32.HI R0, RZ, 0x5, R0 ;
      |        /*0080*/                   LEA R0, R3, R0, 0x1 ;
      |        /*0090*/                   MOV R3, RZ ;
      |        /*00a0*/                   LOP3.LUT R2, R5, 0x3, RZ, 0xc0, !PT ;
      |        /*00b0*/                   SHF.R.U32.HI R5, RZ, 0x2, R5 ;
      |        /*00c0*/                   SHF.L.U32 R0, R0, 0x9, RZ ;
      |        /*00d0*/                   IMAD.WIDE.U32 R4, R5, 0x10, R2 ;
      |        /*00e0*/                   IMAD.WIDE.U32 R2, R0, R7, c[0x0][0x160] ;
      |        /*00f0*/                   IMAD.WIDE.U32 R6, R0, R7, c[0x0][0x168] ;
      |        /*0100*/                   LEA R8, P0, R4, R2, 0x2 ;
      |        /*0110*/                   IMAD.WIDE.U32 R12, R0, R13, c[0x0][0x170] ;
      |        /*0120*/                   LEA R10, P1, R4.reuse, R6, 0x2 ;
      |        /*0130*/                   LEA.HI.X R9, R4.reuse, R3, R5.reuse, 0x2, P0 ;
      |        /*0140*/                   LEA R2, P2, R4.reuse, R12, 0x3 ;
      |        /*0150*/                   LEA.HI.X R11, R4.reuse, R7, R5.reuse, 0x2, P1 ;
      |        /*0160*/                   LDG.E R6, [R8.64+0x10] ;
      |        /*0170*/                   LEA.HI.X R3, R4, R13, R5, 0x3, P2 ;
      |        /*0180*/                   LDG.E R4, [R8.64] ;
      |        /*0190*/                   LDG.E R5, [R8.64+0x200] ;
      |        /*01a0*/                   LDG.E R7, [R8.64+0x210] ;
      |        /*01b0*/                   LDG.E R12, [R10.64] ;
      |        /*01c0*/                   LDG.E R13, [R10.64+0x10] ;
      |        /*01d0*/                   LDG.E.64 R16, [R2.64] ;
      |        /*01e0*/                   LDG.E.64 R18, [R2.64+0x400] ;
      |        /*01f0*/                   LDG.E R14, [R10.64+0x200] ;
      |        /*0200*/                   LDG.E R15, [R10.64+0x210] ;
      |        /*0210*/                   LDG.E.64 R20, [R2.64+0x20] ;
      |        /*0220*/                   LDG.E.64 R22, [R2.64+0x420] ;
      |        /*0230*/                   HMMA.16816.F32 R16, R4.reuse, R12, R16 ;
      |        /*0240*/                   HMMA.16816.F32 R20, R4, R14, R20 ;
      |        /*0250*/                   @!UPT UIADD3 URZ, URZ, URZ, URZ ;
      |        /*0260*/                   FADD R16, R16, R16 ;
      |        /*0270*/                   FADD R17, R17, R17 ;
      |        /*0280*/                   FADD R18, R18, R18 ;
      |        /*0290*/                   FADD R19, R19, R19 ;
      |        /*02a0*/                   STG.E.64 [R2.64], R16 ;
      |        /*02b0*/                   STG.E.64 [R2.64+0x400], R18 ;
      |        /*02c0*/                   FADD R20, R20, R20 ;
      |        /*02d0*/                   FADD R21, R21, R21 ;
      |        /*02e0*/                   FADD R22, R22, R22 ;
      |        /*02f0*/                   FADD R23, R23, R23 ;
      |        /*0300*/                   STG.E.64 [R2.64+0x20], R20 ;
      |        /*0310*/                   STG.E.64 [R2.64+0x420], R22 ;
      |        /*0320*/                   EXIT ;
      |.L_x_0:
      |        /*0330*/                   BRA `(.L_x_0);
      |        /*0340*/                   NOP;
      |        /*0350*/                   NOP;
      |        /*0360*/                   NOP;
      |        /*0370*/                   NOP;
      |        /*0380*/                   NOP;
      |        /*0390*/                   NOP;
      |        /*03a0*/                   NOP;
      |        /*03b0*/                   NOP;
      |        /*03c0*/                   NOP;
      |        /*03d0*/                   NOP;
      |        /*03e0*/                   NOP;
      |        /*03f0*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class TensorFixtures extends NvidiaFixtureSuite {
  import TensorFixtures.*

  reproduces("tensorSm86")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "tensor.cu" -> conformanceUnifiedSrcCudaTensorCudaSource
    ),
    script = Vec(
      "printf '#include <mma.h>\\n#include <cuda_fp16.h>\\nnamespace cuda_tensor {\\n' > tensor-kernel.cu",
      "sed -n '/^__global__ void tensor_gemm(/,/^}/p' tensor.cu >> tensor-kernel.cu",
      "printf '}\\n' >> tensor-kernel.cu",
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_86 tensor-kernel.cu -o tensor-sm86.cubin",
      "nvdisasm -c -ndf tensor-sm86.cubin > tensor-sm86.sass"
    ),
    output = "tensor-sm86.sass",
    expected = tensorSm86
  )

}
