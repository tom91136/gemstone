#include <array>

#include <dlfcn.h>

#include <catch2/catch_test_macros.hpp>

namespace {

// Resolved by libomptarget's CUDA plugin (LLVM 22.1.8, dynamic_cuda/cuda.cpp). The plugin treats every entry as
// mandatory, so one missing symbol makes cuInit fail and the device count zero.
constexpr std::array PluginSymbols{
    "cuInit",
    "cuCtxGetDevice",
    "cuDeviceGet",
    "cuDeviceGetAttribute",
    "cuDeviceGetCount",
    "cuFuncGetAttribute",
    "cuFuncSetAttribute",
    "cuDeviceGetName",
    "cuDeviceGetUuid",
    "cuDeviceTotalMem_v2",
    "cuDriverGetVersion",
    "cuGetErrorString",
    "cuLaunchKernel",
    "cuLaunchHostFunc",
    "cuMemAlloc_v2",
    "cuMemAllocHost",
    "cuMemAllocManaged",
    "cuMemAllocAsync",
    "cuMemcpyDtoDAsync_v2",
    "cuMemcpyDtoH_v2",
    "cuMemcpyDtoHAsync_v2",
    "cuMemcpyHtoD_v2",
    "cuMemcpyHtoDAsync_v2",
    "cuMemsetD8Async",
    "cuMemsetD16Async",
    "cuMemsetD32Async",
    "cuMemsetD2D8Async",
    "cuMemsetD2D16Async",
    "cuMemsetD2D32Async",
    "cuMemFree_v2",
    "cuMemFreeHost",
    "cuMemFreeAsync",
    "cuModuleGetFunction",
    "cuModuleGetGlobal_v2",
    "cuModuleUnload",
    "cuStreamCreate",
    "cuStreamDestroy_v2",
    "cuStreamSynchronize",
    "cuStreamQuery",
    "cuStreamAddCallback",
    "cuCtxSetCurrent",
    "cuDevicePrimaryCtxRelease_v2",
    "cuDevicePrimaryCtxGetState",
    "cuDevicePrimaryCtxSetFlags_v2",
    "cuDevicePrimaryCtxRetain",
    "cuModuleLoadDataEx",
    "cuOccupancyMaxPotentialBlockSize",
    "cuFuncGetParamInfo",
    "cuDeviceCanAccessPeer",
    "cuCtxEnablePeerAccess",
    "cuMemcpyPeerAsync",
    "cuCtxGetLimit",
    "cuCtxSetLimit",
    "cuEventCreate",
    "cuEventRecord",
    "cuEventQuery",
    "cuStreamWaitEvent",
    "cuEventSynchronize",
    "cuEventDestroy",
    "cuMemUnmap",
    "cuMemRelease",
    "cuMemAddressFree",
    "cuMemGetInfo",
    "cuMemAddressReserve",
    "cuMemMap",
    "cuMemCreate",
    "cuMemSetAccess",
    "cuMemGetAllocationGranularity",
};

} // namespace

TEST_CASE("CUDA driver exports every symbol the libomptarget plugin resolves", "[cuda]") {
  for (const char *symbol : PluginSymbols) {
    INFO(symbol);
    CHECK(dlsym(RTLD_DEFAULT, symbol) != nullptr);
  }
}
