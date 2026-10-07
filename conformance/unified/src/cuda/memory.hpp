#pragma once
#include <dlfcn.h>

#include "memory_files.hpp"
#include "memory_support.hpp"

namespace memory_patterns {
struct Backend {
  std::string kind;
  void *library, *context{}, *module{}, *function{};
  template <class F> F symbol(const char *name) {
    auto *p = dlsym(library, name);
    require(p != nullptr, name);
    return reinterpret_cast<F>(p);
  }
  Backend(std::string kind, uint64_t selected, const char *fixture)
      : kind(std::move(kind)), library(dlopen("libcuda.so.1", RTLD_NOW | RTLD_LOCAL)) {
    require(library != nullptr, "CUDA library");
    check(symbol<int (*)(unsigned)>("cuInit")(0));
    check(symbol<int (*)(void **, unsigned, int)>("cuCtxCreate_v2")(&context, 0, static_cast<int>(selected)));
    auto code = image(fixture);
    check(symbol<int (*)(void **, const void *)>("cuModuleLoadData")(&module, code.data()));
    check(symbol<int (*)(void **, void *, const char *)>("cuModuleGetFunction")(&function, module, "probe"));
  }
  ~Backend() {
    symbol<int (*)(void *)>("cuModuleUnload")(module);
    symbol<int (*)(void *)>("cuCtxDestroy_v2")(context);
    dlclose(library);
  }
  void *allocate(size_t size) {
    if (kind == "registered") {
      auto *p = ordinary(size);
      check(symbol<int (*)(void *, size_t, unsigned)>("cuMemHostRegister_v2")(p, size, 2));
      uint64_t alias{};
      check(symbol<int (*)(uint64_t *, void *, unsigned)>("cuMemHostGetDevicePointer_v2")(&alias, p, 0));
      if (alias != reinterpret_cast<uintptr_t>(p)) {
        check(symbol<int (*)(void *)>("cuMemHostUnregister")(p));
        std::free(p);
        throw Unsupported("registered pointer graphs require identical host/device addresses");
      }
      return p;
    }
    uint64_t p{};
    if (kind == "pinned") check(symbol<int (*)(void **, size_t)>("cuMemAllocHost_v2")(reinterpret_cast<void **>(&p), size));
    else check(symbol<int (*)(uint64_t *, size_t, unsigned)>("cuMemAllocManaged")(&p, size, 1));
    return reinterpret_cast<void *>(p);
  }
  void release(void *p) {
    if (kind == "registered") {
      check(symbol<int (*)(void *)>("cuMemHostUnregister")(p));
      std::free(p);
    } else if (kind == "pinned") check(symbol<int (*)(void *)>("cuMemFreeHost")(p));
    else check(symbol<int (*)(uint64_t)>("cuMemFree_v2")(reinterpret_cast<uintptr_t>(p)));
  }
  void enqueue(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count, void *stream) {
    void *args[]{&input, &output, &indices, &mode};
    check(symbol<int (*)(void *, unsigned, unsigned, unsigned, unsigned, unsigned, unsigned, unsigned, void *, void **, void **)>(
        "cuLaunchKernel")(function, count == 1 ? 1 : count / 32, 1, 1, count == 1 ? 1 : 32, 1, 1, 0, stream, args, nullptr));
  }
  void launch(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) {
    enqueue(input, output, indices, mode, count, nullptr);
    check(symbol<int (*)()>("cuCtxSynchronize")());
  }
  void ordered(uint32_t *input, uint32_t *middle, uint32_t *output, unsigned count, bool crossQueue) {
    void *first{}, *second{}, *produced{}, *consumed{};
    check(symbol<int (*)(void **, unsigned)>("cuStreamCreate")(&first, 1));
    check(symbol<int (*)(void **, unsigned)>("cuStreamCreate")(&second, 1));
    check(symbol<int (*)(void **, unsigned)>("cuEventCreate")(&produced, 2));
    check(symbol<int (*)(void **, unsigned)>("cuEventCreate")(&consumed, 2));
    auto *consumer = crossQueue ? second : first;
    enqueue(input, middle, input, 0, count, first);
    check(symbol<int (*)(void *, void *)>("cuEventRecord")(produced, first));
    check(symbol<int (*)(void *, void *, unsigned)>("cuStreamWaitEvent")(consumer, produced, 0));
    enqueue(middle, output, input, 0, count, consumer);
    check(symbol<int (*)(void *, void *)>("cuEventRecord")(consumed, consumer));
    check(symbol<int (*)(void *)>("cuEventSynchronize")(consumed));
    check(symbol<int (*)(void *)>("cuEventDestroy_v2")(consumed));
    check(symbol<int (*)(void *)>("cuEventDestroy_v2")(produced));
    check(symbol<int (*)(void *)>("cuStreamDestroy_v2")(second));
    check(symbol<int (*)(void *)>("cuStreamDestroy_v2")(first));
  }
};

} // namespace memory_patterns
