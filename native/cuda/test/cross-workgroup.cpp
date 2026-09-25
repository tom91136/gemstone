#include <algorithm>
#include <array>
#include <cstdio>
#include <cstdlib>
#include <future>
#include <memory>
#include <stdexcept>
#include <string>

#include <dlfcn.h>
#include <stddef.h>
#include <stdint.h>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

#include "../../rpc/test/queued_lifetime.hpp"

namespace {

// Standalone native Driver API client; MUnit supplies the emulator endpoint and client identity.
constexpr char Source[] = ".version 8.0\n"
                          ".target sm_80\n"
                          ".address_size 64\n"
                          ".global .align 4 .u32 metadata_probe[4];\n"
                          ".global .align 4 .b8 switch_table[4] = {42, 0, 0, 0};\n"
                          ".visible .entry read_switch(.param .u64 output) {\n"
                          "    .reg .u64 %rd<3>; .reg .u32 %r0;\n"
                          "    ld.param.u64 %rd0, [output];\n"
                          "    mov.b64 %rd1, switch_table;\n"
                          "    ld.global.nc.b32 %r0, [%rd1];\n"
                          "    st.global.u32 [%rd0], %r0;\n"
                          "    ret;\n"
                          "  }\n"
                          ".visible .entry compact(\n"
                          "    .param .u64 descriptors, .param .u64 output, .param .u32 count) {\n"
                          "    .reg .pred %p<4>;\n"
                          "    .reg .u32 %r<12>;\n"
                          "    .reg .u64 %rd<7>;\n"
                          "    mov.u32 %r0, %tid.x;\n"
                          "    setp.ne.u32 %p0, %r0, 0;\n"
                          "    @%p0 ret;\n"
                          "    ld.param.u64 %rd0, [descriptors];\n"
                          "    ld.param.u64 %rd1, [output];\n"
                          "    ld.param.u32 %r1, [count];\n"
                          "    mov.u32 %r2, %ctaid.x;\n"
                          "    mov.u32 %r3, %ntid.x;\n"
                          "    mul.lo.u32 %r4, %r2, %r3;\n"
                          "    add.u32 %r5, %r4, %r3;\n"
                          "    min.u32 %r5, %r5, %r1;\n"
                          "    mul.wide.u32 %rd2, %r2, 8;\n"
                          "    add.u64 %rd2, %rd2, %rd0;\n"
                          "    mov.u32 %r6, 0;\n"
                          "    setp.eq.u32 %p1, %r2, 0;\n"
                          "    @%p1 bra select;\n"
                          "  predecessor:\n"
                          "    ld.acquire.gpu.v2.u32 {%r7, %r6}, [%rd2-8];\n"
                          "    setp.ne.u32 %p1, %r7, 101;\n"
                          "    @%p1 bra predecessor;\n"
                          "  select:\n"
                          "    setp.ge.u32 %p2, %r4, %r5;\n"
                          "    @%p2 bra publish;\n"
                          "    and.b32 %r8, %r4, 1;\n"
                          "    setp.ne.u32 %p3, %r8, 0;\n"
                          "    @%p3 bra next;\n"
                          "    mul.wide.u32 %rd3, %r6, 4;\n"
                          "    add.u64 %rd3, %rd3, %rd1;\n"
                          "    st.global.u32 [%rd3], %r4;\n"
                          "    add.u32 %r6, %r6, 1;\n"
                          "  next:\n"
                          "    add.u32 %r4, %r4, 1;\n"
                          "    bra select;\n"
                          "  publish:\n"
                          "    mov.u32 %r7, 101;\n"
                          "    st.release.gpu.v2.u32 [%rd2], {%r7, %r6};\n"
                          "    ret;\n"
                          "  }\n"
                          ".visible .entry count(.param .u64 output, .param .u32 size, .param .u32 epoch) {\n"
                          ".reg .pred %p; .reg .u32 %r<7>; .reg .u64 %rd<3>;\n"
                          "ld.param.u64 %rd0, [output]; ld.param.u32 %r0, [size]; ld.param.u32 %r6, [epoch];\n"
                          "mov.u32 %r1, %ctaid.x; mov.u32 %r2, %ntid.x; mov.u32 %r3, %tid.x;\n"
                          "mad.lo.u32 %r4, %r1, %r2, %r3;\n"
                          "setp.ge.u32 %p, %r4, %r0; @%p ret;\n"
                          "atom.global.add.u32 %r5, [%rd0], 1;\n"
                          "mul.wide.u32 %rd1, %r4, 4; add.u64 %rd1, %rd1, %rd0;\n"
                          "add.u32 %r4, %r4, %r6; st.global.u32 [%rd1+8], %r4;\n"
                          "ret;\n"
                          "}\n"
                          "\n"
                          ".visible .entry publish(.param .u64 memory) {\n"
                          "    .reg .pred %p<2>;\n"
                          "    .reg .u32 %r<3>;\n"
                          "    .reg .u64 %rd<7>;\n"
                          "    mov.u32 %r0, %tid.x;\n"
                          "    setp.ne.u32 %p0, %r0, 0;\n"
                          "    @%p0 ret;\n"
                          "    ld.param.u64 %rd0, [memory];\n"
                          "    mov.u32 %r1, %ctaid.x;\n"
                          "    setp.ne.u32 %p0, %r1, 0;\n"
                          "    @%p0 bra observe;\n"
                          "    mov.u64 %rd1, 0x1122334455667788;\n"
                          "    mov.u64 %rd2, 0x1020304050607080;\n"
                          "    st.global.v2.u64 [%rd0], {%rd1, %rd2};\n"
                          "    mov.u32 %r2, 1;\n"
                          "    st.release.gpu.u32 [%rd0+16], %r2;\n"
                          "  observe:\n"
                          "    ld.acquire.gpu.u32 %r2, [%rd0+16];\n"
                          "    setp.eq.u32 %p1, %r2, 0;\n"
                          "    @%p1 bra observe;\n"
                          "    ld.global.v2.u64 {%rd3, %rd4}, [%rd0];\n"
                          "    mul.wide.u32 %rd6, %r1, 16;\n"
                          "    add.u64 %rd6, %rd6, %rd0;\n"
                          "    st.global.u64 [%rd6+24], %rd3;\n"
                          "    st.global.u64 [%rd6+32], %rd4;\n"
                          "    ret;\n"
                          "  }\n";

class SharedLibrary {
public:
  explicit SharedLibrary(const char *path) : handle_(dlopen(path, RTLD_NOW | RTLD_LOCAL)) {
    if (!handle_) throw std::runtime_error(dlerror());
  }

  SharedLibrary(const SharedLibrary &) = delete;
  SharedLibrary &operator=(const SharedLibrary &) = delete;
  ~SharedLibrary() { dlclose(handle_); }

  template <typename Function> Function load(const char *name) const {
    dlerror();
    void *symbol = dlsym(handle_, name);
    if (const char *error = dlerror(); error) throw std::runtime_error(std::string("cannot load ") + name + ": " + error);
    return reinterpret_cast<Function>(symbol);
  }

private:
  void *handle_;
};

void check(CUresult status) {
  CAPTURE(status);
  REQUIRE((status == CUDA_SUCCESS));
}

CUdeviceptr devicePointer(const void *pointer) { return reinterpret_cast<CUdeviceptr>(pointer); }

void run() {
  const char *configuredPath = std::getenv("GEMSTONE_CUDA_TEST_LIBRARY");
  const SharedLibrary library{configuredPath ? configuredPath : "libcuda.so.1"};
  const auto init = library.load<CUresult (*)(unsigned)>("cuInit");
  const auto primaryContextRetain = library.load<CUresult (*)(CUcontext *, CUdevice)>("cuDevicePrimaryCtxRetain");
  const auto primaryContextRelease = library.load<CUresult (*)(CUdevice)>("cuDevicePrimaryCtxRelease");
  const auto contextCreate = library.load<CUresult (*)(CUcontext *, unsigned, CUdevice)>("cuCtxCreate_v2");
  const auto contextDestroy = library.load<CUresult (*)(CUcontext)>("cuCtxDestroy_v2");
  const auto contextSetCurrent = library.load<CUresult (*)(CUcontext)>("cuCtxSetCurrent");
  const auto moduleLoadData = library.load<CUresult (*)(CUmodule *, const void *)>("cuModuleLoadData");
  const auto moduleGetFunction = library.load<CUresult (*)(CUfunction *, CUmodule, const char *)>("cuModuleGetFunction");
  const auto moduleUnload = library.load<CUresult (*)(CUmodule)>("cuModuleUnload");
  const auto moduleGetGlobal = library.load<CUresult (*)(CUdeviceptr *, size_t *, CUmodule, const char *)>("cuModuleGetGlobal_v2");
  const auto memoryAllocate = library.load<CUresult (*)(CUdeviceptr *, size_t)>("cuMemAlloc_v2");
  const auto memoryFree = library.load<CUresult (*)(CUdeviceptr)>("cuMemFree_v2");
  const auto pointerGetAttribute = library.load<CUresult (*)(void *, CUpointer_attribute, CUdeviceptr)>("cuPointerGetAttribute");
  const auto pointerGetAttributes =
      library.load<CUresult (*)(unsigned, CUpointer_attribute *, void **, CUdeviceptr)>("cuPointerGetAttributes");
  const auto managedAllocate = library.load<CUresult (*)(CUdeviceptr *, size_t, unsigned)>("cuMemAllocManaged");
  const auto hostAllocate = library.load<CUresult (*)(void **, size_t, unsigned)>("cuMemHostAlloc");
  const auto hostFree = library.load<CUresult (*)(void *)>("cuMemFreeHost");
  const auto copyHostToDevice = library.load<CUresult (*)(CUdeviceptr, const void *, size_t)>("cuMemcpyHtoD_v2");
  const auto copyDeviceToHost = library.load<CUresult (*)(void *, CUdeviceptr, size_t)>("cuMemcpyDtoH_v2");
  const auto copy = library.load<CUresult (*)(CUdeviceptr, CUdeviceptr, size_t)>("cuMemcpy");
  const auto copyAsync = library.load<CUresult (*)(CUdeviceptr, CUdeviceptr, size_t, CUstream)>("cuMemcpyAsync");
  const auto launch =
      library
          .load<CUresult (*)(CUfunction, unsigned, unsigned, unsigned, unsigned, unsigned, unsigned, unsigned, CUstream, void **, void **)>(
              "cuLaunchKernel");

  check(init(0));
  CUcontext primary = nullptr;
  check(primaryContextRetain(&primary, 0));
  CUdeviceptr primaryMemory = 0;
  check(memoryAllocate(&primaryMemory, sizeof(uint32_t)));
  check(memoryFree(primaryMemory));
  check(primaryContextRelease(0));
  CUcontext context = nullptr;
  CUmodule module = nullptr;
  CUfunction compact = nullptr;
  CUfunction count = nullptr;
  CUfunction publish = nullptr;
  CUfunction readSwitch = nullptr;
  check(contextCreate(&context, 0, 0));
  check(moduleLoadData(&module, Source));
  check(moduleGetFunction(&compact, module, "compact"));
  check(moduleGetFunction(&count, module, "count"));
  check(moduleGetFunction(&publish, module, "publish"));
  check(moduleGetFunction(&readSwitch, module, "read_switch"));
  CUdeviceptr memory = 0;
  std::array<uint32_t, 1024> host{};
  check(memoryAllocate(&memory, sizeof(host)));
  std::array<void *, 1> switchArguments{&memory};
  check(launch(readSwitch, 1, 1, 1, 1, 1, 1, 0, nullptr, switchArguments.data(), nullptr));
  uint32_t switchValue = 0;
  check(copyDeviceToHost(&switchValue, memory, sizeof(switchValue)));
  REQUIRE((switchValue == 42));
  unsigned memoryType = 99;
  unsigned managed = 99;
  CUdeviceptr pointerValue = 0;
  size_t rangeSize = 0;
  uint64_t bufferId = 0;
  std::array<std::byte, 64> ordinaryHeap{};
  const std::array unregistered{devicePointer(host.data()), devicePointer(ordinaryHeap.data()), CUdeviceptr{0}};
  for (const auto pointer : unregistered) {
    REQUIRE((pointerGetAttribute(&memoryType, CU_POINTER_ATTRIBUTE_MEMORY_TYPE, pointer) == CUDA_ERROR_INVALID_VALUE));
    std::array attributes{CU_POINTER_ATTRIBUTE_MEMORY_TYPE, CU_POINTER_ATTRIBUTE_DEVICE_POINTER, CU_POINTER_ATTRIBUTE_IS_MANAGED};
    std::array<void *, 3> values{&memoryType, &pointerValue, &managed};
    memoryType = managed = 99;
    pointerValue = 99;
    check(pointerGetAttributes(attributes.size(), attributes.data(), values.data(), pointer));
    REQUIRE((memoryType == 0 && pointerValue == 0 && managed == 0));
  }
  check(pointerGetAttribute(&memoryType, CU_POINTER_ATTRIBUTE_MEMORY_TYPE, memory + 4));
  REQUIRE((memoryType == CU_MEMORYTYPE_DEVICE));
  check(pointerGetAttribute(&pointerValue, CU_POINTER_ATTRIBUTE_RANGE_START_ADDR, memory + 4));
  REQUIRE((pointerValue == memory));
  check(pointerGetAttribute(&rangeSize, CU_POINTER_ATTRIBUTE_RANGE_SIZE, memory + 4));
  REQUIRE((rangeSize == sizeof(host)));
  check(pointerGetAttribute(&bufferId, CU_POINTER_ATTRIBUTE_BUFFER_ID, memory));
  REQUIRE((bufferId != 0));
  REQUIRE((pointerGetAttribute(&pointerValue, CU_POINTER_ATTRIBUTE_HOST_POINTER, memory) == CUDA_ERROR_INVALID_VALUE));
  uint32_t sample = 0x12345678;
  uint32_t copied = 0;
  check(copy(memory + 4, devicePointer(&sample), sizeof(sample)));
  check(copyAsync(devicePointer(&copied), memory + 4, sizeof(copied), nullptr));
  check(library.load<CUresult (*)()>("cuCtxSynchronize")());
  REQUIRE((copied == sample));
  CUdeviceptr unified = 0;
  check(managedAllocate(&unified, 64, CU_MEM_ATTACH_GLOBAL));
  check(pointerGetAttribute(&managed, CU_POINTER_ATTRIBUTE_IS_MANAGED, unified + 8));
  REQUIRE((managed == 1));
  check(pointerGetAttribute(&pointerValue, CU_POINTER_ATTRIBUTE_HOST_POINTER, unified + 8));
  REQUIRE((pointerValue == unified + 8));
  copied = 0;
  check(copy(unified + 8, devicePointer(&sample), sizeof(sample)));
  check(copy(devicePointer(&copied), unified + 8, sizeof(copied)));
  REQUIRE((copied == sample));
  check(memoryFree(unified));
  REQUIRE((pointerGetAttribute(&managed, CU_POINTER_ATTRIBUTE_IS_MANAGED, unified) == CUDA_ERROR_INVALID_VALUE));
  void *pinned = nullptr;
  check(hostAllocate(&pinned, 64, 0));
  check(pointerGetAttribute(&memoryType, CU_POINTER_ATTRIBUTE_MEMORY_TYPE, devicePointer(pinned)));
  REQUIRE((memoryType == CU_MEMORYTYPE_HOST));
  check(pointerGetAttribute(&managed, CU_POINTER_ATTRIBUTE_IS_MANAGED, devicePointer(pinned)));
  REQUIRE((managed == 0));
  check(hostFree(pinned));
  REQUIRE((pointerGetAttribute(&memoryType, CU_POINTER_ATTRIBUTE_MEMORY_TYPE, devicePointer(pinned)) == CUDA_ERROR_INVALID_VALUE));
  CUcontext allocationContext = nullptr;
  CUcontext otherContext = nullptr;
  check(contextCreate(&otherContext, 0, 0));
  REQUIRE((otherContext != context));
  CUdeviceptr otherMemory = 0;
  check(memoryAllocate(&otherMemory, sizeof(uint32_t)));
  const uint32_t otherValue = 99;
  check(copyHostToDevice(otherMemory, &otherValue, sizeof(otherValue)));
  check(pointerGetAttribute(&allocationContext, CU_POINTER_ATTRIBUTE_CONTEXT, memory));
  REQUIRE((allocationContext == context));
  check(contextDestroy(otherContext));
  REQUIRE((copyHostToDevice(otherMemory, &otherValue, sizeof(otherValue)) != CUDA_SUCCESS));
  check(contextSetCurrent(nullptr));
  REQUIRE((pointerGetAttribute(&pointerValue, CU_POINTER_ATTRIBUTE_DEVICE_POINTER, memory) == CUDA_ERROR_INVALID_CONTEXT));
  check(pointerGetAttribute(&allocationContext, CU_POINTER_ATTRIBUTE_CONTEXT, memory));
  REQUIRE((allocationContext == context));
  check(contextSetCurrent(context));
  CUdeviceptr global = 0;
  size_t globalSize = 0;
  check(moduleGetGlobal(&global, &globalSize, module, "metadata_probe"));
  REQUIRE((globalSize == 16));
  check(pointerGetAttribute(&rangeSize, CU_POINTER_ATTRIBUTE_RANGE_SIZE, global + 4));
  REQUIRE((rangeSize == 16));
  constexpr std::array groups{1U, 2U, 3U, 5U, 9U};
  constexpr std::array widths{31U, 32U, 33U, 64U, 65U};
  unsigned launches = 0;
  std::array<void *, 1> streamProbeArguments{&memory};
  check(launch(publish, 1, 1, 1, 1, 1, 1, 0, reinterpret_cast<CUstream>(uintptr_t{1}), streamProbeArguments.data(), nullptr));
  for (const unsigned groupCount : groups) {
    for (const unsigned width : widths) {
      unsigned n = groupCount * width - width / 2;
      for (unsigned repeat = 0; repeat < 2; ++repeat) {
        host.fill(0);
        check(copyHostToDevice(memory, host.data(), sizeof(host)));
        std::array<void *, 1> publicationArguments{&memory};
        check(launch(publish, groupCount, 1, 1, width, 1, 1, 0, nullptr, publicationArguments.data(), nullptr));
        ++launches;
        check(copyDeviceToHost(host.data(), memory, sizeof(host)));
        for (unsigned block = 0; block < groupCount; ++block) {
          const uint64_t first = host[6 + block * 4] | (static_cast<uint64_t>(host[7 + block * 4]) << 32);
          const uint64_t second = host[8 + block * 4] | (static_cast<uint64_t>(host[9 + block * 4]) << 32);
          REQUIRE((first == static_cast<uint64_t>(0x1122334455667788)));
          REQUIRE((second == static_cast<uint64_t>(0x1020304050607080)));
        }
        host.fill(0);
        check(copyHostToDevice(memory, host.data(), sizeof(host)));
        CUdeviceptr output = memory + groupCount * 8;
        std::array<void *, 3> arguments{&memory, &output, &n};
        check(launch(compact, groupCount, 1, 1, width, 1, 1, 0, nullptr, arguments.data(), nullptr));
        ++launches;
        check(copyDeviceToHost(host.data(), memory, sizeof(host)));
        CAPTURE(groupCount, width, repeat, n, host[0], host[1]);
        REQUIRE((host[2 * groupCount - 1] == (n + 1) / 2));
        for (unsigned block = 0; block < groupCount; ++block) {
          unsigned end = (block + 1) * width;
          if (end > n) end = n;
          REQUIRE((host[2 * block] == 101));
          REQUIRE((host[2 * block + 1] == (end + 1) / 2));
        }
        for (unsigned i = 0; i < (n + 1) / 2; ++i)
          REQUIRE((host[2 * groupCount + i] == 2 * i));
      }
      host.fill(0);
      check(copyHostToDevice(memory, host.data(), sizeof(host)));
      for (unsigned repeat = 1; repeat <= 2; ++repeat) {
        unsigned epoch = repeat * 1000;
        std::array<void *, 3> arguments{&memory, &n, &epoch};
        check(launch(count, groupCount, 1, 1, width, 1, 1, 0, nullptr, arguments.data(), nullptr));
        ++launches;
        check(copyDeviceToHost(host.data(), memory, sizeof(host)));
        REQUIRE((host[0] == repeat * n));
        for (unsigned i = 0; i < n; ++i)
          REQUIRE((host[2 + i] == epoch + i));
      }
    }
  }
  check(memoryFree(memory));
  REQUIRE((pointerGetAttribute(&memoryType, CU_POINTER_ATTRIBUTE_MEMORY_TYPE, memory) == CUDA_ERROR_INVALID_VALUE));
  check(moduleUnload(module));
  REQUIRE((pointerGetAttribute(&memoryType, CU_POINTER_ATTRIBUTE_MEMORY_TYPE, global) == CUDA_ERROR_INVALID_VALUE));
  check(memoryAllocate(&memory, 32));
  uint64_t newId = 0;
  check(pointerGetAttribute(&newId, CU_POINTER_ATTRIBUTE_BUFFER_ID, memory));
  REQUIRE((newId != bufferId));
  check(contextDestroy(context));
  REQUIRE((pointerGetAttribute(&memoryType, CU_POINTER_ATTRIBUTE_MEMORY_TYPE, memory) == CUDA_ERROR_INVALID_VALUE));
  std::printf("CUDA cross-workgroup: %u checked launches, groups 1/2/3/5/9, lanes 31/32/33/64/65\n", launches);
}

void runStreamCallbacks() {
  // Synchronization publishes callback effects to the calling thread.
  const char *configuredPath = std::getenv("GEMSTONE_CUDA_TEST_LIBRARY");
  const SharedLibrary library{configuredPath ? configuredPath : "libcuda.so.1"};
  const auto init = library.load<CUresult (*)(unsigned)>("cuInit");
  const auto contextCreate = library.load<CUresult (*)(CUcontext *, unsigned, CUdevice)>("cuCtxCreate_v2");
  const auto contextDestroy = library.load<CUresult (*)(CUcontext)>("cuCtxDestroy_v2");
  const auto streamCreate = library.load<CUresult (*)(CUstream *, unsigned)>("cuStreamCreate");
  const auto streamDestroy = library.load<CUresult (*)(CUstream)>("cuStreamDestroy_v2");
  const auto streamSynchronize = library.load<CUresult (*)(CUstream)>("cuStreamSynchronize");
  const auto streamAddCallback = library.load<CUresult (*)(CUstream, CUstreamCallback, void *, unsigned)>("cuStreamAddCallback");

  check(init(0));
  CUcontext context = nullptr;
  check(contextCreate(&context, 0, 0));
  CUstream first = nullptr;
  CUstream second = nullptr;
  check(streamCreate(&first, 0));
  check(streamCreate(&second, 0));
  struct Observed {
    CUstream expected;
    int calls = 0;
    bool valid = true;
  } observed{first};
  check(streamAddCallback(
      first,
      [](CUstream stream, CUresult status, void *data) {
        auto &value = *static_cast<Observed *>(data);
        ++value.calls;
        value.valid = value.valid && stream == value.expected && status == CUDA_SUCCESS;
      },
      &observed, 0));
  check(streamSynchronize(second));
  check(streamSynchronize(first));
  REQUIRE((observed.calls == 1));
  REQUIRE(observed.valid);
  check(streamDestroy(second));
  check(streamDestroy(first));
  check(contextDestroy(context));
}

void runSystemPaging() {
  constexpr char kernel[] = ".version 8.0\n"
                            ".target sm_80\n"
                            ".address_size 64\n"
                            ".visible .entry increment_host(.param .u64 .ptr .global ptr) {\n"
                            "  .reg .u64 %rd; .reg .u32 %r<2>;\n"
                            "  ld.param.u64 %rd, [ptr];\n"
                            "  ld.global.u32 %r0, [%rd];\n"
                            "  add.u32 %r1, %r0, 1;\n"
                            "  st.global.u32 [%rd], %r1;\n"
                            "  ret;\n"
                            "}\n"
                            ".visible .entry increment_indirect(.param .u64 .ptr .global root) {\n"
                            "  .reg .u64 %rd<2>; .reg .u32 %r<2>;\n"
                            "  ld.param.u64 %rd0, [root];\n"
                            "  ld.global.u64 %rd1, [%rd0];\n"
                            "  ld.global.u32 %r0, [%rd1];\n"
                            "  add.u32 %r1, %r0, 1;\n"
                            "  st.global.u32 [%rd1], %r1;\n"
                            "  ret;\n"
                            "}\n";
  const char *configuredPath = std::getenv("GEMSTONE_CUDA_TEST_LIBRARY");
  const SharedLibrary library{configuredPath ? configuredPath : "libcuda.so.1"};
  const auto init = library.load<CUresult (*)(unsigned)>("cuInit");
  const auto attribute = library.load<CUresult (*)(int *, CUdevice_attribute, CUdevice)>("cuDeviceGetAttribute");
  const auto contextCreate = library.load<CUresult (*)(CUcontext *, unsigned, CUdevice)>("cuCtxCreate_v2");
  const auto contextDestroy = library.load<CUresult (*)(CUcontext)>("cuCtxDestroy_v2");
  const auto moduleLoad = library.load<CUresult (*)(CUmodule *, const void *)>("cuModuleLoadData");
  const auto moduleGetFunction = library.load<CUresult (*)(CUfunction *, CUmodule, const char *)>("cuModuleGetFunction");
  const auto moduleUnload = library.load<CUresult (*)(CUmodule)>("cuModuleUnload");
  const auto managedAllocate = library.load<CUresult (*)(CUdeviceptr *, size_t, unsigned)>("cuMemAllocManaged");
  const auto memoryFree = library.load<CUresult (*)(CUdeviceptr)>("cuMemFree_v2");
  const auto launch =
      library
          .load<CUresult (*)(CUfunction, unsigned, unsigned, unsigned, unsigned, unsigned, unsigned, unsigned, CUstream, void **, void **)>(
              "cuLaunchKernel");
  check(init(0));
  int pageable = 0;
  check(attribute(&pageable, CU_DEVICE_ATTRIBUTE_PAGEABLE_MEMORY_ACCESS, 0));
  // The opt-in demand path does not claim full system-memory atomic coherence.
  REQUIRE((pageable == 0));
  CUcontext context = nullptr;
  check(contextCreate(&context, 0, 0));
  CUmodule module = nullptr;
  check(moduleLoad(&module, kernel));
  CUfunction function = nullptr;
  check(moduleGetFunction(&function, module, "increment_host"));
  unsigned value = 41;
  CUdeviceptr pointer = devicePointer(&value);
  void *arguments[] = {&pointer};
  check(launch(function, 1, 1, 1, 1, 1, 1, 0, nullptr, arguments, nullptr));
  check(library.load<CUresult (*)()>("cuCtxSynchronize")());
  REQUIRE((value == 42));
  check(moduleGetFunction(&function, module, "increment_indirect"));
  auto target = std::make_unique<unsigned>(20);
  CUdeviceptr indirect = devicePointer(target.get());
  pointer = devicePointer(&indirect);
  check(launch(function, 1, 1, 1, 1, 1, 1, 0, nullptr, arguments, nullptr));
  check(library.load<CUresult (*)()>("cuCtxSynchronize")());
  REQUIRE((*target == 21));
  CUdeviceptr managed = 0;
  check(managedAllocate(&managed, sizeof(unsigned), CU_MEM_ATTACH_GLOBAL));
  auto heap = std::make_unique<unsigned>(30);
  pointer = devicePointer(heap.get());
  check(moduleGetFunction(&function, module, "increment_host"));
  check(launch(function, 1, 1, 1, 1, 1, 1, 0, nullptr, arguments, nullptr));
  check(library.load<CUresult (*)()>("cuCtxSynchronize")());
  REQUIRE((*heap == 31));
  check(memoryFree(managed));
  check(moduleUnload(module));
  check(contextDestroy(context));
}

void runManagedRuntime() {
  const char *configuredPath = std::getenv("GEMSTONE_CUDA_TEST_LIBRARY");
  const SharedLibrary library{configuredPath ? configuredPath : "libcuda.so.1"};
  const auto mallocManaged = library.load<int32_t (*)(void **, size_t, unsigned)>("cudaMallocManaged");
  const auto malloc = library.load<int32_t (*)(void **, size_t)>("cudaMalloc");
  const auto free = library.load<int32_t (*)(void *)>("cudaFree");
  const auto copy = library.load<int32_t (*)(void *, const void *, size_t, int32_t)>("cudaMemcpy");
  const auto deviceSynchronize = library.load<int32_t (*)()>("cudaDeviceSynchronize");

  constexpr int32_t success = 0;
  constexpr int32_t hostToDevice = 1;
  constexpr int32_t deviceToHost = 2;
  constexpr unsigned attachGlobal = 1;
  constexpr size_t bytes = 64 * sizeof(uint32_t);

  void *managed = nullptr;
  REQUIRE((mallocManaged(&managed, bytes, attachGlobal) == success));
  REQUIRE((managed != nullptr));
  void *device = nullptr;
  REQUIRE((malloc(&device, bytes) == success));

  // Host writes reach the device directly, since the managed allocation is the host buffer.
  auto *words = static_cast<uint32_t *>(managed);
  for (unsigned index = 0; index < 64; ++index)
    words[index] = index * 7U + 1U;
  REQUIRE((copy(device, managed, bytes, hostToDevice) == success));

  // Device writes copied back land in the same host buffer the caller already holds.
  std::array<uint32_t, 64> replacement{};
  for (unsigned index = 0; index < replacement.size(); ++index)
    replacement[index] = 0xdead0000U | index;
  REQUIRE((copy(device, replacement.data(), bytes, hostToDevice) == success));
  REQUIRE((copy(managed, device, bytes, deviceToHost) == success));
  for (unsigned index = 0; index < replacement.size(); ++index)
    REQUIRE((words[index] == replacement[index]));

  REQUIRE((deviceSynchronize() == success));
  REQUIRE((copy(managed, device, bytes, deviceToHost) == success));
  for (unsigned index = 0; index < replacement.size(); ++index)
    REQUIRE((words[index] == replacement[index]));

  REQUIRE((free(managed) == success));
  REQUIRE((free(managed) != success));
  REQUIRE((free(device) == success));
}

} // namespace

TEST_CASE("CUDA cross-workgroup execution", "[cuda][integration]") { run(); }

TEST_CASE("CUDA callbacks complete once before stream synchronization", "[cuda][integration]") { runStreamCallbacks(); }

TEST_CASE("CUDA system paging reaches a plain host pointer", "[cuda][integration]") { runSystemPaging(); }

namespace {
// Exercise actual PTX execution and synchronization through the existing server.
struct UnifiedMemoryClient {
  SharedLibrary library{std::getenv("GEMSTONE_CUDA_TEST_LIBRARY") ? std::getenv("GEMSTONE_CUDA_TEST_LIBRARY") : "libcuda.so.1"};
  CUcontext context = nullptr;
  CUmodule module = nullptr;
  decltype(&cuMemAllocManaged) managed = library.load<decltype(&cuMemAllocManaged)>("cuMemAllocManaged");
  decltype(&cuMemFree) free = library.load<decltype(&cuMemFree)>("cuMemFree_v2");
  decltype(&cuMemHostAlloc) hostAlloc = library.load<decltype(&cuMemHostAlloc)>("cuMemHostAlloc");
  decltype(&cuMemFreeHost) hostFree = library.load<decltype(&cuMemFreeHost)>("cuMemFreeHost");
  decltype(&cuMemHostRegister) hostRegister = library.load<decltype(&cuMemHostRegister)>("cuMemHostRegister_v2");
  decltype(&cuMemHostUnregister) hostUnregister = library.load<decltype(&cuMemHostUnregister)>("cuMemHostUnregister");
  decltype(&cuMemHostGetDevicePointer) hostDevicePointer =
      library.load<decltype(&cuMemHostGetDevicePointer)>("cuMemHostGetDevicePointer_v2");
  decltype(&cuLaunchKernel) launch = library.load<decltype(&cuLaunchKernel)>("cuLaunchKernel");
  decltype(&cuCtxSynchronize) synchronize = library.load<decltype(&cuCtxSynchronize)>("cuCtxSynchronize");
  decltype(&cuModuleGetFunction) function = library.load<decltype(&cuModuleGetFunction)>("cuModuleGetFunction");
  UnifiedMemoryClient() {
    check(library.load<CUresult (*)(unsigned)>("cuInit")(0));
    check(library.load<CUresult (*)(CUcontext *, unsigned, CUdevice)>("cuCtxCreate_v2")(&context, CU_CTX_MAP_HOST, 0));
    constexpr char source[] = ".version 8.0\n.target sm_80\n.address_size 64\n"
                              ".visible .entry alias_increment(.param .u64 first, .param .u64 second) {\n"
                              ".reg .u64 %p, %q; .reg .u32 %v;\n"
                              "ld.param.u64 %p, [first]; ld.param.u64 %q, [second];\n"
                              "ld.global.u32 %v, [%p]; add.u32 %v, %v, 1; st.global.u32 [%p], %v;\n"
                              "ld.global.u32 %v, [%q]; add.u32 %v, %v, 1; st.global.u32 [%q], %v; ret; }\n"
                              ".visible .entry publish_pointer(.param .u64 output, .param .u64 target) {\n"
                              ".reg .u64 %p, %q; ld.param.u64 %p, [output]; ld.param.u64 %q, [target];\n"
                              "st.global.u64 [%p], %q; ret; }\n"
                              ".visible .entry indirect_increment(.param .u64 table) {\n"
                              ".reg .u64 %p, %q; .reg .u32 %v;\n"
                              "ld.param.u64 %p, [table]; ld.global.u64 %q, [%p];\n"
                              "ld.global.u32 %v, [%q]; add.u32 %v, %v, 1; st.global.u32 [%q], %v; ret; }\n";
    check(library.load<CUresult (*)(CUmodule *, const void *)>("cuModuleLoadData")(&module, source));
  }
  ~UnifiedMemoryClient() {
    if (module) (void)library.load<CUresult (*)(CUmodule)>("cuModuleUnload")(module);
    if (context) (void)library.load<CUresult (*)(CUcontext)>("cuCtxDestroy_v2")(context);
  }
  void increment(CUdeviceptr address) {
    CUfunction kernel = nullptr;
    check(function(&kernel, module, "alias_increment"));
    void *args[] = {&address, &address};
    check(launch(kernel, 1, 1, 1, 1, 1, 1, 0, nullptr, args, nullptr));
    check(synchronize());
  }
};
} // namespace

TEST_CASE("CUDA managed interior aliases preserve host edits between launches", "[cuda][integration][unified-memory]") {
  UnifiedMemoryClient api;
  CUdeviceptr allocation = 0;
  check(api.managed(&allocation, 64, CU_MEM_ATTACH_GLOBAL));
  auto *host = reinterpret_cast<uint32_t *>(allocation);
  for (uint32_t seed : {11U, 91U, 123U}) {
    std::fill_n(host, 16, 0xa5a5a5a5U);
    host[3] = seed;
    api.increment(allocation + 3 * sizeof(uint32_t));
    CHECK((host[3] == seed + 2));
    for (unsigned index = 0; index < 16; ++index)
      if (index != 3) CHECK((host[index] == 0xa5a5a5a5U));
  }
  check(api.free(allocation));
}

TEST_CASE("CUDA pinned interior aliases preserve host edits between launches", "[cuda][integration][unified-memory]") {
  UnifiedMemoryClient api;
  void *allocation = nullptr;
  check(api.hostAlloc(&allocation, 64, CU_MEMHOSTALLOC_DEVICEMAP));
  CUdeviceptr device = 0;
  check(api.hostDevicePointer(&device, allocation, 0));
  auto *host = static_cast<uint32_t *>(allocation);
  for (uint32_t seed : {11U, 91U}) {
    std::fill_n(host, 16, 0xa5a5a5a5U);
    host[3] = seed;
    api.increment(device + 3 * sizeof(uint32_t));
    CHECK((host[3] == seed + 2));
    CHECK((host[2] == 0xa5a5a5a5U && host[4] == 0xa5a5a5a5U));
  }
  check(api.hostFree(allocation));
}

TEST_CASE("CUDA registered stack subrange supports kernels and unregister", "[cuda][integration][unified-memory]") {
  UnifiedMemoryClient api;
  std::array<uint32_t, 16> host{};
  auto *registered = host.data() + 2;
  for (uint32_t seed : {11U, 91U}) {
    host.fill(0xa5a5a5a5U);
    host[3] = seed;
    check(api.hostRegister(registered, 8 * sizeof(uint32_t), CU_MEMHOSTREGISTER_DEVICEMAP));
    CUdeviceptr device = 0;
    check(api.hostDevicePointer(&device, registered, 0));
    api.increment(device + sizeof(uint32_t));
    CHECK((host[3] == seed + 2));
    check(api.hostUnregister(registered));
    CHECK((host[2] == 0xa5a5a5a5U && host[10] == 0xa5a5a5a5U));
  }
}

TEST_CASE("CUDA registered heap subrange supports kernels and unregister", "[cuda][integration][unified-memory]") {
  UnifiedMemoryClient api;
  auto host = std::make_unique<uint32_t[]>(16);
  std::fill_n(host.get(), 16, 11U);
  check(api.hostRegister(host.get() + 2, 8 * sizeof(uint32_t), CU_MEMHOSTREGISTER_DEVICEMAP));
  CUdeviceptr device = 0;
  check(api.hostDevicePointer(&device, host.get() + 2, 0));
  api.increment(device + sizeof(uint32_t));
  CHECK((host[3] == 13));
  check(api.hostUnregister(host.get() + 2));
  host[3] = 71;
  CHECK((host[3] == 71));
}

TEST_CASE("CUDA managed pointer graph preserves interior targets and pointer bytes", "[cuda][integration][unified-memory]") {
  UnifiedMemoryClient api;
  CUdeviceptr allocation = 0, table = 0;
  check(api.managed(&allocation, 64, CU_MEM_ATTACH_GLOBAL));
  check(api.managed(&table, sizeof(CUdeviceptr), CU_MEM_ATTACH_GLOBAL));
  auto *host = reinterpret_cast<uint32_t *>(allocation);
  CUfunction kernel = nullptr;
  check(api.function(&kernel, api.module, "indirect_increment"));
  for (unsigned offset : {1U, 7U, 14U}) {
    std::fill_n(host, 16, 41U);
    *reinterpret_cast<CUdeviceptr *>(table) = allocation + offset * sizeof(uint32_t);
    void *args[] = {&table};
    check(api.launch(kernel, 1, 1, 1, 1, 1, 1, 0, nullptr, args, nullptr));
    check(api.synchronize());
    CHECK((host[offset] == 42));
    CHECK((*reinterpret_cast<CUdeviceptr *>(table) == allocation + offset * sizeof(uint32_t)));
    CHECK((host[offset - 1] == 41 && host[offset + 1] == 41));
  }
  check(api.free(table));
  check(api.free(allocation));
}

TEST_CASE("CUDA managed device pointer publication preserves host addresses", "[cuda][integration][unified-memory]") {
  UnifiedMemoryClient api;
  CUdeviceptr allocation = 0, table = 0;
  check(api.managed(&allocation, 64, CU_MEM_ATTACH_GLOBAL));
  check(api.managed(&table, sizeof(CUdeviceptr), CU_MEM_ATTACH_GLOBAL));
  CUfunction kernel = nullptr;
  check(api.function(&kernel, api.module, "publish_pointer"));
  for (unsigned offset : {1U, 7U, 15U}) {
    *reinterpret_cast<CUdeviceptr *>(table) = 0;
    CUdeviceptr target = allocation + offset * sizeof(uint32_t);
    void *args[] = {&table, &target};
    check(api.launch(kernel, 1, 1, 1, 1, 1, 1, 0, nullptr, args, nullptr));
    check(api.synchronize());
    CHECK((*reinterpret_cast<CUdeviceptr *>(table) == target));
  }
  check(api.free(table));
  check(api.free(allocation));
}

TEST_CASE("CUDA runtime host registration supports a borrowed stack range", "[cuda][integration][unified-memory]") {
  UnifiedMemoryClient api;
  const auto registerHost = api.library.load<int (*)(void *, size_t, unsigned)>("cudaHostRegister");
  const auto unregisterHost = api.library.load<int (*)(void *)>("cudaHostUnregister");
  const auto devicePointer = api.library.load<int (*)(void **, void *, unsigned)>("cudaHostGetDevicePointer");
  std::array<uint32_t, 16> host{};
  host[3] = 61;
  const int registered = registerHost(host.data() + 2, 8 * sizeof(uint32_t), 2 /* cudaHostRegisterMapped */);
  REQUIRE((registered == 0));
  void *device = nullptr;
  const int mapped = devicePointer(&device, host.data() + 2, 0);
  CHECK((mapped == 0));
  if (mapped == 0) {
    api.increment(reinterpret_cast<CUdeviceptr>(device) + sizeof(uint32_t));
    CHECK((host[3] == 63));
  }
  CHECK((unregisterHost(host.data() + 2) == 0));
}

TEST_CASE("CUDA free waits for queued memory uses", "[cuda][integration]") {
  const char *configuredPath = std::getenv("GEMSTONE_CUDA_TEST_LIBRARY");
  const SharedLibrary library{configuredPath ? configuredPath : "libcuda.so.1"};
  check(library.load<decltype(&cuInit)>("cuInit")(0));
  CUcontext context{};
  check(library.load<CUresult (*)(CUcontext *, unsigned, CUdevice)>("cuCtxCreate_v2")(&context, 0, 0));
  gemstone_test::queuedLifetime(
      [&](size_t size) {
        CUdeviceptr p{};
        check(library.load<decltype(&cuMemAlloc_v2)>("cuMemAlloc_v2")(&p, size));
        return reinterpret_cast<void *>(p);
      },
      [&](void *dst, const void *src, size_t size, bool upload) {
        if (upload) check(library.load<decltype(&cuMemcpyHtoDAsync_v2)>("cuMemcpyHtoDAsync_v2")(devicePointer(dst), src, size, nullptr));
        else check(library.load<decltype(&cuMemcpyDtoHAsync_v2)>("cuMemcpyDtoHAsync_v2")(dst, devicePointer(src), size, nullptr));
      },
      [&](void (*callback)(void *), void *data) {
        check(library.load<decltype(&cuLaunchHostFunc)>("cuLaunchHostFunc")(nullptr, callback, data));
      },
      [&](void *p) { return library.load<decltype(&cuMemFree_v2)>("cuMemFree_v2")(devicePointer(p)); });
  check(library.load<decltype(&cuCtxDestroy_v2)>("cuCtxDestroy_v2")(context));
}

TEST_CASE("CUDA managed runtime allocation round-trips host and device", "[cuda][integration]") { runManagedRuntime(); }
