#include <array>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <stdexcept>
#include <string>
#include <string_view>
#include <utility>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <cuda.h>
#include <cuda_fp16.h>
#include <cuda_runtime_api.h>

#include "conformance/device.hpp"
#include "conformance/environment_keys.hpp"

#include "memory_files.hpp"
#include "test_support.hpp"

using namespace aspartame;

namespace {
void check(CUresult status, const char *operation) {
  if (status != CUDA_SUCCESS) throw std::runtime_error(std::string(operation) + " failed with CUDA status " + std::to_string(status));
}

struct DeviceBuffer {
  CUdeviceptr value{};
  explicit DeviceBuffer(size_t bytes) { check(cuMemAlloc(&value, bytes), "cuMemAlloc"); }
  ~DeviceBuffer() {
    if (value) (void)cuMemFree(value);
  }
  DeviceBuffer(const DeviceBuffer &) = delete;
  DeviceBuffer &operator=(const DeviceBuffer &) = delete;
};

// The embedded image follows CONFORMANCE_CUDA_CODE_MODE, so a launcher whose strict code mode disagrees rejects it.
CUmodule loadModule() {
  const auto code = image("mma884.ptx");
  const char *mode = std::getenv(conformance::environment::CudaCodeMode);
  CUmodule module{};
  check(cuModuleLoadData(&module, code.data()), mode && std::string_view(mode) == "sass"
                                                    ? "cuModuleLoadData of the SASS image"
                                                    : "cuModuleLoadData of the PTX image (CONFORMANCE_CUDA_CODE_MODE is not sass)");
  return module;
}

uint16_t halfBits(int value) {
  const auto half = __int2half_rn(value);
  uint16_t bits{};
  std::memcpy(&bits, &half, sizeof(bits));
  return bits;
}
using unified::support::floatBits;
uint32_t tile(unsigned lane) { return lane % 16 / 4; }
std::pair<unsigned, unsigned> aCoordinate(unsigned lane, unsigned item, bool col) {
  const auto thread = lane % 4;
  const auto high = lane < 16 ? 0U : 4U;
  return col ? std::pair{(item % 4) + high, thread} : std::pair{thread + high, item};
}
std::pair<unsigned, unsigned> bCoordinate(unsigned lane, unsigned item, bool col) {
  const auto thread = lane % 4;
  const auto high = lane < 16 ? 0U : 4U;
  return col ? std::pair{item, thread + high} : std::pair{thread, item + high};
}
std::pair<unsigned, unsigned> cCoordinate(unsigned lane, unsigned item, bool f32) {
  if (f32) return {(lane & 1) + (item & 2) + (lane < 16 ? 0U : 4U), (item & 4) + (lane & 2) + (item & 1)};
  return {(lane % 4) + (lane < 16 ? 0U : 4U), item};
}

void runVariant(const char *accum, const char *aLayout, const char *bLayout, const char *mode, CUmodule module) {
  const bool f32 = std::string(accum) == "f32";
  const bool aCol = std::string(aLayout) == "col";
  const bool bCol = std::string(bLayout) == "col";
  const bool alias = std::string(mode) != "plain";
  const bool zeroC = std::string(mode) == "zeroalias";
  const unsigned registers = f32 ? 8 : 4;
  std::array<int, size_t{4} * 8 * 4> a{};
  std::array<int, size_t{4} * 4 * 8> b{};
  std::array<uint32_t, 64> aw{};
  std::array<uint32_t, 64> bw{};
  for (unsigned lane = 0; lane < 32; ++lane)
    for (unsigned item = 0; item < 4; ++item) {
      const auto group = tile(lane);
      const auto [ar, ac] = aCoordinate(lane, item, aCol);
      const auto [br, bc] = bCoordinate(lane, item, bCol);
      const int av = static_cast<int>(((group * 5) + (ar * 3) + (ac * 2)) % 7) - 3;
      const int bv = static_cast<int>(((group * 3) + (br * 2) + (bc * 5)) % 7) - 3;
      a[(((group * 8) + ar) * 4) + ac] = av;
      b[(((group * 4) + br) * 8) + bc] = bv;
      aw[(lane * 2) + (item / 2)] |= static_cast<uint32_t>(halfBits(av)) << ((item % 2) * 16);
      bw[(lane * 2) + (item / 2)] |= static_cast<uint32_t>(halfBits(bv)) << ((item % 2) * 16);
    }
  std::array<uint32_t, 256> cwords{};
  std::array<uint32_t, 256> expected{};
  std::array<int, size_t{4} * 8 * 8> c{};
  for (unsigned lane = 0; lane < 32; ++lane)
    for (unsigned item = 0; item < 8; ++item) {
      const auto group = tile(lane);
      const auto [row, col] = cCoordinate(lane, item, f32);
      int value = 0;
      if (!zeroC)
        value = f32 ? static_cast<int>((group * 19) + (row * 3)) - static_cast<int>(col * 2)
                    : static_cast<int>((group * 7) + row) - static_cast<int>(col);
      c[(((group * 8) + row) * 8) + col] = value;
      const auto word = (lane * registers) + (f32 ? item : item / 2);
      if (f32) cwords[word] = floatBits(static_cast<float>(value));
      else cwords[word] |= static_cast<uint32_t>(halfBits(value)) << ((item % 2) * 16);
    }
  for (unsigned group = 0; group < 4; ++group)
    for (unsigned row = 0; row < 8; ++row)
      for (unsigned col = 0; col < 8; ++col) {
        int value = c[(((group * 8) + row) * 8) + col];
        for (unsigned k = 0; k < 4; ++k)
          value += a[(((group * 8) + row) * 4) + k] * b[(((group * 4) + k) * 8) + col];
        c[(((group * 8) + row) * 8) + col] = value;
      }
  for (unsigned lane = 0; lane < 32; ++lane)
    for (unsigned item = 0; item < 8; ++item) {
      const auto [row, col] = cCoordinate(lane, item, f32);
      const auto value = c[(((tile(lane) * 8) + row) * 8) + col];
      const auto word = (lane * registers) + (f32 ? item : item / 2);
      if (f32) expected[word] = floatBits(static_cast<float>(value));
      else expected[word] |= static_cast<uint32_t>(halfBits(value)) << ((item % 2) * 16);
    }
  std::array<uint32_t, 256> output{};
  const auto bytes = static_cast<size_t>(registers) * 32 * sizeof(uint32_t);
  DeviceBuffer da(sizeof(aw));
  DeviceBuffer db(sizeof(bw));
  DeviceBuffer dc(bytes);
  DeviceBuffer dd(bytes);
  check(cuMemcpyHtoD(da.value, aw.data(), sizeof(aw)), "upload A");
  check(cuMemcpyHtoD(db.value, bw.data(), sizeof(bw)), "upload B");
  check(cuMemcpyHtoD(dc.value, cwords.data(), bytes), "upload C");
  check(cuMemcpyHtoD(dd.value, cwords.data(), bytes), "initialise D");
  CUfunction function{};
  const auto name = std::string("mma884_") + accum + "_" + aLayout + "_" + bLayout + "_" + mode;
  check(cuModuleGetFunction(&function, module, name.c_str()), "cuModuleGetFunction");
  auto aPtr = da.value;
  auto bPtr = db.value;
  auto cPtr = dc.value;
  auto dPtr = alias ? dc.value : dd.value;
  void *arguments[] = {&aPtr, &bPtr, &cPtr, &dPtr};
  check(cuLaunchKernel(function, 1, 1, 1, 32, 1, 1, 0, nullptr, arguments, nullptr), "cuLaunchKernel");
  check(cuCtxSynchronize(), "cuCtxSynchronize");
  const auto result = alias ? dc.value : dd.value;
  check(cuMemcpyDtoH(output.data(), result, bytes), "download result");
  CAPTURE(accum, aLayout, bLayout, mode);
  const auto words = static_cast<size_t>(registers) * 32;
  REQUIRE((expected ^ take(words)) == (output ^ take(words)));
}
} // namespace

TEST_CASE("runtime/cuda/mma884-lane-partitions", "[unified][runtime][tensor][compact]") {
  check(cuInit(0), "cuInit");
  const auto selected = conformance::deviceSelection().nativeHandle;
  const auto runtimeDevice = cudaSetDevice(static_cast<int>(selected));
  if (runtimeDevice != cudaSuccess) throw std::runtime_error(std::string("cudaSetDevice failed: ") + cudaGetErrorString(runtimeDevice));
  const auto initialised = cudaFree(nullptr);
  if (initialised != cudaSuccess) throw std::runtime_error(std::string("cudaFree failed: ") + cudaGetErrorString(initialised));
  int major{};
  CUdevice device{};
  check(cuCtxGetDevice(&device), "cuCtxGetDevice");
  check(cuDeviceGetAttribute(&major, CU_DEVICE_ATTRIBUTE_COMPUTE_CAPABILITY_MAJOR, device), "compute capability");
  if (major < 7) SKIP("mma.sync.m8n8k4 requires compute capability 7.0");
  CUmodule module = loadModule();
  for (const auto *accum : {"f32", "f16"})
    for (const auto *a : {"row", "col"})
      for (const auto *b : {"row", "col"})
        for (const auto *mode : {"plain", "alias", "zeroalias"})
          runVariant(accum, a, b, mode, module);
  check(cuModuleUnload(module), "cuModuleUnload");
}
