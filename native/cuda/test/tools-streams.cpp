#include <array>
#include <cstddef>
#include <cstdint>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

extern "C" CUresult cuCtxCreate_v2(CUcontext *context, unsigned flags, CUdevice device);

namespace {

gemstone_test::RpcFixture fixture;
const std::string Endpoint = fixture.endpoint();
const gemstone_test::Environment RpcEndpoint(gemstone::environment::RpcEndpoint, Endpoint.c_str());
const gemstone_test::Environment RpcClient(gemstone::environment::RpcClientId, "42");
const gemstone_test::Environment Target(gemstone::environment::TargetArch, "sm_89");

using ExportId = std::array<unsigned char, 16>;

constexpr ExportId ContextId{0x21, 0x31, 0x8c, 0x60, 0x97, 0x14, 0x32, 0x48, 0x8c, 0xa6, 0x41, 0xff, 0x73, 0x24, 0xc8, 0xf2};
constexpr ExportId CopyId{0x35, 0x77, 0x0f, 0x1b, 0x09, 0x2e, 0x03, 0x48, 0xa4, 0x8e, 0x05, 0x6f, 0xc4, 0x23, 0x96, 0x8d};
constexpr ExportId CommandsId{0xa6, 0xb1, 0xff, 0x99, 0xec, 0xc4, 0xc9, 0x4f, 0x92, 0xf9, 0x19, 0x28, 0x66, 0x3d, 0x55, 0x85};
constexpr ExportId AllocationId{0xbf, 0xdb, 0x43, 0x2d, 0xbf, 0x3c, 0x5a, 0x4a, 0x94, 0x5e, 0xb3, 0x40, 0x29, 0xe8, 0x1e, 0x75};

constexpr size_t StreamCreateSlot = 38;
constexpr size_t StreamIdSlot = 39;
constexpr size_t StreamWaitSlot = 1;
constexpr size_t CopySlot = 1;
constexpr size_t AllocateSlot = 10;
constexpr size_t AllocateAsyncSlot = 47;
constexpr size_t EncodeFillSlot = 28;
constexpr size_t SubmitCommandsSlot = 27;
constexpr size_t FillWords = 13;

using StreamCreate = CUresult (*)(CUcontext, uint32_t, uint32_t, uint64_t *);
using StreamId = CUresult (*)(CUcontext, uint64_t, uint64_t *);
using StreamWait = CUresult (*)(CUcontext, uint64_t);
using Copy = CUresult (*)(CUcontext, uint64_t, void *, uint64_t, const void *, size_t);
using Allocate = CUresult (*)(CUcontext, size_t, uint64_t *, void **);
using EncodeFill = CUresult (*)(uint32_t **, CUcontext, uint64_t, uint64_t, uint64_t, size_t, uint64_t);
using SubmitCommands = CUresult (*)(CUcontext, uint32_t, uint64_t, const uint32_t *, uint64_t);

template <class Function> Function slot(const ExportId &id, size_t index) {
  const void *table = nullptr;
  REQUIRE(cuGetExportTable(&table, reinterpret_cast<const CUuuid *>(id.data())) == CUDA_SUCCESS);
  return reinterpret_cast<Function>(reinterpret_cast<uintptr_t>(static_cast<const void *const *>(table)[index]));
}

CUcontext createContext() {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
  return context;
}

uint64_t createStream(CUcontext context, uint32_t mode = 0) {
  uint64_t handle = 0;
  REQUIRE(slot<StreamCreate>(ContextId, StreamCreateSlot)(context, mode, 0, &handle) == CUDA_SUCCESS);
  REQUIRE(handle != 0);
  return handle;
}

struct Allocation {
  uint64_t device;
  void *descriptor;
};

Allocation allocate(CUcontext context, size_t size) {
  Allocation allocation{};
  REQUIRE(slot<Allocate>(AllocationId, AllocateSlot)(context, size, &allocation.device, &allocation.descriptor) == CUDA_SUCCESS);
  REQUIRE(allocation.device != 0);
  REQUIRE(allocation.descriptor != nullptr);
  return allocation;
}

} // namespace

TEST_CASE("CUDA tools streams are created per context with distinct handles and ids", "[cuda][tools]") {
  auto *const context = createContext();
  const auto create = slot<StreamCreate>(ContextId, StreamCreateSlot);
  const auto identify = slot<StreamId>(ContextId, StreamIdSlot);

  const auto first = createStream(context, 0);
  const auto second = createStream(context, 1);
  CHECK(first != second);

  uint64_t firstId = 0xaa;
  uint64_t secondId = 0xaa;
  CHECK(identify(context, first, &firstId) == CUDA_SUCCESS);
  CHECK(identify(context, second, &secondId) == CUDA_SUCCESS);
  CHECK(firstId != 0xaa);
  CHECK(secondId != firstId);

  CUstream publicStream = nullptr;
  REQUIRE(cuStreamCreate(&publicStream, 0) == CUDA_SUCCESS);
  unsigned long long publicId = 0;
  REQUIRE(cuStreamGetId(publicStream, &publicId) == CUDA_SUCCESS);
  CHECK(publicId > secondId);

  uint64_t handle = 0xaa;
  CHECK(create(context, 2, 0, &handle) == CUDA_ERROR_INVALID_VALUE);
  CHECK(handle == 0xaa);
  CHECK(create(nullptr, 0, 0, &handle) == CUDA_ERROR_INVALID_VALUE);
  CHECK(create(context, 0, 0, nullptr) == CUDA_ERROR_INVALID_VALUE);
  CHECK(identify(context, first, nullptr) == CUDA_ERROR_INVALID_VALUE);
}

TEST_CASE("CUDA tools stream ids are refused for another context", "[cuda][tools]") {
  auto *const owner = createContext();
  auto *const other = createContext();
  const auto handle = createStream(owner);
  uint64_t id = 0xaa;
  CHECK(slot<StreamId>(ContextId, StreamIdSlot)(other, handle, &id) == CUDA_ERROR_INVALID_VALUE);
  CHECK(id == 0xaa);
  CHECK(slot<StreamId>(ContextId, StreamIdSlot)(owner, handle, &id) == CUDA_SUCCESS);
}

TEST_CASE("CUDA tools stream wait accepts its own streams repeatedly and refuses others", "[cuda][tools]") {
  auto *const context = createContext();
  const auto wait = slot<StreamWait>(ContextId, StreamWaitSlot);
  const auto handle = createStream(context);
  CHECK(wait(context, handle) == CUDA_SUCCESS);
  CHECK(wait(context, handle) == CUDA_SUCCESS);
  CHECK(wait(nullptr, handle) == CUDA_SUCCESS);
  CHECK(wait(context, 0) == CUDA_ERROR_INVALID_VALUE);

  CUstream publicStream = nullptr;
  REQUIRE(cuStreamCreate(&publicStream, 0) == CUDA_SUCCESS);
  CHECK(wait(context, reinterpret_cast<uintptr_t>(publicStream)) == CUDA_ERROR_INVALID_HANDLE);
}

TEST_CASE("CUDA tools allocations hand out device memory and a descriptor", "[cuda][tools]") {
  auto *const context = createContext();
  const auto allocate = slot<Allocate>(AllocationId, AllocateSlot);

  const auto first = ::allocate(context, 0x1000);
  const auto second = ::allocate(context, 0x1000);
  CHECK(first.device != second.device);
  CHECK(first.descriptor != second.descriptor);

  uint64_t device = 0;
  CHECK(allocate(context, 0x10, &device, nullptr) == CUDA_SUCCESS);
  CHECK(device != 0);
  CHECK(allocate(context, 0, &device, nullptr) == CUDA_SUCCESS);
  CHECK(allocate(context, size_t{1} << 50, &device, nullptr) == CUDA_ERROR_OUT_OF_MEMORY);
  CHECK(allocate(nullptr, 0x10, &device, nullptr) == CUDA_ERROR_INVALID_CONTEXT);
  CHECK(allocate(context, 0x10, nullptr, nullptr) == CUDA_ERROR_UNKNOWN);
}

TEST_CASE("CUDA tools copies land in the allocation and honour its bounds", "[cuda][tools]") {
  auto *const context = createContext();
  const auto copy = slot<Copy>(CopyId, CopySlot);
  const auto wait = slot<StreamWait>(ContextId, StreamWaitSlot);
  const auto stream = createStream(context);
  constexpr size_t bytes = 0x1000;
  const auto allocation = allocate(context, bytes);

  std::vector<uint8_t> source(bytes);
  for (size_t i = 0; i < bytes; ++i)
    source[i] = static_cast<uint8_t>(0x40 + (i % 13));

  CHECK(copy(context, stream, allocation.descriptor, 0, source.data(), bytes) == CUDA_SUCCESS);
  CHECK(wait(context, stream) == CUDA_SUCCESS);
  std::vector<uint8_t> readBack(bytes);
  REQUIRE(cuMemcpyDtoH_v2(readBack.data(), allocation.device, bytes) == CUDA_SUCCESS);
  CHECK(readBack == source);

  std::vector<uint8_t> patch(0x10, 0xee);
  CHECK(copy(context, stream, allocation.descriptor, 0xff0, patch.data(), patch.size()) == CUDA_SUCCESS);
  CHECK(wait(context, stream) == CUDA_SUCCESS);
  REQUIRE(cuMemcpyDtoH_v2(readBack.data(), allocation.device, bytes) == CUDA_SUCCESS);
  CHECK(readBack[0xfef] == source[0xfef]);
  CHECK(readBack[0xff0] == 0xee);
  CHECK(readBack[0xfff] == 0xee);

  CHECK(copy(context, stream, allocation.descriptor, 0, source.data(), 0) == CUDA_SUCCESS);
  CHECK(copy(context, stream, allocation.descriptor, 0, source.data(), bytes + 1) == CUDA_ERROR_INVALID_VALUE);
  CHECK(copy(context, stream, allocation.descriptor, 0x10, source.data(), bytes - 0xf) == CUDA_ERROR_INVALID_VALUE);
  CHECK(copy(context, stream, allocation.descriptor, 0x2000, source.data(), 0x10) == CUDA_ERROR_INVALID_VALUE);
  CHECK(copy(context, stream, allocation.descriptor, 0, nullptr, 0x10) == CUDA_ERROR_INVALID_VALUE);
  CHECK(copy(context, 0, allocation.descriptor, 0, source.data(), 0x10) == CUDA_ERROR_INVALID_VALUE);
  CHECK(copy(nullptr, stream, allocation.descriptor, 0, source.data(), 0x10) == CUDA_ERROR_INVALID_CONTEXT);
  CHECK(copy(context, stream, source.data(), 0, source.data(), 0x10) == CUDA_ERROR_INVALID_HANDLE);
}

TEST_CASE("CUDA tools asynchronous allocations match the plain ones", "[cuda][tools]") {
  auto *const context = createContext();
  const auto allocate = slot<Allocate>(AllocationId, AllocateAsyncSlot);
  uint64_t device = 0;
  void *descriptor = nullptr;
  CHECK(allocate(context, 0x1000, &device, &descriptor) == CUDA_SUCCESS);
  CHECK(device != 0);
  CHECK(descriptor != nullptr);
  CHECK(allocate(nullptr, 0x10, &device, nullptr) == CUDA_ERROR_INVALID_CONTEXT);
  CHECK(allocate(context, 0x10, nullptr, nullptr) == CUDA_ERROR_UNKNOWN);

  const auto stream = createStream(context);
  std::vector<uint8_t> source(0x1000, 0x5a);
  CHECK(slot<Copy>(CopyId, CopySlot)(context, stream, descriptor, 0, source.data(), source.size()) == CUDA_SUCCESS);
}

TEST_CASE("CUDA tools encoded fills zero the allocation when submitted", "[cuda][tools]") {
  auto *const context = createContext();
  const auto encode = slot<EncodeFill>(CommandsId, EncodeFillSlot);
  const auto submit = slot<SubmitCommands>(CommandsId, SubmitCommandsSlot);
  const auto wait = slot<StreamWait>(ContextId, StreamWaitSlot);
  const auto stream = createStream(context);
  constexpr size_t bytes = 0x400;
  const auto allocation = allocate(context, bytes);

  std::vector<uint8_t> ones(bytes, 0x11);
  REQUIRE(cuMemcpyHtoD_v2(allocation.device, ones.data(), bytes) == CUDA_SUCCESS);

  std::array<uint32_t, 32> words{};
  auto *cursor = words.data();
  CHECK(encode(&cursor, context, allocation.device, 0, 1, bytes, 2) == CUDA_SUCCESS);
  CHECK(cursor == words.data() + FillWords);

  CHECK(submit(context, 1, stream, words.data(), FillWords) == CUDA_SUCCESS);
  CHECK(wait(context, stream) == CUDA_SUCCESS);
  std::vector<uint8_t> readBack(bytes, 0xff);
  REQUIRE(cuMemcpyDtoH_v2(readBack.data(), allocation.device, bytes) == CUDA_SUCCESS);
  CHECK(readBack == std::vector<uint8_t>(bytes, 0));

  CHECK(submit(context, 1, 0, words.data(), FillWords) == CUDA_ERROR_INVALID_VALUE);
  CHECK(submit(context, 1, stream, nullptr, FillWords) == CUDA_ERROR_INVALID_VALUE);
  CHECK(submit(nullptr, 1, stream, words.data(), FillWords) == CUDA_ERROR_INVALID_CONTEXT);
  CHECK(submit(context, 1, stream, words.data(), 0) == CUDA_SUCCESS);
  std::array<uint32_t, FillWords> unknown{};
  CHECK(submit(context, 1, stream, unknown.data(), FillWords) == CUDA_ERROR_INVALID_VALUE);
}
