#include <bit>
#include <cstdio>
#include <random>

#include "reference/compressed_format.hpp"
#include "reference/vertex_format.hpp"

int main() {
  using namespace gemstone::vulkan;
  const std::array<VkFormat, 10> formats{VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK,   VK_FORMAT_ETC2_R8G8B8_SRGB_BLOCK,
                                         VK_FORMAT_ETC2_R8G8B8A1_UNORM_BLOCK, VK_FORMAT_ETC2_R8G8B8A1_SRGB_BLOCK,
                                         VK_FORMAT_ETC2_R8G8B8A8_UNORM_BLOCK, VK_FORMAT_ETC2_R8G8B8A8_SRGB_BLOCK,
                                         VK_FORMAT_EAC_R11_UNORM_BLOCK,       VK_FORMAT_EAC_R11_SNORM_BLOCK,
                                         VK_FORMAT_EAC_R11G11_UNORM_BLOCK,    VK_FORMAT_EAC_R11G11_SNORM_BLOCK};
  const std::array<unsigned, 10> encodings{1, 1, 2, 2, 3, 3, 4, 6, 5, 7};
  std::mt19937 random(0x3754);
  for (size_t index = 0; index < formats.size(); ++index) {
    const auto format = compressedImageFormat(formats[index]).value();
    for (unsigned iteration = 0; iteration < 512; ++iteration) {
      std::array<uint8_t, 16> bytes{};
      for (auto &byte : bytes)
        byte = static_cast<uint8_t>(random());
      if (iteration == 0) bytes.fill(0);
      if (iteration == 1) bytes.fill(255);
      std::printf("C %u %u ", encodings[index], format.srgb);
      for (unsigned i = 0; i < format.bytes; ++i)
        std::printf("%02x", bytes[i]);
      for (const auto value : decodeCompressedBlock(format, bytes.data()))
        std::printf(" %08x", std::bit_cast<uint32_t>(value));
      std::puts("");
    }
  }
  for (int id = VK_FORMAT_UNDEFINED; id <= VK_FORMAT_B10G11R11_UFLOAT_PACK32; ++id) {
    const auto format = vertexFormat(static_cast<VkFormat>(id));
    if (!format) continue;
    for (unsigned iteration = 0; iteration < 128; ++iteration) {
      std::array<uint8_t, 16> bytes{};
      for (auto &byte : bytes)
        byte = static_cast<uint8_t>(random());
      const std::array<VertexEncoding, 8> encodings{VertexEncoding::Unorm, VertexEncoding::Snorm,   VertexEncoding::Uint,
                                                    VertexEncoding::Sint,  VertexEncoding::Uscaled, VertexEncoding::Sscaled,
                                                    VertexEncoding::Float, VertexEncoding::Ufloat};
      const auto encoding = static_cast<unsigned>(std::find(encodings.begin(), encodings.end(), format->encoding) - encodings.begin());
      std::printf("V %u %u %u %u", encoding, format->bytes, format->bgra, format->components);
      for (unsigned lane = 0; lane < format->components; ++lane)
        std::printf(" %u", format->widths[lane]);
      std::printf(" ");
      for (unsigned i = 0; i < format->bytes; ++i)
        std::printf("%02x", bytes[i]);
      for (const auto value : decodeVertex(*format, bytes.data()))
        std::printf(" %08x", value);
      std::puts("");
    }
  }
}
