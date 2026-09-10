// SPDX-License-Identifier: Apache-2.0
#include "gemstone/hsa/code_object_metadata.hpp"

#include <algorithm>
#include <array>
#include <cstring>
#include <iterator>
#include <limits>
#include <string_view>
#include <utility>

#include <elf.h>

namespace gemstone::hsa {
namespace {

constexpr uint32_t kAmdGpuMetadataNote = 32;
constexpr size_t kMaximumDepth = 64;
constexpr size_t kMaximumContainerEntries = 1U << 20;

class MessagePackReader {
public:
  MessagePackReader(const uint8_t *bytes, size_t size) : cursor_(bytes), end_(bytes + size) {}

  bool finished() const { return cursor_ == end_; }

  bool map_size(size_t &size, std::string &error) { return container_size(false, size, error); }
  bool array_size(size_t &size, std::string &error) { return container_size(true, size, error); }

  bool string(std::string_view &value, std::string &error) {
    uint8_t tag = 0;
    if (!read_unsigned(tag)) return fail(error, "truncated MessagePack string");
    size_t size = 0;
    if ((tag & 0xe0) == 0xa0) size = tag & 0x1f;
    else if (tag == 0xd9) {
      uint8_t value = 0;
      if (!read_unsigned(value)) return fail(error, "truncated str8 size");
      size = value;
    } else if (tag == 0xda) {
      uint16_t value = 0;
      if (!read_unsigned(value)) return fail(error, "truncated str16 size");
      size = value;
    } else if (tag == 0xdb) {
      uint32_t value = 0;
      if (!read_unsigned(value)) return fail(error, "truncated str32 size");
      size = value;
    } else return fail(error, "AMDGPU metadata key is not a string");
    if (remaining() < size) return fail(error, "truncated MessagePack string");
    value = std::string_view(reinterpret_cast<const char *>(cursor_), size);
    cursor_ += size;
    return true;
  }

  bool uint32(uint32_t &value, std::string &error) {
    uint8_t tag = 0;
    if (!read_unsigned(tag)) return fail(error, "truncated MessagePack integer");
    uint64_t number = 0;
    if (tag <= 0x7f) number = tag;
    else if (tag == 0xcc) {
      uint8_t x = 0;
      if (!read_unsigned(x)) return fail(error, "truncated uint8");
      number = x;
    } else if (tag == 0xcd) {
      uint16_t x = 0;
      if (!read_unsigned(x)) return fail(error, "truncated uint16");
      number = x;
    } else if (tag == 0xce) {
      uint32_t x = 0;
      if (!read_unsigned(x)) return fail(error, "truncated uint32");
      number = x;
    } else if (tag == 0xcf) {
      if (!read_unsigned(number)) return fail(error, "truncated uint64");
    } else return fail(error, "AMDGPU metadata integer is negative or malformed");
    if (number > std::numeric_limits<uint32_t>::max()) return fail(error, "AMDGPU metadata integer exceeds uint32");
    value = static_cast<uint32_t>(number);
    return true;
  }

  bool skip(std::string &error, size_t depth = 0) {
    if (depth > kMaximumDepth) return fail(error, "MessagePack nesting is too deep");
    uint8_t tag = 0;
    if (!read_unsigned(tag)) return fail(error, "truncated MessagePack value");
    if (tag <= 0x7f || tag >= 0xe0 || tag == 0xc0 || tag == 0xc2 || tag == 0xc3) return true;
    size_t count = 0;
    if ((tag & 0xe0) == 0xa0) return skip_bytes(tag & 0x1f, error);
    if ((tag & 0xf0) == 0x90) count = tag & 0x0f;
    else if ((tag & 0xf0) == 0x80) count = 2 * (tag & 0x0f);
    else switch (tag) {
      case 0xcc:
      case 0xd0: return skip_bytes(1, error);
      case 0xcd:
      case 0xd1: return skip_bytes(2, error);
      case 0xce:
      case 0xd2: return skip_bytes(4, error);
      case 0xcf:
      case 0xd3: return skip_bytes(8, error);
      case 0xd9: {
        uint8_t size = 0;
        return read_unsigned(size) && skip_bytes(size, error);
      }
      case 0xda: {
        uint16_t size = 0;
        return read_unsigned(size) && skip_bytes(size, error);
      }
      case 0xdb: {
        uint32_t size = 0;
        return read_unsigned(size) && skip_bytes(size, error);
      }
      case 0xdc: {
        uint16_t size = 0;
        if (!read_unsigned(size)) return fail(error, "truncated array16 size");
        count = size;
        break;
      }
      case 0xdd: {
        uint32_t size = 0;
        if (!read_unsigned(size)) return fail(error, "truncated array32 size");
        count = size;
        break;
      }
      case 0xde: {
        uint16_t size = 0;
        if (!read_unsigned(size)) return fail(error, "truncated map16 size");
        count = 2ULL * size;
        break;
      }
      case 0xdf: {
        uint32_t size = 0;
        if (!read_unsigned(size)) return fail(error, "truncated map32 size");
        count = 2ULL * size;
        break;
      }
      default: return fail(error, "unsupported MessagePack type in AMDGPU metadata");
    }
    if (count > 2 * kMaximumContainerEntries) return fail(error, "MessagePack container is unreasonably large");
    for (size_t index = 0; index < count; ++index)
      if (!skip(error, depth + 1)) return false;
    return true;
  }

private:
  size_t remaining() const { return static_cast<size_t>(end_ - cursor_); }

  template <typename T> bool read_unsigned(T &result) {
    if (remaining() < sizeof(T)) return false;
    result = 0;
    for (size_t index = 0; index < sizeof(T); ++index)
      result = static_cast<T>((result << 8) | cursor_[index]);
    cursor_ += sizeof(T);
    return true;
  }

  static bool fail(std::string &error, const char *message) {
    error = message;
    return false;
  }

  bool skip_bytes(size_t size, std::string &error) {
    if (remaining() < size) return fail(error, "truncated MessagePack value");
    cursor_ += size;
    return true;
  }

  bool container_size(bool array, size_t &size, std::string &error) {
    uint8_t tag = 0;
    if (!read_unsigned(tag)) return fail(error, "truncated MessagePack container");
    if (array && (tag & 0xf0) == 0x90) size = tag & 0x0f;
    else if (!array && (tag & 0xf0) == 0x80) size = tag & 0x0f;
    else if (array && tag == 0xdc) {
      uint16_t value = 0;
      if (!read_unsigned(value)) return fail(error, "truncated array16 size");
      size = value;
    } else if (array && tag == 0xdd) {
      uint32_t value = 0;
      if (!read_unsigned(value)) return fail(error, "truncated array32 size");
      size = value;
    } else if (!array && tag == 0xde) {
      uint16_t value = 0;
      if (!read_unsigned(value)) return fail(error, "truncated map16 size");
      size = value;
    } else if (!array && tag == 0xdf) {
      uint32_t value = 0;
      if (!read_unsigned(value)) return fail(error, "truncated map32 size");
      size = value;
    } else return fail(error, array ? "AMDGPU metadata value is not an array" : "AMDGPU metadata value is not a map");
    return size <= kMaximumContainerEntries || fail(error, "MessagePack container is unreasonably large");
  }

  const uint8_t *cursor_;
  const uint8_t *end_;
};

bool allocation_value_kind(std::string_view kind) {
  constexpr std::array<std::string_view, 3> pointer_kinds = {"global_buffer", "by_reference", "queue"};
  return std::find(pointer_kinds.begin(), pointer_kinds.end(), kind) != pointer_kinds.end();
}

bool decode_arguments(MessagePackReader &reader, CodeObjectKernelMetadata &kernel, std::string &error) {
  size_t count = 0;
  if (!reader.array_size(count, error)) return false;
  kernel.fields.reserve(count);
  for (size_t index = 0; index < count; ++index) {
    size_t fields = 0;
    if (!reader.map_size(fields, error)) return false;
    uint32_t offset = 0, size = 0;
    std::string_view value_kind;
    bool have_offset = false, have_size = false, have_kind = false;
    for (size_t field = 0; field < fields; ++field) {
      std::string_view key;
      if (!reader.string(key, error)) return false;
      if (key == ".offset") have_offset = reader.uint32(offset, error);
      else if (key == ".size") have_size = reader.uint32(size, error);
      else if (key == ".value_kind") have_kind = reader.string(value_kind, error);
      else if (!reader.skip(error)) return false;
      if (!error.empty()) return false;
    }
    if (!have_offset || !have_size || !have_kind || size == 0) {
      error = "invalid AMDGPU kernel argument metadata";
      return false;
    }
    const auto kind = allocation_value_kind(value_kind) ? GEMSTONE_HSA_ARGUMENT_ALLOCATION : GEMSTONE_HSA_ARGUMENT_SCALAR;
    if (kind == GEMSTONE_HSA_ARGUMENT_ALLOCATION && size != sizeof(uint64_t)) {
      error = "AMDGPU pointer kernarg is not 64 bits";
      return false;
    }
    if (value_kind.size() < 7 || value_kind.substr(0, 7) != "hidden_") ++kernel.explicit_argument_count;
    kernel.fields.push_back({kind, offset, size});
  }
  return true;
}

bool decode_kernel(MessagePackReader &reader, CodeObjectKernelMetadata &kernel, std::string &error) {
  size_t count = 0;
  if (!reader.map_size(count, error)) return false;
  bool have_name = false, have_symbol = false, have_size = false, have_arguments = false;
  for (size_t index = 0; index < count; ++index) {
    std::string_view key;
    if (!reader.string(key, error)) return false;
    if (key == ".name" || key == ".symbol") {
      std::string_view value;
      if (!reader.string(value, error)) return false;
      if (key == ".name") { kernel.name.assign(value.data(), value.size()); have_name = true; }
      else { kernel.symbol.assign(value.data(), value.size()); have_symbol = true; }
    } else if (key == ".kernarg_segment_size") have_size = reader.uint32(kernel.kernarg_size, error);
    else if (key == ".args") { have_arguments = true; if (!decode_arguments(reader, kernel, error)) return false; }
    else if (!reader.skip(error)) return false;
  }
  if (!have_name || !have_symbol || !have_size || (!have_arguments && kernel.kernarg_size != 0)) {
    error = "AMDGPU kernel metadata is missing name, symbol, kernarg size, or arguments";
    return false;
  }
  for (const auto &field : kernel.fields)
    if (static_cast<uint64_t>(field.offset) + field.size > kernel.kernarg_size) {
      error = "invalid AMDGPU kernel argument metadata";
      return false;
    }
  std::sort(kernel.fields.begin(), kernel.fields.end(), [](const auto &left, const auto &right) { return left.offset < right.offset; });
  for (size_t index = 1; index < kernel.fields.size(); ++index) {
    const auto &previous = kernel.fields[index - 1];
    if (static_cast<uint64_t>(previous.offset) + previous.size > kernel.fields[index].offset) {
      error = "overlapping AMDGPU kernarg fields";
      return false;
    }
  }
  return true;
}

bool decode_metadata(MessagePackReader &reader, std::vector<CodeObjectKernelMetadata> &kernels, std::string &error) {
  size_t fields = 0;
  if (!reader.map_size(fields, error)) return false;
  bool found = false;
  for (size_t field = 0; field < fields; ++field) {
    std::string_view key;
    if (!reader.string(key, error)) return false;
    if (key != "amdhsa.kernels") {
      if (!reader.skip(error)) return false;
      continue;
    }
    size_t count = 0;
    if (!reader.array_size(count, error)) return false;
    kernels.reserve(kernels.size() + count);
    for (size_t index = 0; index < count; ++index) {
      CodeObjectKernelMetadata kernel;
      if (!decode_kernel(reader, kernel, error)) return false;
      kernels.push_back(std::move(kernel));
    }
    found = true;
  }
  if (!found) error = "AMDGPU metadata has no amdhsa.kernels array";
  return found && reader.finished();
}

bool checked_range(size_t offset, size_t size, size_t total) { return offset <= total && size <= total - offset; }

size_t align_four(size_t value) { return (value + 3U) & ~size_t{3U}; }

bool decode_note_range(const uint8_t *bytes, size_t size, std::vector<CodeObjectKernelMetadata> &kernels, std::string &error) {
  size_t offset = 0;
  bool found_metadata = false;
  while (checked_range(offset, 12, size)) {
    uint32_t name_size = 0;
    uint32_t description_size = 0;
    uint32_t type = 0;
    std::memcpy(&name_size, bytes + offset, 4);
    std::memcpy(&description_size, bytes + offset + 4, 4);
    std::memcpy(&type, bytes + offset + 8, 4);
    offset += 12;
    if (name_size > std::numeric_limits<size_t>::max() - 3 || description_size > std::numeric_limits<size_t>::max() - 3) {
      error = "ELF note length overflow";
      return false;
    }
    const size_t padded_name = align_four(name_size);
    const size_t padded_description = align_four(description_size);
    if (!checked_range(offset, padded_name, size) || !checked_range(offset + padded_name, padded_description, size)) {
      error = "truncated ELF note";
      return false;
    }
    const bool owner = name_size == 7 && std::memcmp(bytes + offset, "AMDGPU\0", 7) == 0;
    const uint8_t *description = bytes + offset + padded_name;
    offset += padded_name + padded_description;
    if (!owner || type != kAmdGpuMetadataNote) continue;

    MessagePackReader reader(description, description_size);
    std::vector<CodeObjectKernelMetadata> decoded;
    if (!decode_metadata(reader, decoded, error)) return false;
    kernels.insert(kernels.end(), std::make_move_iterator(decoded.begin()), std::make_move_iterator(decoded.end()));
    found_metadata = true;
  }
  if (found_metadata) return true;
  error = "AMDGPU metadata note is absent";
  return false;
}

} // namespace

bool parse_code_object_metadata(const void *image, size_t image_size, std::vector<CodeObjectKernelMetadata> &kernels, std::string &error) {
  kernels.clear();
  error.clear();
  if (image == nullptr || image_size < sizeof(Elf64_Ehdr)) {
    error = "AMDGPU code object is truncated";
    return false;
  }
  const auto *bytes = static_cast<const uint8_t *>(image);
  Elf64_Ehdr header{};
  std::memcpy(&header, bytes, sizeof(header));
  if (std::memcmp(header.e_ident, ELFMAG, SELFMAG) != 0 || header.e_ident[EI_CLASS] != ELFCLASS64 || header.e_ident[EI_DATA] != ELFDATA2LSB
      || header.e_machine != EM_AMDGPU || header.e_shentsize != sizeof(Elf64_Shdr) || header.e_shnum == 0
      || !checked_range(header.e_shoff, static_cast<size_t>(header.e_shnum) * sizeof(Elf64_Shdr), image_size)) {
    error = "invalid ELF64 AMDGPU code object";
    return false;
  }
  for (uint16_t index = 0; index < header.e_shnum; ++index) {
    Elf64_Shdr section{};
    const size_t section_header_offset = static_cast<size_t>(header.e_shoff) + index * sizeof(section);
    std::memcpy(&section, bytes + section_header_offset, sizeof(section));
    if (section.sh_type != SHT_NOTE) continue;
    if (section.sh_offset > std::numeric_limits<size_t>::max() || section.sh_size > std::numeric_limits<size_t>::max()
        || !checked_range(static_cast<size_t>(section.sh_offset), static_cast<size_t>(section.sh_size), image_size)) {
      error = "invalid ELF note section range";
      return false;
    }
    std::string section_error;
    if (decode_note_range(bytes + section.sh_offset, section.sh_size, kernels, section_error)) return true;
    if (section_error != "AMDGPU metadata note is absent") {
      error = std::move(section_error);
      return false;
    }
  }
  error = "AMDGPU metadata note is absent";
  return false;
}

bool parse_code_object_variables(const void *image, size_t image_size, std::vector<CodeObjectVariable> &variables, std::string &error) {
  variables.clear();
  error.clear();
  const auto fail = [&](const char *message) {
    variables.clear();
    error = message;
    return false;
  };
  if (image == nullptr || image_size < sizeof(Elf64_Ehdr)) return fail("AMDGPU code object is truncated");
  const auto *bytes = static_cast<const uint8_t *>(image);
  Elf64_Ehdr header{};
  std::memcpy(&header, bytes, sizeof(header));
  if (std::memcmp(header.e_ident, ELFMAG, SELFMAG) != 0 || header.e_ident[EI_CLASS] != ELFCLASS64 || header.e_ident[EI_DATA] != ELFDATA2LSB
      || header.e_machine != EM_AMDGPU || header.e_shentsize != sizeof(Elf64_Shdr) || header.e_shnum == 0
      || !checked_range(header.e_shoff, size_t(header.e_shnum) * sizeof(Elf64_Shdr), image_size))
    return fail("invalid ELF64 AMDGPU code object");
  std::vector<Elf64_Shdr> sections(header.e_shnum);
  std::memcpy(sections.data(), bytes + header.e_shoff, sections.size() * sizeof(Elf64_Shdr));
  std::vector<CodeObjectVariable> parsed;
  for (const auto &section : sections) {
    if (section.sh_type != SHT_SYMTAB && section.sh_type != SHT_DYNSYM) continue;
    if (section.sh_entsize != sizeof(Elf64_Sym) || section.sh_size % sizeof(Elf64_Sym) != 0
        || !checked_range(section.sh_offset, section.sh_size, image_size) || section.sh_link >= sections.size())
      return fail("invalid ELF symbol table");
    const auto &strings = sections[section.sh_link];
    if (strings.sh_type != SHT_STRTAB || !checked_range(strings.sh_offset, strings.sh_size, image_size))
      return fail("invalid ELF symbol string table");
    for (uint64_t offset = 0; offset < section.sh_size; offset += sizeof(Elf64_Sym)) {
      Elf64_Sym symbol{};
      std::memcpy(&symbol, bytes + section.sh_offset + offset, sizeof(symbol));
      if (ELF64_ST_TYPE(symbol.st_info) != STT_OBJECT || symbol.st_shndx == SHN_UNDEF
          || (ELF64_ST_BIND(symbol.st_info) != STB_GLOBAL && ELF64_ST_BIND(symbol.st_info) != STB_WEAK))
        continue;
      if (symbol.st_shndx >= sections.size()) return fail("invalid ELF variable section");
      const auto &storage = sections[symbol.st_shndx];
      if ((storage.sh_flags & SHF_ALLOC) == 0) continue;
      if (storage.sh_type != SHT_PROGBITS && storage.sh_type != SHT_NOBITS) return fail("invalid ELF variable storage type");
      if (symbol.st_name >= strings.sh_size) return fail("invalid ELF variable name offset");
      const auto *name = reinterpret_cast<const char *>(bytes + strings.sh_offset + symbol.st_name);
      const void *terminator = std::memchr(name, 0, strings.sh_size - symbol.st_name);
      if (terminator == nullptr || name[0] == '\0') return fail("invalid ELF variable name");
      if (symbol.st_size > UINT32_MAX || symbol.st_value > UINT64_MAX - symbol.st_size || symbol.st_value < storage.sh_addr
          || symbol.st_value - storage.sh_addr > storage.sh_size || symbol.st_size > storage.sh_size - (symbol.st_value - storage.sh_addr))
        return fail("ELF variable exceeds its storage section");
      CodeObjectVariable variable;
      variable.name = name;
      variable.address = symbol.st_value;
      variable.size = static_cast<uint32_t>(symbol.st_size);
      if (storage.sh_type != SHT_NOBITS) {
        if (!checked_range(storage.sh_offset, storage.sh_size, image_size)) return fail("invalid ELF variable storage range");
        const auto *start = bytes + storage.sh_offset + (symbol.st_value - storage.sh_addr);
        variable.initial_data.assign(start, start + variable.size);
      }
      const auto duplicate = std::find_if(parsed.begin(), parsed.end(), [&](const auto &item) { return item.name == variable.name; });
      if (duplicate == parsed.end()) parsed.push_back(std::move(variable));
      else if (duplicate->address != variable.address || duplicate->size != variable.size
               || duplicate->initial_data != variable.initial_data)
        return fail("conflicting ELF variable definitions");
    }
  }
  for (const auto &section : sections) {
    if (section.sh_type != SHT_RELA) continue;
    if (section.sh_entsize != sizeof(Elf64_Rela) || section.sh_size % sizeof(Elf64_Rela) != 0
        || !checked_range(section.sh_offset, section.sh_size, image_size))
      return fail("invalid ELF relocation table");
    for (uint64_t offset = 0; offset < section.sh_size; offset += sizeof(Elf64_Rela)) {
      Elf64_Rela relocation{};
      std::memcpy(&relocation, bytes + section.sh_offset + offset, sizeof(relocation));
      // R_AMDGPU_ABS64 is the pointer initializer also applied by the shared
      // code-object loader. Track its exact location rather than guessing
      // whether arbitrary small scalar values are ELF addresses.
      if (ELF64_R_TYPE(relocation.r_info) != 3) continue;
      for (auto &variable : parsed) {
        if (relocation.r_offset < variable.address || relocation.r_offset - variable.address > variable.size) continue;
        const auto relative = relocation.r_offset - variable.address;
        if (variable.size - relative >= sizeof(uint64_t)) variable.pointer_offsets.push_back(static_cast<uint32_t>(relative));
      }
    }
  }
  variables = std::move(parsed);
  return true;
}

} // namespace gemstone::hsa
