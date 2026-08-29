#include "gemstone_execution_rpc.h"

#include <assert.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>

#include <chrono>
#include <thread>
#include <vector>

int main(void) {
  gemstone_execution_client *memory_client = gemstone_execution_client_create_from_environment();
  gemstone_execution_client *execution_client = gemstone_execution_client_create_from_environment();
  assert(memory_client != nullptr);
  assert(execution_client != nullptr);

  const uint64_t allocation = 7;
  assert(gemstone_execution_register_allocation(memory_client, allocation, 8, GEMSTONE_ALLOCATION_SHARED) == 0);
  if (getenv("GEMSTONE_EXECUTION_HOLD_ALLOCATION") != nullptr)
    std::this_thread::sleep_for(std::chrono::milliseconds(750));
  const uint8_t initial[] = {1, 2, 3, 4};
  assert(gemstone_execution_write_allocation(memory_client, allocation, 0, initial, sizeof(initial)) == 0);

  const uint8_t module[] = {3, 2, 35, 7};
  uint64_t program = 0;
  assert(gemstone_execution_load_program(execution_client, GEMSTONE_PROGRAM_SPIRV, module, sizeof(module), &program) ==
         0);

  const uint8_t scalar[] = {9, 8};
  const gemstone_kernel_argument arguments[] = {
      {GEMSTONE_ARGUMENT_ALLOCATION, nullptr, 0, allocation, 2, 0},
      {GEMSTONE_ARGUMENT_SCALAR, scalar, sizeof(scalar), 0, 0, 0},
      {GEMSTONE_ARGUMENT_LOCAL, nullptr, 0, 0, 0, 64},
  };
  uint64_t completion = 0;
  const gemstone_dimensions one = {1, 1, 1};
  assert(gemstone_execution_launch(execution_client, program, "write", one, one, one, 0, 0, arguments, 3, &completion) == 0);
  int completion_status = 1;
  while (completion_status == 1) {
    completion_status = gemstone_execution_query_completion(memory_client, completion);
    if (completion_status == 1) std::this_thread::sleep_for(std::chrono::microseconds(50));
  }
  assert(completion_status == 0);

  uint32_t dirty_count = 0;
  assert(gemstone_execution_completion_dirty_ranges(memory_client, completion, nullptr, 0, &dirty_count) == 0);
  assert(dirty_count == 1);
  gemstone_dirty_range dirty{};
  assert(gemstone_execution_completion_dirty_ranges(execution_client, completion, &dirty, 1, &dirty_count) == 0);
  assert(dirty_count == 1 && dirty.allocation_id == allocation && dirty.offset == 0 && dirty.size == 8);

  uint8_t actual[8]{};
  const uint8_t expected[] = {1, 2, 9, 8, 0, 0, 0, 0};
  assert(gemstone_execution_read_allocation(memory_client, allocation, 0, actual, sizeof(actual)) == 0);
  assert(memcmp(actual, expected, sizeof(actual)) == 0);

  constexpr uint64_t large_allocation = 8;
  constexpr size_t large_size = 16U * 1024U * 1024U + 37U;
  std::vector<uint8_t> large_expected(large_size);
  for (size_t index = 0; index < large_expected.size(); ++index)
    large_expected[index] = static_cast<uint8_t>((index * 131U + 17U) & 0xffU);
  assert(gemstone_execution_register_allocation(memory_client, large_allocation, large_size,
                                                 GEMSTONE_ALLOCATION_SHARED) == 0);
  assert(gemstone_execution_write_allocation(execution_client, large_allocation, 0, large_expected.data(),
                                              static_cast<uint32_t>(large_expected.size())) == 0);
  std::vector<uint8_t> large_actual(large_size);
  for (unsigned round = 0; round < 4; ++round) {
    assert(gemstone_execution_read_allocation(execution_client, large_allocation, 0, large_actual.data(),
                                               16U * 1024U * 1024U) == 0);
    assert(memcmp(large_actual.data(), large_expected.data(), 16U * 1024U * 1024U) == 0);
    assert(gemstone_execution_read_allocation(memory_client, large_allocation, 16U * 1024U * 1024U,
                                               large_actual.data() + 16U * 1024U * 1024U, 37) == 0);
    assert(memcmp(large_actual.data(), large_expected.data(), large_size) == 0);
  }

  assert(gemstone_execution_release_completion(execution_client, completion) == 0);
  assert(gemstone_execution_unload_program(memory_client, program) == 0);
  assert(gemstone_execution_release_allocation(execution_client, large_allocation) == 0);
  assert(gemstone_execution_release_allocation(memory_client, allocation) == 0);
  gemstone_execution_client_destroy(execution_client);
  gemstone_execution_client_destroy(memory_client);
  return 0;
}
