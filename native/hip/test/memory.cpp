// Native memory transport and scheduling regression client.
// Parse chrono/format before HIP defines its __noinline__ compatibility macro.
#include <chrono>

#include <hip/hip_runtime_api.h>

#include "../../rpc/test/memory_patterns.hpp"

namespace memory_patterns {
struct MemoryClient {
  std::string kind;
  std::string allocationKind() const { return kind; }
  hipModule_t module{};
  hipFunction_t function{};
  explicit MemoryClient(std::string kind, uint64_t selected = 0) : kind(std::move(kind)) {
    check(hipInit(0));
    auto code = image(GEMSTONE_MEMORY_IMAGE_DIRECTORY, "kernel.hsaco");
    check(hipModuleLoadData(&module, code.data()));
    check(hipModuleGetFunction(&function, module, "probe"));
  }
  ~MemoryClient() { (void)hipModuleUnload(module); }
  hipStream_t overlapStreams[2]{};
  void overlapSubmit(unsigned i, void *input, uint32_t *output, unsigned mode) {
    check(hipStreamCreateWithFlags(&overlapStreams[i], hipStreamNonBlocking));
    enqueue(input, output, reinterpret_cast<uint32_t *>(input), mode, 1, overlapStreams[i]);
  }
  void overlapFinish() {
    for (auto stream : overlapStreams) {
      check(hipStreamSynchronize(stream));
      check(hipStreamDestroy(stream));
    }
  }

  void *allocateDevice(size_t size) {
    void *p{};
    check(hipMalloc(&p, size));
    return p;
  }
  void releaseDevice(void *p) { check(hipFree(p)); }
  void deviceWrite(void *p, const void *bytes, size_t size) { check(hipMemcpy(p, bytes, size, hipMemcpyHostToDevice)); }
  void deviceRead(void *bytes, const void *p, size_t size) { check(hipMemcpy(bytes, p, size, hipMemcpyDeviceToHost)); }
  void *allocateBacking(size_t size) {
    void *p{};
    const auto kind = allocationKind();
    if (kind == "registered") {
      p = ordinary(size);
      check(hipHostRegister(p, size, hipHostRegisterMapped));
    } else if (kind == "host") check(hipHostMalloc(&p, size, hipHostMallocMapped | hipHostMallocCoherent));
    else check(hipMallocManaged(&p, size, hipMemAttachGlobal));
    if (kind == "registered" || kind == "host") {
      void *alias{};
      check(hipHostGetDevicePointer(&alias, p, 0));
      if (alias != p) {
        if (kind == "registered") {
          check(hipHostUnregister(p));
          std::free(p);
        } else check(hipHostFree(p));
        throw std::runtime_error("host pointer graphs require identical host/device addresses");
      }
    }
    return p;
  }
  void releaseBacking(void *p) {
    if (allocationKind() == "registered") {
      check(hipHostUnregister(p));
      std::free(p);
    } else if (allocationKind() == "host") check(hipHostFree(p));
    else check(hipFree(p));
  }
  void enqueue(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count, hipStream_t stream) {
    void *args[]{&input, &output, &indices, &mode};
    check(hipModuleLaunchKernel(function, count == 1 ? 1 : count / 32, 1, 1, count == 1 ? 1 : 32, 1, 1, 0, stream, args, nullptr));
  }
  void launch(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) {
    enqueue(input, output, indices, mode, count, nullptr);
    check(hipDeviceSynchronize());
  }
  explicit MemoryClient(bool raw = false) : MemoryClient(memory_patterns::allocationKind(concurrencyMode() ? "host" : "managed")) {
    this->raw = raw;
  }
  bool raw = false;
  void *allocate(size_t size) { return raw ? ordinary(size) : allocateBacking(size); }
  void release(void *p) {
    if (raw) std::free(p);
    else releaseBacking(p);
  }
  hipEvent_t submittedEvent{};
  void *submittedOutput{};
  std::vector<uint8_t> copiedOutput;
  void submit(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) {
    enqueue(input, output, indices, mode, count, nullptr);
    check(hipEventCreate(&submittedEvent));
    check(hipEventRecord(submittedEvent, nullptr));
    require(hipEventQuery(submittedEvent) == hipErrorNotReady, "HIP pending event reported complete");
    require(hipStreamQuery(nullptr) == hipErrorNotReady, "HIP pending stream reported complete");
    submittedOutput = output;
    copiedOutput.resize(32 * (mode == 5 ? 4 : 8));
    check(hipMemcpyAsync(copiedOutput.data(), output, copiedOutput.size(), hipMemcpyDeviceToHost, nullptr));
    check(hipEventRecord(submittedEvent, nullptr));
  }
  void finish() {
    check(hipEventSynchronize(submittedEvent));
    check(hipEventQuery(submittedEvent));
    check(hipStreamQuery(nullptr));
    require(std::memcmp(copiedOutput.data(), submittedOutput, copiedOutput.size()) == 0, "HIP async copy preceded kernel writes");
    check(hipEventDestroy(submittedEvent));
  }
  bool concurrentCapabilities() {
    hipDeviceProp_t properties{};
    check(hipGetDeviceProperties(&properties, 0));
    require(properties.concurrentManagedAccess == 1, "HIP managed concurrency unavailable");
    require(properties.hostNativeAtomicSupported == 1, "HIP host native atomics unavailable");
    int concurrent{}, atomic{};
    check(hipDeviceGetAttribute(&concurrent, hipDeviceAttributeConcurrentManagedAccess, 0));
    check(hipDeviceGetAttribute(&atomic, hipDeviceAttributeHostNativeAtomicSupported, 0));
    require(concurrent == properties.concurrentManagedAccess && atomic == properties.hostNativeAtomicSupported,
            "HIP property/attribute memory capabilities disagree");
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
