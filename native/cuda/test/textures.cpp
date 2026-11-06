#include <algorithm>
#include <array>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>
#include <cuda_runtime_api.h>

#include <gemstone/test_support.hpp>
#include <gemstone_texture_layout.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

extern "C" CUresult cuCtxCreate_v2(CUcontext *context, unsigned flags, CUdevice device);

namespace {

namespace layout = gemstone::texture::Layout;

// The facade connects to the execution service once per process, so every case shares this fixture.
gemstone_test::RpcFixture fixture;
const std::string Endpoint = fixture.endpoint();
const gemstone_test::Environment RpcEndpoint(gemstone::environment::RpcEndpoint, Endpoint.c_str());
const gemstone_test::Environment RpcClient(gemstone::environment::RpcClientId, "42");

void setup() {
  static CUcontext context = nullptr;
  if (context) return;
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
}

const void *table() { return reinterpret_cast<const void *>(layout::Table::BaseAddress); }

struct Entry {
  uint64_t base = 0;
  uint64_t pitch = 0;
  uint64_t slicePitch = 0;
  uint32_t width = 0;
  uint32_t height = 0;
  uint32_t depth = 0;
  uint8_t object = 0;
  uint8_t dimensions = 0;
  uint8_t kind = 0;
  uint8_t flags = 0;
  std::array<uint8_t, 4> bits{};
  std::array<uint8_t, 3> address{};
  uint8_t filter = 0;
  std::array<float, 4> border{};
};

template <class F> Entry configure(F setter) {
  Entry entry;
  setter(entry);
  return entry;
}

std::array<uint64_t, 8> words(const Entry &entry) {
  std::array<unsigned char, layout::Table::EntryBytes> bytes{};
  const auto put = [&](uint32_t offset, const auto &value) { std::memcpy(bytes.data() + offset, &value, sizeof(value)); };
  put(layout::Field::Base, entry.base);
  put(layout::Field::Pitch, entry.pitch);
  put(layout::Field::SlicePitch, entry.slicePitch);
  put(layout::Field::Width, entry.width);
  put(layout::Field::Height, entry.height);
  put(layout::Field::Depth, entry.depth);
  put(layout::Field::Object, entry.object);
  put(layout::Field::Dimensions, entry.dimensions);
  put(layout::Field::ChannelKind, entry.kind);
  put(layout::Field::Flags, entry.flags);
  const std::array bits{layout::Field::BitsX, layout::Field::BitsY, layout::Field::BitsZ, layout::Field::BitsW};
  for (size_t i = 0; i < bits.size(); ++i)
    put(bits[i], entry.bits[i]);
  const std::array modes{layout::Field::AddressX, layout::Field::AddressY, layout::Field::AddressZ};
  for (size_t i = 0; i < modes.size(); ++i)
    put(modes[i], entry.address[i]);
  put(layout::Field::Filter, entry.filter);
  const std::array colours{layout::Field::BorderX, layout::Field::BorderY, layout::Field::BorderZ, layout::Field::BorderW};
  for (size_t i = 0; i < colours.size(); ++i)
    put(colours[i], entry.border[i]);
  std::array<uint64_t, 8> result{};
  std::memcpy(result.data(), bytes.data(), bytes.size());
  return result;
}

// The base word is skipped when `checkBase` is false because array storage is private to the facade.
void checkEntry(uint64_t handle, const Entry &expected, bool checkBase = true) {
  const auto expectedWords = words(expected);
  for (size_t word = checkBase ? 0 : 1; word < expectedWords.size(); ++word)
    fixture.checkAllocationWord(table(), (handle * layout::Table::EntryBytes) + (word * 8), expectedWords[word]);
}

constexpr uint32_t flagsOf(uint32_t resource, uint32_t extra = 0) { return (resource << layout::Flag::ResourceShift) | extra; }

CUarray makeArray(CUarray_format format, unsigned channels, size_t width, size_t height, size_t depth = 0, unsigned flags = 0) {
  CUDA_ARRAY3D_DESCRIPTOR descriptor{};
  descriptor.Width = width;
  descriptor.Height = height;
  descriptor.Depth = depth;
  descriptor.Format = format;
  descriptor.NumChannels = channels;
  descriptor.Flags = flags;
  CUarray array = nullptr;
  REQUIRE(cuArray3DCreate_v2(&array, &descriptor) == CUDA_SUCCESS);
  return array;
}

CUDA_RESOURCE_DESC arrayResource(CUarray array) {
  CUDA_RESOURCE_DESC resource{};
  resource.resType = CU_RESOURCE_TYPE_ARRAY;
  resource.res.array.hArray = array;
  return resource;
}

CUDA_RESOURCE_DESC linearResource(CUdeviceptr pointer, CUarray_format format, unsigned channels, size_t bytes) {
  CUDA_RESOURCE_DESC resource{};
  resource.resType = CU_RESOURCE_TYPE_LINEAR;
  resource.res.linear.devPtr = pointer;
  resource.res.linear.format = format;
  resource.res.linear.numChannels = channels;
  resource.res.linear.sizeInBytes = bytes;
  return resource;
}

CUDA_RESOURCE_DESC pitchResource(CUdeviceptr pointer, CUarray_format format, unsigned channels, size_t width, size_t height, size_t pitch) {
  CUDA_RESOURCE_DESC resource{};
  resource.resType = CU_RESOURCE_TYPE_PITCH2D;
  resource.res.pitch2D.devPtr = pointer;
  resource.res.pitch2D.format = format;
  resource.res.pitch2D.numChannels = channels;
  resource.res.pitch2D.width = width;
  resource.res.pitch2D.height = height;
  resource.res.pitch2D.pitchInBytes = pitch;
  return resource;
}

CUDA_TEXTURE_DESC elementTexture() {
  CUDA_TEXTURE_DESC texture{};
  for (auto &mode : texture.addressMode)
    mode = CU_TR_ADDRESS_MODE_CLAMP;
  texture.flags = CU_TRSF_READ_AS_INTEGER;
  return texture;
}

CUdeviceptr allocate(size_t bytes) {
  CUdeviceptr pointer = 0;
  REQUIRE(cuMemAlloc_v2(&pointer, bytes) == CUDA_SUCCESS);
  return pointer;
}

cudaChannelFormatDesc channels(int bits, int count, cudaChannelFormatKind kind) {
  return cudaCreateChannelDesc(bits, count > 1 ? bits : 0, count > 2 ? bits : 0, count > 3 ? bits : 0, kind);
}

cudaResourceDesc runtimeLinear(void *pointer, cudaChannelFormatDesc desc, size_t bytes) {
  cudaResourceDesc resource{};
  resource.resType = cudaResourceTypeLinear;
  resource.res.linear.devPtr = pointer;
  resource.res.linear.desc = desc;
  resource.res.linear.sizeInBytes = bytes;
  return resource;
}

cudaResourceDesc runtimeArray(CUarray array) {
  cudaResourceDesc resource{};
  resource.resType = cudaResourceTypeArray;
  resource.res.array.array = reinterpret_cast<cudaArray_t>(array);
  return resource;
}

cudaTextureDesc runtimeTexture() {
  cudaTextureDesc texture{};
  texture.readMode = cudaReadModeElementType;
  for (auto &mode : texture.addressMode)
    mode = cudaAddressModeClamp;
  return texture;
}

CUresult createStatus(const CUDA_RESOURCE_DESC &resource, const CUDA_TEXTURE_DESC &texture) {
  CUtexObject handle = 0;
  const auto status = cuTexObjectCreate(&handle, &resource, &texture, nullptr);
  if (status == CUDA_SUCCESS) REQUIRE(cuTexObjectDestroy(handle) == CUDA_SUCCESS);
  return status;
}

CUresult surfaceStatus(const CUDA_RESOURCE_DESC &resource) {
  CUsurfObject handle = 0;
  const auto status = cuSurfObjectCreate(&handle, &resource);
  if (status == CUDA_SUCCESS) REQUIRE(cuSurfObjectDestroy(handle) == CUDA_SUCCESS);
  return status;
}

cudaError_t runtimeCreate(const cudaResourceDesc &resource, const cudaTextureDesc &texture) {
  cudaTextureObject_t handle = 0;
  const auto status = cudaCreateTextureObject(&handle, &resource, &texture, nullptr);
  if (status == cudaSuccess) REQUIRE(cudaDestroyTextureObject(handle) == cudaSuccess);
  return status;
}

cudaError_t runtimeSurface(const cudaResourceDesc &resource) {
  cudaSurfaceObject_t handle = 0;
  const auto status = cudaCreateSurfaceObject(&handle, &resource);
  if (status == cudaSuccess) REQUIRE(cudaDestroySurfaceObject(handle) == cudaSuccess);
  return status;
}

} // namespace

TEST_CASE("texture handles are table indices from one, reused lowest first", "[cuda][texture]") {
  setup();
  auto *const array = makeArray(CU_AD_FORMAT_FLOAT, 1, 8, 8);
  const auto resource = arrayResource(array);
  const auto texture = elementTexture();
  CUtexObject first = 0;
  CUtexObject second = 0;
  CUtexObject third = 0;
  REQUIRE(cuTexObjectCreate(&first, &resource, &texture, nullptr) == CUDA_SUCCESS);
  REQUIRE(cuTexObjectCreate(&second, &resource, &texture, nullptr) == CUDA_SUCCESS);
  CHECK(first == 1);
  CHECK(second == 2);
  REQUIRE(cuTexObjectDestroy(first) == CUDA_SUCCESS);
  REQUIRE(cuTexObjectCreate(&third, &resource, &texture, nullptr) == CUDA_SUCCESS);
  CHECK(third == 1);
  CUsurfObject surface = 0;
  REQUIRE(cuSurfObjectCreate(&surface, &resource) == CUDA_SUCCESS);
  CHECK(surface == 3);
  REQUIRE(cuTexObjectDestroy(second) == CUDA_SUCCESS);
  REQUIRE(cuTexObjectDestroy(third) == CUDA_SUCCESS);
  REQUIRE(cuSurfObjectDestroy(surface) == CUDA_SUCCESS);
  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
}

TEST_CASE("the descriptor table is one registered allocation at its fixed address", "[cuda][texture]") {
  setup();
  auto *const array = makeArray(CU_AD_FORMAT_FLOAT, 1, 8, 8);
  const auto resource = arrayResource(array);
  const auto texture = elementTexture();
  CUtexObject first = 0;
  REQUIRE(cuTexObjectCreate(&first, &resource, &texture, nullptr) == CUDA_SUCCESS);
  fixture.checkAllocationAddress(table());
  const auto count = fixture.allocationCount();
  CUtexObject second = 0;
  REQUIRE(cuTexObjectCreate(&second, &resource, &texture, nullptr) == CUDA_SUCCESS);
  CHECK(fixture.allocationCount() == count);
  REQUIRE(cuTexObjectDestroy(first) == CUDA_SUCCESS);
  REQUIRE(cuTexObjectDestroy(second) == CUDA_SUCCESS);
  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
}

TEST_CASE("array textures write their geometry, format, and sampler state", "[cuda][texture]") {
  setup();
  auto *const array = makeArray(CU_AD_FORMAT_UNSIGNED_INT8, 4, 64, 32, 4);
  const auto resource = arrayResource(array);
  CUDA_TEXTURE_DESC texture{};
  texture.addressMode[0] = CU_TR_ADDRESS_MODE_WRAP;
  texture.addressMode[1] = CU_TR_ADDRESS_MODE_MIRROR;
  texture.addressMode[2] = CU_TR_ADDRESS_MODE_BORDER;
  texture.filterMode = CU_TR_FILTER_MODE_LINEAR;
  texture.flags = CU_TRSF_NORMALIZED_COORDINATES | CU_TRSF_SRGB;
  texture.borderColor[0] = 0.25F;
  texture.borderColor[3] = 1.0F;
  CUtexObject handle = 0;
  REQUIRE(cuTexObjectCreate(&handle, &resource, &texture, nullptr) == CUDA_SUCCESS);
  checkEntry(handle, configure([](Entry &e) {
               e.base = 0;
               e.pitch = 256;
               e.slicePitch = static_cast<uint64_t>(256 * 32);
               e.width = 64;
               e.height = 32;
               e.depth = 4;
               e.object = layout::Object::Texture;
               e.dimensions = 3;
               e.kind = layout::Channel::Unsigned;
               e.flags = static_cast<uint8_t>(flagsOf(layout::Resource::Array, layout::Flag::NormalisedCoordinates
                                                                                   | layout::Flag::NormalisedRead | layout::Flag::Srgb));
               e.bits = {8, 8, 8, 8};
               e.address = {layout::Address::Wrap, layout::Address::Mirror, layout::Address::Border};
               e.filter = layout::Filter::Linear;
               e.border = {0.25F, 0.0F, 0.0F, 1.0F};
             }),
             false);
  REQUIRE(cuTexObjectDestroy(handle) == CUDA_SUCCESS);
  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
}

TEST_CASE("array dimensions follow the extent", "[cuda][texture]") {
  setup();
  auto *const flat = makeArray(CU_AD_FORMAT_HALF, 2, 16, 0);
  auto *const rows = makeArray(CU_AD_FORMAT_SIGNED_INT16, 1, 16, 4);
  const auto texture = elementTexture();
  CUtexObject a = 0;
  CUtexObject b = 0;
  const auto flatResource = arrayResource(flat);
  const auto rowsResource = arrayResource(rows);
  REQUIRE(cuTexObjectCreate(&a, &flatResource, &texture, nullptr) == CUDA_SUCCESS);
  REQUIRE(cuTexObjectCreate(&b, &rowsResource, &texture, nullptr) == CUDA_SUCCESS);
  checkEntry(a, configure([](Entry &e) {
               e.pitch = 64;
               e.slicePitch = 64;
               e.width = 16;
               e.height = 1;
               e.depth = 1;
               e.object = layout::Object::Texture;
               e.dimensions = 1;
               e.kind = layout::Channel::Float;
               e.flags = static_cast<uint8_t>(flagsOf(layout::Resource::Array));
               e.bits = {16, 16, 0, 0};
               e.address = {layout::Address::Clamp, layout::Address::Clamp, layout::Address::Clamp};
             }),
             false);
  checkEntry(b, configure([](Entry &e) {
               e.pitch = 32;
               e.slicePitch = 128;
               e.width = 16;
               e.height = 4;
               e.depth = 1;
               e.object = layout::Object::Texture;
               e.dimensions = 2;
               e.kind = layout::Channel::Signed;
               e.flags = static_cast<uint8_t>(flagsOf(layout::Resource::Array));
               e.bits = {16, 0, 0, 0};
               e.address = {layout::Address::Clamp, layout::Address::Clamp, layout::Address::Clamp};
             }),
             false);
  REQUIRE(cuTexObjectDestroy(a) == CUDA_SUCCESS);
  REQUIRE(cuTexObjectDestroy(b) == CUDA_SUCCESS);
  REQUIRE(cuArrayDestroy(flat) == CUDA_SUCCESS);
  REQUIRE(cuArrayDestroy(rows) == CUDA_SUCCESS);
}

TEST_CASE("linear textures address memory directly with border reads outside", "[cuda][texture]") {
  setup();
  const auto memory = allocate(4096);
  const auto resource = linearResource(memory, CU_AD_FORMAT_FLOAT, 4, 1024);
  auto texture = elementTexture();
  texture.addressMode[0] = CU_TR_ADDRESS_MODE_WRAP;
  texture.borderColor[1] = 5.0F;
  CUtexObject handle = 0;
  REQUIRE(cuTexObjectCreate(&handle, &resource, &texture, nullptr) == CUDA_SUCCESS);
  checkEntry(handle, configure([&](Entry &e) {
               e.base = memory;
               e.pitch = 1024;
               e.slicePitch = 1024;
               e.width = 64;
               e.height = 1;
               e.depth = 1;
               e.object = layout::Object::Texture;
               e.dimensions = 1;
               e.kind = layout::Channel::Float;
               e.flags = static_cast<uint8_t>(flagsOf(layout::Resource::Linear));
               e.bits = {32, 32, 32, 32};
               e.address = {layout::Address::Border, layout::Address::Border, layout::Address::Border};
             }));
  REQUIRE(cuTexObjectDestroy(handle) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(memory) == CUDA_SUCCESS);
}

TEST_CASE("linear width rounds down to whole elements", "[cuda][texture]") {
  setup();
  const auto memory = allocate(4096);
  const auto resource = linearResource(memory, CU_AD_FORMAT_UNSIGNED_INT16, 2, 1002);
  const auto texture = elementTexture();
  CUtexObject handle = 0;
  REQUIRE(cuTexObjectCreate(&handle, &resource, &texture, nullptr) == CUDA_SUCCESS);
  fixture.checkAllocationWord(table(), (handle * layout::Table::EntryBytes) + (static_cast<CUtexObject>(3 * 8)), 250 | (uint64_t{1} << 32));
  REQUIRE(cuTexObjectDestroy(handle) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(memory) == CUDA_SUCCESS);
}

TEST_CASE("pitch2D textures carry the row pitch", "[cuda][texture]") {
  setup();
  CUdeviceptr memory = 0;
  size_t pitch = 0;
  REQUIRE(cuMemAllocPitch_v2(&memory, &pitch, static_cast<size_t>(100 * 4), 64, 16) == CUDA_SUCCESS);
  const auto resource = pitchResource(memory, CU_AD_FORMAT_FLOAT, 1, 100, 64, pitch);
  auto texture = elementTexture();
  texture.flags |= CU_TRSF_NORMALIZED_COORDINATES;
  texture.addressMode[1] = CU_TR_ADDRESS_MODE_BORDER;
  CUtexObject handle = 0;
  REQUIRE(cuTexObjectCreate(&handle, &resource, &texture, nullptr) == CUDA_SUCCESS);
  checkEntry(handle, configure([&](Entry &e) {
               e.base = memory;
               e.pitch = pitch;
               e.slicePitch = pitch * 64;
               e.width = 100;
               e.height = 64;
               e.depth = 1;
               e.object = layout::Object::Texture;
               e.dimensions = 2;
               e.kind = layout::Channel::Float;
               e.flags = static_cast<uint8_t>(flagsOf(layout::Resource::Pitch2D, layout::Flag::NormalisedCoordinates));
               e.bits = {32, 0, 0, 0};
               e.address = {layout::Address::Clamp, layout::Address::Border, layout::Address::Clamp};
             }));
  REQUIRE(cuTexObjectDestroy(handle) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(memory) == CUDA_SUCCESS);
}

TEST_CASE("surfaces write an array entry with clamped state", "[cuda][texture]") {
  setup();
  auto *const array = makeArray(CU_AD_FORMAT_UNSIGNED_INT32, 4, 8, 4, 0, CUDA_ARRAY3D_SURFACE_LDST);
  const auto resource = arrayResource(array);
  CUsurfObject handle = 0;
  REQUIRE(cuSurfObjectCreate(&handle, &resource) == CUDA_SUCCESS);
  checkEntry(handle, configure([](Entry &e) {
               e.pitch = 128;
               e.slicePitch = 512;
               e.width = 8;
               e.height = 4;
               e.depth = 1;
               e.object = layout::Object::Surface;
               e.dimensions = 2;
               e.kind = layout::Channel::Unsigned;
               e.flags = static_cast<uint8_t>(flagsOf(layout::Resource::Array));
               e.bits = {32, 32, 32, 32};
               e.address = {layout::Address::Clamp, layout::Address::Clamp, layout::Address::Clamp};
             }),
             false);
  REQUIRE(cuSurfObjectDestroy(handle) == CUDA_SUCCESS);
  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
}

TEST_CASE("sRGB applies to 8-bit normalised reads only", "[cuda][texture]") {
  setup();
  auto *const array = makeArray(CU_AD_FORMAT_UNSIGNED_INT8, 4, 8, 8);
  const auto resource = arrayResource(array);
  auto texture = elementTexture();
  texture.flags |= CU_TRSF_SRGB;
  CUtexObject element = 0;
  REQUIRE(cuTexObjectCreate(&element, &resource, &texture, nullptr) == CUDA_SUCCESS);
  texture.flags = CU_TRSF_SRGB;
  CUtexObject normalised = 0;
  REQUIRE(cuTexObjectCreate(&normalised, &resource, &texture, nullptr) == CUDA_SUCCESS);
  fixture.checkAllocationWord(table(), (element * layout::Table::EntryBytes) + (static_cast<CUtexObject>(4 * 8)),
                              (uint64_t{layout::Object::Texture} << 32) | (uint64_t{2} << 40) | (uint64_t{layout::Channel::Unsigned} << 48)
                                  | (uint64_t{flagsOf(layout::Resource::Array)} << 56) | 1);
  fixture.checkAllocationWord(table(), (normalised * layout::Table::EntryBytes) + (static_cast<CUtexObject>(4 * 8)),
                              (uint64_t{layout::Object::Texture} << 32) | (uint64_t{2} << 40) | (uint64_t{layout::Channel::Unsigned} << 48)
                                  | (uint64_t{flagsOf(layout::Resource::Array, layout::Flag::Srgb | layout::Flag::NormalisedRead)} << 56)
                                  | 1);
  REQUIRE(cuTexObjectDestroy(element) == CUDA_SUCCESS);
  REQUIRE(cuTexObjectDestroy(normalised) == CUDA_SUCCESS);
  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
}

TEST_CASE("destroying an object frees its entry and the slot", "[cuda][texture]") {
  setup();
  auto *const array = makeArray(CU_AD_FORMAT_FLOAT, 1, 8, 8);
  const auto resource = arrayResource(array);
  const auto texture = elementTexture();
  CUtexObject handle = 0;
  REQUIRE(cuTexObjectCreate(&handle, &resource, &texture, nullptr) == CUDA_SUCCESS);
  REQUIRE(cuTexObjectDestroy(handle) == CUDA_SUCCESS);
  checkEntry(handle, {});
  CUDA_RESOURCE_DESC out{};
  CHECK(cuTexObjectGetResourceDesc(&out, handle) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuTexObjectDestroy(handle) == CUDA_SUCCESS);
  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
}

TEST_CASE("queries return the descriptors the object was created from", "[cuda][texture]") {
  setup();
  const auto memory = allocate(4096);
  const auto resource = linearResource(memory, CU_AD_FORMAT_HALF, 2, 2048);
  auto texture = elementTexture();
  texture.filterMode = CU_TR_FILTER_MODE_LINEAR;
  texture.maxAnisotropy = 4;
  texture.borderColor[2] = 0.5F;
  CUtexObject handle = 0;
  REQUIRE(cuTexObjectCreate(&handle, &resource, &texture, nullptr) == CUDA_SUCCESS);
  CUDA_RESOURCE_DESC gotResource{};
  CUDA_TEXTURE_DESC gotTexture{};
  CUDA_RESOURCE_VIEW_DESC gotView{};
  REQUIRE(cuTexObjectGetResourceDesc(&gotResource, handle) == CUDA_SUCCESS);
  REQUIRE(cuTexObjectGetTextureDesc(&gotTexture, handle) == CUDA_SUCCESS);
  REQUIRE(cuTexObjectGetResourceViewDesc(&gotView, handle) == CUDA_SUCCESS);
  CHECK(gotResource.resType == CU_RESOURCE_TYPE_LINEAR);
  CHECK(gotResource.res.linear.devPtr == memory);
  CHECK(gotResource.res.linear.format == CU_AD_FORMAT_HALF);
  CHECK(gotResource.res.linear.numChannels == 2);
  CHECK(gotResource.res.linear.sizeInBytes == 2048);
  CHECK(gotTexture.filterMode == CU_TR_FILTER_MODE_LINEAR);
  CHECK(gotTexture.flags == CU_TRSF_READ_AS_INTEGER);
  CHECK(gotTexture.maxAnisotropy == 4);
  CHECK(gotTexture.borderColor[2] == 0.5F);
  CHECK(gotView.format == CU_RES_VIEW_FORMAT_NONE);
  CUDA_RESOURCE_DESC wrongKind{};
  CHECK(cuSurfObjectGetResourceDesc(&wrongKind, handle) == CUDA_ERROR_INVALID_VALUE);
  REQUIRE(cuTexObjectDestroy(handle) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(memory) == CUDA_SUCCESS);
}

TEST_CASE("driver argument checks", "[cuda][texture]") {
  setup();
  const auto memory = allocate(8192);
  const auto linear = linearResource(memory, CU_AD_FORMAT_FLOAT, 1, 1024);
  const auto texture = elementTexture();
  CUtexObject handle = 0;
  CHECK(cuTexObjectCreate(nullptr, &linear, &texture, nullptr) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuTexObjectCreate(&handle, nullptr, &texture, nullptr) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuTexObjectCreate(&handle, &linear, nullptr, nullptr) == CUDA_ERROR_INVALID_VALUE);

  auto flagged = linear;
  flagged.flags = 1;
  CHECK(cuTexObjectCreate(&handle, &flagged, &texture, nullptr) == CUDA_ERROR_INVALID_VALUE);

  CHECK(createStatus(linearResource(0, CU_AD_FORMAT_FLOAT, 1, 1024), texture) == CUDA_ERROR_INVALID_VALUE);
  CHECK(createStatus(linearResource(memory + 4, CU_AD_FORMAT_FLOAT, 1, 1024), texture) == CUDA_ERROR_INVALID_VALUE);
  CHECK(createStatus(linearResource(memory, CU_AD_FORMAT_FLOAT, 3, 1024), texture) == CUDA_ERROR_INVALID_VALUE);
  CHECK(createStatus(linearResource(memory, CU_AD_FORMAT_FLOAT, 0, 1024), texture) == CUDA_ERROR_INVALID_VALUE);
  CHECK(createStatus(linearResource(memory, CU_AD_FORMAT_NV12, 1, 1024), texture) == CUDA_ERROR_INVALID_VALUE);
  CHECK(createStatus(pitchResource(memory, CU_AD_FORMAT_FLOAT, 1, 10, 10, 36), texture) == CUDA_ERROR_INVALID_VALUE);

  auto badAddress = texture;
  badAddress.addressMode[1] = static_cast<CUaddress_mode>(9);
  CHECK(cuTexObjectCreate(&handle, &linear, &badAddress, nullptr) == CUDA_ERROR_INVALID_VALUE);
  auto badFilter = texture;
  badFilter.filterMode = static_cast<CUfilter_mode>(9);
  CHECK(cuTexObjectCreate(&handle, &linear, &badFilter, nullptr) == CUDA_ERROR_INVALID_VALUE);
  auto badFlags = texture;
  badFlags.flags |= 0x100;
  CHECK(cuTexObjectCreate(&handle, &linear, &badFlags, nullptr) == CUDA_ERROR_INVALID_VALUE);

  CUDA_RESOURCE_DESC bogusType{};
  bogusType.resType = static_cast<CUresourcetype>(9);
  CHECK(cuTexObjectCreate(&handle, &bogusType, &texture, nullptr) == CUDA_ERROR_INVALID_VALUE);
  auto mipmapped = bogusType;
  mipmapped.resType = CU_RESOURCE_TYPE_MIPMAPPED_ARRAY;
  CHECK(cuTexObjectCreate(&handle, &mipmapped, &texture, nullptr) == CUDA_ERROR_INVALID_VALUE);
  mipmapped.res.mipmap.hMipmappedArray = reinterpret_cast<CUmipmappedArray>(&mipmapped);
  CHECK(cuTexObjectCreate(&handle, &mipmapped, &texture, nullptr) == CUDA_ERROR_NOT_SUPPORTED);
  CUDA_RESOURCE_VIEW_DESC view{};
  view.format = CU_RES_VIEW_FORMAT_UINT_1X8;
  CHECK(cuTexObjectCreate(&handle, &linear, &texture, &view) == CUDA_ERROR_NOT_SUPPORTED);
  CHECK(handle == 0);

  CHECK(cuSurfObjectCreate(nullptr, &linear) == CUDA_ERROR_INVALID_VALUE);
  CUsurfObject surface = 0;
  CHECK(cuSurfObjectCreate(&surface, nullptr) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuSurfObjectCreate(&surface, &linear) == CUDA_ERROR_INVALID_VALUE);
  CHECK(surfaceStatus(arrayResource(nullptr)) == CUDA_ERROR_INVALID_VALUE);

  CUDA_RESOURCE_DESC out{};
  CHECK(cuTexObjectGetResourceDesc(nullptr, 1) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuTexObjectGetResourceDesc(&out, 0) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuTexObjectGetResourceDesc(&out, 0xdeadbeef) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuTexObjectDestroy(0) == CUDA_SUCCESS);
  CHECK(cuTexObjectDestroy(0x12345) == CUDA_SUCCESS);
  CHECK(cuSurfObjectDestroy(0x12345) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(memory) == CUDA_SUCCESS);
}

TEST_CASE("destroying through the wrong object kind leaves the object", "[cuda][texture]") {
  setup();
  auto *const array = makeArray(CU_AD_FORMAT_FLOAT, 1, 8, 8);
  const auto resource = arrayResource(array);
  const auto texture = elementTexture();
  CUtexObject handle = 0;
  REQUIRE(cuTexObjectCreate(&handle, &resource, &texture, nullptr) == CUDA_SUCCESS);
  CHECK(cuSurfObjectDestroy(handle) == CUDA_SUCCESS);
  CUDA_RESOURCE_DESC out{};
  CHECK(cuTexObjectGetResourceDesc(&out, handle) == CUDA_SUCCESS);
  REQUIRE(cuTexObjectDestroy(handle) == CUDA_SUCCESS);
  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
}

TEST_CASE("runtime creation maps descriptors and reports cudart error codes", "[cuda][texture]") {
  setup();
  void *memory = nullptr;
  REQUIRE(cudaMalloc(&memory, 8192) == cudaSuccess);
  const auto floats = channels(32, 1, cudaChannelFormatKindFloat);
  const auto bytes = channels(8, 4, cudaChannelFormatKindUnsigned);
  cudaTextureDesc const texture = runtimeTexture();
  cudaTextureObject_t handle = 0;
  const auto linear = runtimeLinear(memory, floats, 1024);

  REQUIRE(cudaCreateTextureObject(&handle, &linear, &texture, nullptr) == cudaSuccess);
  CHECK(handle == 1);
  REQUIRE(cudaDestroyTextureObject(handle) == cudaSuccess);

  CHECK(cudaCreateTextureObject(nullptr, &linear, &texture, nullptr) == cudaErrorInvalidValue);
  CHECK(cudaCreateTextureObject(&handle, nullptr, &texture, nullptr) == cudaErrorInvalidValue);
  CHECK(cudaCreateTextureObject(&handle, &linear, nullptr, nullptr) == cudaErrorInvalidResourceHandle);
  CHECK(runtimeCreate(runtimeArray(nullptr), texture) == cudaErrorInvalidResourceHandle);
  CHECK(runtimeCreate(runtimeLinear(nullptr, floats, 1024), texture) == cudaErrorInvalidValue);
  CHECK(runtimeCreate(runtimeLinear(memory, channels(32, 3, cudaChannelFormatKindFloat), 1024), texture)
        == cudaErrorInvalidChannelDescriptor);
  CHECK(runtimeCreate(runtimeLinear(memory, cudaCreateChannelDesc(8, 16, 0, 0, cudaChannelFormatKindUnsigned), 1024), texture)
        == cudaErrorInvalidChannelDescriptor);
  CHECK(runtimeCreate(runtimeLinear(memory, channels(8, 1, cudaChannelFormatKindFloat), 1024), texture)
        == cudaErrorInvalidChannelDescriptor);

  const auto packed = runtimeLinear(memory, bytes, 1024);
  auto linearFilter = texture;
  linearFilter.filterMode = cudaFilterModeLinear;
  CHECK(cudaCreateTextureObject(&handle, &packed, &linearFilter, nullptr) == cudaErrorInvalidFilterSetting);
  linearFilter.readMode = cudaReadModeNormalizedFloat;
  REQUIRE(cudaCreateTextureObject(&handle, &packed, &linearFilter, nullptr) == cudaSuccess);
  REQUIRE(cudaDestroyTextureObject(handle) == cudaSuccess);
  auto normalised = texture;
  normalised.readMode = cudaReadModeNormalizedFloat;
  CHECK(cudaCreateTextureObject(&handle, &linear, &normalised, nullptr) == cudaErrorInvalidNormSetting);
  CHECK(runtimeCreate(runtimeLinear(memory, channels(32, 1, cudaChannelFormatKindUnsigned), 1024), normalised)
        == cudaErrorInvalidNormSetting);
  auto srgb = texture;
  srgb.sRGB = 1;
  CHECK(cudaCreateTextureObject(&handle, &linear, &srgb, nullptr) == cudaErrorInvalidNormSetting);
  auto badMode = texture;
  badMode.addressMode[2] = static_cast<cudaTextureAddressMode>(7);
  CHECK(cudaCreateTextureObject(&handle, &linear, &badMode, nullptr) == cudaErrorInvalidValue);

  CHECK(runtimeCreate(runtimeLinear(static_cast<char *>(memory) + 4, floats, 1024), texture) == cudaErrorInvalidValue);
  cudaResourceDesc pitched{};
  pitched.resType = cudaResourceTypePitch2D;
  pitched.res.pitch2D.devPtr = memory;
  pitched.res.pitch2D.desc = floats;
  pitched.res.pitch2D.width = 100;
  pitched.res.pitch2D.height = 4;
  pitched.res.pitch2D.pitchInBytes = 256;
  CHECK(runtimeCreate(pitched, texture) == cudaErrorInvalidValue);
  pitched.res.pitch2D.pitchInBytes = 512;
  REQUIRE(cudaCreateTextureObject(&handle, &pitched, &texture, nullptr) == cudaSuccess);
  REQUIRE(cudaDestroyTextureObject(handle) == cudaSuccess);
  pitched.res.pitch2D.pitchInBytes = 0;
  REQUIRE(cudaCreateTextureObject(&handle, &pitched, &texture, nullptr) == cudaSuccess);
  REQUIRE(cudaDestroyTextureObject(handle) == cudaSuccess);
  REQUIRE(cudaFree(memory) == cudaSuccess);
}

TEST_CASE("runtime queries round trip descriptors", "[cuda][texture]") {
  setup();
  void *memory = nullptr;
  REQUIRE(cudaMalloc(&memory, 8192) == cudaSuccess);
  const auto resource = runtimeLinear(memory, channels(16, 2, cudaChannelFormatKindSigned), 2048);
  cudaTextureDesc texture = runtimeTexture();
  texture.addressMode[0] = cudaAddressModeBorder;
  texture.normalizedCoords = 1;
  texture.maxAnisotropy = 8;
  texture.borderColor[1] = 0.75F;
  texture.mipmapLevelBias = 1.5F;
  cudaTextureObject_t handle = 0;
  REQUIRE(cudaCreateTextureObject(&handle, &resource, &texture, nullptr) == cudaSuccess);
  cudaResourceDesc gotResource{};
  cudaTextureDesc gotTexture{};
  REQUIRE(cudaGetTextureObjectResourceDesc(&gotResource, handle) == cudaSuccess);
  REQUIRE(cudaGetTextureObjectTextureDesc(&gotTexture, handle) == cudaSuccess);
  CHECK(gotResource.resType == cudaResourceTypeLinear);
  CHECK(gotResource.res.linear.devPtr == memory);
  CHECK(gotResource.res.linear.sizeInBytes == 2048);
  CHECK(gotResource.res.linear.desc.x == 16);
  CHECK(gotResource.res.linear.desc.y == 16);
  CHECK(gotResource.res.linear.desc.z == 0);
  CHECK(gotResource.res.linear.desc.f == cudaChannelFormatKindSigned);
  CHECK(gotTexture.addressMode[0] == cudaAddressModeBorder);
  CHECK(gotTexture.addressMode[1] == cudaAddressModeClamp);
  CHECK(gotTexture.readMode == cudaReadModeElementType);
  CHECK(gotTexture.normalizedCoords == 1);
  CHECK(gotTexture.maxAnisotropy == 8);
  CHECK(gotTexture.borderColor[1] == 0.75F);
  CHECK(gotTexture.mipmapLevelBias == 1.5F);
  CHECK(cudaGetTextureObjectResourceDesc(nullptr, handle) == cudaErrorInvalidValue);
  CHECK(cudaGetTextureObjectTextureDesc(nullptr, handle) == cudaErrorInvalidResourceHandle);
  CHECK(cudaGetTextureObjectResourceDesc(&gotResource, 0) == cudaErrorInvalidValue);
  REQUIRE(cudaDestroyTextureObject(handle) == cudaSuccess);
  CHECK(cudaGetTextureObjectResourceDesc(&gotResource, handle) == cudaErrorInvalidValue);
  CHECK(cudaDestroyTextureObject(handle) == cudaSuccess);
  REQUIRE(cudaFree(memory) == cudaSuccess);
}

TEST_CASE("runtime surfaces take arrays only and share the handle space", "[cuda][texture]") {
  setup();
  auto *const array = makeArray(CU_AD_FORMAT_UNSIGNED_INT8, 4, 16, 16, 0, CUDA_ARRAY3D_SURFACE_LDST);
  const auto resource = runtimeArray(array);
  cudaSurfaceObject_t surface = 0;
  cudaTextureObject_t texture = 0;
  REQUIRE(cudaCreateSurfaceObject(&surface, &resource) == cudaSuccess);
  const auto defaults = runtimeTexture();
  REQUIRE(cudaCreateTextureObject(&texture, &resource, &defaults, nullptr) == cudaSuccess);
  CHECK(surface == 1);
  CHECK(texture == 2);

  CHECK(cudaCreateSurfaceObject(nullptr, &resource) == cudaErrorInvalidValue);
  CHECK(cudaCreateSurfaceObject(&surface, nullptr) == cudaErrorInvalidValue);
  CHECK(runtimeSurface(runtimeArray(nullptr)) == cudaErrorInvalidResourceHandle);
  void *memory = nullptr;
  REQUIRE(cudaMalloc(&memory, 4096) == cudaSuccess);
  CHECK(runtimeSurface(runtimeLinear(memory, channels(32, 1, cudaChannelFormatKindFloat), 1024)) == cudaErrorInvalidValue);

  cudaResourceDesc got{};
  REQUIRE(cudaGetSurfaceObjectResourceDesc(&got, surface) == cudaSuccess);
  CHECK(got.resType == cudaResourceTypeArray);
  CHECK(got.res.array.array == resource.res.array.array);
  CHECK(cudaGetSurfaceObjectResourceDesc(nullptr, surface) == cudaErrorInvalidDevice);
  CHECK(cudaGetSurfaceObjectResourceDesc(&got, texture) == cudaErrorInvalidValue);
  CHECK(cudaGetTextureObjectResourceDesc(&got, surface) == cudaErrorInvalidValue);
  REQUIRE(cudaDestroySurfaceObject(surface) == cudaSuccess);
  REQUIRE(cudaDestroyTextureObject(texture) == cudaSuccess);
  REQUIRE(cudaFree(memory) == cudaSuccess);
  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
}

TEST_CASE("channel descriptors describe arrays", "[cuda][texture]") {
  setup();
  auto *const array = makeArray(CU_AD_FORMAT_HALF, 4, 8, 8);
  const auto created = cudaCreateChannelDesc(8, 8, 8, 8, cudaChannelFormatKindUnsigned);
  CHECK(created.x == 8);
  CHECK(created.w == 8);
  CHECK(created.f == cudaChannelFormatKindUnsigned);
  cudaChannelFormatDesc described{};
  REQUIRE(cudaGetChannelDesc(&described, reinterpret_cast<cudaArray_t>(array)) == cudaSuccess);
  CHECK(described.x == 16);
  CHECK(described.y == 16);
  CHECK(described.z == 16);
  CHECK(described.w == 16);
  CHECK(described.f == cudaChannelFormatKindFloat);
  CHECK(cudaGetChannelDesc(nullptr, reinterpret_cast<cudaArray_t>(array)) == cudaErrorInvalidValue);
  CHECK(cudaGetChannelDesc(&described, nullptr) == cudaErrorInvalidResourceHandle);
  REQUIRE(cuArrayDestroy(array) == CUDA_SUCCESS);
}

namespace {

CUDA_ARRAY3D_DESCRIPTOR mipmappedDescriptor(size_t width, size_t height, size_t depth, unsigned flags = 0) {
  CUDA_ARRAY3D_DESCRIPTOR descriptor{};
  descriptor.Width = width;
  descriptor.Height = height;
  descriptor.Depth = depth;
  descriptor.Format = CU_AD_FORMAT_UNSIGNED_INT32;
  descriptor.NumChannels = 1;
  descriptor.Flags = flags;
  return descriptor;
}

CUresult copyRows(CUarray array, std::vector<uint32_t> &host, size_t width, size_t height, bool upload) {
  CUDA_MEMCPY2D copy{};
  copy.WidthInBytes = width * sizeof(uint32_t);
  copy.Height = height;
  if (upload) {
    copy.srcMemoryType = CU_MEMORYTYPE_HOST;
    copy.srcHost = host.data();
    copy.srcPitch = copy.WidthInBytes;
    copy.dstMemoryType = CU_MEMORYTYPE_ARRAY;
    copy.dstArray = array;
  } else {
    copy.srcMemoryType = CU_MEMORYTYPE_ARRAY;
    copy.srcArray = array;
    copy.dstMemoryType = CU_MEMORYTYPE_HOST;
    copy.dstHost = host.data();
    copy.dstPitch = copy.WidthInBytes;
  }
  return cuMemcpy2D_v2(&copy);
}

} // namespace

TEST_CASE("mipmapped arrays hold independent halving levels", "[cuda][texture]") {
  setup();
  constexpr size_t width = 13;
  constexpr size_t height = 6;
  constexpr unsigned levels = 3;
  const auto descriptor = mipmappedDescriptor(width, height, 0);
  CUmipmappedArray mipmapped = nullptr;
  REQUIRE(cuMipmappedArrayCreate(&mipmapped, &descriptor, levels) == CUDA_SUCCESS);
  REQUIRE(mipmapped != nullptr);

  std::array<CUarray, levels> handles{};
  std::array<std::vector<uint32_t>, levels> contents;
  for (unsigned level = 0; level < levels; ++level) {
    CAPTURE(level);
    const size_t levelWidth = std::max<size_t>(1, width >> level);
    const size_t levelHeight = std::max<size_t>(1, height >> level);
    REQUIRE(cuMipmappedArrayGetLevel(&handles[level], mipmapped, level) == CUDA_SUCCESS);
    CUDA_ARRAY3D_DESCRIPTOR described{};
    REQUIRE(cuArray3DGetDescriptor_v2(&described, handles[level]) == CUDA_SUCCESS);
    CHECK(described.Width == levelWidth);
    CHECK(described.Height == levelHeight);
    CHECK(described.Depth == 0);
    CHECK(described.Format == CU_AD_FORMAT_UNSIGNED_INT32);
    for (size_t index = 0; index < levelWidth * levelHeight; ++index)
      contents[level].push_back(static_cast<uint32_t>((level << 16) | index));
    REQUIRE(copyRows(handles[level], contents[level], levelWidth, levelHeight, true) == CUDA_SUCCESS);
  }
  for (unsigned level = 0; level < levels; ++level) {
    CAPTURE(level);
    const size_t levelWidth = std::max<size_t>(1, width >> level);
    const size_t levelHeight = std::max<size_t>(1, height >> level);
    std::vector<uint32_t> back(contents[level].size(), 0);
    REQUIRE(copyRows(handles[level], back, levelWidth, levelHeight, false) == CUDA_SUCCESS);
    CHECK(back == contents[level]);
  }
  CHECK(cuMipmappedArrayDestroy(mipmapped) == CUDA_SUCCESS);
}

TEST_CASE("mipmapped array volumes halve every axis and layered arrays keep their layers", "[cuda][texture]") {
  setup();
  CUarray level = nullptr;
  CUDA_ARRAY3D_DESCRIPTOR described{};
  CUmipmappedArray volume = nullptr;
  const auto volumeDescriptor = mipmappedDescriptor(8, 4, 6);
  REQUIRE(cuMipmappedArrayCreate(&volume, &volumeDescriptor, 4) == CUDA_SUCCESS);
  REQUIRE(cuMipmappedArrayGetLevel(&level, volume, 2) == CUDA_SUCCESS);
  REQUIRE(cuArray3DGetDescriptor_v2(&described, level) == CUDA_SUCCESS);
  CHECK(described.Width == 2);
  CHECK(described.Height == 1);
  CHECK(described.Depth == 1);
  CHECK(cuMipmappedArrayDestroy(volume) == CUDA_SUCCESS);

  CUmipmappedArray layered = nullptr;
  const auto layeredDescriptor = mipmappedDescriptor(8, 4, 5, CUDA_ARRAY3D_LAYERED);
  REQUIRE(cuMipmappedArrayCreate(&layered, &layeredDescriptor, 3) == CUDA_SUCCESS);
  REQUIRE(cuMipmappedArrayGetLevel(&level, layered, 2) == CUDA_SUCCESS);
  REQUIRE(cuArray3DGetDescriptor_v2(&described, level) == CUDA_SUCCESS);
  CHECK(described.Width == 2);
  CHECK(described.Height == 1);
  CHECK(described.Depth == 5);
  CHECK(cuMipmappedArrayDestroy(layered) == CUDA_SUCCESS);
}

TEST_CASE("mipmapped array argument checks", "[cuda][texture]") {
  setup();
  const auto descriptor = mipmappedDescriptor(13, 6, 0);
  CUmipmappedArray mipmapped = nullptr;
  CHECK(cuMipmappedArrayCreate(nullptr, &descriptor, 1) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMipmappedArrayCreate(&mipmapped, nullptr, 1) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMipmappedArrayCreate(&mipmapped, &descriptor, 0) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMipmappedArrayCreate(&mipmapped, &descriptor, 5) == CUDA_ERROR_INVALID_VALUE);
  REQUIRE(cuMipmappedArrayCreate(&mipmapped, &descriptor, 4) == CUDA_SUCCESS);
  CUarray level = nullptr;
  CHECK(cuMipmappedArrayGetLevel(nullptr, mipmapped, 0) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMipmappedArrayGetLevel(&level, nullptr, 0) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMipmappedArrayGetLevel(&level, mipmapped, 4) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMipmappedArrayDestroy(nullptr) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMipmappedArrayDestroy(mipmapped) == CUDA_SUCCESS);
}
