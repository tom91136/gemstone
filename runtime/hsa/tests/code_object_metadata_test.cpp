// SPDX-License-Identifier: Apache-2.0
#include "gemstone/hsa/code_object_metadata.hpp"

#include <cassert>
#include <fstream>
#include <iterator>
#include <string>
#include <vector>

#include "code_object_fixture.hpp"

int main(int argc, char **argv) {
  assert(argc == 1 || argc == 2);
  const auto image = argc == 1 ? gemstone::hsa::test::code_object_fixture() : [&]() {
    std::ifstream stream(argv[1], std::ios::binary);
    assert(stream.good());
    return std::vector<uint8_t>(std::istreambuf_iterator<char>(stream), std::istreambuf_iterator<char>());
  }();
  std::vector<gemstone::hsa::CodeObjectKernelMetadata> kernels;
  std::string error;
  assert(gemstone::hsa::parse_code_object_metadata(image.data(), image.size(), kernels, error));
  assert(error.empty());
  assert(kernels.size() == 1);
  const bool fixture = argc == 1;
  assert(kernels[0].name == (fixture ? "add" : "vector_copy"));
  assert(kernels[0].symbol == (fixture ? "add.kd" : "vector_copy.kd"));
  assert(kernels[0].kernarg_size == (fixture ? 20 : 80));
  assert(kernels[0].fields.size() == (fixture ? 3 : 10));
  assert(kernels[0].fields[0].kind == GEMSTONE_HSA_ARGUMENT_ALLOCATION);
  assert(kernels[0].fields[0].offset == 0 && kernels[0].fields[0].size == 8);
  assert(kernels[0].fields[1].kind == GEMSTONE_HSA_ARGUMENT_ALLOCATION);
  assert(kernels[0].fields[1].offset == 8 && kernels[0].fields[1].size == 8);
  assert(kernels[0].fields[2].kind == (fixture ? GEMSTONE_HSA_ARGUMENT_SCALAR : GEMSTONE_HSA_ARGUMENT_ALLOCATION));
  assert(kernels[0].fields[2].offset == 16 && kernels[0].fields[2].size == (fixture ? 4 : 8));

  if (fixture) {
    const auto multiple_notes = gemstone::hsa::test::code_object_fixture(2);
    assert(gemstone::hsa::parse_code_object_metadata(multiple_notes.data(), multiple_notes.size(), kernels, error));
    assert(error.empty());
    assert(kernels.size() == 2);
    assert(kernels[0].name == "add" && kernels[1].name == "add");

    auto malformed = image;
    malformed[EI_MAG0] = 0;
    assert(!gemstone::hsa::parse_code_object_metadata(malformed.data(), malformed.size(), kernels, error));
    assert(!error.empty());
  }
  auto variables_image = gemstone::hsa::test::variable_object_fixture();
  std::vector<gemstone::hsa::CodeObjectVariable> variables;
  assert(gemstone::hsa::parse_code_object_variables(variables_image.data(), variables_image.size(), variables, error));
  assert(variables.size() == 4);
  assert(variables[0].name == "arbitrary_data" && variables[0].address == 0x1004 && variables[0].size == 8);
  assert((variables[0].initial_data == std::vector<uint8_t>{5, 6, 7, 8, 9, 10, 11, 12}));
  assert(variables[1].name == "arbitrary_bss" && variables[1].size == 32 && variables[1].initial_data.empty());
  assert(variables[2].name == "data_alias" && variables[2].address == variables[0].address);
  assert(variables[3].name == "zero_global" && variables[3].size == 0);
  // A valid code object need not contain exported variables.
  assert(gemstone::hsa::parse_code_object_variables(image.data(), image.size(), variables, error));
  assert(variables.empty());
  for (int defect = 0; defect < 4; ++defect) {
    auto broken = variables_image;
    Elf64_Shdr table{};
    std::memcpy(&table, broken.data() + sizeof(Elf64_Ehdr) + 2 * sizeof(Elf64_Shdr), sizeof(table));
    if (defect == 0) {
      table.sh_offset = UINT64_MAX;
      std::memcpy(broken.data() + sizeof(Elf64_Ehdr) + 2 * sizeof(Elf64_Shdr), &table, sizeof(table));
    } else {
      Elf64_Sym symbol{};
      std::memcpy(&symbol, broken.data() + table.sh_offset + sizeof(Elf64_Sym), sizeof(symbol));
      if (defect == 1) symbol.st_size = 64;
      if (defect == 2) symbol.st_name = UINT32_MAX;
      if (defect == 3) symbol.st_shndx = SHN_ABS;
      std::memcpy(broken.data() + table.sh_offset + sizeof(Elf64_Sym), &symbol, sizeof(symbol));
    }
    assert(!gemstone::hsa::parse_code_object_variables(broken.data(), broken.size(), variables, error));
    assert(!error.empty() && variables.empty());
  }
  return 0;
}
