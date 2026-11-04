#pragma once

#include <cstdint>
#include <string>
#include <string_view>
#include <vector>

#include <gemstone/hsa/target.hpp>

namespace gemstone::hip::test {

inline std::vector<uint8_t> offloadBundle(const std::vector<uint8_t> &code) {
  constexpr std::string_view magic = "__CLANG_OFFLOAD_BUNDLE__";
  const std::string triple = std::string("hipv4-amdgcn-amd-amdhsa--") + gemstone::hsa::targetArch();
  std::vector<uint8_t> bundle;
  bundle.insert(bundle.end(), magic.begin(), magic.end());
  const auto append = [&](uint64_t value) {
    const auto *bytes = reinterpret_cast<const uint8_t *>(&value);
    bundle.insert(bundle.end(), bytes, bytes + sizeof(value));
  };
  append(1);
  append(magic.size() + (4 * sizeof(uint64_t)) + triple.size());
  append(code.size());
  append(triple.size());
  bundle.insert(bundle.end(), triple.begin(), triple.end());
  bundle.insert(bundle.end(), code.begin(), code.end());
  return bundle;
}

} // namespace gemstone::hip::test
