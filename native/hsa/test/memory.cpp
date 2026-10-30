// Native memory transport and scheduling regression client.
#include <chrono>
#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <stdexcept>
#include <string>
#include <thread>
#include <utility>

#include <gemstone/test_support.hpp>

#if __has_include(<hsa/hsa.h>)
  #include <hsa/hsa.h>
  #include <hsa/hsa_ext_amd.h>
#else
  #include <hsa.h>
  #include <hsa_ext_amd.h>
#endif

#include "../../rpc/test/memory_patterns.hpp"

namespace memory_patterns { namespace {
struct MemoryClient : MemoryClientBase<MemoryClient> {
  hsa_agent_t cpu{}, gpu{};
  hsa_region_t region{};
  hsa_amd_memory_pool_t pool{};
  hsa_code_object_reader_t reader{};
  hsa_executable_t executable{};
  hsa_queue_t *queue{};
  hsa_signal_t done{};
  uint64_t kernel{};
  uint32_t argumentSize{}, groupSize{}, privateSize{};
  void *arguments{};
  explicit MemoryClient(std::string kind, uint64_t selected = 0) : MemoryClientBase(std::move(kind)) {
    check(hsa_init());
    check(hsa_iterate_agents(
        [](hsa_agent_t agent, void *data) {
          auto &self = *static_cast<MemoryClient *>(data);
          hsa_device_type_t type{};
          auto status = hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type);
          if (status != HSA_STATUS_SUCCESS) return status;
          if (type == HSA_DEVICE_TYPE_CPU) self.cpu = agent;
          else if (type == HSA_DEVICE_TYPE_GPU) self.gpu = agent;
          return HSA_STATUS_SUCCESS;
        },
        this));
    if (selected) gpu.handle = selected;
    require(cpu.handle && gpu.handle, "HSA agents");
    check(hsa_agent_iterate_regions(
        cpu,
        [](hsa_region_t r, void *data) {
          hsa_region_segment_t segment{};
          hsa_region_global_flag_t flags{};
          if (hsa_region_get_info(r, HSA_REGION_INFO_SEGMENT, &segment) == HSA_STATUS_SUCCESS && segment == HSA_REGION_SEGMENT_GLOBAL
              && hsa_region_get_info(r, HSA_REGION_INFO_GLOBAL_FLAGS, &flags) == HSA_STATUS_SUCCESS
              && (flags & HSA_REGION_GLOBAL_FLAG_KERNARG))
            *static_cast<hsa_region_t *>(data) = r;
          return HSA_STATUS_SUCCESS;
        },
        &region));
    require(region.handle, "HSA kernarg region");
    check(hsa_amd_agent_iterate_memory_pools(
        cpu,
        [](hsa_amd_memory_pool_t p, void *data) {
          hsa_amd_segment_t segment{};
          uint32_t flags{};
          if (hsa_amd_memory_pool_get_info(p, HSA_AMD_MEMORY_POOL_INFO_SEGMENT, &segment) == HSA_STATUS_SUCCESS
              && segment == HSA_AMD_SEGMENT_GLOBAL
              && hsa_amd_memory_pool_get_info(p, HSA_AMD_MEMORY_POOL_INFO_GLOBAL_FLAGS, &flags) == HSA_STATUS_SUCCESS
              && (flags & HSA_AMD_MEMORY_POOL_GLOBAL_FLAG_FINE_GRAINED))
            *static_cast<hsa_amd_memory_pool_t *>(data) = p;
          return HSA_STATUS_SUCCESS;
        },
        &pool));
    auto code = image(GEMSTONE_MEMORY_IMAGE_DIRECTORY, "kernel.hsaco");
    check(hsa_code_object_reader_create_from_memory(code.data(), code.size() - 1, &reader));
    check(hsa_executable_create_alt(HSA_PROFILE_FULL, HSA_DEFAULT_FLOAT_ROUNDING_MODE_DEFAULT, nullptr, &executable));
    check(hsa_executable_load_agent_code_object(executable, gpu, reader, nullptr, nullptr));
    check(hsa_executable_freeze(executable, nullptr));
    hsa_executable_symbol_t symbol{};
    check(hsa_executable_get_symbol_by_name(executable, "probe", &gpu, &symbol));
    check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &kernel));
    check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_KERNARG_SEGMENT_SIZE, &argumentSize));
    check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_GROUP_SEGMENT_SIZE, &groupSize));
    check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_PRIVATE_SEGMENT_SIZE, &privateSize));
    check(hsa_memory_allocate(region, argumentSize, &arguments));
    check(hsa_signal_create(1, 0, nullptr, &done));
    check(hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue));
  }
  ~MemoryClient() {
    hsa_queue_destroy(queue);
    hsa_signal_destroy(done);
    hsa_memory_free(arguments);
    hsa_executable_destroy(executable);
    hsa_code_object_reader_destroy(reader);
    hsa_shut_down();
  }
  hsa_queue_t *overlapQueues[2]{};
  hsa_signal_t overlapSignals[2]{};
  void *overlapArgs[2]{};
  void overlapSubmit(unsigned i, void *input, uint32_t *output, unsigned mode) {
    check(hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &overlapQueues[i]));
    check(hsa_signal_create(1, 0, nullptr, &overlapSignals[i]));
    check(hsa_memory_allocate(region, argumentSize, &overlapArgs[i]));
    enqueue(input, output, reinterpret_cast<uint32_t *>(input), mode, 1, overlapArgs[i], overlapQueues[i], overlapSignals[i]);
  }
  void overlapFinish() {
    for (unsigned i = 0; i < 2; ++i) {
      wait(overlapSignals[i]);
      check(hsa_queue_destroy(overlapQueues[i]));
      check(hsa_signal_destroy(overlapSignals[i]));
      check(hsa_memory_free(overlapArgs[i]));
    }
  }

  [[nodiscard]] void *allocateDevice(size_t size) const {
    hsa_amd_memory_pool_t devicePool{};
    check(hsa_amd_agent_iterate_memory_pools(
        gpu,
        [](hsa_amd_memory_pool_t p, void *data) {
          hsa_amd_segment_t segment{};
          if (hsa_amd_memory_pool_get_info(p, HSA_AMD_MEMORY_POOL_INFO_SEGMENT, &segment) == HSA_STATUS_SUCCESS
              && segment == HSA_AMD_SEGMENT_GLOBAL)
            *static_cast<hsa_amd_memory_pool_t *>(data) = p;
          return HSA_STATUS_SUCCESS;
        },
        &devicePool));
    require(devicePool.handle, "HSA device pool");
    void *p{};
    check(hsa_amd_memory_pool_allocate(devicePool, size, 0, &p));
    return p;
  }
  static void releaseDevice(void *p) { check(hsa_amd_memory_pool_free(p)); }
  static void deviceWrite(void *p, const void *bytes, size_t size) { check(hsa_memory_copy(p, bytes, size)); }
  static void deviceRead(void *bytes, const void *p, size_t size) { check(hsa_memory_copy(bytes, p, size)); }
  void *allocateBacking(size_t size) {
    void *p{};
    if (kind == "pool") {
      check(hsa_amd_memory_pool_allocate(pool, size, 0, &p));
    } else if (kind == "locked") {
      p = ordinary(size);
      void *alias{};
      check(hsa_amd_memory_lock(p, size, &gpu, 1, &alias));
      if (alias != p) {
        check(hsa_amd_memory_unlock(p));
        std::free(p);
        throw std::runtime_error("locked host pointer graphs require identical host/device addresses");
      }
    } else {
      check(hsa_memory_allocate(region, size, &p));
    }
    if (kind == "pool") check(hsa_amd_agents_allow_access(1, &gpu, nullptr, p));
    return p;
  }
  void releaseBacking(void *p) const {
    if (kind == "locked") {
      check(hsa_amd_memory_unlock(p));
      std::free(p);
    } else if (kind == "pool") {
      check(hsa_amd_memory_pool_free(p));
    } else {
      check(hsa_memory_free(p));
    }
  }
  void enqueue(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count, void *args, hsa_queue_t *target,
               hsa_signal_t completion) const {
    std::memset(args, 0, argumentSize);
    std::memcpy(args, &input, 8);
    std::memcpy(static_cast<char *>(args) + 8, &output, 8);
    std::memcpy(static_cast<char *>(args) + 16, &indices, 8);
    std::memcpy(static_cast<char *>(args) + 24, &mode, 4);
    hsa_signal_store_screlease(completion, 1);
    const auto index = hsa_queue_add_write_index_relaxed(target, 1);
    auto *p = &static_cast<hsa_kernel_dispatch_packet_t *>(target->base_address)[index & (target->size - 1)];
    std::memset(p, 0, sizeof(*p));
    p->setup = 1 << HSA_KERNEL_DISPATCH_PACKET_SETUP_DIMENSIONS;
    p->workgroup_size_x = count == 1 ? 1 : 32;
    p->workgroup_size_y = p->workgroup_size_z = 1;
    p->grid_size_x = count;
    p->grid_size_y = p->grid_size_z = 1;
    p->kernel_object = kernel;
    p->kernarg_address = args;
    p->completion_signal = completion;
    p->group_segment_size = groupSize;
    p->private_segment_size = privateSize;
    const uint16_t header = HSA_PACKET_TYPE_KERNEL_DISPATCH | (1 << HSA_PACKET_HEADER_BARRIER)
                            | (HSA_FENCE_SCOPE_SYSTEM << HSA_PACKET_HEADER_SCACQUIRE_FENCE_SCOPE)
                            | (HSA_FENCE_SCOPE_SYSTEM << HSA_PACKET_HEADER_SCRELEASE_FENCE_SCOPE);
    __atomic_store_n(&p->header, header, __ATOMIC_RELEASE);
    hsa_signal_store_screlease(target->doorbell_signal, static_cast<hsa_signal_value_t>(index));
  }
  static void wait(hsa_signal_t signal) {
    const auto deadline = std::chrono::steady_clock::now() + gemstone_test::scaled(std::chrono::seconds(60));
    while (hsa_signal_load_scacquire(signal) == 1 && std::chrono::steady_clock::now() < deadline)
      std::this_thread::yield();
    require(hsa_signal_load_scacquire(signal) == 0, "HSA dispatch failed or timed out");
  }
  void submit(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) const {
    enqueue(input, output, indices, mode, count, arguments, queue, done);
  }
  void finish() const { wait(done); }
  void launch(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) const {
    enqueue(input, output, indices, mode, count, arguments, queue, done);
    wait(done);
  }
  explicit MemoryClient(bool raw = false) : MemoryClient(memory_patterns::allocationKind("region")) { this->raw = raw; }
  [[nodiscard]] bool concurrentCapabilities() const {
    hsa_region_global_flag_t flags{};
    check(hsa_region_get_info(region, HSA_REGION_INFO_GLOBAL_FLAGS, &flags));
    require(flags & HSA_REGION_GLOBAL_FLAG_FINE_GRAINED, "HSA fine-grained region unavailable");
    return true;
  }
};
}} // namespace memory_patterns

int main(int argc, char **) { return memory_patterns::runMain<memory_patterns::MemoryClient>(argc); }
