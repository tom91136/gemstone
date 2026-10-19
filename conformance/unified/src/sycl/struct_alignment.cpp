#include "struct_alignment.hpp"

#include <cstddef>
#include <cstdint>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <sycl/sycl.hpp>

#include "support.hpp"
#include "test_support.hpp"

namespace {
using namespace unified::struct_alignment;

template <class T> void run() {
  const auto expected = fixture<T>();
  sycl::queue queue(sycl::gpu_selector_v);
  unified::test_runtime::Cleanup cleanup;
  auto *image = sycl_usm::device<uint8_t>(queue, cleanup, expected.input.size());
  auto *out = sycl_usm::device<uint32_t>(queue, cleanup, expected.words.size());
  REQUIRE(reinterpret_cast<uintptr_t>(image) % BaseAlignment == 0);
  queue.memcpy(image, expected.input.data(), expected.input.size()).wait_and_throw();
  queue.fill(out, 0xdeadbeefU, expected.words.size()).wait_and_throw();
  queue
      .parallel_for(sycl::range<1>(Count),
                    [=](sycl::id<1> id) {
                      const auto i = id[0];
                      T *records = reinterpret_cast<T *>(image + Fixture<T>::Offset);
                      const float x = records[i].x;
                      const float y = records[i].y;
                      const float z = records[i].z;
                      const int type = records[i].type;
                      uint32_t tag = 0;
                      if constexpr (Tagged<T>) tag = static_cast<uint8_t>(records[i].tag);
                      const float tx = sycl::fma(R00, x, sycl::fma(R01, y, sycl::fma(R02, z, T0)));
                      const float ty = sycl::fma(R10, x, sycl::fma(R11, y, sycl::fma(R12, z, T1)));
                      const float tz = sycl::fma(R20, x, sycl::fma(R21, y, sycl::fma(R22, z, T2)));
                      uint32_t *row = out + (i * Words);
                      row[0] = sycl::bit_cast<uint32_t>(tx);
                      row[1] = sycl::bit_cast<uint32_t>(ty);
                      row[2] = sycl::bit_cast<uint32_t>(tz);
                      row[3] = sycl::bit_cast<uint32_t>(x);
                      row[4] = sycl::bit_cast<uint32_t>(y);
                      row[5] = sycl::bit_cast<uint32_t>(z);
                      row[6] = static_cast<uint32_t>(type);
                      row[7] = tag;
                      if (i % 2 == 0) {
                        records[i].x = tx;
                        records[i].y = ty;
                        records[i].z = tz;
                        records[i].type = ~type;
                        if constexpr (Tagged<T>) records[i].tag = static_cast<char>(tag ^ TagMask);
                      }
                    })
      .wait_and_throw();
  std::vector<uint8_t> actualImage(expected.input.size());
  std::vector<uint32_t> actualWords(expected.words.size());
  queue.memcpy(actualImage.data(), image, actualImage.size()).wait_and_throw();
  queue.memcpy(actualWords.data(), out, actualWords.size() * sizeof(uint32_t)).wait_and_throw();
  verify(expected, actualImage, actualWords);
}

} // namespace

TEST_CASE("memory/sycl/struct-alignment/packed", "[unified][memory]") { run<Packed>(); }

TEST_CASE("memory/sycl/struct-alignment/packed-aligned-2", "[unified][memory]") { run<Packed2>(); }

TEST_CASE("memory/sycl/struct-alignment/packed-aligned-4", "[unified][memory]") { run<Packed4>(); }

TEST_CASE("memory/sycl/struct-alignment/packed-aligned-8", "[unified][memory]") { run<Packed8>(); }

TEST_CASE("memory/sycl/struct-alignment/natural", "[unified][memory]") { run<Natural>(); }

TEST_CASE("memory/sycl/struct-alignment/aligned-16", "[unified][memory]") { run<Aligned16>(); }

TEST_CASE("memory/sycl/struct-alignment/mixed-packed", "[unified][memory]") { run<MixedPacked>(); }

TEST_CASE("memory/sycl/struct-alignment/mixed-natural", "[unified][memory]") { run<MixedNatural>(); }
