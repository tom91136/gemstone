#include <cstddef>
#include <cstdint>

#include <catch2/catch_test_macros.hpp>
#include <sycl/sycl.hpp>

#include "support.hpp"

TEST_CASE("edge-cases/runtime/sycl/accessor-local-barrier/i32/n128", "[unified][runtime][compact]") {
  constexpr size_t size = 128, local = 32;
  const auto values = unified::runtime::input(size);
  std::vector<int32_t> output(size, 0);
  sycl::queue queue(sycl::gpu_selector_v);
  {
    sycl::buffer<int32_t> source(values.data(), sycl::range<1>(size));
    sycl::buffer<int32_t> destination(output.data(), sycl::range<1>(size));
    queue.submit([&](sycl::handler &handler) {
      auto input = source.get_access<sycl::access::mode::read>(handler);
      auto out = destination.get_access<sycl::access::mode::write>(handler);
      sycl::local_accessor<int32_t> tile(sycl::range<1>(local), handler);
      handler.parallel_for(sycl::nd_range<1>(size, local), [=](sycl::nd_item<1> item) {
        const auto i = item.get_global_linear_id(), lane = item.get_local_linear_id();
        tile[lane] = input[i];
        item.barrier(sycl::access::fence_space::local_space);
        out[i] = tile[(lane + 1) % local] + static_cast<int32_t>(item.get_group_linear_id());
      });
    });
    queue.wait_and_throw();
  } // Buffer destruction must publish the result into host storage.
  for (size_t i = 0; i < size; ++i) {
    INFO("SYCL accessor/local barrier mismatch at " + std::to_string(i));
    REQUIRE((output[i] == values[(i / local) * local + (i + 1) % local] + static_cast<int32_t>(i / local)));
  }
}

TEST_CASE("edge-cases/runtime/sycl/usm-event-offset/i32/n33", "[unified][runtime][compact]") {
  sycl::queue queue(sycl::gpu_selector_v); // No in-order property: dependencies must be explicit.
  unified::runtime::Cleanup cleanup;
  auto *memory = sycl::malloc_device<int32_t>(35, queue);
  {
    INFO("device USM allocation failed");
    REQUIRE((memory != nullptr));
  }
  cleanup.add([&] { sycl::free(memory, queue); });
  std::vector<int32_t> values(35, -12345);
  for (size_t i = 0; i < 33; ++i)
    values[i + 1] = static_cast<int32_t>(i) - 16;
  const auto uploaded = queue.memcpy(memory, values.data(), values.size() * sizeof(int32_t));
  const auto executed = queue.submit([&](sycl::handler &handler) {
    handler.depends_on(uploaded);
    handler.parallel_for(sycl::range<1>(33), [=](sycl::id<1> i) { memory[i[0] + 1] *= 3; });
  });
  queue
      .submit([&](sycl::handler &handler) {
        handler.depends_on(executed);
        handler.memcpy(values.data(), memory, values.size() * sizeof(int32_t));
      })
      .wait_and_throw();
  {
    INFO("USM boundary canary changed");
    REQUIRE((values.front() == -12345 && values.back() == -12345));
  }
  for (size_t i = 0; i < 33; ++i) {
    INFO("USM event/offset mismatch");
    REQUIRE((values[i + 1] == 3 * (static_cast<int32_t>(i) - 16)));
  }
}
