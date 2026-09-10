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

inline std::vector<uint8_t> code_object_fixture(uint32_t metadata_note_count = 1, bool single_output = false, bool with_hidden = false) {
  std::vector<uint8_t> metadata;
  metadata.push_back(0x81);
  append_string(metadata, "amdhsa.kernels");
  metadata.push_back(0x91);
  metadata.push_back(0x84);
  append_string(metadata, ".args");
  metadata.push_back(single_output ? (with_hidden ? 0x9c : 0x92) : 0x93);
  append_argument(metadata, 0, 8, "global_buffer");
  if (single_output) append_argument(metadata, 8, 4, "by_value");
  else {
    append_argument(metadata, 8, 8, "global_buffer");
    append_argument(metadata, 16, 4, "by_value");
  }
  if (single_output && with_hidden) {
    append_argument(metadata, 16, 4, "hidden_block_count_x");
    append_argument(metadata, 20, 4, "hidden_block_count_y");
    append_argument(metadata, 24, 4, "hidden_block_count_z");
    append_argument(metadata, 28, 2, "hidden_group_size_x");
    append_argument(metadata, 30, 2, "hidden_group_size_y");
    append_argument(metadata, 32, 2, "hidden_group_size_z");
    append_argument(metadata, 34, 2, "hidden_remainder_x");
    append_argument(metadata, 36, 2, "hidden_remainder_y");
    append_argument(metadata, 38, 2, "hidden_remainder_z");
    append_argument(metadata, 80, 2, "hidden_grid_dims");
  }
  append_string(metadata, ".kernarg_segment_size");
  append_uint(metadata, single_output ? (with_hidden ? 88 : 12) : 20);
  append_string(metadata, ".name");
  append_string(metadata, "add");
  append_string(metadata, ".symbol");
  append_string(metadata, "add.kd");

  constexpr size_t note_offset = sizeof(Elf64_Ehdr);
  const size_t name_size = 7;
  const size_t padded_name = 8;
  const size_t padded_description = (metadata.size() + 3) & ~size_t{3};
  const size_t note_size = 12 + padded_name + padded_description;
  const size_t section_offset = note_offset + note_size * metadata_note_count;
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
  header.e_phentsize = sizeof(Elf64_Phdr);
  header.e_shoff = section_offset;
  header.e_shentsize = sizeof(Elf64_Shdr);
  header.e_shnum = 2;
  std::memcpy(image.data(), &header, sizeof(header));

  const uint32_t note_header[] = {static_cast<uint32_t>(name_size), static_cast<uint32_t>(metadata.size()), 32};
  for (uint32_t index = 0; index < metadata_note_count; ++index) {
    const size_t current = note_offset + static_cast<size_t>(index) * note_size;
    std::memcpy(image.data() + current, note_header, sizeof(note_header));
    std::memcpy(image.data() + current + 12, "AMDGPU\0", name_size);
    std::memcpy(image.data() + current + 12 + padded_name, metadata.data(), metadata.size());
  }

  Elf64_Shdr note{};
  note.sh_type = SHT_NOTE;
  note.sh_offset = note_offset;
  note.sh_size = note_size * metadata_note_count;
  std::memcpy(image.data() + section_offset + sizeof(Elf64_Shdr), &note, sizeof(note));
  return image;
}

// ELF-only variable fixture: initialized data, zero-filled BSS, an alias,
// a zero-sized definition and an undefined declaration. Both symbol tables
// contain the same definitions, as a linked DSO normally does.
inline std::vector<uint8_t> variable_object_fixture() {
  constexpr char names[] = "\0arbitrary_data\0arbitrary_bss\0data_alias\0zero_global\0undefined_global\0";
  constexpr size_t count = 6;
  constexpr size_t strings_offset = sizeof(Elf64_Ehdr) + count * sizeof(Elf64_Shdr);
  constexpr size_t symbols_offset = strings_offset + sizeof(names);
  constexpr size_t data_offset = symbols_offset + 6 * sizeof(Elf64_Sym);
  std::vector<uint8_t> bytes(data_offset + 16, 0);
  Elf64_Ehdr header{};
  std::memcpy(header.e_ident, ELFMAG, SELFMAG);
  header.e_ident[EI_CLASS] = ELFCLASS64;
  header.e_ident[EI_DATA] = ELFDATA2LSB;
  header.e_machine = EM_AMDGPU;
  header.e_type = ET_DYN;
  header.e_shoff = sizeof(header);
  header.e_shentsize = sizeof(Elf64_Shdr);
  header.e_shnum = count;
  std::memcpy(bytes.data(), &header, sizeof(header));
  Elf64_Shdr sections[count]{};
  sections[1].sh_type = SHT_STRTAB;
  sections[1].sh_offset = strings_offset;
  sections[1].sh_size = sizeof(names);
  sections[2].sh_type = SHT_SYMTAB;
  sections[2].sh_offset = symbols_offset;
  sections[2].sh_size = 6 * sizeof(Elf64_Sym);
  sections[2].sh_entsize = sizeof(Elf64_Sym);
  sections[2].sh_link = 1;
  sections[3].sh_type = SHT_PROGBITS;
  sections[3].sh_flags = SHF_ALLOC | SHF_WRITE;
  sections[3].sh_addr = 0x1000;
  sections[3].sh_offset = data_offset;
  sections[3].sh_size = 16;
  sections[4].sh_type = SHT_NOBITS;
  sections[4].sh_flags = SHF_ALLOC | SHF_WRITE;
  sections[4].sh_addr = 0x2000;
  sections[4].sh_size = 32;
  sections[5] = sections[2];
  sections[5].sh_type = SHT_DYNSYM;
  std::memcpy(bytes.data() + sizeof(header), sections, sizeof(sections));
  std::memcpy(bytes.data() + strings_offset, names, sizeof(names));
  Elf64_Sym symbols[6]{};
  constexpr uint32_t offsets[] = {1, 16, 30, 41, 53};
  for (size_t index = 1; index < 6; ++index) {
    symbols[index].st_name = offsets[index - 1];
    symbols[index].st_info = ELF64_ST_INFO(STB_GLOBAL, STT_OBJECT);
  }
  symbols[1].st_shndx = 3;
  symbols[1].st_value = 0x1004;
  symbols[1].st_size = 8;
  symbols[2].st_shndx = 4;
  symbols[2].st_value = 0x2000;
  symbols[2].st_size = 32;
  symbols[3].st_shndx = 3;
  symbols[3].st_value = 0x1004;
  symbols[3].st_size = 8;
  symbols[4].st_shndx = 4;
  symbols[4].st_value = 0x2020;
  symbols[4].st_size = 0;
  symbols[5].st_shndx = SHN_UNDEF;
  std::memcpy(bytes.data() + symbols_offset, symbols, sizeof(symbols));
  for (uint8_t index = 0; index < 16; ++index)
    bytes[data_offset + index] = index + 1;
  return bytes;
}

inline std::vector<uint8_t> variable_kernel_object_fixture() {
  auto bytes = variable_object_fixture();
  const auto kernel = code_object_fixture(1, true);
  Elf64_Ehdr kernel_header{};
  std::memcpy(&kernel_header, kernel.data(), sizeof(kernel_header));
  Elf64_Shdr note{};
  for (uint16_t index = 0; index < kernel_header.e_shnum; ++index) {
    std::memcpy(&note, kernel.data() + kernel_header.e_shoff + index * sizeof(note), sizeof(note));
    if (note.sh_type == SHT_NOTE) break;
  }
  const auto note_offset = bytes.size();
  bytes.insert(bytes.end(), kernel.begin() + note.sh_offset, kernel.begin() + note.sh_offset + note.sh_size);
  note.sh_offset = note_offset;
  Elf64_Ehdr header{};
  std::memcpy(&header, bytes.data(), sizeof(header));
  std::vector<Elf64_Shdr> sections(header.e_shnum);
  std::memcpy(sections.data(), bytes.data() + header.e_shoff, sections.size() * sizeof(Elf64_Shdr));
  sections.push_back(note);
  header.e_shoff = bytes.size();
  header.e_shnum = sections.size();
  bytes.resize(bytes.size() + sections.size() * sizeof(Elf64_Shdr));
  std::memcpy(bytes.data() + header.e_shoff, sections.data(), sections.size() * sizeof(Elf64_Shdr));
  std::memcpy(bytes.data(), &header, sizeof(header));
  return bytes;
}

} // namespace gemstone::hsa::test

#endif
