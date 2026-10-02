#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <iterator>
#include <stdexcept>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

extern "C" CUresult cuCtxCreate_v2(CUcontext *context, unsigned flags, CUdevice device);

namespace {

constexpr char Ptx[] = ".version 8.0\n.target sm_89\n.address_size 64\n.visible .entry probe() { ret; }\n";

void store16(std::vector<uint8_t> &bytes, size_t offset, uint16_t value) { std::memcpy(bytes.data() + offset, &value, sizeof(value)); }

void store32(std::vector<uint8_t> &bytes, size_t offset, uint32_t value) { std::memcpy(bytes.data() + offset, &value, sizeof(value)); }

void store64(std::vector<uint8_t> &bytes, size_t offset, uint64_t value) { std::memcpy(bytes.data() + offset, &value, sizeof(value)); }

std::vector<uint8_t> minimalCubin() {
  std::vector<uint8_t> image(128);
  constexpr uint8_t magic[]{0x7f, 'E', 'L', 'F', 2, 1};
  std::copy(std::begin(magic), std::end(magic), image.begin());
  store16(image, 18, 190);
  store64(image, 40, 64);
  store16(image, 58, 64);
  store16(image, 60, 1);
  return image;
}

void appendFatbinEntry(std::vector<uint8_t> &fatbin, uint16_t kind, int32_t target, const uint8_t *data, size_t size) {
  const auto entry = fatbin.size();
  fatbin.resize(entry + 64 + size);
  store16(fatbin, entry, kind);
  store32(fatbin, entry + 4, 64);
  store32(fatbin, entry + 8, static_cast<uint32_t>(size));
  store32(fatbin, entry + 16, static_cast<uint32_t>(size));
  store32(fatbin, entry + 28, static_cast<uint32_t>(target));
  std::copy(data, data + size, fatbin.data() + entry + 64);
}

std::vector<uint8_t> mixedFatbin(const std::vector<uint8_t> &cubin) {
  std::vector<uint8_t> fatbin(16);
  store32(fatbin, 0, 0xba55ed50);
  store16(fatbin, 6, 16);
  appendFatbinEntry(fatbin, 1, 0, reinterpret_cast<const uint8_t *>(Ptx), sizeof(Ptx) - 1);
  appendFatbinEntry(fatbin, 2, 89, cubin.data(), cubin.size());
  store64(fatbin, 8, fatbin.size() - 16);
  return fatbin;
}

std::vector<uint8_t> metadataFatbin(const std::vector<uint8_t> &cubin) {
  std::vector<uint8_t> fatbin(16);
  store32(fatbin, 0, 0xba55ed50);
  store16(fatbin, 6, 16);
  appendFatbinEntry(fatbin, 2, 89, cubin.data(), cubin.size());
  store64(fatbin, 8, fatbin.size() - 16);
  return fatbin;
}

void setCodeMode(const char *mode) {
  if (setenv(gemstone::environment::CudaCode, mode, 1) != 0) throw std::runtime_error("cannot set CUDA code mode");
}

} // namespace

TEST_CASE("CUDA strict code modes reject wrong kinds and select matching fatbin images", "[cuda][code-mode]") {
  gemstone_test::RpcFixture fixture;
  const auto endpoint = fixture.endpoint();
  REQUIRE(setenv(gemstone::environment::RpcEndpoint, endpoint.c_str(), 1) == 0);
  REQUIRE(setenv(gemstone::environment::RpcClient, "42", 1) == 0);
  REQUIRE(setenv(gemstone::environment::TargetArch, "compute_89", 1) == 0);

  REQUIRE((cuInit(0) == CUDA_SUCCESS));
  CUcontext context = nullptr;
  REQUIRE((cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS));

  const auto cubin = minimalCubin();
  const auto fatbin = mixedFatbin(cubin);
  const auto metadataOnlyFatbin = metadataFatbin(cubin);
  std::string ptx(Ptx);
  const auto loadsBefore = fixture.programLoads();

  setCodeMode("sass");
  CUmodule module = nullptr;
  CHECK((cuModuleLoadData(&module, ptx.c_str()) == CUDA_ERROR_NOT_SUPPORTED));
  CHECK((module == nullptr));
  CHECK(fixture.programLoads() == loadsBefore);

  setCodeMode("ptx");
  module = nullptr;
  CHECK((cuModuleLoadData(&module, cubin.data()) == CUDA_ERROR_NOT_SUPPORTED));
  CHECK((module == nullptr));
  CHECK(fixture.programLoads() == loadsBefore);

  module = nullptr;
  REQUIRE((cuModuleLoadData(&module, ptx.c_str()) == CUDA_SUCCESS));
  CHECK((module != nullptr));
  CHECK(fixture.lastProgramImage().front() == static_cast<uint8_t>('.'));
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  for (const char *mode : {"sass", "cubin"}) {
    setCodeMode(mode);
    module = nullptr;
    REQUIRE((cuModuleLoadData(&module, cubin.data()) == CUDA_SUCCESS));
    CHECK((module != nullptr));
    CHECK(fixture.lastProgramImage().front() == 0x7f);
    REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

    module = nullptr;
    CHECK((cuModuleLoadData(&module, ptx.c_str()) == CUDA_ERROR_NOT_SUPPORTED));
    CHECK((module == nullptr));
  }

  setCodeMode("ptx");
  module = nullptr;
  REQUIRE((cuModuleLoadFatBinary(&module, fatbin.data()) == CUDA_SUCCESS));
  CHECK((module != nullptr));
  CHECK(fixture.lastProgramImage().front() == static_cast<uint8_t>('.'));
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  setCodeMode("sass");
  module = nullptr;
  REQUIRE((cuModuleLoadFatBinary(&module, fatbin.data()) == CUDA_SUCCESS));
  CHECK((module != nullptr));
  CHECK(fixture.lastProgramImage().front() == 0x7f);
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  const auto loadsBeforeMetadata = fixture.programLoads();
  module = nullptr;
  REQUIRE((cuModuleLoadFatBinary(&module, metadataOnlyFatbin.data()) == CUDA_SUCCESS));
  CHECK((module != nullptr));
  const auto metadataImage = fixture.lastProgramImage();
  CHECK(std::string(metadataImage.begin(), metadataImage.end()) == ".version 9.3\n.target sm_89\n.address_size 64\n");
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  module = nullptr;
  CHECK((cuModuleLoadData(&module, ptx.c_str()) == CUDA_ERROR_NOT_SUPPORTED));
  CHECK((module == nullptr));
  CHECK(fixture.programLoads() == loadsBeforeMetadata + 1);

  CUlibrary metadataLibrary = nullptr;
  REQUIRE((cuLibraryLoadData(&metadataLibrary, metadataOnlyFatbin.data(), nullptr, nullptr, 0, nullptr, nullptr, 0) == CUDA_SUCCESS));
  const auto metadataLibraryImage = fixture.lastProgramImage();
  CHECK(std::string(metadataLibraryImage.begin(), metadataLibraryImage.end()) == ".version 9.3\n.target sm_89\n.address_size 64\n");
  REQUIRE((cuLibraryUnload(metadataLibrary) == CUDA_SUCCESS));

  setCodeMode("ptx");
  module = nullptr;
  REQUIRE((cuModuleLoadDataEx(&module, fatbin.data(), 0, nullptr, nullptr) == CUDA_SUCCESS));
  CHECK(fixture.lastProgramImage().front() == static_cast<uint8_t>('.'));
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  setCodeMode("sass");
  module = nullptr;
  REQUIRE((cuModuleLoadDataEx(&module, fatbin.data(), 0, nullptr, nullptr) == CUDA_SUCCESS));
  CHECK(fixture.lastProgramImage().front() == 0x7f);
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  CUlibrary library = nullptr;
  CHECK((cuLibraryLoadData(&library, nullptr, nullptr, nullptr, 0, nullptr, nullptr, 0) == CUDA_ERROR_INVALID_VALUE));
  CHECK((library == nullptr));

  setCodeMode("ptx");
  library = nullptr;
  REQUIRE((cuLibraryLoadData(&library, fatbin.data(), nullptr, nullptr, 0, nullptr, nullptr, 0) == CUDA_SUCCESS));
  CHECK(fixture.lastProgramImage().front() == static_cast<uint8_t>('.'));
  REQUIRE((cuLibraryUnload(library) == CUDA_SUCCESS));

  setCodeMode("sass");
  library = nullptr;
  REQUIRE((cuLibraryLoadData(&library, fatbin.data(), nullptr, nullptr, 0, nullptr, nullptr, 0) == CUDA_SUCCESS));
  CHECK(fixture.lastProgramImage().front() == 0x7f);
  REQUIRE((cuLibraryUnload(library) == CUDA_SUCCESS));

  library = nullptr;
  CHECK((cuLibraryLoadData(&library, ptx.c_str(), nullptr, nullptr, 0, nullptr, nullptr, 0) == CUDA_ERROR_NOT_SUPPORTED));
  CHECK((library == nullptr));

  CHECK((cuCtxDestroy_v2(context) == CUDA_SUCCESS));
  unsetenv(gemstone::environment::RpcEndpoint);
  unsetenv(gemstone::environment::RpcClient);
  unsetenv(gemstone::environment::TargetArch);
  unsetenv(gemstone::environment::CudaCode);
}
