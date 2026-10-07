#include <array>
#include <chrono>
#include <cstdio>
#include <cstdlib>
#include <future>
#include <string_view>

#include <dlfcn.h>
#include <stdint.h>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>
#include <cuda_runtime_api.h>

#include <gemstone/test_support.hpp>

#include <environment_keys.hpp>

namespace {

struct TargetCase {
  std::string_view value;
  int major;
  int minor;
};

bool targetSelectionWorks() {
  constexpr std::array cases{
      TargetCase{"sm_75", 7, 5},
      TargetCase{"compute_89", 8, 9},
      TargetCase{"sm89", 8, 9},
      TargetCase{"compute90", 9, 0},
  };
  bool valid = true;
  unsetenv(gemstone::environment::TargetArch);
  for (const auto &target : cases) {
    if (setenv(gemstone::environment::TargetArch, target.value.data(), 1) != 0) {
      std::fprintf(stderr, "cannot set target case %s\n", target.value.data());
      valid = false;
      continue;
    }
    int major = -1;
    int minor = -1;
    const CUresult status = cuDeviceComputeCapability(&major, &minor, 0);
    if (status != CUDA_SUCCESS || major != target.major || minor != target.minor) {
      std::fprintf(stderr, "target case %s returned status=%d capability=%d.%d, expected %d.%d\n", target.value.data(),
                   static_cast<int>(status), major, minor, target.major, target.minor);
      valid = false;
    }
  }
  return valid;
}

bool driverErrorQueryWorks() {
  const char *name = nullptr;
  const char *message = nullptr;
  const CUresult nameStatus = cuGetErrorName(CUDA_ERROR_INVALID_VALUE, &name);
  const CUresult messageStatus = cuGetErrorString(CUDA_ERROR_INVALID_VALUE, &message);
  if (nameStatus == CUDA_SUCCESS && messageStatus == CUDA_SUCCESS && name && message && name == std::string_view{"CUDA_ERROR_INVALID_VALUE"}
      && message[0] != '\0')
    return true;
  std::fprintf(stderr, "driver error query returned name=%d message=%d name-value=%p message-value=%p\n", static_cast<int>(nameStatus),
               static_cast<int>(messageStatus), static_cast<const void *>(name), static_cast<const void *>(message));
  return false;
}

struct Lookup {
  const char *symbol;
  int version;
  CUdriverProcAddressQueryResult status;
  const char *expected;
};

// Exported only as the not-supported stub, which stands in for a revision whose ABI no export provides.
constexpr const char *Unsupported = "cuCtxAttach";

// Each row names the exported symbol cuGetProcAddress must return; the CUDA 3.2 split selects the legacy ABI.
constexpr std::array Lookups{
    Lookup{"cuMemAlloc", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemAlloc_v2"},
    Lookup{"cuMemAlloc", 3010, CU_GET_PROC_ADDRESS_SUCCESS, Unsupported},
    Lookup{"cuMemAlloc_v2", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemAlloc_v2"},
    Lookup{"cuArrayCreate", 3010, CU_GET_PROC_ADDRESS_SUCCESS, "cuArrayCreate"},
    Lookup{"cuArrayCreate", 3020, CU_GET_PROC_ADDRESS_SUCCESS, "cuArrayCreate_v2"},
    Lookup{"cuArrayCreate", 0, CU_GET_PROC_ADDRESS_SUCCESS, "cuArrayCreate_v2"},
    Lookup{"cuModuleGetGlobal", 2000, CU_GET_PROC_ADDRESS_SUCCESS, "cuModuleGetGlobal"},
    Lookup{"cuModuleGetGlobal", 12000, CU_GET_PROC_ADDRESS_SUCCESS, "cuModuleGetGlobal_v2"},
    Lookup{"cuMemcpy2D", 3000, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemcpy2D"},
    Lookup{"cuMemcpy2D", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemcpy2D_v2"},
    Lookup{"cuDeviceTotalMem", 3010, CU_GET_PROC_ADDRESS_SUCCESS, "cuDeviceTotalMem"},
    Lookup{"cuDeviceTotalMem", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuDeviceTotalMem_v2"},
    Lookup{"cuMemcpyHtoD", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemcpyHtoD_v2"},
    Lookup{"cuMemcpyHtoDAsync", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemcpyHtoDAsync_v2"},
    Lookup{"cuMemsetD8", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemsetD8_v2"},
    Lookup{"cuMemAllocPitch", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemAllocPitch_v2"},
    Lookup{"cuMemHostGetDevicePointer", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemHostGetDevicePointer_v2"},
    Lookup{"cuMemGetAddressRange", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemGetAddressRange_v2"},
    Lookup{"cuCtxDestroy", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuCtxDestroy_v2"},
    Lookup{"cuEventDestroy", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuEventDestroy_v2"},
    Lookup{"cuEventElapsedTime", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuEventElapsedTime_v2"},
    Lookup{"cuLinkCreate", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuLinkCreate_v2"},
    Lookup{"cuLinkAddData", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuLinkAddData_v2"},
    Lookup{"cuCtxCreate_v2", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuCtxCreate_v2"},
    Lookup{"cuCtxCreate", 11030, CU_GET_PROC_ADDRESS_SUCCESS, "cuCtxCreate"},
    Lookup{"cuCtxCreate", 11040, CU_GET_PROC_ADDRESS_SUCCESS, "cuCtxCreate_v3"},
    Lookup{"cuCtxCreate", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuCtxCreate_v4"},
    Lookup{"cuGetProcAddress", 11030, CU_GET_PROC_ADDRESS_SUCCESS, "cuGetProcAddress"},
    Lookup{"cuGetProcAddress", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuGetProcAddress_v2"},
    Lookup{"cuCtxSynchronize", 12080, CU_GET_PROC_ADDRESS_SUCCESS, "cuCtxSynchronize"},
    Lookup{"cuCtxSynchronize", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuCtxSynchronize_v2"},
    Lookup{"cuMemAllocHost", 3010, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemAllocHost"},
    Lookup{"cuMemAllocHost", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemAllocHost_v2"},
    Lookup{"cuLaunchHostFunc", 13000, CU_GET_PROC_ADDRESS_SUCCESS, "cuLaunchHostFunc"},
    Lookup{"cuLaunchHostFunc", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuLaunchHostFunc_v2"},
    Lookup{"cuCtxDestroy", 3020, CU_GET_PROC_ADDRESS_SUCCESS, Unsupported},
    Lookup{"cuLaunchKernelEx", 11000, CU_GET_PROC_ADDRESS_VERSION_NOT_SUFFICIENT, nullptr},
    Lookup{"cuStreamDestroy", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuStreamDestroy_v2"},
    Lookup{"cuMemcpyHtoD_ptds", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemcpyHtoD_v2"},
    Lookup{"cuStreamQuery_ptsz", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuStreamQuery"},
    Lookup{"cuGetProcAddress_v2", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuGetProcAddress_v2"},
    Lookup{"cuLaunchKernel", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuLaunchKernel"},
    Lookup{"cuLaunchKernel_ptsz", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuLaunchKernel_ptsz"},
    Lookup{"cuLaunchKernelEx", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuLaunchKernelEx"},
    Lookup{"cuStreamGetCaptureInfo", 10000, CU_GET_PROC_ADDRESS_VERSION_NOT_SUFFICIENT, nullptr},
    Lookup{"cuStreamGetCaptureInfo", 10010, CU_GET_PROC_ADDRESS_SUCCESS, "cuStreamGetCaptureInfo"},
    Lookup{"cuStreamGetCaptureInfo", 11030, CU_GET_PROC_ADDRESS_SUCCESS, "cuStreamGetCaptureInfo_v2"},
    Lookup{"cuStreamGetCaptureInfo", 12030, CU_GET_PROC_ADDRESS_SUCCESS, "cuStreamGetCaptureInfo_v3"},
    Lookup{"cuStreamGetCaptureInfo_ptsz", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuStreamGetCaptureInfo_v3"},
    Lookup{"cuStreamGetCaptureInfo_v2_ptsz", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuStreamGetCaptureInfo_v2"},
    Lookup{"cuStreamIsCapturing_ptsz", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuStreamIsCapturing"},
    Lookup{"cuMemAllocAsync", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemAllocAsync"},
    Lookup{"cuMemsetD8Async", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemsetD8Async"},
    Lookup{"cuEventRecordWithFlags", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuEventRecordWithFlags"},
    Lookup{"cuLibraryLoadData", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuLibraryLoadData"},
    Lookup{"cuGetExportTable", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuGetExportTable"},
    Lookup{"cuGraphCreate", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuGraphCreate"},
    Lookup{"cuMemAdvise", 12000, CU_GET_PROC_ADDRESS_SUCCESS, Unsupported},
    Lookup{"cuMemAdvise", 12020, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemAdvise_v2"},
    Lookup{"cuMemAdvise", 13030, CU_GET_PROC_ADDRESS_SUCCESS, "cuMemAdvise_v2"},
    Lookup{"cuDefinitelyMissing", 13030, CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND, nullptr},
    Lookup{"cudaMalloc", 13030, CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND, nullptr},
    Lookup{"", 13030, CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND, nullptr},
};

bool driverEntryPointQueryWorks() {
  bool valid = true;
  for (const auto flags : {CU_GET_PROC_ADDRESS_DEFAULT, CU_GET_PROC_ADDRESS_PER_THREAD_DEFAULT_STREAM}) {
    for (const auto &lookup : Lookups) {
      void *resolved = reinterpret_cast<void *>(uintptr_t{1});
      auto symbolStatus = static_cast<CUdriverProcAddressQueryResult>(-1);
      const CUresult status = cuGetProcAddress(lookup.symbol, &resolved, lookup.version, flags, &symbolStatus);
      void *expected = lookup.expected ? dlsym(RTLD_DEFAULT, lookup.expected) : nullptr;
      if (status != CUDA_SUCCESS || symbolStatus != lookup.status || resolved != expected || (lookup.expected && !expected)) {
        std::fprintf(stderr, "lookup %s@%d returned status=%d symbol-status=%d resolved=%p, expected %s=%p\n", lookup.symbol,
                     lookup.version, static_cast<int>(status), static_cast<int>(symbolStatus), resolved,
                     lookup.expected ? lookup.expected : "null", expected);
        valid = false;
      }
    }
  }
  const auto legacy = reinterpret_cast<CUresult (*)(const char *, void **, int, cuuint64_t)>(dlsym(RTLD_DEFAULT, "cuGetProcAddress"));
  void *resolved = nullptr;
  if (!legacy || legacy("cuMemAlloc", &resolved, 13030, 0) != CUDA_SUCCESS || resolved != dlsym(RTLD_DEFAULT, "cuMemAlloc_v2")
      || legacy("cuDefinitelyMissing", &resolved, 13030, 0) != CUDA_ERROR_NOT_FOUND) {
    std::fprintf(stderr, "legacy cuGetProcAddress lookup failed\n");
    valid = false;
  }
  return valid;
}

template <class Function> Function entryPoint(const char *symbol) {
  void *resolved = nullptr;
  CUdriverProcAddressQueryResult symbolStatus{};
  return cuGetProcAddress(symbol, &resolved, CUDA_VERSION, CU_GET_PROC_ADDRESS_DEFAULT, &symbolStatus) == CUDA_SUCCESS
                 && symbolStatus == CU_GET_PROC_ADDRESS_SUCCESS
             ? reinterpret_cast<Function>(resolved)
             : nullptr;
}

bool resolvedEntryPointsBehave() {
  const auto attribute = entryPoint<CUresult (*)(int *, CUdevice_attribute, CUdevice)>("cuDeviceGetAttribute");
  const auto memoryInfo = entryPoint<CUresult (*)(size_t *, size_t *)>("cuMemGetInfo");
  const auto errorName = entryPoint<CUresult (*)(CUresult, const char **)>("cuGetErrorName");
  int warp = 0;
  size_t free = 0;
  size_t total = 0;
  const char *name = nullptr;
  return attribute && memoryInfo && errorName && attribute(&warp, CU_DEVICE_ATTRIBUTE_WARP_SIZE, 0) == CUDA_SUCCESS && warp == 32
         && memoryInfo(&free, &total) == CUDA_SUCCESS && total != 0 && errorName(CUDA_ERROR_NOT_FOUND, &name) == CUDA_SUCCESS
         && name == std::string_view{"CUDA_ERROR_NOT_FOUND"};
}

} // namespace

TEST_CASE("CUDA facade fails closed without execution service", "[cuda]") {
  const gemstone_test::Environment noClient(gemstone::environment::RpcClient, nullptr);
  const gemstone_test::Environment noEndpoint(gemstone::environment::RpcEndpoint, nullptr);
  const CUresult status = cuInit(0);
  REQUIRE((status == CUDA_ERROR_NOT_INITIALIZED));
  REQUIRE((targetSelectionWorks()));
  REQUIRE((driverErrorQueryWorks()));
  REQUIRE((driverEntryPointQueryWorks()));
  REQUIRE((resolvedEntryPointsBehave()));
}

TEST_CASE("CUDA runtime stream synchronisation waits only for the requested stream", "[cuda]") {
  CUcontext context = nullptr;
  REQUIRE((cuCtxCreate(&context, nullptr, 0, 0) == CUDA_SUCCESS));
  CUstream blocked = nullptr;
  CUstream idle = nullptr;
  REQUIRE((cuStreamCreate(&blocked, CU_STREAM_NON_BLOCKING) == CUDA_SUCCESS));
  REQUIRE((cuStreamCreate(&idle, CU_STREAM_NON_BLOCKING) == CUDA_SUCCESS));
  std::promise<void> release;
  auto released = release.get_future().share();
  REQUIRE(
      (cuLaunchHostFunc(blocked, [](void *gate) { static_cast<std::shared_future<void> *>(gate)->wait(); }, &released) == CUDA_SUCCESS));
  auto synchronised = std::async(std::launch::async, [context, idle] {
    return cuCtxSetCurrent(context) == CUDA_SUCCESS ? cudaStreamSynchronize(idle) : cudaErrorInvalidValue;
  });
  const bool prompt = synchronised.wait_for(std::chrono::seconds(5)) == std::future_status::ready;
  release.set_value();
  CHECK(prompt);
  CHECK((synchronised.get() == cudaSuccess));
  CHECK((cuStreamSynchronize(blocked) == CUDA_SUCCESS));
  CHECK((cuStreamDestroy(blocked) == CUDA_SUCCESS));
  CHECK((cuStreamDestroy(idle) == CUDA_SUCCESS));
  CHECK((cuCtxDestroy(context) == CUDA_SUCCESS));
}

TEST_CASE("CUDA runtime channel descriptors carry their components", "[cuda]") {
  const auto descriptor = cudaCreateChannelDesc(8, 16, 0, 32, cudaChannelFormatKindFloat);
  CHECK((descriptor.x == 8 && descriptor.y == 16 && descriptor.z == 0 && descriptor.w == 32));
  CHECK((descriptor.f == cudaChannelFormatKindFloat));
}
