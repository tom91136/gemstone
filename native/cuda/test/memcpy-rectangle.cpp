#include <cstddef>
#include <cstdint>
#include <cstring>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

extern "C" CUresult cuCtxCreate_v2(CUcontext *context, unsigned flags, CUdevice device);

namespace {

// The facade connects to the execution service once per process, so every case shares this fixture.
gemstone_test::RpcFixture fixture;
const std::string Endpoint = fixture.endpoint();
const gemstone_test::Environment RpcEndpoint(gemstone::environment::RpcEndpoint, Endpoint.c_str());
const gemstone_test::Environment RpcClient(gemstone::environment::RpcClient, "42");

constexpr size_t Width = 6;
constexpr size_t Height = 4;
constexpr size_t Depth = 3;

struct Counts {
  unsigned reads;
  unsigned rectangleReads;
  unsigned rectangleWrites;
};

Counts counts() {
  return {
      .reads = fixture.reads.load(), .rectangleReads = fixture.rectangleReads.load(), .rectangleWrites = fixture.rectangleWrites.load()};
}

// Logical Width x Height x Depth contents, rows packed back to back.
std::vector<uint8_t> packedPattern() {
  std::vector<uint8_t> packed(Width * Height * Depth);
  for (size_t index = 0; index < packed.size(); ++index)
    packed[index] = static_cast<uint8_t>((index * 37) + 11);
  return packed;
}

// Places packed rows at the given pitches in a `size` byte image, leaving the gaps at `fill`.
std::vector<uint8_t> place(const std::vector<uint8_t> &packed, size_t depth, size_t rowPitch, size_t slicePitch, size_t size,
                           uint8_t fill) {
  std::vector<uint8_t> image(size, fill);
  for (size_t z = 0; z < depth; ++z)
    for (size_t y = 0; y < Height; ++y)
      std::memcpy(image.data() + (z * slicePitch) + (y * rowPitch), packed.data() + (((z * Height) + y) * Width), Width);
  return image;
}

std::vector<uint8_t> readDevice(CUdeviceptr device, size_t size) {
  std::vector<uint8_t> bytes(size);
  REQUIRE(cuMemcpyDtoH_v2(bytes.data(), device, size) == CUDA_SUCCESS);
  return bytes;
}

CUdeviceptr allocateFilled(size_t size, uint8_t fill) {
  CUdeviceptr device = 0;
  REQUIRE(cuMemAlloc_v2(&device, size) == CUDA_SUCCESS);
  const std::vector<uint8_t> bytes(size, fill);
  REQUIRE(cuMemcpyHtoD_v2(device, bytes.data(), size) == CUDA_SUCCESS);
  return device;
}

CUDA_MEMCPY3D volume(CUmemorytype sourceType, const void *sourceHost, CUdeviceptr sourceDevice, size_t sourcePitch, size_t sourceHeight,
                     CUmemorytype destinationType, void *destinationHost, CUdeviceptr destinationDevice, size_t destinationPitch,
                     size_t destinationHeight) {
  CUDA_MEMCPY3D copy{};
  copy.srcMemoryType = sourceType;
  copy.srcHost = sourceHost;
  copy.srcDevice = sourceDevice;
  copy.srcPitch = sourcePitch;
  copy.srcHeight = sourceHeight;
  copy.dstMemoryType = destinationType;
  copy.dstHost = destinationHost;
  copy.dstDevice = destinationDevice;
  copy.dstPitch = destinationPitch;
  copy.dstHeight = destinationHeight;
  copy.WidthInBytes = Width;
  copy.Height = Height;
  copy.Depth = Depth;
  return copy;
}

} // namespace

TEST_CASE("pitched 3D copies move each device side as one rectangle request", "[cuda][memcpy]") {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
  const auto packed = packedPattern();

  // Host rows are 10 bytes apart and the source slice is 5 rows, so 50 bytes apart.
  std::vector<uint8_t> host = place(packed, Depth, 10, 50, 160, 0xAA);
  const auto source = volume(CU_MEMORYTYPE_HOST, host.data(), 0, 10, 5, CU_MEMORYTYPE_DEVICE, nullptr, 0, 16, 6);
  const CUdeviceptr device = allocateFilled(256, 0xEE);
  auto copy = source;
  copy.dstDevice = device;
  auto before = counts();
  REQUIRE(cuMemcpy3D_v2(&copy) == CUDA_SUCCESS);
  CHECK(counts().rectangleWrites - before.rectangleWrites == 1);
  CHECK(counts().reads == before.reads);
  CHECK(readDevice(device, 256) == place(packed, Depth, 16, 96, 256, 0xEE));

  std::vector<uint8_t> out(170, 0x11);
  auto download = volume(CU_MEMORYTYPE_DEVICE, nullptr, device, 16, 6, CU_MEMORYTYPE_HOST, out.data(), 0, 12, 5);
  before = counts();
  REQUIRE(cuMemcpy3D_v2(&download) == CUDA_SUCCESS);
  CHECK(counts().rectangleReads - before.rectangleReads == 1);
  CHECK(counts().reads == before.reads);
  CHECK(out == place(packed, Depth, 12, 60, 170, 0x11));

  const CUdeviceptr other = allocateFilled(128, 0x33);
  auto transfer = volume(CU_MEMORYTYPE_DEVICE, nullptr, device, 16, 6, CU_MEMORYTYPE_DEVICE, nullptr, other, 8, 4);
  before = counts();
  REQUIRE(cuMemcpy3D_v2(&transfer) == CUDA_SUCCESS);
  CHECK(counts().rectangleReads - before.rectangleReads == 1);
  CHECK(counts().rectangleWrites - before.rectangleWrites == 1);
  CHECK(counts().reads == before.reads);
  CHECK(readDevice(other, 128) == place(packed, Depth, 8, 32, 128, 0x33));

  REQUIRE(cuMemFree_v2(device) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(other) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("pitched 2D copies move each device side as one rectangle request", "[cuda][memcpy]") {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
  const auto packed = packedPattern();
  const CUdeviceptr device = allocateFilled(64, 0x55);

  std::vector<uint8_t> host = place(packed, 1, 10, 0, 64, 0xAA);
  CUDA_MEMCPY2D upload{};
  upload.srcMemoryType = CU_MEMORYTYPE_HOST;
  upload.srcHost = host.data();
  upload.srcPitch = 10;
  upload.dstMemoryType = CU_MEMORYTYPE_DEVICE;
  upload.dstDevice = device;
  upload.dstPitch = 16;
  upload.WidthInBytes = Width;
  upload.Height = Height;
  auto before = counts();
  REQUIRE(cuMemcpy2D_v2(&upload) == CUDA_SUCCESS);
  CHECK(counts().rectangleWrites - before.rectangleWrites == 1);
  CHECK(readDevice(device, 64) == place(packed, 1, 16, 0, 64, 0x55));

  std::vector<uint8_t> out(60, 0x66);
  CUDA_MEMCPY2D download{};
  download.srcMemoryType = CU_MEMORYTYPE_DEVICE;
  download.srcDevice = device;
  download.srcPitch = 16;
  download.dstMemoryType = CU_MEMORYTYPE_HOST;
  download.dstHost = out.data();
  download.dstPitch = 12;
  download.WidthInBytes = Width;
  download.Height = Height;
  before = counts();
  REQUIRE(cuMemcpy2D_v2(&download) == CUDA_SUCCESS);
  CHECK(counts().rectangleReads - before.rectangleReads == 1);
  CHECK(out == place(packed, 1, 12, 0, 60, 0x66));

  const CUdeviceptr other = allocateFilled(64, 0x77);
  CUDA_MEMCPY2D transfer{};
  transfer.srcMemoryType = CU_MEMORYTYPE_DEVICE;
  transfer.srcDevice = device;
  transfer.srcPitch = 16;
  transfer.dstMemoryType = CU_MEMORYTYPE_DEVICE;
  transfer.dstDevice = other;
  transfer.dstPitch = 8;
  transfer.WidthInBytes = Width;
  transfer.Height = Height;
  before = counts();
  REQUIRE(cuMemcpy2D_v2(&transfer) == CUDA_SUCCESS);
  CHECK(counts().rectangleReads - before.rectangleReads == 1);
  CHECK(counts().rectangleWrites - before.rectangleWrites == 1);
  CHECK(readDevice(other, 64) == place(packed, 1, 8, 0, 64, 0x77));

  REQUIRE(cuMemFree_v2(device) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(other) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("pitched copies from mirrored memory and out-of-range rows keep the row path", "[cuda][memcpy]") {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);

  void *pinned = nullptr;
  REQUIRE(cuMemAllocHost_v2(&pinned, 128) == CUDA_SUCCESS);
  const auto packed = packedPattern();
  std::memcpy(pinned, place(packed, 1, 16, 0, 128, 0x00).data(), 128);
  std::vector<uint8_t> out(60, 0x44);
  CUDA_MEMCPY2D download{};
  download.srcMemoryType = CU_MEMORYTYPE_DEVICE;
  download.srcDevice = reinterpret_cast<CUdeviceptr>(pinned);
  download.srcPitch = 16;
  download.dstMemoryType = CU_MEMORYTYPE_HOST;
  download.dstHost = out.data();
  download.dstPitch = 12;
  download.WidthInBytes = Width;
  download.Height = Height;
  const auto before = counts();
  REQUIRE(cuMemcpy2D_v2(&download) == CUDA_SUCCESS);
  CHECK(counts().rectangleReads == before.rectangleReads);
  CHECK(counts().reads == before.reads);
  CHECK(out == place(packed, 1, 12, 0, 60, 0x44));
  REQUIRE(cuMemFreeHost(pinned) == CUDA_SUCCESS);

  const CUdeviceptr device = allocateFilled(256, 0xEE);
  std::vector<uint8_t> host(160, 0xAA);
  const CUDA_MEMCPY3D overflow = volume(CU_MEMORYTYPE_HOST, host.data(), 0, 10, 5, CU_MEMORYTYPE_DEVICE, nullptr, device, 4096, 6);
  const auto rowsBefore = counts();
  CHECK(cuMemcpy3D_v2(&overflow) == CUDA_ERROR_INVALID_VALUE);
  CHECK(counts().rectangleWrites == rowsBefore.rectangleWrites);
  REQUIRE(cuMemFree_v2(device) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}
