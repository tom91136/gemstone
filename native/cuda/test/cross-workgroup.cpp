#include <array>
#include <cstdio>
#include <cstdlib>
#include <stdexcept>
#include <string>

#include <dlfcn.h>
#include <stddef.h>
#include <stdint.h>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

namespace {

// Standalone native Driver API client; MUnit supplies the emulator endpoint and client identity.
constexpr char Source[] = ".version 8.0\n"
                          ".target sm_80\n"
                          ".address_size 64\n"
                          ".global .align 4 .u32 metadata_probe[4];\n"
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
  check(contextCreate(&context, 0, 0));
  check(moduleLoadData(&module, Source));
  check(moduleGetFunction(&compact, module, "compact"));
  check(moduleGetFunction(&count, module, "count"));
  check(moduleGetFunction(&publish, module, "publish"));
  CUdeviceptr memory = 0;
  std::array<uint32_t, 1024> host{};
  check(memoryAllocate(&memory, sizeof(host)));
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
          REQUIRE((first == UINT64_C(0x1122334455667788)));
          REQUIRE((second == UINT64_C(0x1020304050607080)));
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

} // namespace

TEST_CASE("CUDA cross-workgroup execution", "[cuda][integration]") { run(); }
