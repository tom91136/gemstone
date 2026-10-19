#include <cstdint>
#include <cstring>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

namespace {

uint32_t word(const CUtensorMap &map, size_t offset) {
  uint32_t value = 0;
  std::memcpy(&value, reinterpret_cast<const uint8_t *>(&map) + offset, sizeof(value));
  return value;
}

uint64_t address(const CUtensorMap &map) {
  uint64_t value = 0;
  std::memcpy(&value, &map, sizeof(value));
  return value;
}

constexpr uint64_t Base = 0x10000000000ULL;

} // namespace

TEST_CASE("cuda tiled tensor maps place each field where tensormap.replace writes it") {
  // The fields tensormap.replace reaches are where ptxas 13.3 writes them, so device-side edits and the VM agree.
  CUtensorMap map{};
  const cuuint64_t dimensions[3] = {100, 50, 7};
  const cuuint64_t strides[2] = {400, 0x1000000010ULL};
  const cuuint32_t box[3] = {32, 16, 3};
  const cuuint32_t traversal[3] = {1, 2, 8};
  REQUIRE(cuTensorMapEncodeTiled(&map, CU_TENSOR_MAP_DATA_TYPE_TFLOAT32, 3, reinterpret_cast<void *>(Base), dimensions, strides, box,
                                 traversal, CU_TENSOR_MAP_INTERLEAVE_NONE, CU_TENSOR_MAP_SWIZZLE_128B_ATOM_32B,
                                 CU_TENSOR_MAP_L2_PROMOTION_L2_128B, CU_TENSOR_MAP_FLOAT_OOB_FILL_NAN_REQUEST_ZERO_FMA)
          == CUDA_SUCCESS);
  CHECK(address(map) == Base);
  CHECK(word(map, 8) == ((2U << 4) | (7U << 7) | (3U << 13) | (1U << 15) | (1U << 16) | (1U << 19)));
  CHECK(word(map, 0x0c) == 400 / 16);
  CHECK(word(map, 0x10) == 0x00000001U);
  CHECK(word(map, 0x1c) == (0x1U << 4));
  CHECK(word(map, 0x20) == 99);
  CHECK(word(map, 0x24) == 49);
  CHECK(word(map, 0x28) == 6);
  CHECK(word(map, 0x34) == ((31U << 24) | (0U) | (1U << 3) | (7U << 6)));
  CHECK(word(map, 0x38) == (15U | (2U << 8)));
  REQUIRE(cuTensorMapReplaceAddress(&map, reinterpret_cast<void *>(Base + 0x40)) == CUDA_SUCCESS);
  CHECK(address(map) == Base + 0x40);
}

TEST_CASE("cuda tensor map encoders reject maps the hardware cannot describe") {
  CUtensorMap map{};
  const cuuint64_t dimensions[2] = {64, 8};
  const cuuint64_t strides[1] = {256};
  const cuuint32_t box[2] = {32, 8};
  const cuuint32_t traversal[2] = {1, 1};
  const auto encode = [&](void *base, const cuuint64_t *stride, const cuuint32_t *size, CUtensorMapSwizzle swizzle,
                          CUtensorMapDataType type = CU_TENSOR_MAP_DATA_TYPE_FLOAT32) {
    return cuTensorMapEncodeTiled(&map, type, 2, base, dimensions, stride, size, traversal, CU_TENSOR_MAP_INTERLEAVE_NONE, swizzle,
                                  CU_TENSOR_MAP_L2_PROMOTION_NONE, CU_TENSOR_MAP_FLOAT_OOB_FILL_NONE);
  };
  auto *const base = reinterpret_cast<void *>(Base);
  CHECK(encode(base, strides, box, CU_TENSOR_MAP_SWIZZLE_128B) == CUDA_SUCCESS);
  CHECK(encode(reinterpret_cast<void *>(Base + 8), strides, box, CU_TENSOR_MAP_SWIZZLE_NONE) == CUDA_ERROR_INVALID_VALUE);
  const cuuint64_t unaligned[1] = {260};
  CHECK(encode(base, unaligned, box, CU_TENSOR_MAP_SWIZZLE_NONE) == CUDA_ERROR_INVALID_VALUE);
  CHECK(encode(base, strides, box, CU_TENSOR_MAP_SWIZZLE_64B) == CUDA_ERROR_INVALID_VALUE);
  const cuuint32_t ragged[2] = {3, 8};
  CHECK(encode(base, strides, ragged, CU_TENSOR_MAP_SWIZZLE_NONE) == CUDA_ERROR_INVALID_VALUE);
  CHECK(encode(base, strides, box, CU_TENSOR_MAP_SWIZZLE_NONE, CU_TENSOR_MAP_DATA_TYPE_16U4_ALIGN16B) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuTensorMapEncodeTiled(reinterpret_cast<CUtensorMap *>(reinterpret_cast<uint8_t *>(&map) + 8), CU_TENSOR_MAP_DATA_TYPE_FLOAT32, 2,
                               base, dimensions, strides, box, traversal, CU_TENSOR_MAP_INTERLEAVE_NONE, CU_TENSOR_MAP_SWIZZLE_NONE,
                               CU_TENSOR_MAP_L2_PROMOTION_NONE, CU_TENSOR_MAP_FLOAT_OOB_FILL_NONE)
        == CUDA_ERROR_INVALID_VALUE);
}

TEST_CASE("cuda tiled tensor maps of rank one reject a null strides array") {
  // GH200 (sm_90, driver 580.178.04, CUDA 12.6): the probe encoded a rank 1 map with null strides and a 3D map (dims
  // 16x16x16, strides 64 and 1024 bytes, box 4x4x4) and reported only that one failed. The 3D map meets every documented
  // limit, so the null strides are the cause; the status is the documented CUDA_ERROR_INVALID_VALUE.
  CUtensorMap map{};
  auto *const base = reinterpret_cast<void *>(Base);
  const cuuint64_t line[1] = {4096};
  const cuuint32_t lineBox[1] = {64};
  const cuuint32_t unit[3] = {1, 1, 1};
  const auto encodeLine = [&](const cuuint64_t *strides) {
    return cuTensorMapEncodeTiled(&map, CU_TENSOR_MAP_DATA_TYPE_UINT32, 1, base, line, strides, lineBox, unit,
                                  CU_TENSOR_MAP_INTERLEAVE_NONE, CU_TENSOR_MAP_SWIZZLE_NONE, CU_TENSOR_MAP_L2_PROMOTION_NONE,
                                  CU_TENSOR_MAP_FLOAT_OOB_FILL_NONE);
  };
  const cuuint64_t none[1] = {0};
  CHECK(encodeLine(none) == CUDA_SUCCESS);
  CHECK(encodeLine(nullptr) == CUDA_ERROR_INVALID_VALUE);
  const cuuint64_t cube[3] = {16, 16, 16};
  const cuuint64_t strides[2] = {64, 1024};
  const cuuint32_t cubeBox[3] = {4, 4, 4};
  CHECK(cuTensorMapEncodeTiled(&map, CU_TENSOR_MAP_DATA_TYPE_UINT32, 3, base, cube, strides, cubeBox, unit, CU_TENSOR_MAP_INTERLEAVE_NONE,
                               CU_TENSOR_MAP_SWIZZLE_NONE, CU_TENSOR_MAP_L2_PROMOTION_NONE, CU_TENSOR_MAP_FLOAT_OOB_FILL_NONE)
        == CUDA_SUCCESS);
}

TEST_CASE("cuda im2col tensor maps record their corners, channels and pixels") {
  CUtensorMap map{};
  const cuuint64_t dimensions[4] = {64, 9, 14, 2};
  const cuuint64_t strides[3] = {256, cuuint64_t{256} * 9, cuuint64_t{256} * 9 * 14};
  const int lower[2] = {-1, -2};
  const int upper[2] = {1, 0};
  const cuuint32_t traversal[4] = {1, 1, 1, 1};
  REQUIRE(cuTensorMapEncodeIm2col(&map, CU_TENSOR_MAP_DATA_TYPE_FLOAT32, 4, reinterpret_cast<void *>(Base), dimensions, strides, lower,
                                  upper, 32, 64, traversal, CU_TENSOR_MAP_INTERLEAVE_NONE, CU_TENSOR_MAP_SWIZZLE_128B,
                                  CU_TENSOR_MAP_L2_PROMOTION_NONE, CU_TENSOR_MAP_FLOAT_OOB_FILL_NONE)
          == CUDA_SUCCESS);
  CHECK(word(map, 0x40) == 1);
  CHECK(word(map, 0x48) == 32);
  CHECK(word(map, 0x4c) == 64);
  CHECK(static_cast<int32_t>(word(map, 0x50)) == -1);
  CHECK(static_cast<int32_t>(word(map, 0x54)) == -2);
  CHECK(static_cast<int32_t>(word(map, 0x5c)) == 1);
  CHECK(static_cast<int32_t>(word(map, 0x60)) == 0);
  const int far[2] = {-200, 0};
  CHECK(cuTensorMapEncodeIm2col(&map, CU_TENSOR_MAP_DATA_TYPE_FLOAT32, 4, reinterpret_cast<void *>(Base), dimensions, strides, far, upper,
                                32, 64, traversal, CU_TENSOR_MAP_INTERLEAVE_NONE, CU_TENSOR_MAP_SWIZZLE_128B,
                                CU_TENSOR_MAP_L2_PROMOTION_NONE, CU_TENSOR_MAP_FLOAT_OOB_FILL_NONE)
        == CUDA_ERROR_INVALID_VALUE);
}

TEST_CASE("cuda tensor maps of packed sub-byte types carry the codes tensormap.replace writes for them") {
  // ptxas 13.3 lowers tensormap.replace.tile.elemtype 13, 14 and 15 to element codes 11, 12 and 13
  CUtensorMap map{};
  const cuuint64_t dimensions[2] = {256, 4};
  const cuuint64_t strides[1] = {256};
  const cuuint32_t box[2] = {128, 4};
  const cuuint32_t traversal[2] = {1, 1};
  const auto encode = [&](CUtensorMapDataType type, uint64_t base) {
    return cuTensorMapEncodeTiled(&map, type, 2, reinterpret_cast<void *>(base), dimensions, strides, box, traversal,
                                  CU_TENSOR_MAP_INTERLEAVE_NONE, CU_TENSOR_MAP_SWIZZLE_128B, CU_TENSOR_MAP_L2_PROMOTION_NONE,
                                  CU_TENSOR_MAP_FLOAT_OOB_FILL_NONE);
  };
  REQUIRE(encode(CU_TENSOR_MAP_DATA_TYPE_16U4_ALIGN8B, Base) == CUDA_SUCCESS);
  CHECK(((word(map, 8) >> 7) & 0xf) == 11);
  REQUIRE(encode(CU_TENSOR_MAP_DATA_TYPE_16U4_ALIGN16B, Base) == CUDA_SUCCESS);
  CHECK(((word(map, 8) >> 7) & 0xf) == 12);
  REQUIRE(encode(CU_TENSOR_MAP_DATA_TYPE_16U6_ALIGN16B, Base) == CUDA_SUCCESS);
  CHECK(((word(map, 8) >> 7) & 0xf) == 13);
  CHECK(encode(CU_TENSOR_MAP_DATA_TYPE_16U6_ALIGN16B, Base + 16) == CUDA_ERROR_INVALID_VALUE);
}
