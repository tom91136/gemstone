#include <bit>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <sycl/sycl.hpp>

#include "support.hpp"
#include "test_support.hpp"
#include <fmt/format.h>

using namespace aspartame;

namespace {
namespace matrix = sycl::ext::oneapi::experimental::matrix;
using Bfloat16 = sycl::ext::oneapi::bfloat16;

// joint_matrix lowers to OpCooperativeMatrixLoadKHR, OpCooperativeMatrixMulAddKHR with Intel's bfloat16 component flags,
// element access through OpCooperativeMatrixLengthKHR and OpCooperativeMatrixStoreKHR; an 8-lane sub-group multiplies
// an 8x16 bfloat16 A by a 16x8 B into an 8x8 float accumulator, as every Intel matrix engine supports
void multiply() {
  constexpr size_t tileM = 8;
  constexpr size_t tileN = 8;
  constexpr size_t tileK = 16;
  sycl::queue queue(sycl::gpu_selector_v);
  const auto sizes = queue.get_device().get_info<sycl::info::device::sub_group_sizes>();
  if (!(sizes ^ contains(size_t{8}))) SKIP("8-lane sub-groups unavailable");
  // the runtime refuses joint_matrix kernels on devices whose architecture it does not know to have this combination
  std::vector<matrix::combination> combinations;
  try {
    combinations = queue.get_device().get_info<sycl::ext::oneapi::experimental::info::device::matrix_combinations>();
  } catch (const sycl::exception &) {
    combinations.clear();
  }
  if (combinations ^ none_match([](const matrix::combination &c) {
        return c.atype == matrix::matrix_type::bf16 && c.btype == matrix::matrix_type::bf16 && c.ctype == matrix::matrix_type::fp32
               && (c.msize == tileM || (c.msize == 0 && c.max_msize >= tileM))
               && (c.nsize == tileN || (c.nsize == 0 && c.max_nsize >= tileN))
               && (c.ksize == tileK || (c.ksize == 0 && c.max_ksize >= tileK));
      }))
    SKIP("the SYCL runtime reports no bfloat16 8x8x16 joint_matrix combination for the device");
  unified::test_runtime::Cleanup cleanup;
  auto *a = sycl_usm::shared<Bfloat16>(queue, cleanup, tileM * tileK);
  auto *b = sycl_usm::shared<Bfloat16>(queue, cleanup, tileK * tileN);
  auto *c = sycl_usm::shared<float>(queue, cleanup, tileM * tileN);
  std::vector<float> left(tileM * tileK);
  std::vector<float> right(tileK * tileN);
  for (size_t i = 0; i < tileM; ++i)
    for (size_t k = 0; k < tileK; ++k)
      a[(i * tileK) + k] = left[(i * tileK) + k] = static_cast<float>(static_cast<int>((i + (2 * k)) % 5) - 2);
  for (size_t k = 0; k < tileK; ++k)
    for (size_t j = 0; j < tileN; ++j)
      b[(k * tileN) + j] = right[(k * tileN) + j] = static_cast<float>(static_cast<int>(((3 * k) + j) % 7) - 3) / 2;
  queue
      .parallel_for(
          sycl::nd_range<1>(8, 8),
          [=](sycl::nd_item<1> item) [[sycl::reqd_sub_group_size(8)]] {
            auto group = item.get_sub_group();
            matrix::joint_matrix<sycl::sub_group, Bfloat16, matrix::use::a, tileM, tileK, matrix::layout::row_major> ma;
            matrix::joint_matrix<sycl::sub_group, Bfloat16, matrix::use::b, tileK, tileN, matrix::layout::row_major> mb;
            matrix::joint_matrix<sycl::sub_group, float, matrix::use::accumulator, tileM, tileN> mc;
            matrix::joint_matrix_fill(group, mc, 1.0F);
            matrix::joint_matrix_load(
                group, ma, sycl::address_space_cast<sycl::access::address_space::global_space, sycl::access::decorated::no>(a), tileK);
            matrix::joint_matrix_load(
                group, mb, sycl::address_space_cast<sycl::access::address_space::global_space, sycl::access::decorated::no>(b), tileN);
            matrix::joint_matrix_mad(group, mc, ma, mb, mc);
            matrix::joint_matrix_apply(group, mc, [](float &x) { x *= 2; });
            matrix::joint_matrix_store(group, mc,
                                       sycl::address_space_cast<sycl::access::address_space::global_space, sycl::access::decorated::no>(c),
                                       tileN, matrix::layout::row_major);
          })
      .wait_and_throw();
  for (size_t i = 0; i < tileM; ++i)
    for (size_t j = 0; j < tileN; ++j) {
      const float sum = iota<size_t>(0, tileK)
                        | fold_left(1.0F, [&](float acc, size_t k) { return acc + (left[(i * tileK) + k] * right[(k * tileN) + j]); });
      INFO(fmt::format("element {}, {}", i, j));
      REQUIRE(c[(i * tileN) + j] == 2 * sum);
    }
}

// sycl::ext::oneapi::bfloat16 converts through OpConvertFToBF16INTEL and OpConvertBF16ToFINTEL, rounding to nearest even
void bfloat16Conversions() {
  constexpr size_t size = 64;
  sycl::queue queue(sycl::gpu_selector_v);
  unified::test_runtime::Cleanup cleanup;
  auto *values = sycl_usm::shared<float>(queue, cleanup, size * 2);
  for (size_t i = 0; i < size; ++i)
    values[i] =
        std::ldexp(1.0F + (static_cast<float>((i * 2654435761U) % 1024) / 512.0F), static_cast<int>(i % 30) - 15) * (i % 3 ? 1.0F : -1.0F);
  queue.parallel_for(sycl::range<1>(size), [=](sycl::id<1> i) { values[size + i] = static_cast<float>(Bfloat16(values[i])); })
      .wait_and_throw();
  for (size_t i = 0; i < size; ++i) {
    const uint32_t bits = std::bit_cast<uint32_t>(values[i]);
    const uint32_t expected = (bits + 0x7fff + ((bits >> 16) & 1)) & 0xffff0000U;
    INFO(fmt::format("value {}: {}", i, values[i]));
    REQUIRE(std::bit_cast<uint32_t>(values[size + i]) == expected);
  }
}
} // namespace

TEST_CASE("edge-cases/runtime/sycl/joint-matrix/bf16/m8n8k16", "[unified][runtime][compact]") { multiply(); }

TEST_CASE("edge-cases/runtime/sycl/bfloat16/conversions/n64", "[unified][runtime][compact]") { bfloat16Conversions(); }
