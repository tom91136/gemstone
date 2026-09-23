#ifndef GEMSTONE_SPIRV_SPECIALIZATION_HPP
#define GEMSTONE_SPIRV_SPECIALIZATION_HPP

#include <map>
#include <optional>
#include <vector>

#include <stddef.h>
#include <stdint.h>

namespace gemstone::spirv {

struct SpecializationConstant {
  size_t instructionOffset = 0;
  size_t byteSize = 0;
  bool boolean = false;
};

std::optional<SpecializationConstant> findSpecializationConstant(const std::vector<uint8_t> &module, uint32_t specId);
bool specializeSpirv(const std::vector<uint8_t> &module, const std::map<uint32_t, std::vector<uint8_t>> &values,
                     std::vector<uint8_t> &specialized);

} // namespace gemstone::spirv

#endif
