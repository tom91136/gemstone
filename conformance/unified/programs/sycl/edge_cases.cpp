#include "../provider.hpp"

#include "unified_suite/adapters/onedpl_edge_cases.hpp"
#include "unified_suite/adapters/onemkl.hpp"
#include "unified_suite/edge_cases.hpp"
#include "unified_suite/runtime_checks.hpp"

namespace unified_suite::program {

std::vector<Case> edge_case_cases(const Configuration &configuration) {
  auto result = edge_cases::cases<adapters::OneDPLEdgeCases, adapters::OneMkl>(configuration);
  result.push_back(
      {"edge-cases/runtime/sycl/accessor-local-barrier/i32/n128", [] {
         return capture([] {
           constexpr std::size_t size = 128, local = 32;
           const auto values = runtime_checks::input(size);
           std::vector<int> output(size, 0);
           sycl::queue queue(sycl::gpu_selector_v);
           {
             sycl::buffer<int> source(values.data(), sycl::range<1>(size));
             sycl::buffer<int> destination(output.data(), sycl::range<1>(size));
             queue.submit([&](sycl::handler &handler) {
               auto input = source.get_access<sycl::access::mode::read>(handler);
               auto out = destination.get_access<sycl::access::mode::write>(handler);
               sycl::local_accessor<int> tile(sycl::range<1>(local), handler);
               handler.parallel_for(sycl::nd_range<1>(size, local), [=](sycl::nd_item<1> item) {
                 const auto i = item.get_global_linear_id(), lane = item.get_local_linear_id();
                 tile[lane] = input[i];
                 item.barrier(sycl::access::fence_space::local_space);
                 out[i] = tile[(lane + 1) % local] + static_cast<int>(item.get_group_linear_id());
               });
             });
             queue.wait_and_throw();
           } // Buffer destruction must publish the result into host storage.
           for (std::size_t i = 0; i < size; ++i)
             runtime_checks::require(output[i] == values[(i / local) * local + (i + 1) % local] +
                                                      static_cast<int>(i / local),
                                     "SYCL accessor/local barrier mismatch at " + std::to_string(i));
         });
       }});
  result.push_back(
      {"edge-cases/runtime/sycl/usm-event-offset/i32/n33", [] {
         return capture([] {
           sycl::queue queue(sycl::gpu_selector_v); // No in-order property: dependencies must be explicit.
           runtime_checks::Cleanup cleanup;
           auto *memory = sycl::malloc_device<int>(35, queue);
           runtime_checks::require(memory != nullptr, "device USM allocation failed");
           cleanup.add([&] { sycl::free(memory, queue); });
           std::vector<int> values(35, -12345);
           for (int i = 0; i < 33; ++i)
             values[i + 1] = i - 16;
           const auto uploaded = queue.memcpy(memory, values.data(), values.size() * sizeof(int));
           const auto executed = queue.submit([&](sycl::handler &handler) {
             handler.depends_on(uploaded);
             handler.parallel_for(sycl::range<1>(33), [=](sycl::id<1> i) { memory[i[0] + 1] *= 3; });
           });
           queue
               .submit([&](sycl::handler &handler) {
                 handler.depends_on(executed);
                 handler.memcpy(values.data(), memory, values.size() * sizeof(int));
               })
               .wait_and_throw();
           runtime_checks::require(values.front() == -12345 && values.back() == -12345,
                                   "USM boundary canary changed");
           for (int i = 0; i < 33; ++i)
             runtime_checks::require(values[i + 1] == 3 * (i - 16), "USM event/offset mismatch");
         });
       }});
  return result;
}

} // namespace unified_suite::program
