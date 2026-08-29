// SPDX-License-Identifier: Apache-2.0
#ifndef GEMSTONE_HSA_CODE_OBJECT_METADATA_HPP
#define GEMSTONE_HSA_CODE_OBJECT_METADATA_HPP

#include <cstddef>
#include <cstdint>
#include <string>
#include <vector>

#include "gemstone/hsa/aql_execution.h"

namespace gemstone::hsa {

struct CodeObjectKernelMetadata {
  std::string name;
  std::string symbol;
  uint32_t kernarg_size = 0;
  std::vector<gemstone_hsa_kernarg_field> fields;
};

// Parses the NT_AMDGPU_METADATA MessagePack note from an ELF64 AMDGPU code
// object. The parser is deliberately independent of ROCr's loader so tests do
// not accidentally validate the implementation with itself.
bool parse_code_object_metadata(const void *image, size_t image_size, std::vector<CodeObjectKernelMetadata> &kernels, std::string &error);

} // namespace gemstone::hsa

#endif
