#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <limits>
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

constexpr size_t Bytes = 48;

std::vector<uint8_t> pattern(size_t size, uint8_t seed) {
  std::vector<uint8_t> bytes(size);
  for (size_t index = 0; index < size; ++index)
    bytes[index] = static_cast<uint8_t>((index * 37) + seed);
  return bytes;
}

CUarray makeArray(size_t width, size_t height = 0) {
  CUDA_ARRAY3D_DESCRIPTOR descriptor{};
  descriptor.Width = width;
  descriptor.Height = height;
  descriptor.Format = CU_AD_FORMAT_UNSIGNED_INT8;
  descriptor.NumChannels = 1;
  CUarray array = nullptr;
  REQUIRE(cuArray3DCreate_v2(&array, &descriptor) == CUDA_SUCCESS);
  return array;
}

std::vector<uint8_t> readArray(CUarray array, size_t offset, size_t count) {
  std::vector<uint8_t> bytes(count, 0xEE);
  REQUIRE(cuMemcpyAtoH_v2(bytes.data(), array, offset, count) == CUDA_SUCCESS);
  return bytes;
}

} // namespace

TEST_CASE("host to array to host round trips at offset zero and a non-zero offset", "[cuda][memcpy]") {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
  CUarray array = makeArray(Bytes);

  const auto whole = pattern(Bytes, 11);
  REQUIRE(cuMemcpyHtoA_v2(array, 0, whole.data(), Bytes) == CUDA_SUCCESS);
  CHECK(readArray(array, 0, Bytes) == whole);

  const auto window = pattern(16, 99);
  REQUIRE(cuMemcpyHtoA_v2(array, 20, window.data(), window.size()) == CUDA_SUCCESS);
  auto expected = whole;
  std::copy(window.begin(), window.end(), expected.begin() + 20);
  CHECK(readArray(array, 0, Bytes) == expected);
  CHECK(readArray(array, 20, window.size()) == window);

  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("async host to array to host round trips at offset zero and a non-zero offset", "[cuda][memcpy]") {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  CUarray array = makeArray(Bytes);

  const auto whole = pattern(Bytes, 5);
  REQUIRE(cuMemcpyHtoAAsync_v2(array, 0, whole.data(), Bytes, stream) == CUDA_SUCCESS);
  std::vector<uint8_t> readback(Bytes, 0);
  REQUIRE(cuMemcpyAtoHAsync_v2(readback.data(), array, 0, Bytes, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(readback == whole);

  const auto window = pattern(16, 201);
  REQUIRE(cuMemcpyHtoAAsync_v2(array, 20, window.data(), window.size(), stream) == CUDA_SUCCESS);
  std::vector<uint8_t> windowReadback(window.size(), 0);
  REQUIRE(cuMemcpyAtoHAsync_v2(windowReadback.data(), array, 20, windowReadback.size(), stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(windowReadback == window);

  auto expected = whole;
  std::copy(window.begin(), window.end(), expected.begin() + 20);
  CHECK(readArray(array, 0, Bytes) == expected);

  REQUIRE(cuStreamDestroy(stream) == CUDA_SUCCESS);
  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("array copies reject ranges that leave the array", "[cuda][memcpy]") {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  CUarray array = makeArray(Bytes);
  const auto source = pattern(8, 7);
  std::vector<uint8_t> out(8, 0);

  CHECK(cuMemcpyHtoA_v2(array, Bytes - 8, source.data(), 8) == CUDA_SUCCESS);
  CHECK(cuMemcpyHtoA_v2(array, Bytes - 7, source.data(), 8) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemcpyAtoH_v2(out.data(), array, Bytes, 1) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemcpyAtoH_v2(out.data(), array, std::numeric_limits<size_t>::max(), 2) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemcpyAtoHAsync_v2(out.data(), array, Bytes - 1, 2, stream) == CUDA_ERROR_INVALID_VALUE);

  REQUIRE(cuStreamDestroy(stream) == CUDA_SUCCESS);
  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("zero-byte array copies succeed at any offset", "[cuda][memcpy]") {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  CUarray array = makeArray(Bytes);
  const auto source = pattern(8, 7);
  std::vector<uint8_t> out(8, 0);

  for (const size_t offset : {size_t{0}, Bytes, Bytes + 1, size_t{1000}}) {
    CHECK(cuMemcpyHtoA_v2(array, offset, source.data(), 0) == CUDA_SUCCESS);
    CHECK(cuMemcpyAtoH_v2(out.data(), array, offset, 0) == CUDA_SUCCESS);
    CHECK(cuMemcpyHtoAAsync_v2(array, offset, source.data(), 0, stream) == CUDA_SUCCESS);
    CHECK(cuStreamSynchronize(stream) == CUDA_SUCCESS);
    CHECK(cuMemcpyAtoHAsync_v2(out.data(), array, offset, 0, stream) == CUDA_SUCCESS);
    CHECK(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  }
  CHECK(cuMemcpyHtoA_v2(array, 0, nullptr, 0) == CUDA_SUCCESS);
  CHECK(cuMemcpyAtoH_v2(nullptr, array, 0, 0) == CUDA_SUCCESS);

  REQUIRE(cuStreamDestroy(stream) == CUDA_SUCCESS);
  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("array copies on a 2D array accept only zero-byte ranges", "[cuda][memcpy]") {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
  CUarray array = makeArray(33, 3);
  const auto source = pattern(99, 13);
  std::vector<uint8_t> out(99, 0);

  CHECK(cuMemcpyHtoA_v2(array, 0, source.data(), 99) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemcpyAtoH_v2(out.data(), array, 0, 99) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemcpyHtoA_v2(array, 33, source.data(), 33) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemcpyAtoH_v2(out.data(), array, 33, 33) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemcpyHtoA_v2(array, 98, source.data(), 2) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemcpyHtoA_v2(array, 99, source.data(), 1) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemcpyHtoA_v2(array, 99, source.data(), 0) == CUDA_SUCCESS);
  CHECK(cuMemcpyHtoA_v2(array, 100, source.data(), 0) == CUDA_SUCCESS);
  CHECK(cuMemcpyAtoH_v2(out.data(), array, 99, 0) == CUDA_SUCCESS);
  CHECK(cuMemcpyAtoH_v2(out.data(), array, 100, 0) == CUDA_SUCCESS);

  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}
