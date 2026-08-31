// SPDX-License-Identifier: Apache-2.0
#include "gemstone/hsa/aql_execution.h"

#include <array>
#include <atomic>
#include <cassert>
#include <chrono>
#include <condition_variable>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <mutex>
#include <string>
#include <thread>
#include <unordered_map>
#include <vector>

#include "gemstone/hsa/aql.h"
#include "gemstone/hsa/virtual_kfd.h"

namespace {

using Packet = std::array<uint8_t, 64>;

template <typename T> void write(Packet &packet, size_t offset, T value) { std::memcpy(packet.data() + offset, &value, sizeof(value)); }

struct FakeExecutor {
  std::mutex lock;
  std::condition_variable launched;
  uint32_t launches = 0;
  uint32_t loads = 0;
  uint32_t unloads = 0;
  uint64_t program = 17;
  std::string kernel;
  gemstone_hsa_dimensions group_size{};
  gemstone_hsa_dimensions group_count{};
  gemstone_hsa_dimensions global_size{};
  uint32_t private_segment_bytes = 0;
  uint32_t group_segment_bytes = 0;
  std::vector<uint8_t> scalar;
  uint64_t allocation = 0;
  uint64_t allocation_offset = 0;
  bool fail_launch = false;
  bool fail_await = false;
  bool completion_awaited = false;
  bool completion_released = false;
  uint32_t dirty_queries = 0;
  std::vector<gemstone_hsa_dirty_range> dirty_ranges{{99, 8, 4}};
  std::string error = "fake execution failure";
};

int load_program(void *context, const void *image, uint32_t size, uint64_t *program) {
  assert(context != nullptr && image != nullptr && size == 4 && program != nullptr);
  auto &executor = *static_cast<FakeExecutor *>(context);
  ++executor.loads;
  *program = executor.program;
  return 0;
}

int unload_program(void *context, uint64_t program) {
  auto &executor = *static_cast<FakeExecutor *>(context);
  assert(program == executor.program);
  ++executor.unloads;
  return 0;
}

int launch(void *context, uint64_t program, const char *kernel, gemstone_hsa_dimensions group_size, gemstone_hsa_dimensions group_count,
           gemstone_hsa_dimensions global_size, uint32_t private_segment_bytes, uint32_t group_segment_bytes,
           const gemstone_hsa_argument *arguments, uint32_t argument_count, uint64_t *completion) {
  auto &executor = *static_cast<FakeExecutor *>(context);
  if (executor.fail_launch) return -1;
  assert(program == executor.program && argument_count == 5);
  assert(arguments[0].kind == GEMSTONE_HSA_ARGUMENT_ALLOCATION);
  for (uint32_t index = 1; index < argument_count; ++index) {
    assert(arguments[index].kind == GEMSTONE_HSA_ARGUMENT_SCALAR);
    assert(arguments[index].scalar_size == 4);
  }
  {
    std::lock_guard<std::mutex> guard(executor.lock);
    executor.kernel = kernel;
    executor.group_size = group_size;
    executor.group_count = group_count;
    executor.global_size = global_size;
    executor.private_segment_bytes = private_segment_bytes;
    executor.group_segment_bytes = group_segment_bytes;
    executor.allocation = arguments[0].allocation_id;
    executor.allocation_offset = arguments[0].allocation_offset;
    executor.scalar.clear();
    for (uint32_t index = 1; index < argument_count; ++index)
      executor.scalar.insert(executor.scalar.end(), arguments[index].scalar_bytes,
                             arguments[index].scalar_bytes + arguments[index].scalar_size);
    ++executor.launches;
  }
  executor.launched.notify_all();
  executor.completion_awaited = false;
  executor.completion_released = false;
  *completion = 23;
  return 0;
}

int await_completion(void *context, uint64_t completion) {
  auto &executor = *static_cast<FakeExecutor *>(context);
  if (executor.fail_await) {
    executor.error = "typed completion failure";
    return -1;
  }
  executor.completion_awaited = true;
  return completion == 23 ? 0 : -1;
}

int completion_dirty_ranges(void *context, uint64_t completion, gemstone_hsa_dirty_range *ranges, uint32_t capacity, uint32_t *count) {
  auto &executor = *static_cast<FakeExecutor *>(context);
  assert(completion == 23 && executor.completion_awaited && !executor.completion_released);
  assert(count != nullptr && (capacity == 0 || ranges != nullptr));
  ++executor.dirty_queries;
  *count = static_cast<uint32_t>(executor.dirty_ranges.size());
  if (capacity == 0) return 0;
  if (capacity < executor.dirty_ranges.size()) return -1;
  std::copy(executor.dirty_ranges.begin(), executor.dirty_ranges.end(), ranges);
  return 0;
}

int release_completion(void *context, uint64_t completion) {
  auto &executor = *static_cast<FakeExecutor *>(context);
  executor.completion_released = true;
  executor.error = "release replaced the failure";
  return completion == 23 ? 0 : -1;
}

const char *executor_error(void *context) { return static_cast<FakeExecutor *>(context)->error.c_str(); }
void destroy_executor(void *) {}

gemstone_hsa_executor executor_operations(FakeExecutor &executor) {
  return gemstone_hsa_executor{&executor,          load_program,   unload_program,  launch, await_completion, completion_dirty_ranges,
                               release_completion, executor_error, destroy_executor};
}

struct FakeSignals {
  std::mutex lock;
  std::unordered_map<uint64_t, int64_t> values;
};

int load_signal(void *context, uint64_t signal, int64_t *value) {
  auto &signals = *static_cast<FakeSignals *>(context);
  std::lock_guard<std::mutex> guard(signals.lock);
  const auto found = signals.values.find(signal);
  if (found == signals.values.end()) return -1;
  *value = found->second;
  return 0;
}

int complete_signal(void *context, uint64_t signal) {
  auto &signals = *static_cast<FakeSignals *>(context);
  std::lock_guard<std::mutex> guard(signals.lock);
  const auto found = signals.values.find(signal);
  if (found == signals.values.end()) return -1;
  --found->second;
  return 0;
}

int fail_signal(void *context, uint64_t signal, int32_t error) {
  auto &signals = *static_cast<FakeSignals *>(context);
  std::lock_guard<std::mutex> guard(signals.lock);
  signals.values[signal] = error;
  return 0;
}

int64_t signal_value(FakeSignals &signals, uint64_t signal) {
  std::lock_guard<std::mutex> guard(signals.lock);
  return signals.values.at(signal);
}

void set_signal(FakeSignals &signals, uint64_t signal, int64_t value) {
  std::lock_guard<std::mutex> guard(signals.lock);
  signals.values[signal] = value;
}

Packet kernel_packet(uint64_t kernel_object, uint64_t kernarg, uint64_t completion) {
  Packet packet{};
  write<uint16_t>(packet, 0, GEMSTONE_HSA_AQL_KERNEL_DISPATCH);
  write<uint16_t>(packet, 2, 2);
  write<uint16_t>(packet, 4, 8);
  write<uint16_t>(packet, 6, 4);
  write<uint16_t>(packet, 8, 1);
  write<uint32_t>(packet, 12, 17);
  write<uint32_t>(packet, 16, 8);
  write<uint32_t>(packet, 20, 1);
  write<uint32_t>(packet, 24, 96);
  write<uint32_t>(packet, 28, 128);
  write<uint64_t>(packet, 32, kernel_object);
  write<uint64_t>(packet, 40, kernarg);
  write<uint64_t>(packet, 56, completion);
  return packet;
}

Packet barrier_packet(gemstone_hsa_aql_packet_type type, std::array<uint64_t, 5> dependencies, uint64_t completion) {
  Packet packet{};
  write<uint16_t>(packet, 0, static_cast<uint16_t>(type));
  for (size_t index = 0; index < dependencies.size(); ++index)
    write<uint64_t>(packet, 8 + index * 8, dependencies[index]);
  write<uint64_t>(packet, 56, completion);
  return packet;
}

Packet pm4_ib_packet(uint64_t completion) {
  Packet packet{};
  write<uint16_t>(packet, 0, GEMSTONE_HSA_AQL_VENDOR_SPECIFIC);
  write<uint16_t>(packet, 2, 1);
  write<uint32_t>(packet, 4, 0xc0023f00);
  write<uint32_t>(packet, 20, 0x0a);
  write<uint64_t>(packet, 56, completion);
  return packet;
}

void decoder_is_typed_and_rejects_bad_packets() {
  std::array<uint8_t, 16> kernarg{};
  auto packet = kernel_packet(9, reinterpret_cast<uint64_t>(kernarg.data()), 11);
  gemstone_hsa_aql_packet decoded{};
  assert(gemstone_hsa_decode_aql_packet(packet.data(), packet.size(), &decoded) == GEMSTONE_HSA_AQL_DECODE_SUCCESS);
  assert(decoded.type == GEMSTONE_HSA_AQL_KERNEL_DISPATCH);
  assert(decoded.value.kernel_dispatch.dimensions == 2);
  assert(decoded.value.kernel_dispatch.grid_size_x == 17);
  assert(decoded.value.kernel_dispatch.kernel_object == 9);

  assert(gemstone_hsa_decode_aql_packet(packet.data(), 63, &decoded) == GEMSTONE_HSA_AQL_DECODE_TRUNCATED);
  write<uint16_t>(packet, 10, 1);
  assert(gemstone_hsa_decode_aql_packet(packet.data(), packet.size(), &decoded) == GEMSTONE_HSA_AQL_DECODE_RESERVED_BITS);

  auto pm4 = pm4_ib_packet(12);
  assert(gemstone_hsa_decode_aql_packet(pm4.data(), pm4.size(), &decoded) == GEMSTONE_HSA_AQL_DECODE_SUCCESS);
  assert(decoded.type == GEMSTONE_HSA_AQL_VENDOR_SPECIFIC);
  assert(decoded.value.pm4_ib.indirect_buffer_commands[0] == 0xc0023f00);
  assert(decoded.value.pm4_ib.completion_signal == 12);
  write<uint16_t>(pm4, 2, 2);
  assert(gemstone_hsa_decode_aql_packet(pm4.data(), pm4.size(), &decoded) == GEMSTONE_HSA_AQL_DECODE_UNSUPPORTED_TYPE);
}

void pm4_ib_synchronization_completes() {
  FakeExecutor executor;
  FakeSignals signals;
  auto executor_ops = executor_operations(executor);
  gemstone_hsa_signal_operations signal_ops{&signals, load_signal, complete_signal, fail_signal};
  auto *runtime = gemstone_hsa_aql_runtime_create(&executor_ops, &signal_ops);
  assert(runtime != nullptr);
  set_signal(signals, 31, 1);
  auto packet = pm4_ib_packet(31);
  assert(gemstone_hsa_aql_runtime_consume(1, 0, packet.data(), packet.size(), runtime) == GEMSTONE_HSA_PACKET_ACCEPTED);
  assert(signal_value(signals, 31) == 0);
  assert(executor.launches == 0);
  gemstone_hsa_aql_runtime_destroy(runtime);
}

void malformed_dispatch_fails_its_completion_signal() {
  FakeExecutor executor;
  FakeSignals signals;
  auto executor_ops = executor_operations(executor);
  gemstone_hsa_signal_operations signal_ops{&signals, load_signal, complete_signal, fail_signal};
  auto *runtime = gemstone_hsa_aql_runtime_create(&executor_ops, &signal_ops);
  assert(runtime != nullptr);

  set_signal(signals, 32, 1);
  auto packet = kernel_packet(77, 0, 32);
  write<uint16_t>(packet, 2, 1); // Claims one dimension but retains a four-wide Y workgroup.
  assert(gemstone_hsa_aql_runtime_consume(1, 0, packet.data(), packet.size(), runtime) == GEMSTONE_HSA_PACKET_ACCEPTED);
  assert(signal_value(signals, 32) == -6);
  assert(executor.launches == 0);
  assert(std::string(gemstone_hsa_aql_runtime_error(runtime)) == "dispatch dimensions are invalid");
  gemstone_hsa_aql_runtime_destroy(runtime);
}

void rocr_signal_operations_use_the_stock_value_word() {
  struct RocrSignalStorage {
    uint64_t kind = 0;
    alignas(8) int64_t value = 2;
  } signal;
  static_assert(offsetof(RocrSignalStorage, value) == 8);
  const uint64_t handle = reinterpret_cast<uint64_t>(&signal);
  const auto operations = gemstone_hsa_rocr_signal_operations();
  int64_t loaded = 0;
  assert(operations.load_acquire(operations.context, handle, &loaded) == 0);
  assert(loaded == 2);
  assert(operations.complete_release(operations.context, handle) == 0);
  assert(__atomic_load_n(&signal.value, __ATOMIC_ACQUIRE) == 1);
  assert(operations.fail_release(operations.context, handle, -7) == 0);
  assert(__atomic_load_n(&signal.value, __ATOMIC_ACQUIRE) == -7);
}

void execution_queue_dispatches_and_completes() {
  FakeExecutor executor;
  FakeSignals signals;
  auto executor_ops = executor_operations(executor);
  gemstone_hsa_signal_operations signal_ops{&signals, load_signal, complete_signal, fail_signal};
  auto *runtime = gemstone_hsa_aql_runtime_create(&executor_ops, &signal_ops);
  assert(runtime != nullptr);

  std::array<uint8_t, 32> allocation{};
  std::array<uint8_t, 64> unrelated_allocation{};
  std::array<uint8_t, 24> kernarg{};
  // A one-past pointer is a legal C/C++ loop bound and must remain relocatable
  // even though it cannot be dereferenced.
  const uint64_t pointer = reinterpret_cast<uint64_t>(allocation.data() + allocation.size());
  const uint32_t scalar = 0x12345678;
  std::memcpy(kernarg.data(), &pointer, sizeof(pointer));
  std::memcpy(kernarg.data() + 8, &scalar, sizeof(scalar));
  for (size_t index = 12; index < kernarg.size(); ++index)
    kernarg[index] = static_cast<uint8_t>(index);
  // Model an opaque by-value C++ functor containing a pointer followed by a
  // scalar. The runtime must split and relocate the nested pointer without a
  // producer shim or generated C++ layout knowledge.
  // The four bytes between fields and the four trailing bytes are ABI-significant
  // padding. They must cross RPC verbatim even though metadata does not name them.
  const std::array<gemstone_hsa_kernarg_field, 2> fields{{
      {GEMSTONE_HSA_ARGUMENT_SCALAR, 0, 12},
      {GEMSTONE_HSA_ARGUMENT_SCALAR, 16, 4},
  }};
  const std::array<uint8_t, 4> image{{0x7f, 'E', 'L', 'F'}};
  assert(gemstone_hsa_aql_runtime_register_allocation(runtime, reinterpret_cast<uint64_t>(allocation.data()), allocation.size(), 99) == 0);
  assert(gemstone_hsa_aql_runtime_register_allocation(runtime, reinterpret_cast<uint64_t>(kernarg.data()), kernarg.size(), 100) == 0);
  assert(gemstone_hsa_aql_runtime_register_allocation(runtime, reinterpret_cast<uint64_t>(unrelated_allocation.data()),
                                                      unrelated_allocation.size(), 101)
         == 0);
  assert(gemstone_hsa_aql_runtime_install_kernel(runtime, 77, image.data(), image.size(), "add_one", kernarg.size(), fields.data(),
                                                 fields.size())
         == 0);

  std::array<Packet, 4> ring{};
  for (auto &slot : ring)
    write<uint16_t>(slot, 0, GEMSTONE_HSA_AQL_INVALID);
  auto *kfd = gemstone_hsa_virtual_kfd_create(4);
  gemstone_hsa_virtual_queue_resource resource{};
  auto *queue = gemstone_hsa_virtual_kfd_create_execution_queue(kfd, runtime, ring.data(), ring.size(), &resource);
  assert(queue != nullptr);

  set_signal(signals, 100, 1);
  ring[0] = kernel_packet(77, reinterpret_cast<uint64_t>(kernarg.data()), 100);
  uint32_t dispatch_allocation_count = 0;
  assert(gemstone_hsa_aql_runtime_dispatch_allocations(runtime, ring[0].data(), ring[0].size(), nullptr, 0, &dispatch_allocation_count)
         == 0);
  assert(dispatch_allocation_count == 2);
  std::array<uint64_t, 2> dispatch_allocations{};
  assert(gemstone_hsa_aql_runtime_dispatch_allocations(runtime, ring[0].data(), ring[0].size(), dispatch_allocations.data(),
                                                       dispatch_allocations.size(), &dispatch_allocation_count)
         == 0);
  assert(dispatch_allocations[0] == 100 && dispatch_allocations[1] == 99);
  __atomic_store_n(resource.write_pointer, UINT64_C(1), __ATOMIC_RELEASE);
  __atomic_store_n(resource.doorbell, UINT64_C(0), __ATOMIC_RELEASE);
  {
    std::unique_lock<std::mutex> guard(executor.lock);
    assert(executor.launched.wait_for(guard, std::chrono::seconds(2), [&] { return executor.launches == 1; }));
  }
  while (__atomic_load_n(resource.read_pointer, __ATOMIC_ACQUIRE) != 1)
    std::this_thread::yield();
  assert(executor.kernel == "add_one");
  assert(executor.group_size.x == 8 && executor.group_size.y == 4);
  assert(executor.group_count.x == 3 && executor.group_count.y == 2);
  assert(executor.global_size.x == 17 && executor.global_size.y == 8 && executor.global_size.z == 1);
  assert(executor.private_segment_bytes == 96 && executor.group_segment_bytes == 128);
  assert(executor.allocation == 99 && executor.allocation_offset == allocation.size());
  assert(executor.scalar == std::vector<uint8_t>(kernarg.begin() + 8, kernarg.end()));
  assert(signal_value(signals, 100) == 0);
  assert(executor.dirty_queries == 2 && executor.completion_released);
  uint32_t dirty_count = 0;
  assert(gemstone_hsa_aql_runtime_take_dirty_ranges(runtime, nullptr, 0, &dirty_count) == 0);
  assert(dirty_count == 1);
  gemstone_hsa_dirty_range dirty_range{};
  assert(gemstone_hsa_aql_runtime_take_dirty_ranges(runtime, &dirty_range, 1, &dirty_count) == 0);
  assert(dirty_count == 1 && dirty_range.allocation_id == 99 && dirty_range.offset == 8 && dirty_range.size == 4);
  assert(gemstone_hsa_aql_runtime_take_dirty_ranges(runtime, nullptr, 0, &dirty_count) == 0);
  assert(dirty_count == 0);

  executor.dirty_ranges.clear();
  set_signal(signals, 103, 1);
  ring[1] = kernel_packet(77, reinterpret_cast<uint64_t>(kernarg.data()), 103);
  __atomic_store_n(resource.write_pointer, UINT64_C(2), __ATOMIC_RELEASE);
  __atomic_store_n(resource.doorbell, UINT64_C(1), __ATOMIC_RELEASE);
  while (__atomic_load_n(resource.read_pointer, __ATOMIC_ACQUIRE) != 2)
    std::this_thread::yield();
  assert(signal_value(signals, 103) == 0);
  assert(executor.dirty_queries == 3 && executor.completion_released);
  assert(gemstone_hsa_aql_runtime_take_dirty_ranges(runtime, nullptr, 0, &dirty_count) == 0 && dirty_count == 0);

  set_signal(signals, 101, 1);
  ring[2] = kernel_packet(999, reinterpret_cast<uint64_t>(kernarg.data()), 101);
  __atomic_store_n(resource.write_pointer, UINT64_C(3), __ATOMIC_RELEASE);
  __atomic_store_n(resource.doorbell, UINT64_C(2), __ATOMIC_RELEASE);
  while (__atomic_load_n(resource.read_pointer, __ATOMIC_ACQUIRE) != 3)
    std::this_thread::yield();
  assert(signal_value(signals, 101) == -2);

  executor.fail_await = true;
  set_signal(signals, 102, 1);
  ring[3] = kernel_packet(77, reinterpret_cast<uint64_t>(kernarg.data()), 102);
  __atomic_store_n(resource.write_pointer, UINT64_C(4), __ATOMIC_RELEASE);
  __atomic_store_n(resource.doorbell, UINT64_C(3), __ATOMIC_RELEASE);
  while (__atomic_load_n(resource.read_pointer, __ATOMIC_ACQUIRE) != 4)
    std::this_thread::yield();
  assert(signal_value(signals, 102) == -3);
  assert(executor.dirty_queries == 3 && executor.completion_released);
  assert(std::string(gemstone_hsa_aql_runtime_error(runtime)) == "add_one: typed completion failure");

  gemstone_hsa_virtual_kfd_destroy_queue(queue);
  gemstone_hsa_virtual_kfd_destroy(kfd);
  gemstone_hsa_aql_runtime_destroy(runtime);
  assert(executor.unloads == 1);
}

void one_code_object_binds_multiple_kernel_objects() {
  FakeExecutor executor;
  FakeSignals signals;
  auto executor_ops = executor_operations(executor);
  gemstone_hsa_signal_operations signal_ops{&signals, load_signal, complete_signal, fail_signal};
  auto *runtime = gemstone_hsa_aql_runtime_create(&executor_ops, &signal_ops);
  assert(runtime != nullptr);
  const std::array<uint8_t, 4> image{{0x7f, 'E', 'L', 'F'}};
  const std::array<gemstone_hsa_kernarg_field, 1> fields{{{GEMSTONE_HSA_ARGUMENT_SCALAR, 0, 4}}};
  const std::array<gemstone_hsa_kernel_binding, 2> bindings{{
      {41, "first", 4, fields.data(), fields.size()},
      {42, "second", 4, fields.data(), fields.size()},
  }};
  assert(gemstone_hsa_aql_runtime_install_code_object(runtime, image.data(), image.size(), bindings.data(), bindings.size()) == 0);
  assert(executor.loads == 1 && executor.unloads == 0);
  assert(gemstone_hsa_aql_runtime_remove_kernel(runtime, 41) == 0);
  assert(executor.unloads == 0);
  assert(gemstone_hsa_aql_runtime_remove_kernel(runtime, 42) == 0);
  assert(executor.unloads == 1);
  gemstone_hsa_aql_runtime_destroy(runtime);
  assert(executor.unloads == 1);
}

void barriers_wait_and_complete() {
  FakeExecutor executor;
  FakeSignals signals;
  auto executor_ops = executor_operations(executor);
  gemstone_hsa_signal_operations signal_ops{&signals, load_signal, complete_signal, fail_signal};
  auto *runtime = gemstone_hsa_aql_runtime_create(&executor_ops, &signal_ops);
  assert(runtime != nullptr);
  std::array<Packet, 2> ring{};
  for (auto &slot : ring)
    write<uint16_t>(slot, 0, GEMSTONE_HSA_AQL_INVALID);
  auto *kfd = gemstone_hsa_virtual_kfd_create(2);
  gemstone_hsa_virtual_queue_resource resource{};
  auto *queue = gemstone_hsa_virtual_kfd_create_execution_queue(kfd, runtime, ring.data(), ring.size(), &resource);

  set_signal(signals, 1, 0);
  set_signal(signals, 2, 1);
  set_signal(signals, 3, 1);
  ring[0] = barrier_packet(GEMSTONE_HSA_AQL_BARRIER_AND, {1, 2, 0, 0, 0}, 3);
  __atomic_store_n(resource.write_pointer, UINT64_C(1), __ATOMIC_RELEASE);
  __atomic_store_n(resource.doorbell, UINT64_C(0), __ATOMIC_RELEASE);
  std::this_thread::sleep_for(std::chrono::milliseconds(5));
  assert(__atomic_load_n(resource.read_pointer, __ATOMIC_ACQUIRE) == 0);
  set_signal(signals, 2, 0);
  while (__atomic_load_n(resource.read_pointer, __ATOMIC_ACQUIRE) != 1)
    std::this_thread::yield();
  assert(signal_value(signals, 3) == 0);

  set_signal(signals, 4, 1);
  set_signal(signals, 5, 0);
  set_signal(signals, 6, 1);
  ring[1] = barrier_packet(GEMSTONE_HSA_AQL_BARRIER_OR, {4, 5, 0, 0, 0}, 6);
  __atomic_store_n(resource.write_pointer, UINT64_C(2), __ATOMIC_RELEASE);
  __atomic_store_n(resource.doorbell, UINT64_C(1), __ATOMIC_RELEASE);
  while (__atomic_load_n(resource.read_pointer, __ATOMIC_ACQUIRE) != 2)
    std::this_thread::yield();
  assert(signal_value(signals, 6) == 0);

  gemstone_hsa_virtual_kfd_destroy_queue(queue);
  gemstone_hsa_virtual_kfd_destroy(kfd);
  gemstone_hsa_aql_runtime_destroy(runtime);
}

void a_waiting_barrier_does_not_block_another_queue() {
  FakeExecutor executor;
  FakeSignals signals;
  auto executor_ops = executor_operations(executor);
  gemstone_hsa_signal_operations signal_ops{&signals, load_signal, complete_signal, fail_signal};
  auto *runtime = gemstone_hsa_aql_runtime_create(&executor_ops, &signal_ops);
  assert(runtime != nullptr);

  std::array<Packet, 2> waiting_ring{};
  std::array<Packet, 2> ready_ring{};
  for (auto &slot : waiting_ring)
    write<uint16_t>(slot, 0, GEMSTONE_HSA_AQL_INVALID);
  for (auto &slot : ready_ring)
    write<uint16_t>(slot, 0, GEMSTONE_HSA_AQL_INVALID);
  auto *kfd = gemstone_hsa_virtual_kfd_create(2);
  gemstone_hsa_virtual_queue_resource waiting_resource{};
  gemstone_hsa_virtual_queue_resource ready_resource{};
  auto *waiting_queue =
      gemstone_hsa_virtual_kfd_create_execution_queue(kfd, runtime, waiting_ring.data(), waiting_ring.size(), &waiting_resource);
  auto *ready_queue = gemstone_hsa_virtual_kfd_create_execution_queue(kfd, runtime, ready_ring.data(), ready_ring.size(), &ready_resource);
  assert(waiting_queue != nullptr && ready_queue != nullptr);

  set_signal(signals, 20, 1);
  set_signal(signals, 21, 1);
  set_signal(signals, 22, 1);
  waiting_ring[0] = barrier_packet(GEMSTONE_HSA_AQL_BARRIER_AND, {20, 0, 0, 0, 0}, 21);
  ready_ring[0] = barrier_packet(GEMSTONE_HSA_AQL_BARRIER_AND, {0, 0, 0, 0, 0}, 22);
  __atomic_store_n(waiting_resource.write_pointer, UINT64_C(1), __ATOMIC_RELEASE);
  __atomic_store_n(waiting_resource.doorbell, UINT64_C(0), __ATOMIC_RELEASE);
  __atomic_store_n(ready_resource.write_pointer, UINT64_C(1), __ATOMIC_RELEASE);
  __atomic_store_n(ready_resource.doorbell, UINT64_C(0), __ATOMIC_RELEASE);

  while (__atomic_load_n(ready_resource.read_pointer, __ATOMIC_ACQUIRE) != 1)
    std::this_thread::yield();
  assert(__atomic_load_n(waiting_resource.read_pointer, __ATOMIC_ACQUIRE) == 0);
  assert(signal_value(signals, 22) == 0);

  set_signal(signals, 20, 0);
  while (__atomic_load_n(waiting_resource.read_pointer, __ATOMIC_ACQUIRE) != 1)
    std::this_thread::yield();
  assert(signal_value(signals, 21) == 0);

  gemstone_hsa_virtual_kfd_destroy_queue(ready_queue);
  gemstone_hsa_virtual_kfd_destroy_queue(waiting_queue);
  gemstone_hsa_virtual_kfd_destroy(kfd);
  gemstone_hsa_aql_runtime_destroy(runtime);
}

} // namespace

int main() {
  decoder_is_typed_and_rejects_bad_packets();
  pm4_ib_synchronization_completes();
  malformed_dispatch_fails_its_completion_signal();
  rocr_signal_operations_use_the_stock_value_word();
  execution_queue_dispatches_and_completes();
  one_code_object_binds_multiple_kernel_objects();
  barriers_wait_and_complete();
  a_waiting_barrier_does_not_block_another_queue();
  return 0;
}
