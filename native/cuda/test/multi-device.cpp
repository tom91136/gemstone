#include <cstdint>
#include <cstring>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>
#include <cuda_runtime_api.h>

#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

extern "C" CUresult cuCtxCreate_v2(CUcontext *context, unsigned flags, CUdevice device);

namespace {

gemstone_test::RpcFixture fixture;
const std::string Endpoint = fixture.endpoint();
const gemstone_test::Environment RpcEndpoint(gemstone::environment::RpcEndpoint, Endpoint.c_str());
const gemstone_test::Environment RpcClient(gemstone::environment::RpcClientId, "42");
const gemstone_test::Environment DeviceCount(gemstone::environment::DeviceCount, "2");

CUcontext createContext(CUdevice device) {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, device) == CUDA_SUCCESS);
  return context;
}

size_t freeBytes() {
  size_t available = 0;
  size_t total = 0;
  REQUIRE(cuMemGetInfo_v2(&available, &total) == CUDA_SUCCESS);
  return available;
}

} // namespace

TEST_CASE("CUDA enumerates the configured number of distinct devices", "[cuda][multi-device]") {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  int count = 0;
  REQUIRE(cuDeviceGetCount(&count) == CUDA_SUCCESS);
  CHECK(count == 2);
  CUdevice first = -1;
  CUdevice second = -1;
  REQUIRE(cuDeviceGet(&first, 0) == CUDA_SUCCESS);
  REQUIRE(cuDeviceGet(&second, 1) == CUDA_SUCCESS);
  CHECK(second == 1);
  CHECK(cuDeviceGet(&second, 2) == CUDA_ERROR_INVALID_DEVICE);

  CUuuid firstUuid{};
  CUuuid secondUuid{};
  REQUIRE(cuDeviceGetUuid_v2(&firstUuid, first) == CUDA_SUCCESS);
  REQUIRE(cuDeviceGetUuid_v2(&secondUuid, second) == CUDA_SUCCESS);
  CHECK(std::memcmp(firstUuid.bytes, secondUuid.bytes, sizeof(firstUuid.bytes)) != 0);

  char busIds[2][32]{};
  REQUIRE(cuDeviceGetPCIBusId(busIds[0], sizeof(busIds[0]), first) == CUDA_SUCCESS);
  REQUIRE(cuDeviceGetPCIBusId(busIds[1], sizeof(busIds[1]), second) == CUDA_SUCCESS);
  CHECK(std::string(busIds[0]) != std::string(busIds[1]));
  CUdevice found = -1;
  REQUIRE(cuDeviceGetByPCIBusId(&found, busIds[1]) == CUDA_SUCCESS);
  CHECK(found == 1);
}

TEST_CASE("CUDA contexts and allocations belong to their device", "[cuda][multi-device]") {
  auto *const first = createContext(0);
  auto *const second = createContext(1);
  CUdevice device = -1;
  REQUIRE(cuCtxGetDevice(&device) == CUDA_SUCCESS);
  CHECK(device == 1);
  REQUIRE(cuCtxSetCurrent(first) == CUDA_SUCCESS);
  REQUIRE(cuCtxGetDevice(&device) == CUDA_SUCCESS);
  CHECK(device == 0);

  const auto untouched = freeBytes();
  REQUIRE(cuCtxSetCurrent(second) == CUDA_SUCCESS);
  CUdeviceptr buffer = 0;
  REQUIRE(cuMemAlloc_v2(&buffer, size_t{1} << 20) == CUDA_SUCCESS);
  CHECK(freeBytes() == untouched - (size_t{1} << 20));
  int ordinal = -1;
  REQUIRE(cuPointerGetAttribute(&ordinal, CU_POINTER_ATTRIBUTE_DEVICE_ORDINAL, buffer) == CUDA_SUCCESS);
  CHECK(ordinal == 1);
  REQUIRE(cuCtxSetCurrent(first) == CUDA_SUCCESS);
  CHECK(freeBytes() == untouched);
  REQUIRE(cuCtxSetCurrent(second) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(buffer) == CUDA_SUCCESS);
  CHECK(freeBytes() == untouched);
  CHECK(cuCtxDestroy_v2(second) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(first) == CUDA_SUCCESS);
}

TEST_CASE("CUDA peer access is directed and reports the driver's errors", "[cuda][multi-device]") {
  auto *const first = createContext(0);
  auto *const second = createContext(1);
  int can = -1;
  REQUIRE(cuDeviceCanAccessPeer(&can, 0, 1) == CUDA_SUCCESS);
  CHECK(can == 1);
  REQUIRE(cuDeviceCanAccessPeer(&can, 1, 1) == CUDA_SUCCESS);
  CHECK(can == 0);

  REQUIRE(cuCtxSetCurrent(first) == CUDA_SUCCESS);
  CHECK(cuCtxDisablePeerAccess(second) == CUDA_ERROR_PEER_ACCESS_NOT_ENABLED);
  CHECK(cuCtxEnablePeerAccess(first, 0) == CUDA_ERROR_INVALID_DEVICE);
  CHECK(cuCtxEnablePeerAccess(second, 1) == CUDA_ERROR_INVALID_VALUE);
  REQUIRE(cuCtxEnablePeerAccess(second, 0) == CUDA_SUCCESS);
  CHECK(cuCtxEnablePeerAccess(second, 0) == CUDA_ERROR_PEER_ACCESS_ALREADY_ENABLED);
  REQUIRE(cuCtxSetCurrent(second) == CUDA_SUCCESS);
  CHECK(cuCtxDisablePeerAccess(first) == CUDA_ERROR_PEER_ACCESS_NOT_ENABLED);
  REQUIRE(cuCtxSetCurrent(first) == CUDA_SUCCESS);
  CHECK(cuCtxDisablePeerAccess(second) == CUDA_SUCCESS);
  CHECK(cuCtxDisablePeerAccess(second) == CUDA_ERROR_PEER_ACCESS_NOT_ENABLED);
  CHECK(cuCtxDestroy_v2(second) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(first) == CUDA_SUCCESS);
}

TEST_CASE("CUDA copies bytes between devices", "[cuda][multi-device]") {
  auto *const first = createContext(0);
  auto *const second = createContext(1);
  std::vector<uint8_t> input(4096);
  for (size_t index = 0; index < input.size(); ++index)
    input[index] = static_cast<uint8_t>((index * 7) + 3);

  REQUIRE(cuCtxSetCurrent(first) == CUDA_SUCCESS);
  CUdeviceptr source = 0;
  REQUIRE(cuMemAlloc_v2(&source, input.size()) == CUDA_SUCCESS);
  REQUIRE(cuMemcpyHtoD_v2(source, input.data(), input.size()) == CUDA_SUCCESS);
  REQUIRE(cuCtxSetCurrent(second) == CUDA_SUCCESS);
  CUdeviceptr destination = 0;
  REQUIRE(cuMemAlloc_v2(&destination, input.size()) == CUDA_SUCCESS);
  REQUIRE(cuMemcpyPeer(destination, second, source, first, input.size()) == CUDA_SUCCESS);

  std::vector<uint8_t> output(input.size());
  REQUIRE(cuMemcpyDtoH_v2(output.data(), destination, output.size()) == CUDA_SUCCESS);
  CHECK(output == input);
  CHECK(cuMemcpyPeer(destination, nullptr, source, first, input.size()) == CUDA_ERROR_INVALID_CONTEXT);

  REQUIRE(cuMemFree_v2(destination) == CUDA_SUCCESS);
  REQUIRE(cuCtxSetCurrent(first) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(source) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(second) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(first) == CUDA_SUCCESS);
}

TEST_CASE("CUDA runtime routes calls to the selected device", "[cuda][multi-device]") {
  int count = 0;
  REQUIRE(cudaGetDeviceCount(&count) == cudaSuccess);
  CHECK(count == 2);
  CHECK(cudaSetDevice(2) == cudaErrorInvalidDevice);
  CHECK(cudaSetDevice(-1) == cudaErrorInvalidDevice);

  REQUIRE(cudaSetDevice(1) == cudaSuccess);
  int device = -1;
  REQUIRE(cudaGetDevice(&device) == cudaSuccess);
  CHECK(device == 1);
  void *remote = nullptr;
  REQUIRE(cudaMalloc(&remote, 256) == cudaSuccess);
  cudaPointerAttributes attributes{};
  REQUIRE(cudaPointerGetAttributes(&attributes, remote) == cudaSuccess);
  CHECK(attributes.device == 1);

  REQUIRE(cudaSetDevice(0) == cudaSuccess);
  REQUIRE(cudaGetDevice(&device) == cudaSuccess);
  CHECK(device == 0);
  void *local = nullptr;
  REQUIRE(cudaMalloc(&local, 256) == cudaSuccess);
  REQUIRE(cudaPointerGetAttributes(&attributes, local) == cudaSuccess);
  CHECK(attributes.device == 0);

  CHECK(cudaDeviceDisablePeerAccess(1) == cudaErrorPeerAccessNotEnabled);
  REQUIRE(cudaDeviceEnablePeerAccess(1, 0) == cudaSuccess);
  CHECK(cudaDeviceEnablePeerAccess(1, 0) == cudaErrorPeerAccessAlreadyEnabled);
  CHECK(cudaDeviceEnablePeerAccess(0, 0) == cudaErrorInvalidDevice);
  int can = 0;
  REQUIRE(cudaDeviceCanAccessPeer(&can, 0, 1) == cudaSuccess);
  CHECK(can == 1);

  const std::vector<uint8_t> input(256, 0x5a);
  REQUIRE(cudaMemcpy(local, input.data(), input.size(), cudaMemcpyHostToDevice) == cudaSuccess);
  REQUIRE(cudaMemcpyPeer(remote, 1, local, 0, input.size()) == cudaSuccess);
  std::vector<uint8_t> output(input.size());
  REQUIRE(cudaMemcpy(output.data(), remote, output.size(), cudaMemcpyDeviceToHost) == cudaSuccess);
  CHECK(output == input);
  REQUIRE(cudaDeviceDisablePeerAccess(1) == cudaSuccess);
  CHECK(cudaFree(local) == cudaSuccess);
  CHECK(cudaFree(remote) == cudaSuccess);
}
