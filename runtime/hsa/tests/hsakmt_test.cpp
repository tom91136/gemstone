// SPDX-License-Identifier: Apache-2.0
#include <algorithm>
#include <array>
#include <atomic>
#include <cassert>
#include <chrono>
#include <cstdint>
#include <cstring>
#include <mutex>
#include <thread>
#include <unordered_map>
#include <vector>

#include "code_object_fixture.hpp"
#include "gemstone/hsa/aql_execution.h"
#include "gemstone/hsa/hsakmt_runtime.h"
#include <hsakmt/hsakmt.h>

namespace {

struct MemoryWrite {
  uint64_t id;
  uint64_t offset;
  uint32_t size;
};

using MemoryRead = MemoryWrite;

struct MemoryTransport {
  std::mutex lock;
  std::unordered_map<uint64_t, std::vector<uint8_t>> allocations;
  std::unordered_map<uint64_t, uint64_t> addresses;
  bool reject_address = false;
  std::vector<MemoryWrite> writes;
  std::vector<MemoryRead> reads;
};

int register_memory(void *context, uint64_t id, uint64_t size) {
  auto &transport = *static_cast<MemoryTransport *>(context);
  std::lock_guard<std::mutex> guard(transport.lock);
  if (size > SIZE_MAX) return -1;
  return transport.allocations.emplace(id, std::vector<uint8_t>(size)).second ? 0 : -1;
}

int register_address(void *context, uint64_t id, uint64_t base) {
  auto &transport = *static_cast<MemoryTransport *>(context);
  std::lock_guard<std::mutex> guard(transport.lock);
  assert(transport.allocations.count(id) == 1);
  if (transport.reject_address) return -1;
  return transport.addresses.emplace(id, base).second ? 0 : -1;
}

int release_memory(void *context, uint64_t id) {
  auto &transport = *static_cast<MemoryTransport *>(context);
  std::lock_guard<std::mutex> guard(transport.lock);
  transport.addresses.erase(id);
  return transport.allocations.erase(id) == 1 ? 0 : -1;
}

int write_memory(void *context, uint64_t id, uint64_t offset, const void *bytes, uint32_t size) {
  auto &transport = *static_cast<MemoryTransport *>(context);
  std::lock_guard<std::mutex> guard(transport.lock);
  const auto found = transport.allocations.find(id);
  if (found == transport.allocations.end() || offset > found->second.size() || size > found->second.size() - offset) return -1;
  std::memcpy(found->second.data() + offset, bytes, size);
  transport.writes.push_back(MemoryWrite{id, offset, size});
  return 0;
}

int read_memory(void *context, uint64_t id, uint64_t offset, void *bytes, uint32_t size) {
  auto &transport = *static_cast<MemoryTransport *>(context);
  std::lock_guard<std::mutex> guard(transport.lock);
  const auto found = transport.allocations.find(id);
  if (found == transport.allocations.end() || offset > found->second.size() || size > found->second.size() - offset) return -1;
  std::memcpy(bytes, found->second.data() + offset, size);
  transport.reads.push_back(MemoryRead{id, offset, size});
  return 0;
}

struct ExecutorContext {
  MemoryTransport *memory = nullptr;
  bool reject_load = true;
  bool reject_launch = true;
  uint32_t loads = 0;
  uint32_t unloads = 0;
  bool completion_awaited = false;
  bool completion_released = false;
  std::atomic<bool> block_completion{false};
  std::atomic<bool> completion_waiting{false};
  std::atomic<bool> allow_completion{false};
  gemstone_hsa_dirty_range dirty_range{};
  std::vector<uint8_t> loaded_image;
  bool matrix = false;
  uint32_t workers = 1;
  uint32_t expected_groups = 1;
  uint32_t expected_width = 1;
  uint32_t expected_global = 1;
};

int load_program(void *context, const void *image, uint32_t image_size, uint64_t *program) {
  auto &executor = *static_cast<ExecutorContext *>(context);
  if (executor.reject_load) return -1;
  executor.loaded_image.assign(static_cast<const uint8_t *>(image), static_cast<const uint8_t *>(image) + image_size);
  *program = ++executor.loads;
  return 0;
}
int unload_program(void *context, uint64_t) {
  ++static_cast<ExecutorContext *>(context)->unloads;
  return 0;
}
int launch(void *context, uint64_t, const char *, gemstone_hsa_dimensions group_size, gemstone_hsa_dimensions group_count,
           gemstone_hsa_dimensions global_size, uint32_t, uint32_t, const gemstone_hsa_argument *arguments, uint32_t argument_count,
           uint64_t *completion) {
  auto &executor = *static_cast<ExecutorContext *>(context);
  if (executor.reject_launch || executor.memory == nullptr || arguments == nullptr || argument_count != 3 || completion == nullptr)
    return -1;
  assert(arguments[0].kind == GEMSTONE_HSA_ARGUMENT_ALLOCATION);
  assert(arguments[1].kind == GEMSTONE_HSA_ARGUMENT_ALLOCATION);
  assert(arguments[2].kind == GEMSTONE_HSA_ARGUMENT_SCALAR);
  assert(arguments[2].scalar_size == 4);
  std::lock_guard<std::mutex> guard(executor.memory->lock);
  auto &input = executor.memory->allocations.at(arguments[0].allocation_id);
  auto &output = executor.memory->allocations.at(arguments[1].allocation_id);
  for (auto &[id, allocation] : executor.memory->allocations)
    if (id != arguments[0].allocation_id && id != arguments[1].allocation_id) ++allocation[17];
  int32_t increment = 0;
  std::memcpy(&increment, arguments[2].scalar_bytes, sizeof(increment));
  const uint32_t count = executor.matrix ? global_size.x : 1;
  if (executor.matrix) {
    assert(group_size.x == executor.expected_width && group_size.y == 1 && group_size.z == 1);
    assert(group_count.x == executor.expected_groups && group_count.y == 1 && group_count.z == 1);
    assert(global_size.x == executor.expected_global && global_size.y == 1 && global_size.z == 1);
  }
  std::atomic<uint32_t> next{0};
  const auto execute_groups = [&] {
    for (uint32_t group = next.fetch_add(1); group < group_count.x; group = next.fetch_add(1)) {
      const uint32_t begin = group * group_size.x;
      const uint32_t end = std::min(count, begin + group_size.x);
      for (uint32_t index = begin; index < end; ++index) {
        int32_t value = 0;
        std::memcpy(&value, input.data() + arguments[0].allocation_offset + index * 4, 4);
        value += increment + (executor.matrix ? static_cast<int32_t>(group * 17) : 0);
        std::memcpy(output.data() + arguments[1].allocation_offset + index * 4, &value, 4);
      }
    }
  };
  std::vector<std::thread> workers;
  for (uint32_t worker = 1; worker < executor.workers; ++worker)
    workers.emplace_back(execute_groups);
  execute_groups();
  for (auto &worker : workers)
    worker.join();
  executor.dirty_range = {arguments[1].allocation_id, arguments[1].allocation_offset, count * 4};
  executor.completion_awaited = false;
  executor.completion_released = false;
  *completion = 1;
  return 0;
}
int await_completion(void *context, uint64_t) {
  auto &executor = *static_cast<ExecutorContext *>(context);
  if (executor.reject_launch) return -1;
  if (executor.block_completion.load(std::memory_order_acquire)) {
    executor.completion_waiting.store(true, std::memory_order_release);
    const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(10);
    while (!executor.allow_completion.load(std::memory_order_acquire)) {
      assert(std::chrono::steady_clock::now() < deadline);
      std::this_thread::yield();
    }
  }
  executor.completion_awaited = true;
  return 0;
}
int completion_dirty_ranges(void *context, uint64_t, gemstone_hsa_dirty_range *ranges, uint32_t capacity, uint32_t *count) {
  auto &executor = *static_cast<ExecutorContext *>(context);
  assert(executor.completion_awaited && !executor.completion_released && count != nullptr);
  *count = 1;
  if (capacity == 0) return 0;
  assert(ranges != nullptr && capacity >= 1);
  ranges[0] = executor.dirty_range;
  return 0;
}
int release_completion(void *context, uint64_t) {
  static_cast<ExecutorContext *>(context)->completion_released = true;
  return 0;
}
const char *executor_error(void *) { return "execution is deliberately absent"; }

struct alignas(8) SignalStorage {
  uint64_t kind = 0;
  int64_t value = 1;
  uint64_t event_mailbox = 0;
  uint32_t event_id = 0;
  uint32_t reserved = 0;
};
static_assert(offsetof(SignalStorage, value) == 8);
static_assert(offsetof(SignalStorage, event_id) == 24);

using Packet = std::array<uint8_t, 64>;

template <typename T> void write(Packet &packet, size_t offset, T value) { std::memcpy(packet.data() + offset, &value, sizeof(value)); }

uint32_t read_u32(const std::vector<uint8_t> &bytes, size_t offset) {
  assert(offset <= bytes.size() && bytes.size() - offset >= 4);
  return uint32_t{bytes[offset]} | (uint32_t{bytes[offset + 1]} << 8) | (uint32_t{bytes[offset + 2]} << 16)
         | (uint32_t{bytes[offset + 3]} << 24);
}

Packet kernel_packet(uint64_t kernel_object, uint64_t kernarg, SignalStorage &completion) {
  Packet packet{};
  write<uint16_t>(packet, 0, 2);
  write<uint16_t>(packet, 2, 1);
  write<uint16_t>(packet, 4, 1);
  write<uint16_t>(packet, 6, 1);
  write<uint16_t>(packet, 8, 1);
  write<uint32_t>(packet, 12, 1);
  write<uint32_t>(packet, 16, 1);
  write<uint32_t>(packet, 20, 1);
  write<uint64_t>(packet, 32, kernel_object);
  write<uint64_t>(packet, 40, kernarg);
  write<uint64_t>(packet, 56, reinterpret_cast<uint64_t>(&completion));
  return packet;
}

Packet barrier_packet(SignalStorage &completion) {
  Packet packet{};
  write<uint16_t>(packet, 0, GEMSTONE_HSA_AQL_BARRIER_AND);
  write<uint64_t>(packet, 56, reinterpret_cast<uint64_t>(&completion));
  return packet;
}

void bootstrap_memory_events_and_direct_queue() {
  MemoryTransport transport;
  ExecutorContext executor_context;
  executor_context.memory = &transport;
  const gemstone_hsa_executor executor{
      &executor_context,  load_program,   unload_program, launch,  await_completion, completion_dirty_ranges,
      release_completion, executor_error, nullptr,        nullptr, nullptr};
  const auto signals = gemstone_hsa_rocr_signal_operations();
  auto *aql = gemstone_hsa_aql_runtime_create(&executor, &signals);
  assert(aql != nullptr);
  const gemstone_hsakmt_memory_transport memory{&transport, register_memory, release_memory, write_memory, read_memory, register_address};
  assert(gemstone_hsakmt_install_backend(aql, &memory) == 0);

  assert(hsaKmtOpenKFD() == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtRuntimeEnable(nullptr, false) == HSAKMT_STATUS_NOT_SUPPORTED);
  HSAuint32 runtime_capabilities = UINT32_MAX;
  assert(hsaKmtGetRuntimeCapabilities(&runtime_capabilities) == HSAKMT_STATUS_SUCCESS);
  assert(runtime_capabilities == 0);
  const auto code_object = gemstone::hsa::test::code_object_fixture();
  const gemstone_hsakmt_loaded_kernel loaded{UINT64_C(0x123400), "add", 20};
  const std::array<gemstone_hsa_kernarg_field, 3> internal_fields{{
      {GEMSTONE_HSA_ARGUMENT_ALLOCATION, 0, 8},
      {GEMSTONE_HSA_ARGUMENT_ALLOCATION, 8, 8},
      {GEMSTONE_HSA_ARGUMENT_SCALAR, 16, 4},
  }};
  assert(gemstone_hsakmt_notify_internal_kernel(code_object.data(), code_object.size(), UINT64_C(0x123300), "gfx900", "internal_add", 20, 0,
                                                0, 0x80, 0x8, internal_fields.data(), internal_fields.size())
         == -1);
  assert(std::strcmp(gemstone_hsakmt_loader_error(), "execution is deliberately absent") == 0);
  assert(gemstone_hsakmt_notify_code_object(code_object.data(), code_object.size(), &loaded, 1) == -1);
  assert(std::strcmp(gemstone_hsakmt_loader_error(), "execution is deliberately absent") == 0);
  executor_context.reject_load = false;
  assert(gemstone_hsakmt_notify_internal_kernel(code_object.data(), code_object.size(), UINT64_C(0x123300), "gfx900", "internal_add", 20, 0,
                                                0, 0x80, 0x8, internal_fields.data(), internal_fields.size())
         == 0);
  assert(executor_context.loads == 1);
  const auto &raw = executor_context.loaded_image;
  const std::array<uint8_t, 8> raw_magic{{'G', 'E', 'M', 'A', 'M', 'D', '1', '\0'}};
  assert(raw.size() == 40 + 6 + 12 + code_object.size());
  assert(std::equal(raw_magic.begin(), raw_magic.end(), raw.begin()));
  assert(read_u32(raw, 8) == 6 && read_u32(raw, 12) == 12 && read_u32(raw, 16) == 20);
  assert(read_u32(raw, 20) == 0 && read_u32(raw, 24) == 0 && read_u32(raw, 28) == 0x80 && read_u32(raw, 32) == 0x8);
  assert(read_u32(raw, 36) == static_cast<uint32_t>(code_object.size()));
  assert(std::memcmp(raw.data() + 40, "gfx900internal_add", 18) == 0);
  assert(std::equal(code_object.begin(), code_object.end(), raw.begin() + 58));
  // Repeated internal registration is idempotent, as ROCr may initialize the
  // same helper while multiple blit paths share the virtual agent.
  assert(gemstone_hsakmt_notify_internal_kernel(code_object.data(), code_object.size(), UINT64_C(0x123300), "gfx900", "internal_add", 20, 0,
                                                0, 0x80, 0x8, internal_fields.data(), internal_fields.size())
         == 0);
  assert(executor_context.loads == 1);
  assert(gemstone_hsakmt_notify_code_object(code_object.data(), code_object.size(), &loaded, 1) == 0);
  assert(executor_context.loads == 2);
  executor_context.reject_launch = false;
  // ROCr's symbol iterator also reports kernels from earlier code objects.
  // Repeated notifications must not reload or lose an existing binding.
  assert(gemstone_hsakmt_notify_code_object(code_object.data(), code_object.size(), &loaded, 1) == 0);
  assert(executor_context.loads == 2);
  HsaVersionInfo version{};
  assert(hsaKmtGetVersion(&version) == HSAKMT_STATUS_SUCCESS);
  assert(version.KernelInterfaceMajorVersion == 1);

  HsaSystemProperties system{};
  assert(hsaKmtAcquireSystemProperties(&system) == HSAKMT_STATUS_SUCCESS);
  assert(system.NumNodes == 2);
  HsaNodeProperties cpu{};
  HsaNodeProperties gpu{};
  assert(hsaKmtGetNodeProperties(0, &cpu) == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtGetNodeProperties(1, &gpu) == HSAKMT_STATUS_SUCCESS);
  assert(cpu.NumCPUCores != 0 && cpu.NumFComputeCores == 0);
  assert(gpu.NumCPUCores == 0 && gpu.NumFComputeCores != 0);
  assert(gpu.Capability.ui32.DoorbellType == 2);

  HsaMemFlags flags{};
  flags.ui32.HostAccess = 1;
  constexpr uint64_t upload_chunk = 64U * 1024U;
  constexpr uint64_t primary_allocation_size = 2 * upload_chunk;
  void *allocation = nullptr;
  transport.reject_address = true;
  const auto allocation_count = transport.allocations.size();
  assert(hsaKmtAllocMemory(0, primary_allocation_size, flags, &allocation) != HSAKMT_STATUS_SUCCESS);
  assert(transport.allocations.size() == allocation_count);
  transport.reject_address = false;
  assert(hsaKmtAllocMemory(0, primary_allocation_size, flags, &allocation) == HSAKMT_STATUS_SUCCESS);
  assert(std::any_of(transport.addresses.begin(), transport.addresses.end(),
                     [&](const auto &entry) { return entry.second == reinterpret_cast<uint64_t>(allocation); }));
  assert(allocation != nullptr);
  auto *bytes = static_cast<uint8_t *>(allocation);
  bytes[17] = 0x5a;
  void *unrelated_allocation = nullptr;
  assert(hsaKmtAllocMemory(1, 4096, flags, &unrelated_allocation) == HSAKMT_STATUS_SUCCESS);
  assert(unrelated_allocation != nullptr);
  auto *unrelated_bytes = static_cast<uint8_t *>(unrelated_allocation);
  unrelated_bytes[17] = 0x33;
  assert(hsaKmtMapMemoryToGPU(unrelated_allocation, 4096, nullptr) == HSAKMT_STATUS_SUCCESS);
  uint64_t alternate = 1;
  assert(hsaKmtMapMemoryToGPU(allocation, primary_allocation_size, &alternate) == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtMapMemoryToGPU(bytes + 4096, 4096, nullptr) == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtUnmapMemoryToGPU(bytes + 4096) == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtUnmapMemoryToGPU(allocation) == HSAKMT_STATUS_SUCCESS);
  assert(alternate == 0);
  HsaPointerInfo pointer{};
  assert(hsaKmtQueryPointerInfo(bytes + 17, &pointer) == HSAKMT_STATUS_SUCCESS);
  assert(pointer.CPUAddress == allocation && pointer.SizeInBytes == primary_allocation_size);

  HsaEventDescriptor descriptor{};
  HsaEvent *event = nullptr;
  assert(hsaKmtCreateEvent(&descriptor, true, false, &event) == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtQueryEventState(event) == HSAKMT_STATUS_WAIT_TIMEOUT);
  assert(hsaKmtSetEvent(event) == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtWaitOnEvent(event, HSA_EVENTTIMEOUT_IMMEDIATE) == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtResetEvent(event) == HSAKMT_STATUS_SUCCESS);

  std::array<Packet, 8> ring{};
  for (auto &packet : ring)
    write<uint16_t>(packet, 0, 1);
  HsaQueueResource queue{};
  assert(hsaKmtCreateQueueExt(1, HSA_QUEUE_COMPUTE_AQL, 100, HSA_QUEUE_PRIORITY_NORMAL, 0, ring.data(), sizeof(ring), event, &queue)
         == HSAKMT_STATUS_SUCCESS);
  assert(queue.Queue_DoorBell_aql != nullptr && queue.Queue_write_ptr_aql != nullptr && queue.Queue_read_ptr_aql != nullptr);

  SignalStorage completion;
  completion.event_mailbox = event->EventData.HWData2;
  completion.event_id = event->EventId;
  constexpr size_t input_offset = 64;
  constexpr size_t output_offset = 128;
  const uint64_t input_pointer = reinterpret_cast<uint64_t>(bytes + input_offset);
  const uint64_t output_pointer = reinterpret_cast<uint64_t>(bytes + output_offset);
  const int32_t input_value = 37;
  const int32_t increment = 5;
  const int32_t poison = -1;
  std::memcpy(bytes, &input_pointer, sizeof(input_pointer));
  std::memcpy(bytes + 8, &output_pointer, sizeof(output_pointer));
  std::memcpy(bytes + 16, &increment, sizeof(increment));
  std::memcpy(bytes + input_offset, &input_value, sizeof(input_value));
  std::memcpy(bytes + output_offset, &poison, sizeof(poison));
  // Exercise the internal-helper binding before the ordinary public-loader
  // binding. This is the ordering used by ROCr's own BlitKernel queue.
  ring[0] = kernel_packet(UINT64_C(0x123300), reinterpret_cast<uint64_t>(bytes), completion);
  __atomic_store_n(queue.Queue_write_ptr_aql, UINT64_C(1), __ATOMIC_RELEASE);
  // This is deliberately the same ordinary writable-memory store used by the
  // normal ROCr AQL producer path. There is no producer call or DTIF hook.
  *queue.Queue_DoorBell_aql = 0;
  while (__atomic_load_n(queue.Queue_read_ptr_aql, __ATOMIC_ACQUIRE) != 1)
    std::this_thread::yield();
  assert(__atomic_load_n(&completion.value, __ATOMIC_ACQUIRE) == 0);
  // A physical AQL completion both updates the signal value and raises the
  // signal's KFD event. ROCr async handlers sleep in the event wait and must
  // therefore be woken by the virtual completion too.
  assert(hsaKmtWaitOnEvent(event, HSA_EVENTTIMEOUT_IMMEDIATE) == HSAKMT_STATUS_SUCCESS);
  int32_t output_value = 0;
  std::memcpy(&output_value, bytes + output_offset, sizeof(output_value));
  assert(output_value == input_value + increment);
  // The fake executor deliberately changes this unrelated remote allocation
  // but reports only its actual four-byte output write. Copy-back must leave
  // the unrelated native byte untouched and must not infer dirtiness from
  // launch arguments.
  assert(unrelated_bytes[17] == 0x33);
  {
    std::lock_guard<std::mutex> guard(transport.lock);
    assert(transport.allocations.size() == 2);
    const auto primary = std::find_if(transport.allocations.begin(), transport.allocations.end(),
                                      [=](const auto &entry) { return entry.second.size() == primary_allocation_size; });
    const auto unrelated =
        std::find_if(transport.allocations.begin(), transport.allocations.end(),
                     [allocation_size = uint64_t{4096}](const auto &entry) { return entry.second.size() == allocation_size; });
    assert(primary != transport.allocations.end() && unrelated != transport.allocations.end());
    int32_t remote_output = 0;
    std::memcpy(&remote_output, primary->second.data() + output_offset, sizeof(remote_output));
    assert(remote_output == input_value + increment);
    assert(unrelated->second[17] == 0x34);
    assert(transport.reads.size() == 1);
    assert(transport.reads[0].id == primary->first && transport.reads[0].offset == output_offset
           && transport.reads[0].size == sizeof(output_value));
  }

  SignalStorage repeated;
  size_t writes_after_first = 0;
  {
    std::lock_guard<std::mutex> guard(transport.lock);
    writes_after_first = transport.writes.size();
  }
  ring[1] = kernel_packet(UINT64_C(0x123400), reinterpret_cast<uint64_t>(bytes), repeated);
  __atomic_store_n(queue.Queue_write_ptr_aql, UINT64_C(2), __ATOMIC_RELEASE);
  *queue.Queue_DoorBell_aql = 1;
  while (__atomic_load_n(queue.Queue_read_ptr_aql, __ATOMIC_ACQUIRE) != 2)
    std::this_thread::yield();
  assert(__atomic_load_n(&repeated.value, __ATOMIC_ACQUIRE) == 0);
  assert(unrelated_bytes[17] == 0x33);
  {
    std::lock_guard<std::mutex> guard(transport.lock);
    // Completion copy-back refreshes the exact local snapshot. With no host
    // writes between dispatches, the second kernel requires no upload.
    assert(transport.writes.size() == writes_after_first);
    const auto unrelated =
        std::find_if(transport.allocations.begin(), transport.allocations.end(),
                     [allocation_size = uint64_t{4096}](const auto &entry) { return entry.second.size() == allocation_size; });
    assert(unrelated != transport.allocations.end());
    // A per-packet upload of mapped GPU memory would reset this to 0x34.
    assert(unrelated->second[17] == 0x35);
    assert(transport.reads.size() == 2 && transport.reads.back().offset == output_offset
           && transport.reads.back().size == sizeof(output_value));
  }

  const int32_t changed_input = 91;
  std::memcpy(bytes + input_offset, &changed_input, sizeof(changed_input));
  SignalStorage changed;
  ring[2] = kernel_packet(UINT64_C(0x123400), reinterpret_cast<uint64_t>(bytes), changed);
  __atomic_store_n(queue.Queue_write_ptr_aql, UINT64_C(3), __ATOMIC_RELEASE);
  *queue.Queue_DoorBell_aql = 2;
  while (__atomic_load_n(queue.Queue_read_ptr_aql, __ATOMIC_ACQUIRE) != 3)
    std::this_thread::yield();
  assert(__atomic_load_n(&changed.value, __ATOMIC_ACQUIRE) == 0);
  std::memcpy(&output_value, bytes + output_offset, sizeof(output_value));
  assert(output_value == changed_input + increment);
  {
    std::lock_guard<std::mutex> guard(transport.lock);
    // The host changed one byte range in the first 64 KiB chunk. That exact
    // chunk is uploaded before the next kernel, while the unchanged second
    // chunk is skipped.
    assert(transport.writes.size() == writes_after_first + 1);
    const auto &write = transport.writes.back();
    assert(write.offset == 0 && write.size == upload_chunk);
  }

  SignalStorage rejected;
  ring[3] = kernel_packet(UINT64_C(0xdeadbeef), 0, rejected);
  __atomic_store_n(queue.Queue_write_ptr_aql, UINT64_C(4), __ATOMIC_RELEASE);
  *queue.Queue_DoorBell_aql = 3;
  while (__atomic_load_n(queue.Queue_read_ptr_aql, __ATOMIC_ACQUIRE) != 4)
    std::this_thread::yield();
  assert(__atomic_load_n(&rejected.value, __ATOMIC_ACQUIRE) == -2);

  size_t writes_before_barrier = 0;
  {
    std::lock_guard<std::mutex> guard(transport.lock);
    writes_before_barrier = transport.writes.size();
  }
  SignalStorage barrier_completion;
  ring[4] = barrier_packet(barrier_completion);
  __atomic_store_n(queue.Queue_write_ptr_aql, UINT64_C(5), __ATOMIC_RELEASE);
  *queue.Queue_DoorBell_aql = 4;
  while (__atomic_load_n(queue.Queue_read_ptr_aql, __ATOMIC_ACQUIRE) != 5)
    std::this_thread::yield();
  assert(__atomic_load_n(&barrier_completion.value, __ATOMIC_ACQUIRE) == 0);
  {
    std::lock_guard<std::mutex> guard(transport.lock);
    // Synchronization-only packets cannot observe allocation contents. The
    // following kernel dispatch performs the required fine-grained refresh,
    // so a barrier must not upload every CPU-node allocation itself.
    assert(transport.writes.size() == writes_before_barrier);
  }

  // A packet owns every allocation that can be observed by the remote
  // execution service until completion and copy-back are finished. Freeing
  // one while the executor is blocked must fail without tearing down either
  // the native storage or its remote mirror.
  executor_context.block_completion.store(true, std::memory_order_release);
  executor_context.completion_waiting.store(false, std::memory_order_release);
  executor_context.allow_completion.store(false, std::memory_order_release);
  SignalStorage retained;
  ring[5] = kernel_packet(UINT64_C(0x123400), reinterpret_cast<uint64_t>(bytes), retained);
  __atomic_store_n(queue.Queue_write_ptr_aql, UINT64_C(6), __ATOMIC_RELEASE);
  *queue.Queue_DoorBell_aql = 5;
  while (!executor_context.completion_waiting.load(std::memory_order_acquire))
    std::this_thread::yield();
  assert(hsaKmtFreeMemory(allocation, primary_allocation_size) == HSAKMT_STATUS_UNAVAILABLE);
  executor_context.allow_completion.store(true, std::memory_order_release);
  while (__atomic_load_n(queue.Queue_read_ptr_aql, __ATOMIC_ACQUIRE) != 6)
    std::this_thread::yield();
  assert(__atomic_load_n(&retained.value, __ATOMIC_ACQUIRE) == 0);

  // Extend the existing real AQL/hsakmt copyback boundary across the P=4 group matrix.
  // Remote work is completed before the held await returns; the host must still see poison until release.
  executor_context.matrix = true;
  uint64_t packet_index = 6;
  const auto bounded_wait = [](const auto &ready) {
    const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(10);
    while (!ready()) {
      assert(std::chrono::steady_clock::now() < deadline);
      std::this_thread::yield();
    }
  };
  for (uint32_t workers : {1U, 4U})
    for (uint32_t groups : {1U, 2U, 3U, 5U, 9U})
      for (uint32_t width : {31U, 32U, 33U})
        for (uint32_t tail : {0U, 1U})
          for (int32_t epoch : {7, 29}) {
            const uint32_t count = groups * width - tail;
            executor_context.workers = workers;
            executor_context.expected_groups = groups;
            executor_context.expected_width = width;
            executor_context.expected_global = count;
            constexpr size_t matrix_input = 2048, matrix_output = 4096;
            const uint64_t input_address = reinterpret_cast<uint64_t>(bytes + matrix_input);
            const uint64_t output_address = reinterpret_cast<uint64_t>(bytes + matrix_output);
            std::memcpy(bytes, &input_address, 8);
            std::memcpy(bytes + 8, &output_address, 8);
            std::memcpy(bytes + 16, &epoch, 4);
            for (uint32_t index = 0; index <= count; ++index) {
              const int32_t input = static_cast<int32_t>(index * 3) - 11;
              const int32_t sentinel = -777;
              std::memcpy(bytes + matrix_input + index * 4, &input, 4);
              std::memcpy(bytes + matrix_output + index * 4, &sentinel, 4);
            }
            size_t reads_before = 0;
            {
              std::lock_guard<std::mutex> guard(transport.lock);
              reads_before = transport.reads.size();
            }
            executor_context.completion_waiting.store(false, std::memory_order_release);
            executor_context.allow_completion.store(false, std::memory_order_release);
            SignalStorage matrix_completion;
            auto packet = kernel_packet(UINT64_C(0x123400), reinterpret_cast<uint64_t>(bytes), matrix_completion);
            write<uint16_t>(packet, 4, static_cast<uint16_t>(width));
            write<uint32_t>(packet, 12, count);
            ring[packet_index % ring.size()] = packet;
            __atomic_store_n(queue.Queue_write_ptr_aql, packet_index + 1, __ATOMIC_RELEASE);
            __atomic_store_n(queue.Queue_DoorBell_aql, packet_index, __ATOMIC_RELEASE);
            bounded_wait([&] {
              return executor_context.completion_waiting.load(std::memory_order_acquire)
                     || __atomic_load_n(queue.Queue_read_ptr_aql, __ATOMIC_ACQUIRE) == packet_index + 1;
            });
            if (count < width) {
              // Pinned HSA 7.2.4 hsa.h requires grid_size_x >= workgroup_size_x. Keep a rejection control.
              assert(!executor_context.completion_waiting.load(std::memory_order_acquire));
              assert(__atomic_load_n(&matrix_completion.value, __ATOMIC_ACQUIRE) < 0);
              {
                std::lock_guard<std::mutex> guard(transport.lock);
                assert(transport.reads.size() == reads_before);
              }
              for (uint32_t index = 0; index <= count; ++index) {
                int32_t observed = 0;
                std::memcpy(&observed, bytes + matrix_output + index * 4, 4);
                assert(observed == -777);
              }
              ++packet_index;
              continue;
            }
            assert(executor_context.completion_waiting.load(std::memory_order_acquire));
            assert(__atomic_load_n(&matrix_completion.value, __ATOMIC_ACQUIRE) == 1);
            for (uint32_t index = 0; index <= count; ++index) {
              int32_t observed = 0;
              std::memcpy(&observed, bytes + matrix_output + index * 4, 4);
              assert(observed == -777);
            }
            {
              std::lock_guard<std::mutex> guard(transport.lock);
              assert(transport.reads.size() == reads_before);
            }
            executor_context.allow_completion.store(true, std::memory_order_release);
            bounded_wait([&] { return __atomic_load_n(queue.Queue_read_ptr_aql, __ATOMIC_ACQUIRE) == packet_index + 1; });
            assert(__atomic_load_n(&matrix_completion.value, __ATOMIC_ACQUIRE) == 0);
            for (uint32_t index = 0; index < count; ++index) {
              int32_t observed = 0;
              std::memcpy(&observed, bytes + matrix_output + index * 4, 4);
              assert(observed == static_cast<int32_t>(index * 3) - 11 + epoch + static_cast<int32_t>((index / width) * 17));
            }
            int32_t guard_value = 0;
            std::memcpy(&guard_value, bytes + matrix_output + count * 4, 4);
            assert(guard_value == -777);
            {
              std::lock_guard<std::mutex> guard(transport.lock);
              assert(transport.reads.size() == reads_before + 1);
              assert(transport.reads.back().offset == matrix_output && transport.reads.back().size == count * 4);
            }
            assert(unrelated_bytes[17] == 0x33);
            ++packet_index;
          }

  assert(hsaKmtDestroyQueue(queue.QueueId) == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtDestroyEvent(event) == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtUnmapMemoryToGPU(unrelated_allocation) == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtFreeMemory(unrelated_allocation, 4096) == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtFreeMemory(allocation, primary_allocation_size) == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtReleaseSystemProperties() == HSAKMT_STATUS_SUCCESS);
  assert(hsaKmtCloseKFD() == HSAKMT_STATUS_SUCCESS);
  assert(executor_context.unloads == 2);
  assert(gemstone_hsakmt_remove_backend() == 0);
  gemstone_hsa_aql_runtime_destroy(aql);
}

} // namespace

int main() {
  bootstrap_memory_events_and_direct_queue();
  return 0;
}
