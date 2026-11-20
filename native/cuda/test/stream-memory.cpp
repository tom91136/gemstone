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

// cuda.h maps these names to their _v2 entry points; declare the unversioned exports.
#undef cuMemAdvise
#undef cuMemPrefetchAsync
extern "C" CUresult cuMemPrefetchAsync(CUdeviceptr devPtr, size_t count, CUdevice device, CUstream stream);
extern "C" CUresult cuMemAdvise(CUdeviceptr devPtr, size_t count, CUmem_advise advice, CUdevice device);

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

TEST_CASE("stream flags report whether the stream is non-blocking", "[cuda][stream]") {
  auto *const context = createContext();
  unsigned flags = ~0U;
  REQUIRE(cuStreamGetFlags(nullptr, &flags) == CUDA_SUCCESS);
  CHECK(flags == 0);

  CUstream blocking = nullptr;
  REQUIRE(cuStreamCreate(&blocking, 0) == CUDA_SUCCESS);
  CUstream nonBlocking = nullptr;
  REQUIRE(cuStreamCreate(&nonBlocking, CU_STREAM_NON_BLOCKING) == CUDA_SUCCESS);

  REQUIRE(cuStreamGetFlags(blocking, &flags) == CUDA_SUCCESS);
  CHECK(flags == 0);
  REQUIRE(cuStreamGetFlags(nonBlocking, &flags) == CUDA_SUCCESS);
  CHECK(flags == CU_STREAM_NON_BLOCKING);
  CHECK(cuStreamGetFlags(blocking, nullptr) == CUDA_ERROR_INVALID_VALUE);

  REQUIRE(cuStreamDestroy_v2(blocking) == CUDA_SUCCESS);
  CHECK(cuStreamGetFlags(blocking, &flags) == CUDA_ERROR_INVALID_HANDLE);

  REQUIRE(cuStreamDestroy_v2(nonBlocking) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
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

TEST_CASE("graph memory trim succeeds on device 0 and rejects ordinals past the device count", "[cuda][memory-pool]") {
  auto *const context = createContext();
  int count = 0;
  REQUIRE(cuDeviceGetCount(&count) == CUDA_SUCCESS);
  CHECK(cuDeviceGraphMemTrim(0) == CUDA_SUCCESS);
  CHECK(cuDeviceGraphMemTrim(count) == CUDA_ERROR_INVALID_DEVICE);
  CHECK(cuDeviceGraphMemTrim(-1) == CUDA_ERROR_INVALID_DEVICE);
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

TEST_CASE("freeing a null host pointer succeeds", "[cuda][host]") {
  auto *const context = createContext();
  CHECK(cuMemFreeHost(nullptr) == CUDA_SUCCESS);
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

TEST_CASE("stream id is 7 for the null and legacy streams", "[cuda][stream]") {
  auto *const context = createContext();
  unsigned long long id = 0;
  CHECK(cuStreamGetId(nullptr, &id) == CUDA_SUCCESS);
  CHECK(id == 7);
  CHECK(cuStreamGetId(CU_STREAM_LEGACY, &id) == CUDA_SUCCESS);
  CHECK(id == 7);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("created and per-thread stream ids are distinct and stable", "[cuda][stream]") {
  auto *const context = createContext();
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  unsigned long long createdId = 0;
  unsigned long long repeatedId = 0;
  unsigned long long perThreadId = 0;
  REQUIRE(cuStreamGetId(stream, &createdId) == CUDA_SUCCESS);
  CHECK(createdId >= 13);
  REQUIRE(cuStreamGetId(stream, &repeatedId) == CUDA_SUCCESS);
  CHECK(repeatedId == createdId);
  REQUIRE(cuStreamGetId(CU_STREAM_PER_THREAD, &perThreadId) == CUDA_SUCCESS);
  CHECK(perThreadId >= 13);
  CHECK(perThreadId != createdId);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("SYNC_MEMOPS pointer attribute is accepted on device and managed pointers", "[cuda][memory]") {
  auto *const context = createContext();
  constexpr size_t size = 64;
  CUdeviceptr device = 0;
  CUdeviceptr managed = 0;
  REQUIRE(cuMemAlloc_v2(&device, size) == CUDA_SUCCESS);
  REQUIRE(cuMemAllocManaged(&managed, size, CU_MEM_ATTACH_GLOBAL) == CUDA_SUCCESS);
  int enabled = 1;
  CHECK(cuPointerSetAttribute(&enabled, CU_POINTER_ATTRIBUTE_SYNC_MEMOPS, device) == CUDA_SUCCESS);
  enabled = 0;
  CHECK(cuPointerSetAttribute(&enabled, CU_POINTER_ATTRIBUTE_SYNC_MEMOPS, managed) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(managed) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(device) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("cuMemPrefetchAsync v1 checks the device and then the memory kind", "[cuda][memory][managed]") {
  auto *const context = createContext();
  constexpr size_t size = 4096;
  CUdeviceptr managed = 0;
  CUdeviceptr device = 0;
  REQUIRE(cuMemAllocManaged(&managed, size, CU_MEM_ATTACH_GLOBAL) == CUDA_SUCCESS);
  REQUIRE(cuMemAlloc_v2(&device, size) == CUDA_SUCCESS);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  CHECK(cuMemPrefetchAsync(managed, size, 0, stream) == CUDA_SUCCESS);
  CHECK(cuMemPrefetchAsync(managed, size, CU_DEVICE_CPU, stream) == CUDA_SUCCESS);
  CHECK(cuMemPrefetchAsync(managed, size, 1, stream) == CUDA_ERROR_INVALID_DEVICE);
  CHECK(cuMemPrefetchAsync(managed, size, 99, stream) == CUDA_ERROR_INVALID_DEVICE);
  CHECK(cuMemPrefetchAsync(device, size, 0, stream) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemPrefetchAsync(device, size, 1, stream) == CUDA_ERROR_INVALID_DEVICE);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(device) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(managed) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("cuMemAdvise v1 checks the advice, the device for location advices and the memory kind", "[cuda][memory][managed]") {
  auto *const context = createContext();
  constexpr size_t size = 4096;
  CUdeviceptr managed = 0;
  CUdeviceptr device = 0;
  REQUIRE(cuMemAllocManaged(&managed, size, CU_MEM_ATTACH_GLOBAL) == CUDA_SUCCESS);
  REQUIRE(cuMemAlloc_v2(&device, size) == CUDA_SUCCESS);
  for (const auto advice : {CU_MEM_ADVISE_SET_READ_MOSTLY, CU_MEM_ADVISE_UNSET_READ_MOSTLY, CU_MEM_ADVISE_UNSET_PREFERRED_LOCATION}) {
    CHECK(cuMemAdvise(managed, size, advice, 0) == CUDA_SUCCESS);
    CHECK(cuMemAdvise(managed, size, advice, CU_DEVICE_CPU) == CUDA_SUCCESS);
    CHECK(cuMemAdvise(managed, size, advice, 1) == CUDA_SUCCESS);
    CHECK(cuMemAdvise(managed, size, advice, 99) == CUDA_SUCCESS);
  }
  for (const auto advice : {CU_MEM_ADVISE_SET_PREFERRED_LOCATION, CU_MEM_ADVISE_SET_ACCESSED_BY, CU_MEM_ADVISE_UNSET_ACCESSED_BY}) {
    CHECK(cuMemAdvise(managed, size, advice, 0) == CUDA_SUCCESS);
    CHECK(cuMemAdvise(managed, size, advice, CU_DEVICE_CPU) == CUDA_SUCCESS);
    CHECK(cuMemAdvise(managed, size, advice, 1) == CUDA_ERROR_INVALID_DEVICE);
    CHECK(cuMemAdvise(managed, size, advice, 99) == CUDA_ERROR_INVALID_DEVICE);
  }
  CHECK(cuMemAdvise(managed, size, static_cast<CUmem_advise>(0), 0) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemAdvise(managed, size, static_cast<CUmem_advise>(7), 0) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemAdvise(managed, size, static_cast<CUmem_advise>(99), 0) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemAdvise(device, size, CU_MEM_ADVISE_SET_READ_MOSTLY, 1) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemAdvise(device, size, CU_MEM_ADVISE_SET_PREFERRED_LOCATION, 1) == CUDA_ERROR_INVALID_DEVICE);
  CHECK(cuMemAdvise(device, size, CU_MEM_ADVISE_SET_ACCESSED_BY, 99) == CUDA_ERROR_INVALID_DEVICE);
  REQUIRE(cuMemFree_v2(device) == CUDA_SUCCESS);
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

namespace {

struct Observation {
  const std::atomic<bool> *produced;
  std::atomic<bool> seen{false};
};

void CUDA_CB observe(void *state) {
  auto *observation = static_cast<Observation *>(state);
  observation->seen.store(observation->produced->load());
}

struct Drain {
  Drain() = default;
  Drain(const Drain &) = delete;
  Drain &operator=(const Drain &) = delete;
  ~Drain() { cuCtxSynchronize(); }
};

} // namespace

TEST_CASE("context event captures the work submitted before it is recorded", "[cuda][context][event]") {
  auto *const context = createContext();
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  CUevent event = nullptr;
  REQUIRE(cuEventCreate(&event, 0) == CUDA_SUCCESS);

  std::atomic<bool> beforeOnStream{false};
  std::atomic<bool> beforeOnDefault{false};
  std::atomic<bool> afterOnStream{false};
  const Drain drain{};
  REQUIRE(cuLaunchHostFunc(stream, delayStream, &beforeOnStream) == CUDA_SUCCESS);
  REQUIRE(cuLaunchHostFunc(nullptr, delayStream, &beforeOnDefault) == CUDA_SUCCESS);
  REQUIRE(cuCtxRecordEvent(context, event) == CUDA_SUCCESS);
  REQUIRE(cuLaunchHostFunc(stream, delayStream, &afterOnStream) == CUDA_SUCCESS);

  CHECK(cuEventQuery(event) == CUDA_ERROR_NOT_READY);
  REQUIRE(cuEventSynchronize(event) == CUDA_SUCCESS);
  CHECK(beforeOnStream.load());
  CHECK(beforeOnDefault.load());
  CHECK_FALSE(afterOnStream.load());

  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(afterOnStream.load());
  REQUIRE(cuEventDestroy_v2(event) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("context event ignores work submitted to other contexts", "[cuda][context][event]") {
  auto *const first = createContext();
  CUevent event = nullptr;
  REQUIRE(cuEventCreate(&event, 0) == CUDA_SUCCESS);
  CUcontext second = nullptr;
  REQUIRE(cuCtxCreate_v2(&second, 0, 0) == CUDA_SUCCESS);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);

  std::atomic<bool> delayed{false};
  const Drain drain{};
  REQUIRE(cuLaunchHostFunc(stream, delayStream, &delayed) == CUDA_SUCCESS);
  REQUIRE(cuCtxRecordEvent(first, event) == CUDA_SUCCESS);
  CHECK(cuEventQuery(event) == CUDA_SUCCESS);
  CHECK_FALSE(delayed.load());

  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(delayed.load());
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuEventDestroy_v2(event) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(second) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(first) == CUDA_SUCCESS);
}

TEST_CASE("context event rejects events of other contexts and destroyed contexts", "[cuda][context][event]") {
  auto *const first = createContext();
  CUevent recorded = nullptr;
  REQUIRE(cuEventCreate(&recorded, 0) == CUDA_SUCCESS);
  CUcontext second = nullptr;
  REQUIRE(cuCtxCreate_v2(&second, 0, 0) == CUDA_SUCCESS);
  CHECK(cuCtxRecordEvent(second, recorded) == CUDA_ERROR_INVALID_HANDLE);

  REQUIRE(cuEventDestroy_v2(recorded) == CUDA_SUCCESS);
  CHECK(cuCtxRecordEvent(first, recorded) == CUDA_ERROR_INVALID_HANDLE);
  CHECK(cuCtxWaitEvent(first, recorded) == CUDA_ERROR_INVALID_HANDLE);

  CUevent event = nullptr;
  REQUIRE(cuEventCreate(&event, 0) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(first) == CUDA_SUCCESS);
  CHECK(cuCtxRecordEvent(first, event) == CUDA_ERROR_INVALID_CONTEXT);
  CHECK(cuCtxWaitEvent(first, event) == CUDA_ERROR_INVALID_CONTEXT);
  REQUIRE(cuEventDestroy_v2(event) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(second) == CUDA_SUCCESS);
}

TEST_CASE("context record is unsupported while a stream of the context captures", "[cuda][context][event]") {
  auto *const context = createContext();
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  CUevent event = nullptr;
  REQUIRE(cuEventCreate(&event, 0) == CUDA_SUCCESS);

  REQUIRE(cuStreamBeginCapture_v2(stream, CU_STREAM_CAPTURE_MODE_RELAXED) == CUDA_SUCCESS);
  CHECK(cuCtxRecordEvent(context, event) == CUDA_ERROR_STREAM_CAPTURE_UNSUPPORTED);
  CUgraph graph = nullptr;
  CHECK(cuStreamEndCapture(stream, &graph) == CUDA_ERROR_STREAM_CAPTURE_INVALIDATED);

  REQUIRE(cuEventDestroy_v2(event) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("context wait is unsupported while a stream of the context captures", "[cuda][context][event]") {
  auto *const context = createContext();
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  CUevent event = nullptr;
  REQUIRE(cuEventCreate(&event, 0) == CUDA_SUCCESS);

  REQUIRE(cuStreamBeginCapture_v2(stream, CU_STREAM_CAPTURE_MODE_RELAXED) == CUDA_SUCCESS);
  CHECK(cuCtxWaitEvent(context, event) == CUDA_ERROR_STREAM_CAPTURE_UNSUPPORTED);
  CUgraph graph = nullptr;
  CHECK(cuStreamEndCapture(stream, &graph) == CUDA_ERROR_STREAM_CAPTURE_INVALIDATED);

  REQUIRE(cuEventDestroy_v2(event) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("context wait is unsupported for an event captured by another context", "[cuda][context][event]") {
  auto *const capturing = createContext();
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  CUevent event = nullptr;
  REQUIRE(cuEventCreate(&event, 0) == CUDA_SUCCESS);

  REQUIRE(cuStreamBeginCapture_v2(stream, CU_STREAM_CAPTURE_MODE_RELAXED) == CUDA_SUCCESS);
  REQUIRE(cuEventRecord(event, stream) == CUDA_SUCCESS);
  CUcontext other = nullptr;
  REQUIRE(cuCtxCreate_v2(&other, 0, 0) == CUDA_SUCCESS);
  CHECK(cuCtxWaitEvent(other, event) == CUDA_ERROR_STREAM_CAPTURE_UNSUPPORTED);
  CUgraph graph = nullptr;
  CHECK(cuStreamEndCapture(stream, &graph) == CUDA_ERROR_STREAM_CAPTURE_INVALIDATED);

  REQUIRE(cuEventDestroy_v2(event) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(other) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(capturing) == CUDA_SUCCESS);
}

TEST_CASE("context wait orders existing and later streams after the recorded work", "[cuda][context][event]") {
  auto *const producer = createContext();
  CUstream produce = nullptr;
  REQUIRE(cuStreamCreate(&produce, 0) == CUDA_SUCCESS);
  std::atomic<bool> produced{false};
  Observation existingSeen{.produced = &produced};
  Observation laterSeen{.produced = &produced};
  const Drain drain{};
  REQUIRE(cuLaunchHostFunc(produce, delayStream, &produced) == CUDA_SUCCESS);
  CUevent event = nullptr;
  REQUIRE(cuEventCreate(&event, 0) == CUDA_SUCCESS);
  REQUIRE(cuCtxRecordEvent(producer, event) == CUDA_SUCCESS);

  auto *const consumer = createContext();
  CUstream existing = nullptr;
  REQUIRE(cuStreamCreate(&existing, 0) == CUDA_SUCCESS);
  REQUIRE(cuCtxWaitEvent(consumer, event) == CUDA_SUCCESS);
  CHECK_FALSE(produced.load());

  CUstream later = nullptr;
  REQUIRE(cuStreamCreate(&later, 0) == CUDA_SUCCESS);
  REQUIRE(cuLaunchHostFunc(existing, observe, &existingSeen) == CUDA_SUCCESS);
  REQUIRE(cuLaunchHostFunc(later, observe, &laterSeen) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(existing) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(later) == CUDA_SUCCESS);
  CHECK(existingSeen.seen.load());
  CHECK(laterSeen.seen.load());

  REQUIRE(cuStreamDestroy_v2(later) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(existing) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(consumer) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(produce) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(produce) == CUDA_SUCCESS);
  REQUIRE(cuEventDestroy_v2(event) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(producer) == CUDA_SUCCESS);
}
