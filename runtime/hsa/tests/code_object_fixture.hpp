// SPDX-License-Identifier: Apache-2.0
#ifndef GEMSTONE_HSA_TESTS_CODE_OBJECT_FIXTURE_HPP
#define GEMSTONE_HSA_TESTS_CODE_OBJECT_FIXTURE_HPP

#include <cstdint>
#include <cstring>
#include <string_view>
#include <vector>

#include <elf.h>

namespace gemstone::hsa::test {

inline void append_string(std::vector<uint8_t> &bytes, std::string_view value) {
  if (value.size() < 32) {
    bytes.push_back(static_cast<uint8_t>(0xa0 | value.size()));
  } else {
    bytes.push_back(0xd9);
    bytes.push_back(static_cast<uint8_t>(value.size()));
  }
  bytes.insert(bytes.end(), value.begin(), value.end());
}

inline void append_uint(std::vector<uint8_t> &bytes, uint32_t value) {
  if (value < 128) {
    bytes.push_back(static_cast<uint8_t>(value));
  } else {
    bytes.push_back(0xce);
    bytes.push_back(static_cast<uint8_t>(value >> 24));
    bytes.push_back(static_cast<uint8_t>(value >> 16));
    bytes.push_back(static_cast<uint8_t>(value >> 8));
    bytes.push_back(static_cast<uint8_t>(value));
  }
}

inline void append_argument(std::vector<uint8_t> &metadata, uint32_t offset, uint32_t size, std::string_view kind) {
  metadata.push_back(0x83);
  append_string(metadata, ".offset");
  append_uint(metadata, offset);
  append_string(metadata, ".size");
  append_uint(metadata, size);
  append_string(metadata, ".value_kind");
  append_string(metadata, kind);
}

inline std::vector<uint8_t> code_object_fixture() {
  std::vector<uint8_t> metadata;
  metadata.push_back(0x81);
  append_string(metadata, "amdhsa.kernels");
  metadata.push_back(0x91);
  metadata.push_back(0x84);
  append_string(metadata, ".args");
  metadata.push_back(0x93);
  append_argument(metadata, 0, 8, "global_buffer");
  append_argument(metadata, 8, 8, "global_buffer");
  append_argument(metadata, 16, 4, "by_value");
  append_string(metadata, ".kernarg_segment_size");
  append_uint(metadata, 20);
  append_string(metadata, ".name");
  append_string(metadata, "add");
  append_string(metadata, ".symbol");
  append_string(metadata, "add.kd");

  constexpr size_t note_offset = sizeof(Elf64_Ehdr);
  const size_t name_size = 7;
  const size_t padded_name = 8;
  const size_t padded_description = (metadata.size() + 3) & ~size_t{3};
  const size_t section_offset = note_offset + 12 + padded_name + padded_description;
  std::vector<uint8_t> image(section_offset + 2 * sizeof(Elf64_Shdr));

  Elf64_Ehdr header{};
  std::memcpy(header.e_ident, ELFMAG, SELFMAG);
  header.e_ident[EI_CLASS] = ELFCLASS64;
  header.e_ident[EI_DATA] = ELFDATA2LSB;
  header.e_ident[EI_VERSION] = EV_CURRENT;
  header.e_type = ET_DYN;
  header.e_machine = EM_AMDGPU;
  header.e_version = EV_CURRENT;
  header.e_ehsize = sizeof(header);
  header.e_shoff = section_offset;
  header.e_shentsize = sizeof(Elf64_Shdr);
  header.e_shnum = 2;
  std::memcpy(image.data(), &header, sizeof(header));

  const uint32_t note_header[] = {static_cast<uint32_t>(name_size), static_cast<uint32_t>(metadata.size()), 32};
  std::memcpy(image.data() + note_offset, note_header, sizeof(note_header));
  std::memcpy(image.data() + note_offset + 12, "AMDGPU\0", name_size);
  std::memcpy(image.data() + note_offset + 12 + padded_name, metadata.data(), metadata.size());

  Elf64_Shdr note{};
  note.sh_type = SHT_NOTE;
  note.sh_offset = note_offset;
  note.sh_size = 12 + padded_name + padded_description;
  std::memcpy(image.data() + section_offset + sizeof(Elf64_Shdr), &note, sizeof(note));
  return image;
}

} // namespace gemstone::hsa::test

#endif
