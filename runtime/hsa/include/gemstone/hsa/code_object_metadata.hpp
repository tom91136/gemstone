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
  uint32_t explicit_argument_count = 0;
  std::vector<gemstone_hsa_kernarg_field> fields;
};

struct CodeObjectVariable {
  std::string name;
  uint64_t address = 0;
  uint32_t size = 0;
  // Empty for SHT_NOBITS; the declared storage is zero initialized.
  std::vector<uint8_t> initial_data;
  std::vector<uint32_t> pointer_offsets;
};

// Reads defined ELF object symbols and their initial storage, without inventing
// definitions for absent or undefined names. Repeated dynsym/symtab entries are
// deduplicated only when they describe the same definition.
bool parse_code_object_variables(const void *image, size_t image_size, std::vector<CodeObjectVariable> &variables, std::string &error);

// Parses the NT_AMDGPU_METADATA MessagePack note from an ELF64 AMDGPU code
// object. The parser is deliberately independent of ROCr's loader so tests do
// not accidentally validate the implementation with itself.
bool parse_code_object_metadata(const void *image, size_t image_size, std::vector<CodeObjectKernelMetadata> &kernels, std::string &error);

} // namespace gemstone::hsa

#endif
