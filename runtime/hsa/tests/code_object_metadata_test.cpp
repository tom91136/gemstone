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
  return 0;
}
