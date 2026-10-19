#include "gemstone/hsa/images.hpp"

#include <array>
#include <cstddef>
#include <cstdint>
#include <optional>

#include <catch2/catch_test_macros.hpp>

namespace images = gemstone::hsa::images;

namespace {

images::Format supported(const std::optional<images::Format> &format) {
  if (!format) FAIL("format has no compute layout");
  return *format;
}

TEST_CASE("image formats follow the channel description and read mode", "[images]") {
  const auto bytes = images::format(images::Channels{.x = 8, .y = 8, .z = 8, .w = 8, .kind = images::Kind::Unsigned}, true);
  if (!bytes) FAIL("format has no compute layout");
  REQUIRE((bytes->layout == images::Layout::X8Y8Z8W8 && bytes->number == images::Number::Unorm));
  REQUIRE((bytes->bytes == 4 && bytes->channelType == 2 && bytes->channelOrder == 8));
  const auto pairs = images::format(images::Channels{.x = 32, .y = 32, .z = 0, .w = 0, .kind = images::Kind::Signed}, true);
  if (!pairs) FAIL("format has no compute layout");
  REQUIRE((pairs->number == images::Number::Sint && pairs->bytes == 8 && pairs->channelType == 10 && pairs->channelOrder == 3));
  REQUIRE((!images::format(images::Channels{32, 32, 32, 0, images::Kind::Float}, false).has_value()));
  REQUIRE((!images::format(images::Channels{8, 0, 0, 0, images::Kind::Float}, false).has_value()));
}

TEST_CASE("image descriptors match the layouts the VM decodes", "[images]") {
  const auto format = supported(images::format(images::Channels{.x = 8, .y = 8, .z = 8, .w = 8, .kind = images::Kind::Unsigned}, false));
  // 3 by 2 texels, rows 16 bytes apart, as AMDGPUResourceSuite lays out its images
  REQUIRE((images::image(images::Generation::GFX103, 0x12300, format, images::ImageType::Plane, 3, 2, 1, 16)
           == std::array<uint32_t, 8>{0x123, 0x83c00000, 0x4000, 0x90000fac, 3, 0, 0, 0}));
  REQUIRE((images::image(images::Generation::GFX9, 0x12300, format, images::ImageType::Plane, 3, 2, 1, 16)
           == std::array<uint32_t, 8>{0x123, 0x10a00000, 0x4002, 0x90000fac, 0x6000, 0, 0, 0}));
  REQUIRE((images::image(images::Generation::GFX12, 0x12300, format, images::ImageType::Plane, 3, 2, 1, 16)
           == std::array<uint32_t, 8>{0x123, 0x805c0000, 0x4000, 0x90000fac, 3, 0, 0, 0}));
  REQUIRE((images::image(images::Generation::GFX8, 0x12300, format, images::ImageType::Planes, 3, 2, 4, 16)
           == std::array<uint32_t, 8>{0x123, 0x10a00000, 0x4002, 0xd0800fac, 0x6000, 3 << 13, 0, 0}));
}

TEST_CASE("mip levels follow the layout the VM addresses", "[images]") {
  const auto floats = supported(images::format(images::Channels{.x = 32, .y = 0, .z = 0, .w = 0, .kind = images::Kind::Float}, false));
  // 70 by 9 texels: rows of 280, 140 and 68 bytes pad to 512, 256 and 256, and each level follows the last
  const auto zero = images::level(floats, images::ImageType::Plane, 70, 9, 1, 0);
  const auto one = images::level(floats, images::ImageType::Plane, 70, 9, 1, 1);
  const auto two = images::level(floats, images::ImageType::Plane, 70, 9, 1, 2);
  REQUIRE((zero.offset == 0 && zero.width == 70 && zero.height == 9 && zero.pitch == 512));
  REQUIRE((one.offset == size_t{512} * 9 && one.width == 35 && one.height == 4 && one.pitch == 256));
  REQUIRE((two.offset == (size_t{512} * 9) + (size_t{256} * 4) && two.width == 17 && two.height == 2 && two.pitch == 256));
  REQUIRE(images::levels(images::ImageType::Plane, 70, 9, 1) == 7);
  // layers keep their count at every level; a line has one row
  REQUIRE(images::level(floats, images::ImageType::Planes, 8, 8, 3, 2).depth == 3);
  REQUIRE(images::level(floats, images::ImageType::Lines, 8, 1, 3, 1).offset == size_t{256} * 3);
  // LAST_LEVEL holds the last of three levels
  REQUIRE(images::image(images::Generation::GFX103, 0x12300, floats, images::ImageType::Plane, 70, 9, 1, 512, 3)[3] == 0x90020204);
  REQUIRE(images::image(images::Generation::GFX12, 0x12300, floats, images::ImageType::Plane, 70, 9, 1, 512, 3)[3] == 0x90010204);
}

TEST_CASE("HSA channel types and orders name the formats execution reads", "[images]") {
  // UNSIGNED_INT8 RGBA, SNORM_INT16 RG and FLOAT R
  const auto bytes = images::format(11, 8);
  REQUIRE((bytes.has_value() && bytes->layout == images::Layout::X8Y8Z8W8 && bytes->number == images::Number::Uint));
  const auto pairs = images::format(1, 3);
  REQUIRE((pairs.has_value() && pairs->layout == images::Layout::X16Y16 && pairs->number == images::Number::Snorm));
  const auto floats = images::format(15, 1);
  REQUIRE((floats.has_value() && floats->channelType == 15));
  // RGB and 24-bit types have no compute layout
  REQUIRE((!images::format(11, 6).has_value() && !images::format(4, 1).has_value()));
}

TEST_CASE("texels pack components as stores convert them", "[images]") {
  const auto unorm = supported(images::format(2, 8));
  const auto one = 0x3f800000U;
  const auto half = 0x3f000000U;
  const auto negative = 0xbf800000U;
  REQUIRE((images::texel(unorm, {one, half, negative, 0}) == std::array<uint8_t, 16>{0xff, 0x80, 0, 0}));
  const auto shorts = supported(images::format(9, 3));
  REQUIRE((images::texel(shorts, {0xffff0000U, 70000, 0, 0}) == std::array<uint8_t, 16>{0x00, 0x80, 0xff, 0x7f}));
  const auto halves = supported(images::format(14, 1));
  REQUIRE((images::texel(halves, {0x3fc00000U, 0, 0, 0}) == std::array<uint8_t, 16>{0x00, 0x3e}));
}

TEST_CASE("linear texture descriptors index elements", "[images]") {
  const auto floats = supported(images::format(images::Channels{.x = 32, .y = 0, .z = 0, .w = 0, .kind = images::Kind::Float}, false));
  REQUIRE(
      (images::buffer(images::Generation::GFX103, 0x1'0000'1000, floats, 48) == std::array<uint32_t, 4>{0x1000, 0x40001, 48, 0x01016204}));
  REQUIRE((images::buffer(images::Generation::GFX9, 0x1000, floats, 48) == std::array<uint32_t, 4>{0x1000, 0x40000, 48, 0x27204}));
  REQUIRE((images::buffer(images::Generation::GFX8, 0x1000, floats, 48) == std::array<uint32_t, 4>{0x1000, 0x40000, 192, 0x27204}));
}

} // namespace
