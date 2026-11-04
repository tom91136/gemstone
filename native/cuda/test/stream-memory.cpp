#include <algorithm>
#include <atomic>
#include <chrono>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <string>
#include <thread>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

extern "C" CUresult cuCtxCreate_v2(CUcontext *context, unsigned flags, CUdevice device);
extern "C" CUresult cuProfilerStart();
extern "C" CUresult cuProfilerStop();

namespace {

// The facade connects to the execution service once per process, so every case shares this fixture.
gemstone_test::RpcFixture fixture;
const std::string Endpoint = fixture.endpoint();
const gemstone_test::Environment RpcEndpoint(gemstone::environment::RpcEndpoint, Endpoint.c_str());
const gemstone_test::Environment RpcClient(gemstone::environment::RpcClientId, "42");

CUcontext createContext() {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
  return context;
}

std::vector<uint8_t> readDevice(CUdeviceptr device, size_t size) {
  std::vector<uint8_t> bytes(size);
  REQUIRE(cuMemcpyDtoH_v2(bytes.data(), device, size) == CUDA_SUCCESS);
  return bytes;
}

// Writes `value` in `width` elements of each row, `pitch` bytes apart, leaving the padding untouched.
template <class T> void setRows(std::vector<uint8_t> &image, size_t pitch, T value, size_t width, size_t height) {
  for (size_t row = 0; row < height; ++row)
    for (size_t column = 0; column < width; ++column)
      std::memcpy(image.data() + (row * pitch) + (column * sizeof(T)), &value, sizeof(T));
}

uint64_t poolUsage(CUmemoryPool pool, CUmemPool_attribute attribute) {
  uint64_t bytes = 0;
  REQUIRE(cuMemPoolGetAttribute(pool, attribute, &bytes) == CUDA_SUCCESS);
  return bytes;
}

void CUDA_CB delayStream(void *flag) {
  std::this_thread::sleep_for(std::chrono::milliseconds(150));
  static_cast<std::atomic<bool> *>(flag)->store(true);
}

} // namespace

TEST_CASE("pitched memset writes each row and leaves padding", "[cuda][memset]") {
  auto *const context = createContext();
  constexpr size_t pitch = 16;
  constexpr size_t width = 6;
  constexpr size_t height = 4;
  constexpr size_t size = pitch * height;
  CUdeviceptr device = 0;
  REQUIRE(cuMemAlloc_v2(&device, size) == CUDA_SUCCESS);
  REQUIRE(cuMemsetD8_v2(device, 0xEE, size) == CUDA_SUCCESS);

  std::vector<uint8_t> expected(size, 0xEE);
  REQUIRE(cuMemsetD2D8_v2(device, pitch, 0x5A, width, height) == CUDA_SUCCESS);
  setRows<uint8_t>(expected, pitch, 0x5A, width, height);
  CHECK(readDevice(device, size) == expected);

  REQUIRE(cuMemsetD2D32_v2(device, pitch, 0xA1B2C3D4U, 2, 2) == CUDA_SUCCESS);
  setRows<uint32_t>(expected, pitch, 0xA1B2C3D4U, 2, 2);
  CHECK(readDevice(device, size) == expected);

  REQUIRE(cuMemsetD2D16_v2(device, pitch, 0x1234, 3, 1) == CUDA_SUCCESS);
  setRows<uint16_t>(expected, pitch, 0x1234, 3, 1);
  CHECK(readDevice(device, size) == expected);

  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  REQUIRE(cuMemsetD2D8Async(device, pitch, 0x7F, width, height, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  setRows<uint8_t>(expected, pitch, 0x7F, width, height);
  CHECK(readDevice(device, size) == expected);

  CHECK(cuMemsetD2D8_v2(device, width - 1, 0, width, height) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemsetD2D8_v2(device, pitch, 0, 0, height) == CUDA_SUCCESS);

  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(device) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("stream context reports the context that created the stream", "[cuda][stream]") {
  auto *const first = createContext();
  CUcontext owner = nullptr;
  REQUIRE(cuStreamGetCtx(nullptr, &owner) == CUDA_SUCCESS);
  CHECK(owner == first);
  REQUIRE(cuStreamGetCtx(CU_STREAM_LEGACY, &owner) == CUDA_SUCCESS);
  CHECK(owner == first);
  REQUIRE(cuStreamGetCtx(CU_STREAM_PER_THREAD, &owner) == CUDA_SUCCESS);
  CHECK(owner == first);
  CHECK(cuStreamGetCtx(nullptr, nullptr) == CUDA_ERROR_INVALID_VALUE);

  CUstream firstStream = nullptr;
  REQUIRE(cuStreamCreate(&firstStream, 0) == CUDA_SUCCESS);

  CUcontext second = nullptr;
  REQUIRE(cuCtxCreate_v2(&second, 0, 0) == CUDA_SUCCESS);
  CUstream secondStream = nullptr;
  REQUIRE(cuStreamCreate(&secondStream, 0) == CUDA_SUCCESS);

  owner = nullptr;
  REQUIRE(cuStreamGetCtx(firstStream, &owner) == CUDA_SUCCESS);
  CHECK(owner == first);
  REQUIRE(cuStreamGetCtx(secondStream, &owner) == CUDA_SUCCESS);
  CHECK(owner == second);

  REQUIRE(cuStreamDestroy_v2(firstStream) == CUDA_SUCCESS);
  CHECK(cuStreamGetCtx(firstStream, &owner) == CUDA_ERROR_INVALID_HANDLE);

  REQUIRE(cuStreamDestroy_v2(secondStream) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(second) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(first) == CUDA_SUCCESS);
}

TEST_CASE("memory pool accounting follows allocation, free and trim", "[cuda][memory-pool]") {
  auto *const context = createContext();
  constexpr size_t bytes = static_cast<size_t>(256 * 1024);

  CUmemPoolProps props{};
  props.allocType = CU_MEM_ALLOCATION_TYPE_PINNED;
  props.handleTypes = CU_MEM_HANDLE_TYPE_NONE;
  props.location.type = CU_MEM_LOCATION_TYPE_DEVICE;
  props.location.id = 0;
  CUmemoryPool pool = nullptr;
  REQUIRE(cuMemPoolCreate(&pool, &props) == CUDA_SUCCESS);
  CUmemoryPool defaultPool = nullptr;
  REQUIRE(cuDeviceGetDefaultMemPool(&defaultPool, 0) == CUDA_SUCCESS);
  CHECK(defaultPool != pool);

  uint64_t threshold = ~uint64_t{0};
  REQUIRE(cuMemPoolSetAttribute(pool, CU_MEMPOOL_ATTR_RELEASE_THRESHOLD, &threshold) == CUDA_SUCCESS);
  CHECK(poolUsage(pool, CU_MEMPOOL_ATTR_RELEASE_THRESHOLD) == threshold);

  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  CUdeviceptr pointer = 0;
  REQUIRE(cuMemAllocFromPoolAsync(&pointer, bytes, pool, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(poolUsage(pool, CU_MEMPOOL_ATTR_USED_MEM_CURRENT) >= bytes);
  CHECK(poolUsage(pool, CU_MEMPOOL_ATTR_USED_MEM_HIGH) >= bytes);
  CHECK(poolUsage(pool, CU_MEMPOOL_ATTR_RESERVED_MEM_CURRENT) >= bytes);

  REQUIRE(cuMemFreeAsync(pointer, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(poolUsage(pool, CU_MEMPOOL_ATTR_USED_MEM_CURRENT) == 0);
  CHECK(poolUsage(pool, CU_MEMPOOL_ATTR_USED_MEM_HIGH) >= bytes);
  CHECK(poolUsage(pool, CU_MEMPOOL_ATTR_RESERVED_MEM_CURRENT) >= bytes);
  CHECK(poolUsage(pool, CU_MEMPOOL_ATTR_RESERVED_MEM_HIGH) >= bytes);

  REQUIRE(cuMemPoolTrimTo(pool, 0) == CUDA_SUCCESS);
  CHECK(poolUsage(pool, CU_MEMPOOL_ATTR_RESERVED_MEM_CURRENT) == 0);
  CHECK(poolUsage(pool, CU_MEMPOOL_ATTR_RESERVED_MEM_HIGH) >= bytes);

  const auto defaultUsed = poolUsage(defaultPool, CU_MEMPOOL_ATTR_USED_MEM_CURRENT);
  REQUIRE(cuMemAllocAsync(&pointer, bytes, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(poolUsage(defaultPool, CU_MEMPOOL_ATTR_USED_MEM_CURRENT) == defaultUsed + bytes);
  REQUIRE(cuMemFreeAsync(pointer, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(poolUsage(defaultPool, CU_MEMPOOL_ATTR_USED_MEM_CURRENT) == defaultUsed);

  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuMemPoolDestroy(pool) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("host-visible allocations are zero filled when recycled", "[cuda][host]") {
  auto *const context = createContext();
  constexpr size_t byteCount = 1 << 18;
  enum class Kind { Pinned, HostAlloc, Managed };
  for (const auto kind : {Kind::Pinned, Kind::HostAlloc, Kind::Managed}) {
    for (int round = 0; round < 3; ++round) {
      void *memory = nullptr;
      CUdeviceptr managed = 0;
      switch (kind) {
        case Kind::Pinned: REQUIRE(cuMemAllocHost_v2(&memory, byteCount) == CUDA_SUCCESS); break;
        case Kind::HostAlloc: REQUIRE(cuMemHostAlloc(&memory, byteCount, CU_MEMHOSTALLOC_DEVICEMAP) == CUDA_SUCCESS); break;
        case Kind::Managed:
          REQUIRE(cuMemAllocManaged(&managed, byteCount, CU_MEM_ATTACH_GLOBAL) == CUDA_SUCCESS);
          memory = reinterpret_cast<void *>(managed);
          break;
      }
      const auto *bytes = static_cast<const uint8_t *>(memory);
      CHECK(std::all_of(bytes, bytes + byteCount, [](uint8_t byte) { return byte == 0; }));
      std::memset(memory, 0xA5, byteCount);
      if (kind == Kind::Managed) REQUIRE(cuMemFree_v2(managed) == CUDA_SUCCESS);
      else REQUIRE(cuMemFreeHost(memory) == CUDA_SUCCESS);
    }
  }
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("profiler start and stop succeed without an attached profiler", "[cuda][profiler]") {
  auto *const context = createContext();
  CHECK(cuProfilerStart() == CUDA_SUCCESS);
  CHECK(cuProfilerStop() == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("managed attach validates the attach flag and orders on the stream", "[cuda][stream][managed]") {
  auto *const context = createContext();
  constexpr size_t size = 64;
  CUdeviceptr managed = 0;
  REQUIRE(cuMemAllocManaged(&managed, size, CU_MEM_ATTACH_GLOBAL) == CUDA_SUCCESS);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);

  CHECK(cuStreamAttachMemAsync(stream, managed, size, 0) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuStreamAttachMemAsync(stream, managed, size, CU_MEM_ATTACH_GLOBAL | CU_MEM_ATTACH_HOST) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuStreamAttachMemAsync(stream, managed, size, 8) == CUDA_ERROR_INVALID_VALUE);

  REQUIRE(cuMemsetD8Async(managed, 0x66, size, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamAttachMemAsync(stream, managed, size, CU_MEM_ATTACH_GLOBAL) == CUDA_SUCCESS);
  REQUIRE(cuStreamAttachMemAsync(stream, managed, size, CU_MEM_ATTACH_HOST) == CUDA_SUCCESS);
  REQUIRE(cuStreamAttachMemAsync(stream, managed, size, CU_MEM_ATTACH_SINGLE) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(readDevice(managed, size) == std::vector<uint8_t>(size, 0x66));

  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(managed) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("async copy into pageable memory completes before returning", "[cuda][stream]") {
  auto *const context = createContext();
  constexpr size_t bytes = 64;
  CUdeviceptr device = 0;
  REQUIRE(cuMemAlloc_v2(&device, bytes) == CUDA_SUCCESS);
  REQUIRE(cuMemsetD8_v2(device, 0x3C, bytes) == CUDA_SUCCESS);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);

  std::atomic<bool> delayed{false};
  REQUIRE(cuLaunchHostFunc(stream, delayStream, &delayed) == CUDA_SUCCESS);
  std::vector<uint8_t> pageable(bytes, 0);
  REQUIRE(cuMemcpyDtoHAsync_v2(pageable.data(), device, bytes, stream) == CUDA_SUCCESS);
  CHECK(delayed.load());
  CHECK(pageable == std::vector<uint8_t>(bytes, 0x3C));

  delayed.store(false);
  REQUIRE(cuLaunchHostFunc(stream, delayStream, &delayed) == CUDA_SUCCESS);
  void *pinned = nullptr;
  REQUIRE(cuMemAllocHost_v2(&pinned, bytes) == CUDA_SUCCESS);
  REQUIRE(cuMemcpyDtoHAsync_v2(pinned, device, bytes, stream) == CUDA_SUCCESS);
  CHECK_FALSE(delayed.load());
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(static_cast<const uint8_t *>(pinned)[bytes - 1] == 0x3C);

  REQUIRE(cuMemFreeHost(pinned) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(device) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("async copy from pageable memory is staged before returning", "[cuda][stream]") {
  auto *const context = createContext();
  constexpr size_t bytes = 64;
  CUdeviceptr device = 0;
  REQUIRE(cuMemAlloc_v2(&device, bytes) == CUDA_SUCCESS);
  REQUIRE(cuMemsetD8_v2(device, 0, bytes) == CUDA_SUCCESS);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);

  std::atomic<bool> delayed{false};
  REQUIRE(cuLaunchHostFunc(stream, delayStream, &delayed) == CUDA_SUCCESS);
  std::vector<uint8_t> pageable(bytes, 0x11);
  REQUIRE(cuMemcpyHtoDAsync_v2(device, pageable.data(), bytes, stream) == CUDA_SUCCESS);
  std::fill(pageable.begin(), pageable.end(), 0xFF);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(readDevice(device, bytes) == std::vector<uint8_t>(bytes, 0x11));

  REQUIRE(cuMemsetD8_v2(device, 0, bytes) == CUDA_SUCCESS);
  REQUIRE(cuLaunchHostFunc(stream, delayStream, &delayed) == CUDA_SUCCESS);
  REQUIRE(cuMemcpyAsync(device, reinterpret_cast<CUdeviceptr>(pageable.data()), bytes, stream) == CUDA_SUCCESS);
  std::fill(pageable.begin(), pageable.end(), 0x22);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(readDevice(device, bytes) == std::vector<uint8_t>(bytes, 0xFF));

  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(device) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}
