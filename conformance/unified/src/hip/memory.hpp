#pragma once
// Parse chrono/format before HIP defines its __noinline__ compatibility macro.
#include <chrono>

#include <hip/hip_runtime_api.h>

#include "memory_files.hpp"
#include "memory_support.hpp"

namespace memory_patterns {
struct Backend {
  std::string kind;
  hipModule_t module{};
  hipFunction_t function{};
  explicit Backend(std::string kind) : kind(std::move(kind)) {
    check(hipInit(0));
    auto code = image("kernel.hsaco");
    check(hipModuleLoadData(&module, code.data()));
    check(hipModuleGetFunction(&function, module, "probe"));
  }
  Backend(const Backend &) = delete;
  Backend &operator=(const Backend &) = delete;
  ~Backend() { (void)hipModuleUnload(module); }
  [[nodiscard]] void *allocate(size_t size) const {
    void *p{};
    if (kind == "registered") {
      p = ordinary(size);
      check(hipHostRegister(p, size, hipHostRegisterMapped));
    } else if (kind == "host") {
      check(hipHostMalloc(&p, size, hipHostMallocMapped | hipHostMallocCoherent));
    } else {
      check(hipMallocManaged(&p, size, hipMemAttachGlobal));
    }
    if (kind == "registered" || kind == "host") {
      void *alias{};
      check(hipHostGetDevicePointer(&alias, p, 0));
      if (alias != p) {
        if (kind == "registered") {
          check(hipHostUnregister(p));
          std::free(p);
        } else {
          check(hipHostFree(p));
        }
        throw Unsupported("host pointer graphs require identical host/device addresses");
      }
    }
    return p;
  }
  void release(void *p) const {
    if (kind == "registered") {
      check(hipHostUnregister(p));
      std::free(p);
    } else if (kind == "host") {
      check(hipHostFree(p));
    } else {
      check(hipFree(p));
    }
  }
  void enqueue(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count, hipStream_t stream) const {
    void *args[]{&input, &output, &indices, &mode};
    check(hipModuleLaunchKernel(function, count == 1 ? 1 : count / 32, 1, 1, count == 1 ? 1 : 32, 1, 1, 0, stream, args, nullptr));
  }
  void launch(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) const {
    enqueue(input, output, indices, mode, count, nullptr);
    check(hipDeviceSynchronize());
  }
  void ordered(uint32_t *input, uint32_t *middle, uint32_t *output, unsigned count, bool crossQueue) const {
    hipStream_t first{};
    hipStream_t second{};
    hipEvent_t produced{};
    hipEvent_t consumed{};
    check(hipStreamCreateWithFlags(&first, hipStreamNonBlocking));
    check(hipStreamCreateWithFlags(&second, hipStreamNonBlocking));
    check(hipEventCreateWithFlags(&produced, hipEventDisableTiming));
    check(hipEventCreateWithFlags(&consumed, hipEventDisableTiming));
    auto *consumer = crossQueue ? second : first;
    enqueue(input, middle, input, 0, count, first);
    check(hipEventRecord(produced, first));
    check(hipStreamWaitEvent(consumer, produced, 0));
    enqueue(middle, output, input, 0, count, consumer);
    check(hipEventRecord(consumed, consumer));
    check(hipEventSynchronize(consumed));
    check(hipEventDestroy(consumed));
    check(hipEventDestroy(produced));
    check(hipStreamDestroy(second));
    check(hipStreamDestroy(first));
  }
};

} // namespace memory_patterns
