#include <cstddef>
#include <cstdint>

#include <catch2/catch_test_macros.hpp>
#include <sycl/sycl.hpp>

#include "support.hpp"
#include "test_support.hpp"
#include <fmt/format.h>

namespace {
namespace experimental = sycl::ext::oneapi::experimental;

// sycl_ext_oneapi_clock lowers to OpReadClockKHR; each clock is monotonic within its scope
template <experimental::clock_scope Scope> void clock(sycl::aspect aspect, bool strict) {
  constexpr size_t size = 64;
  constexpr size_t local = 32;
  sycl::queue queue(sycl::gpu_selector_v);
  if (!queue.get_device().has(aspect)) SKIP("clock scope unavailable");
  unified::test_runtime::Cleanup cleanup;
  auto *times = sycl_usm::shared<uint64_t>(queue, cleanup, size * 2);
  queue
      .parallel_for(sycl::nd_range<1>(size, local),
                    [=](sycl::nd_item<1> item) {
                      const auto i = item.get_global_linear_id();
                      const auto before = experimental::clock<Scope>();
                      times[i * 2] = before;
                      times[(i * 2) + 1] = experimental::clock<Scope>();
                    })
      .wait_and_throw();
  for (size_t i = 0; i < size; ++i) {
    INFO(fmt::format("work-item {}: {} -> {}", i, times[i * 2], times[(i * 2) + 1]));
    REQUIRE((strict ? times[(i * 2) + 1] > times[i * 2] : times[(i * 2) + 1] >= times[i * 2]));
  }
}

} // namespace

TEST_CASE("edge-cases/runtime/sycl/clock/sub-group/n64", "[unified][runtime][compact]") {
  clock<experimental::clock_scope::sub_group>(sycl::aspect::ext_oneapi_clock_sub_group, true);
}

TEST_CASE("edge-cases/runtime/sycl/clock/device/n64", "[unified][runtime][compact]") {
  clock<experimental::clock_scope::device>(sycl::aspect::ext_oneapi_clock_device, false);
}
