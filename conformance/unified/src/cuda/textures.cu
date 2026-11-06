#include <algorithm>
#include <cmath>
#include <stdexcept>
#include <vector>

#include "cuda/support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <cuda_runtime.h>

using namespace aspartame;

namespace {

constexpr int AddressWidth = 4;
constexpr float BorderValue = 99.0F;
const std::vector<float> AddressTexels = {10.0F, 20.0F, 30.0F, 40.0F};
const std::vector<int> AddressProbes = {-2, -1, 0, 1, 2, 3, 4, 5};

void expectNear(const std::vector<float> &actual, const std::vector<float> &expected, float tolerance, std::string_view name) {
  for (size_t i = 0; i < expected.size(); ++i)
    if (!(std::fabs(actual[i] - expected[i]) <= tolerance))
      throw std::runtime_error(fmt::format("{} mismatch at {}: expected {}, got {}", name, i, expected[i], actual[i]));
}

template <typename C, typename V> std::vector<C> flattenComponents(const std::vector<V> &texels) {
  std::vector<C> values;
  for (const V &texel : texels) {
    const auto *parts = reinterpret_cast<const C *>(&texel);
    values.insert(values.end(), parts, parts + (sizeof(V) / sizeof(C)));
  }
  return values;
}

template <typename T> std::vector<float> normalised(const std::vector<T> &values, float scale) {
  return values | map([scale](T value) { return static_cast<float>(value) / scale; }) | to_vector();
}

std::vector<float3> texelCentres(size_t width, size_t height, size_t depth) {
  std::vector<float3> coords;
  for (size_t z = 0; z < depth; ++z)
    for (size_t y = 0; y < height; ++y)
      for (size_t x = 0; x < width; ++x)
        coords.push_back(make_float3(static_cast<float>(x) + 0.5F, static_cast<float>(y) + 0.5F, static_cast<float>(z) + 0.5F));
  return coords;
}

template <typename T> std::vector<int3> surfaceCoords(size_t width, size_t height, size_t depth) {
  std::vector<int3> coords;
  for (size_t z = 0; z < depth; ++z)
    for (size_t y = 0; y < height; ++y)
      for (size_t x = 0; x < width; ++x)
        coords.push_back(make_int3(static_cast<int>(x * sizeof(T)), static_cast<int>(y), static_cast<int>(z)));
  return coords;
}

cudaTextureDesc textureDesc(cudaTextureAddressMode address, cudaTextureFilterMode filter, cudaTextureReadMode read, bool normalisedCoords,
                            float border = 0.0F) {
  cudaTextureDesc desc{};
  desc.addressMode[0] = address;
  desc.addressMode[1] = address;
  desc.addressMode[2] = address;
  desc.filterMode = filter;
  desc.readMode = read;
  desc.normalizedCoords = normalisedCoords ? 1 : 0;
  for (float &channel : desc.borderColor)
    channel = border;
  return desc;
}

cudaTextureObject_t makeTexture(const cudaResourceDesc &resource, const cudaTextureDesc &desc) {
  cudaTextureObject_t texture = 0;
  check(cudaCreateTextureObject(&texture, &resource, &desc, nullptr), "texture object");
  return texture;
}

cudaResourceDesc arrayResource(cudaArray_t array) {
  cudaResourceDesc resource{};
  resource.resType = cudaResourceTypeArray;
  resource.res.array.array = array;
  return resource;
}

template <typename T> cudaResourceDesc linearResource(const DeviceBuffer<T> &buffer) {
  cudaResourceDesc resource{};
  resource.resType = cudaResourceTypeLinear;
  resource.res.linear.devPtr = buffer.data;
  resource.res.linear.desc = cudaCreateChannelDesc<T>();
  resource.res.linear.sizeInBytes = buffer.size * sizeof(T);
  return resource;
}

template <typename T> cudaResourceDesc pitchResource(void *devPtr, size_t pitch, size_t width, size_t height) {
  cudaResourceDesc resource{};
  resource.resType = cudaResourceTypePitch2D;
  resource.res.pitch2D.devPtr = devPtr;
  resource.res.pitch2D.desc = cudaCreateChannelDesc<T>();
  resource.res.pitch2D.width = width;
  resource.res.pitch2D.height = height;
  resource.res.pitch2D.pitchInBytes = pitch;
  return resource;
}

template <typename T> cudaArray_t createArray(size_t width, size_t height, size_t depth, unsigned flags = 0) {
  const cudaChannelFormatDesc desc = cudaCreateChannelDesc<T>();
  cudaArray_t array = nullptr;
  check(cudaMalloc3DArray(&array, &desc, make_cudaExtent(width, height, depth), flags), "array");
  return array;
}

cudaExtent copyExtent(size_t width, size_t height, size_t depth) {
  return make_cudaExtent(width, std::max<size_t>(height, 1), std::max<size_t>(depth, 1));
}

template <typename T> void uploadTexels(cudaArray_t array, std::vector<T> texels, size_t width, size_t height, size_t depth) {
  cudaMemcpy3DParms copy{};
  copy.srcPtr = make_cudaPitchedPtr(texels.data(), width * sizeof(T), width, std::max<size_t>(height, 1));
  copy.dstArray = array;
  copy.extent = copyExtent(width, height, depth);
  copy.kind = cudaMemcpyHostToDevice;
  check(cudaMemcpy3D(&copy), "array upload");
}

template <typename T> std::vector<T> downloadTexels(cudaArray_t array, size_t width, size_t height, size_t depth) {
  std::vector<T> texels(width * std::max<size_t>(height, 1) * std::max<size_t>(depth, 1));
  cudaMemcpy3DParms copy{};
  copy.srcArray = array;
  copy.dstPtr = make_cudaPitchedPtr(texels.data(), width * sizeof(T), width, std::max<size_t>(height, 1));
  copy.extent = copyExtent(width, height, depth);
  copy.kind = cudaMemcpyDeviceToHost;
  check(cudaMemcpy3D(&copy), "array download");
  return texels;
}

cudaSurfaceObject_t makeSurface(cudaArray_t array) {
  const cudaResourceDesc resource = arrayResource(array);
  cudaSurfaceObject_t surface = 0;
  check(cudaCreateSurfaceObject(&surface, &resource), "surface object");
  return surface;
}

template <typename V, typename C, int N> __global__ void fetchKernel(cudaTextureObject_t texture, C *out, int count) {
  const int i = static_cast<int>(threadIdx.x);
  if (i >= count) return;
  const V value = tex1Dfetch<V>(texture, i);
  const auto *parts = reinterpret_cast<const C *>(&value);
  for (int k = 0; k < N; ++k)
    out[(i * N) + k] = parts[k];
}

template <int D, typename V, typename C, int N>
__global__ void sampleKernel(cudaTextureObject_t texture, const float3 *coords, C *out, int count) {
  const int i = static_cast<int>(threadIdx.x);
  if (i >= count) return;
  const float3 c = coords[i];
  V value{};
  if constexpr (D == 1) value = tex1D<V>(texture, c.x);
  else if constexpr (D == 2) value = tex2D<V>(texture, c.x, c.y);
  else value = tex3D<V>(texture, c.x, c.y, c.z);
  const auto *parts = reinterpret_cast<const C *>(&value);
  for (int k = 0; k < N; ++k)
    out[(i * N) + k] = parts[k];
}

template <typename V, typename C, int N> __global__ void gatherKernel(cudaTextureObject_t texture, float x, float y, int comp, C *out) {
  const V value = tex2Dgather<V>(texture, x, y, comp);
  const auto *parts = reinterpret_cast<const C *>(&value);
  for (int k = 0; k < N; ++k)
    out[k] = parts[k];
}

template <int D, typename T>
__global__ void surfaceWriteKernel(cudaSurfaceObject_t surface, const int3 *coords, const T *values, cudaSurfaceBoundaryMode mode,
                                   int count) {
  const int i = static_cast<int>(threadIdx.x);
  if (i >= count) return;
  const int3 c = coords[i];
  if constexpr (D == 1) surf1Dwrite(values[i], surface, c.x, mode);
  else if constexpr (D == 2) surf2Dwrite(values[i], surface, c.x, c.y, mode);
  else surf3Dwrite(values[i], surface, c.x, c.y, c.z, mode);
}

template <int D, typename T>
__global__ void surfaceReadKernel(cudaSurfaceObject_t surface, const int3 *coords, cudaSurfaceBoundaryMode mode, T *out, int count) {
  const int i = static_cast<int>(threadIdx.x);
  if (i >= count) return;
  const int3 c = coords[i];
  if constexpr (D == 1) out[i] = surf1Dread<T>(surface, c.x, mode);
  else if constexpr (D == 2) out[i] = surf2Dread<T>(surface, c.x, c.y, mode);
  else out[i] = surf3Dread<T>(surface, c.x, c.y, c.z, mode);
}

template <typename V, typename C, int N, typename T> std::vector<C> fetchLinear(const DeviceBuffer<T> &buffer, cudaTextureReadMode mode) {
  const cudaTextureObject_t texture =
      makeTexture(linearResource(buffer), textureDesc(cudaAddressModeClamp, cudaFilterModePoint, mode, false));
  DeviceBuffer<C> out(std::vector<C>(buffer.size * N, C{}));
  const auto count = static_cast<int>(buffer.size);
  fetchKernel<V, C, N><<<1, static_cast<unsigned>(count)>>>(texture, out.data, count);
  finish("linear fetch");
  check(cudaDestroyTextureObject(texture), "destroy texture");
  return out.download();
}

template <int D, typename V, typename C, int N>
std::vector<C> sampleTexture(cudaTextureObject_t texture, const std::vector<float3> &coords) {
  const DeviceBuffer<float3> deviceCoords(coords);
  const auto count = static_cast<int>(coords.size());
  DeviceBuffer<C> out(std::vector<C>(coords.size() * N, C{}));
  sampleKernel<D, V, C, N><<<1, static_cast<unsigned>(count)>>>(texture, deviceCoords.data, out.data, count);
  finish("texture sample");
  return out.download();
}

template <typename V, typename C, int N> std::vector<C> gatherTexture(cudaTextureObject_t texture, float x, float y, int comp) {
  DeviceBuffer<C> out(std::vector<C>(N, C{}));
  gatherKernel<V, C, N><<<1, 1>>>(texture, x, y, comp, out.data);
  finish("texture gather");
  return out.download();
}

template <int D, typename T>
void writeSurface(cudaSurfaceObject_t surface, const std::vector<int3> &coords, const std::vector<T> &values,
                  cudaSurfaceBoundaryMode mode) {
  const DeviceBuffer<int3> deviceCoords(coords);
  const DeviceBuffer<T> deviceValues(values);
  const auto count = static_cast<int>(values.size());
  surfaceWriteKernel<D, T><<<1, static_cast<unsigned>(count)>>>(surface, deviceCoords.data, deviceValues.data, mode, count);
  finish("surface write");
}

template <int D, typename T>
std::vector<T> readSurface(cudaSurfaceObject_t surface, const std::vector<int3> &coords, cudaSurfaceBoundaryMode mode) {
  const DeviceBuffer<int3> deviceCoords(coords);
  const auto count = static_cast<int>(coords.size());
  DeviceBuffer<T> out(std::vector<T>(coords.size(), T{}));
  surfaceReadKernel<D, T><<<1, static_cast<unsigned>(count)>>>(surface, deviceCoords.data, mode, out.data, count);
  finish("surface read");
  return out.download();
}

std::vector<float> sampleAddressMode(cudaTextureAddressMode mode) {
  cudaArray_t array = createArray<float>(AddressWidth, 0, 0);
  uploadTexels(array, AddressTexels, AddressWidth, 0, 0);
  const cudaTextureObject_t texture =
      makeTexture(arrayResource(array), textureDesc(mode, cudaFilterModePoint, cudaReadModeElementType, true, BorderValue));
  const auto coords =
      AddressProbes | map([](int k) { return make_float3((static_cast<float>(k) + 0.5F) / static_cast<float>(AddressWidth), 0.0F, 0.0F); })
      | to_vector();
  std::vector<float> values = sampleTexture<1, float, float, 1>(texture, coords);
  check(cudaDestroyTextureObject(texture), "destroy texture");
  check(cudaFreeArray(array), "free array");
  return values;
}

int wrapIndex(int k) { return ((k % AddressWidth) + AddressWidth) % AddressWidth; }

int clampIndex(int k) { return std::clamp(k, 0, AddressWidth - 1); }

int mirrorIndex(int k) {
  const int period = 2 * AddressWidth;
  const int m = ((k % period) + period) % period;
  return m < AddressWidth ? m : period - 1 - m;
}

constexpr int MipExtent = 4;
constexpr int MipLevels = 3;
const std::vector<float3> MipProbes = {make_float3(0.375F, 0.625F, 0.0F), make_float3(0.875F, 0.125F, 0.0F),
                                       make_float3(0.625F, 0.875F, 0.0F)};

__global__ void lodKernel(cudaTextureObject_t texture, const float3 *coords, float level, float *out, int count) {
  const int i = static_cast<int>(threadIdx.x);
  if (i < count) out[i] = tex2DLod<float>(texture, coords[i].x, coords[i].y, level);
}

__global__ void gradKernel(cudaTextureObject_t texture, const float3 *coords, float2 dx, float2 dy, float *out, int count) {
  const int i = static_cast<int>(threadIdx.x);
  if (i < count) out[i] = tex2DGrad<float>(texture, coords[i].x, coords[i].y, dx, dy);
}

float mipTexel(int level, const float3 &coord) {
  const int extent = MipExtent >> level;
  const int x = static_cast<int>(coord.x * static_cast<float>(extent));
  const int y = static_cast<int>(coord.y * static_cast<float>(extent));
  return (100.0F * static_cast<float>(level)) + static_cast<float>((y * extent) + x);
}

std::vector<float> mipTexels(int level) {
  return MipProbes | map([level](const float3 &coord) { return mipTexel(level, coord); }) | to_vector();
}

std::vector<float> mipBlend(int lower, float weight) {
  const auto from = mipTexels(lower);
  const auto to = mipTexels(lower + 1);
  std::vector<float> blended;
  for (size_t i = 0; i < from.size(); ++i)
    blended.push_back(from[i] + ((to[i] - from[i]) * weight));
  return blended;
}

cudaMipmappedArray_t createMipmappedArray() {
  const cudaChannelFormatDesc desc = cudaCreateChannelDesc<float>();
  cudaMipmappedArray_t mipmapped = nullptr;
  check(cudaMallocMipmappedArray(&mipmapped, &desc, make_cudaExtent(MipExtent, MipExtent, 0), MipLevels), "mipmapped array");
  for (int level = 0; level < MipLevels; ++level) {
    cudaArray_t array = nullptr;
    check(cudaGetMipmappedArrayLevel(&array, mipmapped, static_cast<unsigned>(level)), "mipmap level");
    const auto extent = static_cast<size_t>(MipExtent >> level);
    std::vector<float> texels(extent * extent);
    for (size_t i = 0; i < texels.size(); ++i)
      texels[i] = (100.0F * static_cast<float>(level)) + static_cast<float>(i);
    uploadTexels(array, texels, extent, extent, 0);
  }
  return mipmapped;
}

cudaTextureObject_t makeMipTexture(cudaMipmappedArray_t mipmapped, cudaTextureFilterMode mipFilter) {
  cudaResourceDesc resource{};
  resource.resType = cudaResourceTypeMipmappedArray;
  resource.res.mipmap.mipmap = mipmapped;
  cudaTextureDesc desc = textureDesc(cudaAddressModeClamp, cudaFilterModePoint, cudaReadModeElementType, true);
  desc.mipmapFilterMode = mipFilter;
  desc.maxMipmapLevelClamp = static_cast<float>(MipLevels - 1);
  return makeTexture(resource, desc);
}

std::vector<float> sampleLod(cudaTextureObject_t texture, float level) {
  const DeviceBuffer<float3> deviceCoords(MipProbes);
  const auto count = static_cast<int>(MipProbes.size());
  DeviceBuffer<float> out(std::vector<float>(MipProbes.size(), 0.0F));
  lodKernel<<<1, static_cast<unsigned>(count)>>>(texture, deviceCoords.data, level, out.data, count);
  finish("mipmap level sample");
  return out.download();
}

std::vector<float> sampleGradient(cudaTextureObject_t texture, float2 dx, float2 dy) {
  const DeviceBuffer<float3> deviceCoords(MipProbes);
  const auto count = static_cast<int>(MipProbes.size());
  DeviceBuffer<float> out(std::vector<float>(MipProbes.size(), 0.0F));
  gradKernel<<<1, static_cast<unsigned>(count)>>>(texture, deviceCoords.data, dx, dy, out.data, count);
  finish("mipmap gradient sample");
  return out.download();
}

} // namespace

TEST_CASE("resources/cuda/texture-linear-fetch", "[unified][resources]") {
  const std::vector<float> floats = {1.0F, 2.5F, -3.0F, 4.0F};
  const std::vector<uchar4> bytes = {make_uchar4(0, 255, 128, 64), make_uchar4(255, 0, 1, 2)};
  const std::vector<int2> pairs = {make_int2(-1, 7), make_int2(1 << 20, -(1 << 20))};
  const std::vector<int> ints = {-1, 7, 1 << 20, -(1 << 20)};

  const DeviceBuffer<float> deviceFloats(floats);
  expectNear(fetchLinear<float, float, 1>(deviceFloats, cudaReadModeElementType), floats, 0.0F, "float fetch");

  const DeviceBuffer<uchar4> deviceBytes(bytes);
  expectNear(fetchLinear<float4, float, 4>(deviceBytes, cudaReadModeNormalizedFloat),
             normalised(flattenComponents<unsigned char>(bytes), 255.0F), 1e-6F, "uchar4 normalised fetch");

  const DeviceBuffer<int2> devicePairs(pairs);
  expectEqual(fetchLinear<int2, int, 2>(devicePairs, cudaReadModeElementType), ints, "int2 fetch");
}

TEST_CASE("resources/cuda/texture-pitch2d", "[unified][resources]") {
  constexpr size_t width = 3;
  constexpr size_t height = 2;
  const std::vector<float> texels = {1.5F, -2.0F, 3.25F, 4.0F, 5.5F, -6.0F};
  void *device = nullptr;
  size_t pitch = 0;
  check(cudaMallocPitch(&device, &pitch, width * sizeof(float), height), "pitched allocation");
  check(cudaMemcpy2D(device, pitch, texels.data(), width * sizeof(float), width * sizeof(float), height, cudaMemcpyHostToDevice),
        "pitched upload");

  const cudaTextureObject_t texture = makeTexture(pitchResource<float>(device, pitch, width, height),
                                                  textureDesc(cudaAddressModeClamp, cudaFilterModePoint, cudaReadModeElementType, false));
  expectNear(sampleTexture<2, float, float, 1>(texture, texelCentres(width, height, 1)), texels, 0.0F, "pitch2D point");
  check(cudaDestroyTextureObject(texture), "destroy texture");
  check(cudaFree(device), "free");
}

TEST_CASE("resources/cuda/texture-filter", "[unified][resources]") {
  constexpr size_t width = 2;
  constexpr size_t height = 2;
  const std::vector<float> texels = {10.0F, 20.0F, 30.0F, 40.0F};
  cudaArray_t array = createArray<float>(width, height, 0);
  uploadTexels(array, texels, width, height, 0);
  const std::vector<float3> coords = {make_float3(1.25F, 1.0F, 0.0F), make_float3(0.75F, 0.25F, 0.0F)};

  const cudaTextureObject_t point =
      makeTexture(arrayResource(array), textureDesc(cudaAddressModeClamp, cudaFilterModePoint, cudaReadModeElementType, false));
  const cudaTextureObject_t linear =
      makeTexture(arrayResource(array), textureDesc(cudaAddressModeClamp, cudaFilterModeLinear, cudaReadModeElementType, false));
  expectNear(sampleTexture<2, float, float, 1>(point, coords), {40.0F, 10.0F}, 0.0F, "point filter");
  expectNear(sampleTexture<2, float, float, 1>(linear, coords), {27.5F, 12.5F}, 0.0F, "linear filter");
  check(cudaDestroyTextureObject(point), "destroy texture");
  check(cudaDestroyTextureObject(linear), "destroy texture");
  check(cudaFreeArray(array), "free array");
}

TEST_CASE("resources/cuda/texture-array-3d", "[unified][resources]") {
  constexpr size_t width = 2;
  constexpr size_t height = 2;
  constexpr size_t depth = 2;
  const std::vector<float> texels = {1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F};
  cudaArray_t array = createArray<float>(width, height, depth);
  uploadTexels(array, texels, width, height, depth);

  const cudaTextureObject_t point =
      makeTexture(arrayResource(array), textureDesc(cudaAddressModeClamp, cudaFilterModePoint, cudaReadModeElementType, false));
  expectNear(sampleTexture<3, float, float, 1>(point, texelCentres(width, height, depth)), texels, 0.0F, "3D point");

  const cudaTextureObject_t linear =
      makeTexture(arrayResource(array), textureDesc(cudaAddressModeClamp, cudaFilterModeLinear, cudaReadModeElementType, false));
  expectNear(sampleTexture<3, float, float, 1>(linear, {make_float3(1.0F, 1.0F, 1.0F)}), {4.5F}, 0.0F, "3D linear");
  check(cudaDestroyTextureObject(point), "destroy texture");
  check(cudaDestroyTextureObject(linear), "destroy texture");
  check(cudaFreeArray(array), "free array");
}

TEST_CASE("resources/cuda/texture-mipmap-level", "[unified][resources]") {
  cudaMipmappedArray_t mipmapped = createMipmappedArray();
  const cudaTextureObject_t nearest = makeMipTexture(mipmapped, cudaFilterModePoint);
  const cudaTextureObject_t blended = makeMipTexture(mipmapped, cudaFilterModeLinear);
  for (int level = 0; level < MipLevels; ++level)
    expectNear(sampleLod(nearest, static_cast<float>(level)), mipTexels(level), 0.0F, "explicit level");
  expectNear(sampleLod(nearest, 0.4F), mipTexels(0), 0.0F, "nearest rounds down");
  expectNear(sampleLod(nearest, 0.5F), mipTexels(1), 0.0F, "nearest ties go up");
  expectNear(sampleLod(nearest, 9.0F), mipTexels(MipLevels - 1), 0.0F, "level clamps to the chain");
  expectNear(sampleLod(blended, 0.5F), mipBlend(0, 0.5F), 1e-3F, "blend of levels 0 and 1");
  expectNear(sampleLod(blended, 1.5F), mipBlend(1, 0.5F), 1e-3F, "blend of levels 1 and 2");
  check(cudaDestroyTextureObject(nearest), "destroy texture");
  check(cudaDestroyTextureObject(blended), "destroy texture");
  check(cudaFreeMipmappedArray(mipmapped), "free mipmapped array");
}

TEST_CASE("resources/cuda/texture-mipmap-gradient", "[unified][resources]") {
  cudaMipmappedArray_t mipmapped = createMipmappedArray();
  const cudaTextureObject_t nearest = makeMipTexture(mipmapped, cudaFilterModePoint);
  const cudaTextureObject_t blended = makeMipTexture(mipmapped, cudaFilterModeLinear);
  expectNear(sampleGradient(nearest, make_float2(0.25F, 0.0F), make_float2(0.0F, 0.25F)), mipTexels(0), 0.0F, "one texel per pixel");
  expectNear(sampleGradient(nearest, make_float2(0.5F, 0.0F), make_float2(0.0F, 0.25F)), mipTexels(1), 0.0F, "two texels along x");
  expectNear(sampleGradient(nearest, make_float2(0.25F, 0.0F), make_float2(0.0F, 0.5F)), mipTexels(1), 0.0F, "two texels along y");
  expectNear(sampleGradient(nearest, make_float2(1.0F, 0.0F), make_float2(0.0F, 0.0F)), mipTexels(2), 0.0F, "four texels along x");
  expectNear(sampleGradient(blended, make_float2(0.375F, 0.0F), make_float2(0.0F, 0.0F)), mipBlend(0, 150.0F / 256.0F), 1e-2F,
             "fractional level from the gradient");
  check(cudaDestroyTextureObject(nearest), "destroy texture");
  check(cudaDestroyTextureObject(blended), "destroy texture");
  check(cudaFreeMipmappedArray(mipmapped), "free mipmapped array");
}

TEST_CASE("resources/cuda/texture-wrap", "[unified][resources]") {
  const auto expected = AddressProbes | map([](int k) { return AddressTexels[static_cast<size_t>(wrapIndex(k))]; }) | to_vector();
  expectNear(sampleAddressMode(cudaAddressModeWrap), expected, 0.0F, "wrap");
}

TEST_CASE("resources/cuda/texture-clamp", "[unified][resources]") {
  const auto expected = AddressProbes | map([](int k) { return AddressTexels[static_cast<size_t>(clampIndex(k))]; }) | to_vector();
  expectNear(sampleAddressMode(cudaAddressModeClamp), expected, 0.0F, "clamp");
}

TEST_CASE("resources/cuda/texture-mirror", "[unified][resources]") {
  const auto expected = AddressProbes | map([](int k) { return AddressTexels[static_cast<size_t>(mirrorIndex(k))]; }) | to_vector();
  expectNear(sampleAddressMode(cudaAddressModeMirror), expected, 0.0F, "mirror");
}

TEST_CASE("resources/cuda/texture-border", "[unified][resources]") {
  const auto expected = AddressProbes
                        | map([](int k) { return (k >= 0 && k < AddressWidth) ? AddressTexels[static_cast<size_t>(k)] : BorderValue; })
                        | to_vector();
  expectNear(sampleAddressMode(cudaAddressModeBorder), expected, 0.0F, "border");
}

TEST_CASE("resources/cuda/texture-read-mode", "[unified][resources]") {
  const std::vector<unsigned char> bytes = {0, 1, 128, 255};
  const std::vector<unsigned short> shorts = {0, 1, 32768, 65535};
  const DeviceBuffer<unsigned char> deviceBytes(bytes);
  const DeviceBuffer<unsigned short> deviceShorts(shorts);

  expectEqual(fetchLinear<unsigned char, unsigned char, 1>(deviceBytes, cudaReadModeElementType), bytes, "8-bit element type");
  expectNear(fetchLinear<float, float, 1>(deviceBytes, cudaReadModeNormalizedFloat), normalised(bytes, 255.0F), 1e-6F, "8-bit normalised");
  expectEqual(fetchLinear<unsigned short, unsigned short, 1>(deviceShorts, cudaReadModeElementType), shorts, "16-bit element type");
  expectNear(fetchLinear<float, float, 1>(deviceShorts, cudaReadModeNormalizedFloat), normalised(shorts, 65535.0F), 1e-6F,
             "16-bit normalised");
}

TEST_CASE("resources/cuda/texture-rgba", "[unified][resources]") {
  constexpr size_t width = 2;
  const std::vector<uchar4> bytes = {make_uchar4(0, 255, 128, 64), make_uchar4(255, 0, 1, 2)};
  const std::vector<float4> floats = {make_float4(1.5F, -2.0F, 3.25F, 4.0F), make_float4(-0.5F, 8.0F, 0.0F, 16.0F)};
  const std::vector<float3> coords = {make_float3(0.5F, 0.5F, 0.0F), make_float3(1.5F, 0.5F, 0.0F)};

  cudaArray_t byteArray = createArray<uchar4>(width, 1, 0);
  uploadTexels(byteArray, bytes, width, 1, 0);
  const cudaTextureObject_t normalisedTexture =
      makeTexture(arrayResource(byteArray), textureDesc(cudaAddressModeClamp, cudaFilterModePoint, cudaReadModeNormalizedFloat, false));
  expectNear(sampleTexture<2, float4, float, 4>(normalisedTexture, coords), normalised(flattenComponents<unsigned char>(bytes), 255.0F),
             1e-6F, "uchar4 normalised");

  cudaArray_t floatArray = createArray<float4>(width, 1, 0);
  uploadTexels(floatArray, floats, width, 1, 0);
  const cudaTextureObject_t floatTexture =
      makeTexture(arrayResource(floatArray), textureDesc(cudaAddressModeClamp, cudaFilterModePoint, cudaReadModeElementType, false));
  expectNear(sampleTexture<2, float4, float, 4>(floatTexture, coords), flattenComponents<float>(floats), 0.0F, "float4");

  check(cudaDestroyTextureObject(normalisedTexture), "destroy texture");
  check(cudaDestroyTextureObject(floatTexture), "destroy texture");
  check(cudaFreeArray(byteArray), "free array");
  check(cudaFreeArray(floatArray), "free array");
}

TEST_CASE("resources/cuda/texture-gather", "[unified][resources]") {
  const std::vector<float4> texels = {make_float4(0.0F, 10.0F, 20.0F, 30.0F), make_float4(1.0F, 11.0F, 21.0F, 31.0F),
                                      make_float4(2.0F, 12.0F, 22.0F, 32.0F), make_float4(3.0F, 13.0F, 23.0F, 33.0F)};
  cudaArray_t array = createArray<float4>(2, 2, 0, cudaArrayTextureGather);
  uploadTexels(array, texels, 2, 2, 0);
  const cudaTextureObject_t texture =
      makeTexture(arrayResource(array), textureDesc(cudaAddressModeClamp, cudaFilterModePoint, cudaReadModeElementType, false));

  for (int comp = 0; comp < 4; ++comp) {
    CAPTURE(comp);
    const float base = 10.0F * static_cast<float>(comp);
    expectNear(gatherTexture<float4, float, 4>(texture, 1.0F, 1.0F, comp), {base + 2.0F, base + 3.0F, base + 1.0F, base + 0.0F}, 0.0F,
               "gather");
  }
  check(cudaDestroyTextureObject(texture), "destroy texture");
  check(cudaFreeArray(array), "free array");
}

TEST_CASE("resources/cuda/surface-1d", "[unified][resources]") {
  constexpr size_t width = 4;
  const std::vector<float> values = {1.5F, -2.0F, 3.25F, 4.0F};
  const std::vector<float> reversed = {40.0F, 30.0F, 20.0F, 10.0F};
  cudaArray_t array = createArray<float>(width, 0, 0, cudaArraySurfaceLoadStore);
  const cudaSurfaceObject_t surface = makeSurface(array);
  const auto coords = surfaceCoords<float>(width, 1, 1);

  writeSurface<1>(surface, coords, values, cudaBoundaryModeZero);
  expectNear(downloadTexels<float>(array, width, 0, 0), values, 0.0F, "1D surface write");
  uploadTexels(array, reversed, width, 0, 0);
  expectNear(readSurface<1, float>(surface, coords, cudaBoundaryModeZero), reversed, 0.0F, "1D surface read");
  check(cudaDestroySurfaceObject(surface), "destroy surface");
  check(cudaFreeArray(array), "free array");
}

TEST_CASE("resources/cuda/surface-2d", "[unified][resources]") {
  constexpr size_t width = 3;
  constexpr size_t height = 2;
  const std::vector<float> values = {0.5F, 1.0F, 1.5F, 2.0F, 2.5F, 3.0F};
  cudaArray_t array = createArray<float>(width, height, 0, cudaArraySurfaceLoadStore);
  const cudaSurfaceObject_t surface = makeSurface(array);
  const auto coords = surfaceCoords<float>(width, height, 1);

  writeSurface<2>(surface, coords, values, cudaBoundaryModeZero);
  expectNear(downloadTexels<float>(array, width, height, 0), values, 0.0F, "2D surface write");
  const std::vector<float> reversed(values.rbegin(), values.rend());
  uploadTexels(array, reversed, width, height, 0);
  expectNear(readSurface<2, float>(surface, coords, cudaBoundaryModeZero), reversed, 0.0F, "2D surface read");
  check(cudaDestroySurfaceObject(surface), "destroy surface");
  check(cudaFreeArray(array), "free array");
}

TEST_CASE("resources/cuda/surface-3d", "[unified][resources]") {
  constexpr size_t width = 2;
  constexpr size_t height = 2;
  constexpr size_t depth = 2;
  const std::vector<float> values = {1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F};
  const std::vector<float> reversed(values.rbegin(), values.rend());
  cudaArray_t array = createArray<float>(width, height, depth, cudaArraySurfaceLoadStore);
  const cudaSurfaceObject_t surface = makeSurface(array);
  const auto coords = surfaceCoords<float>(width, height, depth);

  writeSurface<3>(surface, coords, values, cudaBoundaryModeZero);
  expectNear(downloadTexels<float>(array, width, height, depth), values, 0.0F, "3D surface write");
  uploadTexels(array, reversed, width, height, depth);
  expectNear(readSurface<3, float>(surface, coords, cudaBoundaryModeZero), reversed, 0.0F, "3D surface read");
  check(cudaDestroySurfaceObject(surface), "destroy surface");
  check(cudaFreeArray(array), "free array");
}

TEST_CASE("resources/cuda/surface-boundary", "[unified][resources]") {
  constexpr size_t width = 3;
  constexpr size_t height = 2;
  const std::vector<float> texels = {1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F};
  cudaArray_t array = createArray<float>(width, height, 0, cudaArraySurfaceLoadStore);
  uploadTexels(array, texels, width, height, 0);
  const cudaSurfaceObject_t surface = makeSurface(array);

  const std::vector<int3> probes = {make_int3(-4, 1, 0), make_int3(0, 1, 0), make_int3(8, 1, 0), make_int3(12, 1, 0)};
  expectNear(readSurface<2, float>(surface, probes, cudaBoundaryModeClamp), {4.0F, 4.0F, 6.0F, 6.0F}, 0.0F, "clamp read");
  expectNear(readSurface<2, float>(surface, probes, cudaBoundaryModeZero), {0.0F, 4.0F, 6.0F, 0.0F}, 0.0F, "zero read");

  const std::vector<int3> outside = {make_int3(-4, 1, 0), make_int3(12, 1, 0)};
  writeSurface<2, float>(surface, outside, {99.0F, 99.0F}, cudaBoundaryModeZero);
  expectNear(downloadTexels<float>(array, width, height, 0), texels, 0.0F, "zero write");
  check(cudaDestroySurfaceObject(surface), "destroy surface");
  check(cudaFreeArray(array), "free array");
}

TEST_CASE("resources/cuda/texture-descriptor", "[unified][resources]") {
  const DeviceBuffer<float> buffer(std::vector<float>{1.0F, 2.0F, 3.0F});
  const cudaTextureObject_t linear =
      makeTexture(linearResource(buffer), textureDesc(cudaAddressModeBorder, cudaFilterModePoint, cudaReadModeElementType, false, 0.5F));

  cudaResourceDesc resource{};
  check(cudaGetTextureObjectResourceDesc(&resource, linear), "linear resource descriptor");
  REQUIRE(resource.resType == cudaResourceTypeLinear);
  REQUIRE(resource.res.linear.devPtr == buffer.data);
  REQUIRE(resource.res.linear.sizeInBytes == 3 * sizeof(float));
  REQUIRE(resource.res.linear.desc.x == 32);
  REQUIRE(resource.res.linear.desc.f == cudaChannelFormatKindFloat);

  cudaTextureDesc texture{};
  check(cudaGetTextureObjectTextureDesc(&texture, linear), "linear texture descriptor");
  REQUIRE(texture.addressMode[0] == cudaAddressModeBorder);
  REQUIRE(texture.filterMode == cudaFilterModePoint);
  REQUIRE(texture.readMode == cudaReadModeElementType);
  REQUIRE(texture.normalizedCoords == 0);
  check(cudaDestroyTextureObject(linear), "destroy texture");

  cudaArray_t array = createArray<unsigned char>(3, 0, 0);
  const cudaTextureObject_t arrayTexture =
      makeTexture(arrayResource(array), textureDesc(cudaAddressModeWrap, cudaFilterModeLinear, cudaReadModeNormalizedFloat, true));
  check(cudaGetTextureObjectResourceDesc(&resource, arrayTexture), "array resource descriptor");
  REQUIRE(resource.resType == cudaResourceTypeArray);
  REQUIRE(resource.res.array.array == array);
  check(cudaGetTextureObjectTextureDesc(&texture, arrayTexture), "array texture descriptor");
  REQUIRE(texture.addressMode[2] == cudaAddressModeWrap);
  REQUIRE(texture.filterMode == cudaFilterModeLinear);
  REQUIRE(texture.readMode == cudaReadModeNormalizedFloat);
  REQUIRE(texture.normalizedCoords == 1);
  check(cudaDestroyTextureObject(arrayTexture), "destroy texture");
  check(cudaFreeArray(array), "free array");
}
