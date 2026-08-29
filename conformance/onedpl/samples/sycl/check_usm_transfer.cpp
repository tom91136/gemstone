#pragma region case: sycl-usm-transfer
#pragma region do: {dpcpp} -fsycl -std=c++20 -O2 -o {output} {input}
#pragma region do: {output}
#pragma region requires: pass
#include <sycl/sycl.hpp>

#include <array>
#include <cassert>
#include <cstdint>
#include <iostream>

int main() {
  sycl::queue queue(sycl::gpu_selector_v);
  constexpr std::size_t count = 257;
  std::array<std::uint32_t, count> source{};
  std::array<std::uint32_t, count> result{};
  for (std::size_t index = 0; index < count; ++index) source[index] = static_cast<std::uint32_t>(index * 17 + 3);

  auto *device = sycl::malloc_device<std::uint32_t>(count, queue);
  assert(device != nullptr);
  auto uploaded = queue.copy(source.data(), device, count);
  auto downloaded = queue.copy(device, result.data(), count, uploaded);
  downloaded.wait_and_throw();
  assert(source == result);

  const std::uint32_t fill_value = 0xa5a5a5a5U;
  auto filled = queue.memset(device, 0xa5, count * sizeof(std::uint32_t));
  queue.copy(device, result.data(), count, filled).wait_and_throw();
  for (const auto value : result) assert(value == fill_value);

  sycl::free(device, queue);
  std::cout << "pass\n";
  return 0;
}
