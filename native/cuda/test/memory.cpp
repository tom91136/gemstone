// Native memory transport and scheduling regression client.
#include <dlfcn.h>

#include "../../rpc/test/memory_patterns.hpp"

namespace memory_patterns {
struct MemoryClient {
  std::string kind;
  std::string allocationKind() const { return kind; }
  void *library, *context{}, *module{}, *function{};
  template <class F> F symbol(const char *name) {
    auto *p = dlsym(library, name);
    require(p != nullptr, name);
    return reinterpret_cast<F>(p);
  }
  explicit MemoryClient(std::string kind, uint64_t selected = 0, const char *fixture = "kernel.ptx")
      : kind(std::move(kind)), library(dlopen("libcuda.so.1", RTLD_NOW | RTLD_LOCAL)) {
    require(library != nullptr, "CUDA library");
    check(symbol<int (*)(unsigned)>("cuInit")(0));
    check(symbol<int (*)(void **, unsigned, int)>("cuCtxCreate_v2")(&context, 0, static_cast<int>(selected)));
    auto code = image(GEMSTONE_MEMORY_IMAGE_DIRECTORY, fixture);
    check(symbol<int (*)(void **, const void *)>("cuModuleLoadData")(&module, code.data()));
    check(symbol<int (*)(void **, void *, const char *)>("cuModuleGetFunction")(&function, module, "probe"));
  }
  ~MemoryClient() {
    symbol<int (*)(void *)>("cuModuleUnload")(module);
    symbol<int (*)(void *)>("cuCtxDestroy_v2")(context);
    dlclose(library);
  }
  void *overlapStreams[2]{};
  void overlapSubmit(unsigned i, void *input, uint32_t *output, unsigned mode) {
    check(symbol<int (*)(void **, unsigned)>("cuStreamCreate")(&overlapStreams[i], 1));
    enqueue(input, output, reinterpret_cast<uint32_t *>(input), mode, 1, overlapStreams[i]);
  }
  void overlapFinish() {
    for (auto stream : overlapStreams) {
      check(symbol<int (*)(void *)>("cuStreamSynchronize")(stream));
      check(symbol<int (*)(void *)>("cuStreamDestroy_v2")(stream));
    }
  }

  void *allocateDevice(size_t size) {
    uint64_t p{};
    check(symbol<int (*)(uint64_t *, size_t)>("cuMemAlloc_v2")(&p, size));
    return reinterpret_cast<void *>(p);
  }
  void releaseDevice(void *p) { check(symbol<int (*)(uint64_t)>("cuMemFree_v2")(reinterpret_cast<uintptr_t>(p))); }
  void deviceWrite(void *p, const void *bytes, size_t size) {
    check(symbol<int (*)(uint64_t, const void *, size_t)>("cuMemcpyHtoD_v2")(reinterpret_cast<uintptr_t>(p), bytes, size));
  }
  void deviceRead(void *bytes, const void *p, size_t size) {
    check(symbol<int (*)(void *, uint64_t, size_t)>("cuMemcpyDtoH_v2")(bytes, reinterpret_cast<uintptr_t>(p), size));
  }
  void *allocateBacking(size_t size) {
    const auto kind = allocationKind();
    if (kind == "registered") {
      auto *p = ordinary(size);
      check(symbol<int (*)(void *, size_t, unsigned)>("cuMemHostRegister_v2")(p, size, 2));
      uint64_t alias{};
      check(symbol<int (*)(uint64_t *, void *, unsigned)>("cuMemHostGetDevicePointer_v2")(&alias, p, 0));
      if (alias != reinterpret_cast<uintptr_t>(p)) {
        check(symbol<int (*)(void *)>("cuMemHostUnregister")(p));
        std::free(p);
        throw std::runtime_error("registered pointer graphs require identical host/device addresses");
      }
      return p;
    }
    uint64_t p{};
    if (kind == "pinned") check(symbol<int (*)(void **, size_t)>("cuMemAllocHost_v2")(reinterpret_cast<void **>(&p), size));
    else check(symbol<int (*)(uint64_t *, size_t, unsigned)>("cuMemAllocManaged")(&p, size, 1));
    return reinterpret_cast<void *>(p);
  }
  void releaseBacking(void *p) {
    const auto kind = allocationKind();
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
  explicit MemoryClient(bool raw = false)
      : MemoryClient(memory_patterns::allocationKind(concurrencyMode() && !memory_patterns::probeEnabled("atomic") ? "pinned" : "managed"),
                     0,
                     memory_patterns::probeEnabled("multilane") ? "contended.ptx"
                     : memory_patterns::probeEnabled("fence")   ? "fence.ptx"
                     : memory_patterns::probeEnabled("atomic")  ? "atomic.ptx"
                                                                : "kernel.ptx") {
    this->raw = raw;
  }
  bool raw = false;
  void *allocate(size_t size) { return raw ? ordinary(size) : allocateBacking(size); }
  void release(void *p) {
    if (raw) std::free(p);
    else releaseBacking(p);
  }
  void *submittedEvent{}, *submittedOutput{};
  std::vector<uint8_t> copiedOutput;
  void submit(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) {
    enqueue(input, output, indices, mode, count, nullptr);
    check(symbol<int (*)(void **, unsigned)>("cuEventCreate")(&submittedEvent, 2));
    check(symbol<int (*)(void *, void *)>("cuEventRecord")(submittedEvent, nullptr));
    require(symbol<int (*)(void *)>("cuEventQuery")(submittedEvent) == 600, "CUDA pending event reported complete");
    require(symbol<int (*)(void *)>("cuStreamQuery")(nullptr) == 600, "CUDA pending stream reported complete");
    submittedOutput = output;
    copiedOutput.resize(32 * (mode == 5 ? 4 : 8));
    check(symbol<int (*)(void *, uint64_t, size_t, void *)>("cuMemcpyDtoHAsync_v2")(
        copiedOutput.data(), reinterpret_cast<uintptr_t>(output), copiedOutput.size(), nullptr));
    check(symbol<int (*)(void *, void *)>("cuEventRecord")(submittedEvent, nullptr));
  }
  void finish() {
    check(symbol<int (*)(void *)>("cuEventSynchronize")(submittedEvent));
    check(symbol<int (*)(void *)>("cuEventQuery")(submittedEvent));
    check(symbol<int (*)(void *)>("cuStreamQuery")(nullptr));
    require(std::memcmp(copiedOutput.data(), submittedOutput, copiedOutput.size()) == 0, "CUDA async copy preceded kernel writes");
    check(symbol<int (*)(void *)>("cuEventDestroy_v2")(submittedEvent));
  }
  bool concurrentCapabilities() {
    int value{};
    auto attribute = symbol<int (*)(int *, int, int)>("cuDeviceGetAttribute");
    check(attribute(&value, 89, 0));
    require(value == 1, "CUDA concurrent managed access unavailable");
    check(attribute(&value, 88, 0));
    require(value == 0, "CUDA pageable access overclaims live system atomics");
    check(attribute(&value, 86, 0));
    require(value == 1, "CUDA host native atomics unavailable");
    return true;
  }
};
} // namespace memory_patterns

int main(int argc, char **) {
  try {
    memory_patterns::MemoryClient backend(argc > 1);
    return run(backend);
  } catch (const std::exception &e) {
    std::cerr << e.what() << '\n';
    return 1;
  }
}
