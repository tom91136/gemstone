#include <algorithm>
#include <cmath>
#include <cstdint>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <hip/hip_runtime.h>

#include "support.hpp"
#include <fmt/format.h>

using namespace aspartame;
using namespace hip_support;

// Texture objects as kernels sample them: address modes, filters, normalised and unnormalised coordinates, element and
// normalised read modes, 2D, 3D and layered arrays, pitched memory, gathers and explicit levels.
namespace {

constexpr int AddressWidth = 4;
const std::vector<float> AddressTexels = {10.0F, 20.0F, 30.0F, 40.0F};
const std::vector<int> AddressProbes = {-6, -5, -2, -1, 0, 1, 2, 3, 4, 5, 8, 9};

bool deviceFlag(hipDeviceAttribute_t attribute, std::string_view operation) {
  int supported = 0;
  check(hipDeviceGetAttribute(&supported, attribute, 0), operation);
  return supported != 0;
}

bool imageSupport() { return deviceFlag(hipDeviceAttributeImageSupport, "image support"); }

bool gatherSupport() { return deviceFlag(hipDeviceAttributeMaxTexture2DGather, "gather support"); }

void expectNear(const std::vector<float> &actual, const std::vector<float> &expected, float tolerance, std::string_view name) {
  if (actual.size() < expected.size())
    throw std::runtime_error(fmt::format("{}: {} results, expected {}", name, actual.size(), expected.size()));
  for (size_t i = 0; i < expected.size(); ++i)
    if (!(std::fabs(actual[i] - expected[i]) <= tolerance))
      throw std::runtime_error(fmt::format("{} mismatch at {}: expected {}, got {}", name, i, expected[i], actual[i]));
}

hipTextureDesc textureDesc(hipTextureAddressMode address, hipTextureFilterMode filter, hipTextureReadMode read, bool normalisedCoords) {
  hipTextureDesc desc{};
  desc.addressMode[0] = address;
  desc.addressMode[1] = address;
  desc.addressMode[2] = address;
  desc.filterMode = filter;
  desc.readMode = read;
  desc.normalizedCoords = normalisedCoords ? 1 : 0;
  return desc;
}

hipTextureObject_t makeTexture(const hipResourceDesc &resource, const hipTextureDesc &desc) {
  hipTextureObject_t texture{};
  check(hipCreateTextureObject(&texture, &resource, &desc, nullptr), "texture object");
  return texture;
}

hipResourceDesc arrayResource(hipArray_t array) {
  hipResourceDesc resource{};
  resource.resType = hipResourceTypeArray;
  resource.res.array.array = array;
  return resource;
}

template <typename T> hipArray_t createArray(size_t width, size_t height, size_t depth, unsigned flags = 0) {
  const hipChannelFormatDesc desc = hipCreateChannelDesc<T>();
  hipArray_t array = nullptr;
  if (depth == 0) check(hipMallocArray(&array, &desc, width, height, flags), "array");
  else check(hipMalloc3DArray(&array, &desc, make_hipExtent(width, height, depth), flags), "3D array");
  return array;
}

template <typename T> void uploadTexels(hipArray_t array, const std::vector<T> &texels, size_t width, size_t height, size_t depth) {
  hipMemcpy3DParms up{};
  up.srcPtr = make_hipPitchedPtr(const_cast<T *>(texels.data()), width * sizeof(T), width, std::max<size_t>(height, 1));
  up.dstArray = array;
  up.extent = make_hipExtent(width, std::max<size_t>(height, 1), std::max<size_t>(depth, 1));
  up.kind = hipMemcpyHostToDevice;
  check(hipMemcpy3D(&up), "texel upload");
}

struct Coordinate {
  float x, y, z;
};

template <int D, typename V, typename C, int N>
__global__ void sampleKernel([[maybe_unused]] hipTextureObject_t texture, [[maybe_unused]] const Coordinate *coords,
                             [[maybe_unused]] C *out, [[maybe_unused]] int count) {
#if !__HIP_NO_IMAGE_SUPPORT
  const int i = static_cast<int>(threadIdx.x);
  if (i >= count) return;
  const Coordinate c = coords[i];
  V value{};
  if constexpr (D == 1) value = tex1D<V>(texture, c.x);
  else if constexpr (D == 2) value = tex2D<V>(texture, c.x, c.y);
  else value = tex3D<V>(texture, c.x, c.y, c.z);
  const auto *parts = reinterpret_cast<const C *>(&value);
  for (int k = 0; k < N; ++k)
    out[(i * N) + k] = parts[k];
#endif
}

template <typename V, typename C, int N>
__global__ void layeredKernel([[maybe_unused]] hipTextureObject_t texture, [[maybe_unused]] const Coordinate *coords,
                              [[maybe_unused]] C *out, [[maybe_unused]] int count) {
#if !__HIP_NO_IMAGE_SUPPORT
  const int i = static_cast<int>(threadIdx.x);
  if (i >= count) return;
  const Coordinate c = coords[i];
  const V value = tex2DLayered<V>(texture, c.x, c.y, static_cast<int>(c.z));
  const auto *parts = reinterpret_cast<const C *>(&value);
  for (int k = 0; k < N; ++k)
    out[(i * N) + k] = parts[k];
#endif
}

template <typename V, typename C, int N>
__global__ void gatherKernel([[maybe_unused]] hipTextureObject_t texture, [[maybe_unused]] float x, [[maybe_unused]] float y,
                             [[maybe_unused]] int comp, [[maybe_unused]] C *out) {
#if !__HIP_NO_IMAGE_SUPPORT
  const V value = tex2Dgather<V>(texture, x, y, comp);
  const auto *parts = reinterpret_cast<const C *>(&value);
  for (int k = 0; k < N; ++k)
    out[k] = parts[k];
#endif
}

__global__ void lodKernel([[maybe_unused]] hipTextureObject_t texture, [[maybe_unused]] const Coordinate *coords,
                          [[maybe_unused]] float level, [[maybe_unused]] float *out, [[maybe_unused]] int count) {
#if !__HIP_NO_IMAGE_SUPPORT
  const int i = static_cast<int>(threadIdx.x);
  if (i >= count) return;
  out[i] = tex2DLod<float>(texture, coords[i].x, coords[i].y, level);
#endif
}

template <int D, typename V, typename C, int N>
std::vector<C> sampleTexture(hipTextureObject_t texture, const std::vector<Coordinate> &coords) {
  const DeviceBuffer<Coordinate> deviceCoords(coords);
  const auto count = static_cast<int>(coords.size());
  DeviceBuffer<C> out(std::vector<C>(coords.size() * N, C{}));
  sampleKernel<D, V, C, N><<<1, static_cast<unsigned>(count)>>>(texture, deviceCoords.data, out.data, count);
  finish("texture sample");
  return out.download();
}

template <typename V, typename C, int N> std::vector<C> layeredTexture(hipTextureObject_t texture, const std::vector<Coordinate> &coords) {
  const DeviceBuffer<Coordinate> deviceCoords(coords);
  const auto count = static_cast<int>(coords.size());
  DeviceBuffer<C> out(std::vector<C>(coords.size() * N, C{}));
  layeredKernel<V, C, N><<<1, static_cast<unsigned>(count)>>>(texture, deviceCoords.data, out.data, count);
  finish("layered sample");
  return out.download();
}

std::vector<Coordinate> texelCentres(size_t width, size_t height, size_t depth) {
  std::vector<Coordinate> coords;
  for (size_t z = 0; z < std::max<size_t>(depth, 1); ++z)
    for (size_t y = 0; y < std::max<size_t>(height, 1); ++y)
      for (size_t x = 0; x < width; ++x)
        coords.push_back({.x = static_cast<float>(x) + 0.5F, .y = static_cast<float>(y) + 0.5F, .z = static_cast<float>(z) + 0.5F});
  return coords;
}

std::vector<float> sampleAddressMode(hipTextureAddressMode mode) {
  hipArray_t array = createArray<float>(AddressWidth, 0, 0);
  uploadTexels(array, AddressTexels, AddressWidth, 0, 0);
  auto *const texture = makeTexture(arrayResource(array), textureDesc(mode, hipFilterModePoint, hipReadModeElementType, true));
  const auto coords =
      AddressProbes
      | map([](int k) { return Coordinate{.x = (static_cast<float>(k) + 0.5F) / static_cast<float>(AddressWidth), .y = 0.0F, .z = 0.0F}; })
      | to_vector();
  auto values = sampleTexture<1, float, float, 1>(texture, coords);
  check(hipDestroyTextureObject(texture), "destroy texture");
  check(hipFreeArray(array), "free array");
  return values;
}

int wrapIndex(int k) { return ((k % AddressWidth) + AddressWidth) % AddressWidth; }

int clampIndex(int k) { return std::clamp(k, 0, AddressWidth - 1); }

int mirrorIndex(int k) {
  const int period = 2 * AddressWidth;
  const int m = ((k % period) + period) % period;
  return m < AddressWidth ? m : period - 1 - m;
}

} // namespace

TEST_CASE("resources/hip/texture-wrap", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  const auto expected = AddressProbes | map([](int k) { return AddressTexels[static_cast<size_t>(wrapIndex(k))]; }) | to_vector();
  expectNear(sampleAddressMode(hipAddressModeWrap), expected, 0.0F, "wrap");
}

TEST_CASE("resources/hip/texture-clamp", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  const auto expected = AddressProbes | map([](int k) { return AddressTexels[static_cast<size_t>(clampIndex(k))]; }) | to_vector();
  expectNear(sampleAddressMode(hipAddressModeClamp), expected, 0.0F, "clamp");
}

TEST_CASE("resources/hip/texture-mirror", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  const auto expected = AddressProbes | map([](int k) { return AddressTexels[static_cast<size_t>(mirrorIndex(k))]; }) | to_vector();
  expectNear(sampleAddressMode(hipAddressModeMirror), expected, 0.0F, "mirror");
}

TEST_CASE("resources/hip/texture-border", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  const auto expected =
      AddressProbes | map([](int k) { return (k >= 0 && k < AddressWidth) ? AddressTexels[static_cast<size_t>(k)] : 0.0F; }) | to_vector();
  expectNear(sampleAddressMode(hipAddressModeBorder), expected, 0.0F, "border");
}

TEST_CASE("resources/hip/texture-filter", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  constexpr size_t width = 2;
  constexpr size_t height = 2;
  const std::vector<float> texels = {10.0F, 20.0F, 30.0F, 40.0F};
  hipArray_t array = createArray<float>(width, height, 0);
  uploadTexels(array, texels, width, height, 0);
  const std::vector<Coordinate> coords = {{.x = 1.25F, .y = 1.0F, .z = 0.0F}, {.x = 0.75F, .y = 0.25F, .z = 0.0F},
                                          {.x = 0.5F, .y = 0.5F, .z = 0.0F},  {.x = 1.5F, .y = 1.5F, .z = 0.0F},
                                          {.x = 1.2F, .y = 0.5F, .z = 0.0F},  {.x = 0.5F + 0.0033F, .y = 0.5F, .z = 0.0F}};
  auto *const point =
      makeTexture(arrayResource(array), textureDesc(hipAddressModeClamp, hipFilterModePoint, hipReadModeElementType, false));
  auto *const linear =
      makeTexture(arrayResource(array), textureDesc(hipAddressModeClamp, hipFilterModeLinear, hipReadModeElementType, false));
  expectNear(sampleTexture<2, float, float, 1>(point, coords), {40.0F, 10.0F, 10.0F, 40.0F, 20.0F, 10.0F}, 0.0F, "point filter");
  expectNear(sampleTexture<2, float, float, 1>(linear, coords), {27.5F, 12.5F, 10.0F, 40.0F, 16.9921875F, 10.0390625F}, 0.0F,
             "linear filter");
  auto *const normalisedLinear =
      makeTexture(arrayResource(array), textureDesc(hipAddressModeClamp, hipFilterModeLinear, hipReadModeElementType, true));
  expectNear(
      sampleTexture<2, float, float, 1>(
          normalisedLinear, {{.x = 0.625F, .y = 0.5F, .z = 0.0F}, {.x = 0.5F, .y = 0.5F, .z = 0.0F}, {.x = 0.25F, .y = 0.25F, .z = 0.0F}}),
      {27.5F, 25.0F, 10.0F}, 0.0F, "normalised linear filter");
  check(hipDestroyTextureObject(point), "destroy texture");
  check(hipDestroyTextureObject(linear), "destroy texture");
  check(hipDestroyTextureObject(normalisedLinear), "destroy texture");
  check(hipFreeArray(array), "free array");
}

TEST_CASE("resources/hip/texture-filter-wrap", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  const std::vector<float> texels = {10.0F, 20.0F, 30.0F, 40.0F};
  hipArray_t array = createArray<float>(AddressWidth, 0, 0);
  uploadTexels(array, texels, AddressWidth, 0, 0);
  auto *const wrapped =
      makeTexture(arrayResource(array), textureDesc(hipAddressModeWrap, hipFilterModeLinear, hipReadModeElementType, true));
  auto *const mirrored =
      makeTexture(arrayResource(array), textureDesc(hipAddressModeMirror, hipFilterModeLinear, hipReadModeElementType, true));
  auto *const border =
      makeTexture(arrayResource(array), textureDesc(hipAddressModeBorder, hipFilterModeLinear, hipReadModeElementType, true));
  const std::vector<Coordinate> coords = {{.x = 0.0F, .y = 0.0F, .z = 0.0F},
                                          {.x = 0.125F, .y = 0.0F, .z = 0.0F},
                                          {.x = 1.0F, .y = 0.0F, .z = 0.0F},
                                          {.x = 1.0625F, .y = 0.0F, .z = 0.0F}};
  expectNear(sampleTexture<1, float, float, 1>(wrapped, coords), {25.0F, 10.0F, 25.0F, 17.5F}, 0.0F, "wrap linear");
  expectNear(sampleTexture<1, float, float, 1>(mirrored, coords), {10.0F, 10.0F, 40.0F, 40.0F}, 0.0F, "mirror linear");
  expectNear(sampleTexture<1, float, float, 1>(border, coords), {5.0F, 10.0F, 20.0F, 10.0F}, 0.0F, "border linear");
  check(hipDestroyTextureObject(wrapped), "destroy texture");
  check(hipDestroyTextureObject(mirrored), "destroy texture");
  check(hipDestroyTextureObject(border), "destroy texture");
  check(hipFreeArray(array), "free array");
}

TEST_CASE("resources/hip/texture-3d", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  constexpr size_t width = 2;
  constexpr size_t height = 2;
  constexpr size_t depth = 2;
  const std::vector<float> texels = {1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F};
  hipArray_t array = createArray<float>(width, height, depth);
  uploadTexels(array, texels, width, height, depth);
  auto *const point =
      makeTexture(arrayResource(array), textureDesc(hipAddressModeClamp, hipFilterModePoint, hipReadModeElementType, false));
  expectNear(sampleTexture<3, float, float, 1>(point, texelCentres(width, height, depth)), texels, 0.0F, "3D point");
  auto *const linear =
      makeTexture(arrayResource(array), textureDesc(hipAddressModeClamp, hipFilterModeLinear, hipReadModeElementType, false));
  expectNear(sampleTexture<3, float, float, 1>(
                 linear, {{.x = 1.0F, .y = 1.0F, .z = 1.0F}, {.x = 0.5F, .y = 0.5F, .z = 0.5F}, {.x = 1.5F, .y = 0.5F, .z = 1.5F}}),
             {4.5F, 1.0F, 6.0F}, 0.0F, "3D linear");
  check(hipDestroyTextureObject(point), "destroy texture");
  check(hipDestroyTextureObject(linear), "destroy texture");
  check(hipFreeArray(array), "free array");
}

TEST_CASE("resources/hip/texture-layered", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  constexpr size_t width = 3;
  constexpr size_t height = 2;
  constexpr size_t layers = 3;
  const auto texels =
      iota(size_t{0}, width * height * layers) | map([](size_t i) { return static_cast<float>((i * 3) + 1); }) | to_vector();
  hipArray_t array = createArray<float>(width, height, layers, hipArrayLayered);
  uploadTexels(array, texels, width, height, layers);
  auto *const point =
      makeTexture(arrayResource(array), textureDesc(hipAddressModeClamp, hipFilterModePoint, hipReadModeElementType, false));
  std::vector<Coordinate> coords;
  std::vector<float> expected;
  for (size_t layer = 0; layer < layers; ++layer)
    for (size_t y = 0; y < height; ++y)
      for (size_t x = 0; x < width; ++x) {
        coords.push_back({.x = static_cast<float>(x) + 0.5F, .y = static_cast<float>(y) + 0.5F, .z = static_cast<float>(layer)});
        expected.push_back(texels[(((layer * height) + y) * width) + x]);
      }
  expectNear(layeredTexture<float, float, 1>(point, coords), expected, 0.0F, "layered point");
  auto *const linear =
      makeTexture(arrayResource(array), textureDesc(hipAddressModeClamp, hipFilterModeLinear, hipReadModeElementType, false));
  const auto at = [&](size_t layer, size_t y, size_t x) { return texels[(((layer * height) + y) * width) + x]; };
  expectNear(
      layeredTexture<float, float, 1>(linear, {{.x = 1.0F, .y = 1.0F, .z = 1.0F}, {.x = 2.0F, .y = 1.0F, .z = 2.0F}}),
      {(at(1, 0, 0) + at(1, 0, 1) + at(1, 1, 0) + at(1, 1, 1)) / 4.0F, (at(2, 0, 1) + at(2, 0, 2) + at(2, 1, 1) + at(2, 1, 2)) / 4.0F},
      0.0F, "layered linear stays in its layer");
  check(hipDestroyTextureObject(point), "destroy texture");
  check(hipDestroyTextureObject(linear), "destroy texture");
  check(hipFreeArray(array), "free array");
}

TEST_CASE("resources/hip/texture-read-mode", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  constexpr size_t width = 2;
  const std::vector<uchar4> bytes = {make_uchar4(0, 255, 128, 64), make_uchar4(255, 0, 1, 2)};
  const std::vector<float4> floats = {make_float4(1.5F, -2.0F, 3.25F, 4.0F), make_float4(-0.5F, 8.0F, 0.0F, 16.0F)};
  const std::vector<Coordinate> coords = {{.x = 0.5F, .y = 0.5F, .z = 0.0F}, {.x = 1.5F, .y = 0.5F, .z = 0.0F}};

  hipArray_t byteArray = createArray<uchar4>(width, 1, 0);
  uploadTexels(byteArray, bytes, width, 1, 0);
  auto *const normalisedTexture =
      makeTexture(arrayResource(byteArray), textureDesc(hipAddressModeClamp, hipFilterModePoint, hipReadModeNormalizedFloat, false));
  std::vector<float> normalised;
  for (const auto &texel : bytes)
    for (const float channel :
         {static_cast<float>(texel.x), static_cast<float>(texel.y), static_cast<float>(texel.z), static_cast<float>(texel.w)})
      normalised.push_back(channel / 255.0F);
  expectNear(sampleTexture<2, float4, float, 4>(normalisedTexture, coords), normalised, 1e-6F, "uchar4 normalised");
  auto *const elementTexture =
      makeTexture(arrayResource(byteArray), textureDesc(hipAddressModeClamp, hipFilterModePoint, hipReadModeElementType, false));
  const auto elements = sampleTexture<2, uchar4, unsigned char, 4>(elementTexture, coords);
  const std::vector<unsigned char> expectedBytes = {0, 255, 128, 64, 255, 0, 1, 2};
  for (size_t i = 0; i < expectedBytes.size(); ++i)
    if (elements[i] != expectedBytes[i]) throw std::runtime_error(fmt::format("uchar4 element mismatch at {}", i));

  hipArray_t floatArray = createArray<float4>(width, 1, 0);
  uploadTexels(floatArray, floats, width, 1, 0);
  auto *const floatTexture =
      makeTexture(arrayResource(floatArray), textureDesc(hipAddressModeClamp, hipFilterModePoint, hipReadModeElementType, false));
  expectNear(sampleTexture<2, float4, float, 4>(floatTexture, coords), {1.5F, -2.0F, 3.25F, 4.0F, -0.5F, 8.0F, 0.0F, 16.0F}, 0.0F,
             "float4");
  auto *const floatLinear =
      makeTexture(arrayResource(floatArray), textureDesc(hipAddressModeClamp, hipFilterModeLinear, hipReadModeElementType, false));
  expectNear(sampleTexture<2, float4, float, 4>(floatLinear, {{.x = 1.0F, .y = 0.5F, .z = 0.0F}}), {0.5F, 3.0F, 1.625F, 10.0F}, 0.0F,
             "float4 linear");
  check(hipDestroyTextureObject(normalisedTexture), "destroy texture");
  check(hipDestroyTextureObject(elementTexture), "destroy texture");
  check(hipDestroyTextureObject(floatTexture), "destroy texture");
  check(hipDestroyTextureObject(floatLinear), "destroy texture");
  check(hipFreeArray(byteArray), "free array");
  check(hipFreeArray(floatArray), "free array");
}

TEST_CASE("resources/hip/texture-pitch2d", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  constexpr size_t width = 5;
  constexpr size_t height = 3;
  const auto texels = iota(size_t{0}, width * height) | map([](size_t i) { return static_cast<float>(i * i) - 4.0F; }) | to_vector();
  constexpr size_t pitch = 256;
  void *device = nullptr;
  check(hipMalloc(&device, pitch * height), "pitched allocation");
  check(hipMemcpy2D(device, pitch, texels.data(), width * sizeof(float), width * sizeof(float), height, hipMemcpyHostToDevice), "upload");
  hipResourceDesc resource{};
  resource.resType = hipResourceTypePitch2D;
  resource.res.pitch2D.devPtr = device;
  resource.res.pitch2D.desc = hipCreateChannelDesc<float>();
  resource.res.pitch2D.width = width;
  resource.res.pitch2D.height = height;
  resource.res.pitch2D.pitchInBytes = pitch;
  auto *const point = makeTexture(resource, textureDesc(hipAddressModeClamp, hipFilterModePoint, hipReadModeElementType, false));
  expectNear(sampleTexture<2, float, float, 1>(point, texelCentres(width, height, 0)), texels, 0.0F, "pitch2D point");
  auto *const linear = makeTexture(resource, textureDesc(hipAddressModeClamp, hipFilterModeLinear, hipReadModeElementType, false));
  expectNear(sampleTexture<2, float, float, 1>(linear, {{.x = 1.0F, .y = 1.0F, .z = 0.0F}}),
             {(texels[0] + texels[1] + texels[5] + texels[6]) / 4.0F}, 0.0F, "pitch2D linear");
  check(hipDestroyTextureObject(point), "destroy texture");
  check(hipDestroyTextureObject(linear), "destroy texture");
  check(hipFree(device), "free");
}

TEST_CASE("resources/hip/texture-gather", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  if (!gatherSupport()) SKIP("the device has no gather or explicit-level image sampling");
  const std::vector<float4> texels = {make_float4(0.0F, 10.0F, 20.0F, 30.0F), make_float4(1.0F, 11.0F, 21.0F, 31.0F),
                                      make_float4(2.0F, 12.0F, 22.0F, 32.0F), make_float4(3.0F, 13.0F, 23.0F, 33.0F)};
  hipArray_t array = createArray<float4>(2, 2, 0, hipArrayTextureGather);
  uploadTexels(array, texels, 2, 2, 0);
  auto *const texture =
      makeTexture(arrayResource(array), textureDesc(hipAddressModeClamp, hipFilterModePoint, hipReadModeElementType, false));
  // HIP numbers the components 1 to 3 as red, green and blue, and any other as alpha
  for (int comp = 0; comp < 4; ++comp) {
    CAPTURE(comp);
    const float base = 10.0F * static_cast<float>((comp + 3) % 4);
    DeviceBuffer<float> out(std::vector<float>(4, 0.0F));
    gatherKernel<float4, float, 4><<<1, 1>>>(texture, 1.0F, 1.0F, comp, out.data);
    finish("texture gather");
    expectNear(out.download(), {base + 2.0F, base + 3.0F, base + 1.0F, base + 0.0F}, 0.0F, "gather");
  }
  check(hipDestroyTextureObject(texture), "destroy texture");
  check(hipFreeArray(array), "free array");
}

TEST_CASE("resources/hip/texture-lod", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  if (!gatherSupport()) SKIP("the device has no gather or explicit-level image sampling");
  constexpr size_t width = 2;
  const std::vector<float> texels = {10.0F, 20.0F, 30.0F, 40.0F};
  hipArray_t array = createArray<float>(width, 2, 0);
  uploadTexels(array, texels, width, 2, 0);
  auto *const linear =
      makeTexture(arrayResource(array), textureDesc(hipAddressModeClamp, hipFilterModeLinear, hipReadModeElementType, false));
  const std::vector<Coordinate> coords = {{.x = 1.0F, .y = 1.0F, .z = 0.0F}, {.x = 0.5F, .y = 0.5F, .z = 0.0F}};
  const DeviceBuffer<Coordinate> deviceCoords(coords);
  for (const float level : {0.0F, 3.0F}) {
    DeviceBuffer<float> out(std::vector<float>(coords.size(), 0.0F));
    lodKernel<<<1, 2>>>(linear, deviceCoords.data, level, out.data, static_cast<int>(coords.size()));
    finish("texture lod");
    expectNear(out.download(), {25.0F, 10.0F}, 0.0F, fmt::format("level {}", level));
  }
  check(hipDestroyTextureObject(linear), "destroy texture");
  check(hipFreeArray(array), "free array");
}
