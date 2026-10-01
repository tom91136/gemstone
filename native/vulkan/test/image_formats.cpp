#include <catch2/catch_test_macros.hpp>

#include "gemstone_image_formats.hpp"

TEST_CASE("Vulkan formats preserve storage identity across decoding and depth stencil views", "[vulkan]") {
  using gemstone::execution::GraphicsFormat;
  using gemstone::vulkan::graphicsFormat;
  REQUIRE(graphicsFormat(VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK) == GraphicsFormat::Etc2R8g8b8UnormBlock);
  REQUIRE(graphicsFormat(VK_FORMAT_ETC2_R8G8B8_SRGB_BLOCK) == GraphicsFormat::Etc2R8g8b8SrgbBlock);
  REQUIRE(graphicsFormat(VK_FORMAT_EAC_R11_UNORM_BLOCK) == GraphicsFormat::EacR11UnormBlock);
  REQUIRE(graphicsFormat(VK_FORMAT_R32_SFLOAT) == GraphicsFormat::R32f);
  REQUIRE(graphicsFormat(VK_FORMAT_D32_SFLOAT) == GraphicsFormat::D32Sfloat);
  REQUIRE(graphicsFormat(VK_FORMAT_D32_SFLOAT_S8_UINT) == GraphicsFormat::D32SfloatS8Depth);
  REQUIRE(graphicsFormat(VK_FORMAT_D32_SFLOAT_S8_UINT, VK_IMAGE_ASPECT_DEPTH_BIT) == GraphicsFormat::D32SfloatS8Depth);
  REQUIRE(graphicsFormat(VK_FORMAT_D32_SFLOAT_S8_UINT, VK_IMAGE_ASPECT_STENCIL_BIT) == GraphicsFormat::D32SfloatS8Stencil);
  REQUIRE(graphicsFormat(VK_FORMAT_S8_UINT) == GraphicsFormat::S8Uint);
  REQUIRE(graphicsFormat(VK_FORMAT_R8G8B8A8_UNORM) == graphicsFormat(VK_FORMAT_A8B8G8R8_UNORM_PACK32));
  REQUIRE(gemstone::vulkan::texelSize(VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK) == 8);
  REQUIRE(gemstone::vulkan::texelSize(VK_FORMAT_EAC_R11G11_SNORM_BLOCK) == 16);
  REQUIRE(gemstone::vulkan::texelSize(VK_FORMAT_D32_SFLOAT_S8_UINT) == 8);
  REQUIRE(gemstone::vulkan::texelSize(VK_FORMAT_D16_UNORM) == 2);
  REQUIRE(gemstone::vulkan::depthFormat(VK_FORMAT_D32_SFLOAT_S8_UINT));
  REQUIRE(gemstone::vulkan::stencilFormat(VK_FORMAT_D32_SFLOAT_S8_UINT));
  REQUIRE_FALSE(gemstone::vulkan::depthFormat(VK_FORMAT_R32_SFLOAT));
  REQUIRE_FALSE(gemstone::vulkan::stencilFormat(VK_FORMAT_D32_SFLOAT));
  REQUIRE(gemstone::vulkan::texelSize(VK_FORMAT_UNDEFINED) == 0);
  REQUIRE_FALSE(graphicsFormat(VK_FORMAT_UNDEFINED));
  REQUIRE_FALSE(graphicsFormat(static_cast<VkFormat>(-1)));
}
