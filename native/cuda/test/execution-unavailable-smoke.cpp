#include <array>
#include <chrono>
#include <cstdint>
#include <cstdio>
#include <future>
#include <string_view>

#include <dlfcn.h>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>
#include <cuda_runtime_api.h>

#include <gemstone/test_support.hpp>

#include <environment_keys.hpp>

namespace {

struct TargetCase {
  const char *value;
  int major;
  int minor;
};

bool targetSelectionWorks() {
  constexpr std::array cases{
      TargetCase{.value = "sm_75", .major = 7, .minor = 5},
      TargetCase{.value = "compute_89", .major = 8, .minor = 9},
      TargetCase{.value = "sm89", .major = 8, .minor = 9},
      TargetCase{.value = "compute90", .major = 9, .minor = 0},
  };
  bool valid = true;
  for (const auto &target : cases) {
    const gemstone_test::Environment targetArch(gemstone::environment::TargetArch, target.value);
    int major = -1;
    int minor = -1;
    const CUresult status = cuDeviceComputeCapability(&major, &minor, 0);
    if (status != CUDA_SUCCESS || major != target.major || minor != target.minor) {
      std::fprintf(stderr, "target case %s returned status=%d capability=%d.%d, expected %d.%d\n", target.value, static_cast<int>(status),
                   major, minor, target.major, target.minor);
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
    Lookup{.symbol = "cuMemAlloc", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAlloc_v2"},
    Lookup{.symbol = "cuMemAlloc", .version = 3010, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = Unsupported},
    Lookup{.symbol = "cuMemAlloc_v2", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAlloc_v2"},
    Lookup{.symbol = "cuArrayCreate", .version = 3010, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuArrayCreate"},
    Lookup{.symbol = "cuArrayCreate", .version = 3020, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuArrayCreate_v2"},
    Lookup{.symbol = "cuArrayCreate", .version = 0, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuArrayCreate_v2"},
    Lookup{.symbol = "cuModuleGetGlobal", .version = 2000, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuModuleGetGlobal"},
    Lookup{.symbol = "cuModuleGetGlobal", .version = 12000, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuModuleGetGlobal_v2"},
    Lookup{.symbol = "cuMemcpy2D", .version = 3000, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemcpy2D"},
    Lookup{.symbol = "cuMemcpy2D", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemcpy2D_v2"},
    Lookup{.symbol = "cuDeviceTotalMem", .version = 3010, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuDeviceTotalMem"},
    Lookup{.symbol = "cuDeviceTotalMem", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuDeviceTotalMem_v2"},
    Lookup{.symbol = "cuMemcpyHtoD", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemcpyHtoD_v2"},
    Lookup{.symbol = "cuMemcpyHtoDAsync", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemcpyHtoDAsync_v2"},
    Lookup{.symbol = "cuMemsetD8", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemsetD8_v2"},
    Lookup{.symbol = "cuMemAllocPitch", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAllocPitch_v2"},
    Lookup{.symbol = "cuMemHostGetDevicePointer",
           .version = 13030,
           .status = CU_GET_PROC_ADDRESS_SUCCESS,
           .expected = "cuMemHostGetDevicePointer_v2"},
    Lookup{
        .symbol = "cuMemGetAddressRange", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemGetAddressRange_v2"},
    Lookup{.symbol = "cuCtxDestroy", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxDestroy_v2"},
    Lookup{.symbol = "cuEventDestroy", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuEventDestroy_v2"},
    Lookup{.symbol = "cuEventElapsedTime", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuEventElapsedTime_v2"},
    Lookup{.symbol = "cuLinkCreate", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLinkCreate_v2"},
    Lookup{.symbol = "cuLinkAddData", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLinkAddData_v2"},
    Lookup{.symbol = "cuCtxCreate_v2", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxCreate_v2"},
    Lookup{.symbol = "cuCtxCreate", .version = 11030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxCreate"},
    Lookup{.symbol = "cuCtxCreate", .version = 11040, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxCreate_v3"},
    Lookup{.symbol = "cuCtxCreate", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxCreate_v4"},
    Lookup{.symbol = "cuGetProcAddress", .version = 11030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuGetProcAddress"},
    Lookup{.symbol = "cuGetProcAddress", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuGetProcAddress_v2"},
    Lookup{.symbol = "cuCtxSynchronize", .version = 12080, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxSynchronize"},
    Lookup{.symbol = "cuCtxSynchronize", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxSynchronize_v2"},
    Lookup{.symbol = "cuMemAllocHost", .version = 3010, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAllocHost"},
    Lookup{.symbol = "cuMemAllocHost", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAllocHost_v2"},
    Lookup{.symbol = "cuLaunchHostFunc", .version = 13000, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLaunchHostFunc"},
    Lookup{.symbol = "cuLaunchHostFunc", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLaunchHostFunc_v2"},
    Lookup{.symbol = "cuCtxDestroy", .version = 3020, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = Unsupported},
    Lookup{.symbol = "cuLaunchKernelEx", .version = 11000, .status = CU_GET_PROC_ADDRESS_VERSION_NOT_SUFFICIENT, .expected = nullptr},
    Lookup{.symbol = "cuStreamDestroy", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuStreamDestroy_v2"},
    Lookup{.symbol = "cuMemcpyHtoD_ptds", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemcpyHtoD_v2"},
    Lookup{.symbol = "cuStreamQuery_ptsz", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuStreamQuery"},
    Lookup{.symbol = "cuGetProcAddress_v2", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuGetProcAddress_v2"},
    Lookup{.symbol = "cuLaunchKernel", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLaunchKernel"},
    Lookup{.symbol = "cuLaunchKernel_ptsz", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLaunchKernel_ptsz"},
    Lookup{.symbol = "cuLaunchKernelEx", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLaunchKernelEx"},
    Lookup{.symbol = "cuStreamGetCaptureInfo", .version = 10000, .status = CU_GET_PROC_ADDRESS_VERSION_NOT_SUFFICIENT, .expected = nullptr},
    Lookup{
        .symbol = "cuStreamGetCaptureInfo", .version = 10010, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuStreamGetCaptureInfo"},
    Lookup{.symbol = "cuStreamGetCaptureInfo",
           .version = 11030,
           .status = CU_GET_PROC_ADDRESS_SUCCESS,
           .expected = "cuStreamGetCaptureInfo_v2"},
    Lookup{.symbol = "cuStreamGetCaptureInfo",
           .version = 12030,
           .status = CU_GET_PROC_ADDRESS_SUCCESS,
           .expected = "cuStreamGetCaptureInfo_v3"},
    Lookup{.symbol = "cuStreamGetCaptureInfo_ptsz",
           .version = 13030,
           .status = CU_GET_PROC_ADDRESS_SUCCESS,
           .expected = "cuStreamGetCaptureInfo_v3"},
    Lookup{.symbol = "cuStreamGetCaptureInfo_v2_ptsz",
           .version = 13030,
           .status = CU_GET_PROC_ADDRESS_SUCCESS,
           .expected = "cuStreamGetCaptureInfo_v2"},
    Lookup{
        .symbol = "cuStreamIsCapturing_ptsz", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuStreamIsCapturing"},
    Lookup{.symbol = "cuMemAllocAsync", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAllocAsync"},
    Lookup{.symbol = "cuMemsetD8Async", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemsetD8Async"},
    Lookup{
        .symbol = "cuEventRecordWithFlags", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuEventRecordWithFlags"},
    Lookup{.symbol = "cuLibraryLoadData", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLibraryLoadData"},
    Lookup{.symbol = "cuGetExportTable", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuGetExportTable"},
    Lookup{.symbol = "cuGraphCreate", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuGraphCreate"},
    Lookup{.symbol = "cuMemAdvise", .version = 12000, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = Unsupported},
    Lookup{.symbol = "cuMemAdvise", .version = 12020, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAdvise_v2"},
    Lookup{.symbol = "cuMemAdvise", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAdvise_v2"},
    Lookup{.symbol = "cuDefinitelyMissing", .version = 13030, .status = CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND, .expected = nullptr},
    Lookup{.symbol = "cudaMalloc", .version = 13030, .status = CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND, .expected = nullptr},
    Lookup{.symbol = "", .version = 13030, .status = CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND, .expected = nullptr},
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
