#include "specialization.hpp"

#include <algorithm>
#include <map>
#include <optional>
#include <vector>

namespace gemstone::spirv {
namespace {

uint32_t word(const std::vector<uint8_t> &module, size_t offset) {
  return uint32_t(module[offset]) | (uint32_t(module[offset + 1]) << 8) | (uint32_t(module[offset + 2]) << 16)
         | (uint32_t(module[offset + 3]) << 24);
}

void putWord(std::vector<uint8_t> &module, size_t offset, uint32_t value) {
  for (unsigned byte = 0; byte < 4; ++byte)
    module[offset + byte] = static_cast<uint8_t>(value >> (byte * 8));
}

struct Declaration {
  size_t offset;
  size_t words;
  uint32_t type;
  uint16_t opcode;
};

struct ModuleIndex {
  std::map<uint32_t, uint32_t> specIds;
  std::map<uint32_t, std::pair<size_t, bool>> types;
  std::map<uint32_t, Declaration> declarations;
};

std::optional<ModuleIndex> indexModule(const std::vector<uint8_t> &module) {
  if (module.size() < 20 || module.size() % 4 != 0 || word(module, 0) != 0x07230203) return std::nullopt;
  ModuleIndex index;
  for (size_t offset = 20; offset < module.size();) {
    const uint32_t header = word(module, offset);
    const size_t count = header >> 16;
    const uint16_t opcode = static_cast<uint16_t>(header);
    if (count == 0 || count > (module.size() - offset) / 4) return std::nullopt;
    if (opcode == 71 && count >= 4 && word(module, offset + 8) == 1) // OpDecorate SpecId
      index.specIds[word(module, offset + 12)] = word(module, offset + 4);
    if (opcode == 20 && count >= 2) // OpTypeBool
      index.types[word(module, offset + 4)] = {1, true};
    if ((opcode == 21 || opcode == 22) && count >= 3) { // OpTypeInt / OpTypeFloat
      const uint32_t width = word(module, offset + 8);
      if (width != 0 && width <= 64 && width % 8 == 0) index.types[word(module, offset + 4)] = {width / 8, false};
    }
    if ((opcode == 48 || opcode == 49 || opcode == 50) && count >= 3) // OpSpecConstantTrue/False/number
      index.declarations[word(module, offset + 8)] = {offset, count, word(module, offset + 4), opcode};
    offset += count * 4;
  }
  return index;
}

std::optional<SpecializationConstant> locate(const ModuleIndex &index, uint32_t specId) {
  const auto id = index.specIds.find(specId);
  if (id == index.specIds.end()) return std::nullopt;
  const auto declaration = index.declarations.find(id->second);
  if (declaration == index.declarations.end()) return std::nullopt;
  const auto type = index.types.find(declaration->second.type);
  if (type == index.types.end()) return std::nullopt;
  const auto &[size, boolean] = type->second;
  if (boolean != (declaration->second.opcode == 48 || declaration->second.opcode == 49)) return std::nullopt;
  if (declaration->second.words != 3 + (boolean ? 0 : (size + 3) / 4)) return std::nullopt;
  return SpecializationConstant{declaration->second.offset, size, boolean};
}

} // namespace

std::optional<SpecializationConstant> findSpecializationConstant(const std::vector<uint8_t> &module, uint32_t specId) {
  const auto index = indexModule(module);
  return index ? locate(*index, specId) : std::nullopt;
}

bool specializeSpirv(const std::vector<uint8_t> &module, const std::map<uint32_t, std::vector<uint8_t>> &values,
                     std::vector<uint8_t> &specialized) {
  const auto index = indexModule(module);
  if (!index) return false;
  specialized = module;
  for (const auto &[specId, bytes] : values) {
    // Vulkan specialization map entries that do not name a constant used by
    // the shader have no effect on pipeline creation.
    if (!index->specIds.contains(specId)) continue;
    const auto constant = locate(*index, specId);
    if (!constant || bytes.size() != constant->byteSize) return false;
    const size_t offset = constant->instructionOffset;
    const uint32_t header = word(specialized, offset);
    if (constant->boolean) {
      putWord(specialized, offset, (header & 0xffff0000U) | (bytes[0] == 0 ? 49U : 48U));
    } else {
      std::fill(specialized.begin() + offset + 12, specialized.begin() + offset + (header >> 16) * 4, uint8_t{0});
      std::copy(bytes.begin(), bytes.end(), specialized.begin() + offset + 12);
    }
  }
  return true;
}

} // namespace gemstone::spirv
