#include <array>
#include <cstdio>
#include <cstdlib>
#include <string_view>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

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
  unsetenv("GEMSTONE_TARGET_ARCH");
  for (const auto &target : cases) {
    if (setenv("GEMSTONE_TARGET_ARCH", target.value.data(), 1) != 0) {
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

bool driverEntryPointQueryWorks() {
  struct EntryPoint {
    std::string_view name;
    void *abi;
  };
  const std::array entry_points{
      EntryPoint{"cuMemAllocAsync", reinterpret_cast<void *>(cuMemAllocAsync)},
      EntryPoint{"cuMemAllocFromPoolAsync", reinterpret_cast<void *>(cuMemAllocFromPoolAsync)},
      EntryPoint{"cuMemFreeAsync", reinterpret_cast<void *>(cuMemFreeAsync)},
      EntryPoint{"cuMemAllocPitch", reinterpret_cast<void *>(cuMemAllocPitch)},
      EntryPoint{"cuMemcpyHtoDAsync", reinterpret_cast<void *>(cuMemcpyHtoDAsync)},
      EntryPoint{"cuMemcpyDtoHAsync", reinterpret_cast<void *>(cuMemcpyDtoHAsync)},
      EntryPoint{"cuMemcpyDtoDAsync", reinterpret_cast<void *>(cuMemcpyDtoDAsync)},
      EntryPoint{"cuMemsetD8Async", reinterpret_cast<void *>(cuMemsetD8Async)},
      EntryPoint{"cuStreamSynchronize", reinterpret_cast<void *>(cuStreamSynchronize)},
      EntryPoint{"cuStreamQuery", reinterpret_cast<void *>(cuStreamQuery)},
      EntryPoint{"cuEventCreate", reinterpret_cast<void *>(cuEventCreate)},
      EntryPoint{"cuEventDestroy", reinterpret_cast<void *>(cuEventDestroy)},
      EntryPoint{"cuEventElapsedTime", reinterpret_cast<void *>(cuEventElapsedTime)},
      EntryPoint{"cuEventQuery", reinterpret_cast<void *>(cuEventQuery)},
      EntryPoint{"cuEventRecord", reinterpret_cast<void *>(cuEventRecord)},
      EntryPoint{"cuEventRecordWithFlags", reinterpret_cast<void *>(cuEventRecordWithFlags)},
      EntryPoint{"cuEventSynchronize", reinterpret_cast<void *>(cuEventSynchronize)},
      EntryPoint{"cuStreamWaitEvent", reinterpret_cast<void *>(cuStreamWaitEvent)},
      EntryPoint{"cuModuleGetTexRef", reinterpret_cast<void *>(cuModuleGetTexRef)},
      EntryPoint{"cuModuleGetSurfRef", reinterpret_cast<void *>(cuModuleGetSurfRef)},
      EntryPoint{"cuModuleGetLoadingMode", reinterpret_cast<void *>(cuModuleGetLoadingMode)},
      EntryPoint{"cuLibraryLoadData", reinterpret_cast<void *>(cuLibraryLoadData)},
      EntryPoint{"cuLibraryGetKernel", reinterpret_cast<void *>(cuLibraryGetKernel)},
      EntryPoint{"cuLibraryGetModule", reinterpret_cast<void *>(cuLibraryGetModule)},
      EntryPoint{"cuLibraryUnload", reinterpret_cast<void *>(cuLibraryUnload)},
      EntryPoint{"cuDevSmResourceSplit", reinterpret_cast<void *>(cuDevSmResourceSplit)},
  };
  bool valid = true;
  for (const auto &entry : entry_points) {
    void *resolved = nullptr;
    CUdriverProcAddressQueryResult symbol_status = CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND;
    const CUresult status = cuGetProcAddress(entry.name.data(), &resolved, CUDA_VERSION, CU_GET_PROC_ADDRESS_DEFAULT, &symbol_status);
    if (status != CUDA_SUCCESS || symbol_status != CU_GET_PROC_ADDRESS_SUCCESS || resolved != entry.abi) {
      std::fprintf(stderr, "driver entry point %s returned status=%d symbol-status=%d abi=%p resolved=%p\n", entry.name.data(),
                   static_cast<int>(status), static_cast<int>(symbol_status), entry.abi, resolved);
      valid = false;
    }
  }
  return valid;
}

} // namespace

TEST_CASE("CUDA facade fails closed without execution service", "[cuda]") {
  unsetenv("GEMSTONE_RPC_CLIENT");
  unsetenv("GEMSTONE_RPC_PORT");
  const CUresult status = cuInit(0);
  REQUIRE((status == CUDA_ERROR_NOT_INITIALIZED));
  REQUIRE((targetSelectionWorks()));
  REQUIRE((driverErrorQueryWorks()));
  REQUIRE((driverEntryPointQueryWorks()));
}
