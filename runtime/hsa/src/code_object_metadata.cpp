// SPDX-License-Identifier: Apache-2.0
#include "gemstone/hsa/code_object_metadata.hpp"

#include <algorithm>
#include <array>
#include <cstring>
#include <limits>
#include <string_view>
#include <utility>

#include <elf.h>

namespace gemstone::hsa {
namespace {

constexpr uint32_t kAmdGpuMetadataNote = 32;
constexpr size_t kMaximumDepth = 64;
constexpr size_t kMaximumContainerEntries = 1U << 20;

enum class ValueKind { nil, boolean, integer, string, array, map };

struct Value {
  ValueKind kind = ValueKind::nil;
  bool boolean = false;
  int64_t integer = 0;
  std::string string;
  std::vector<Value> array;
  std::vector<std::pair<Value, Value>> map;
};

class MessagePackReader {
public:
  MessagePackReader(const uint8_t *bytes, size_t size) : cursor_(bytes), end_(bytes + size) {}

  bool read(Value &value, std::string &error, size_t depth = 0) {
    if (depth > kMaximumDepth) return fail(error, "MessagePack nesting is too deep");
    uint8_t tag = 0;
    if (!read_unsigned(tag)) return fail(error, "truncated MessagePack value");
    if (tag <= 0x7f) return set_integer(value, tag);
    if (tag >= 0xe0) return set_integer(value, static_cast<int8_t>(tag));
    if ((tag & 0xe0) == 0xa0) return read_string(value, tag & 0x1f, error);
    if ((tag & 0xf0) == 0x90) return read_array(value, tag & 0x0f, error, depth);
    if ((tag & 0xf0) == 0x80) return read_map(value, tag & 0x0f, error, depth);
    switch (tag) {
      case 0xc0: value = Value{}; return true;
      case 0xc2:
      case 0xc3:
        value = Value{};
        value.kind = ValueKind::boolean;
        value.boolean = tag == 0xc3;
        return true;
      case 0xcc: {
        uint8_t number = 0;
        return read_unsigned(number) ? set_integer(value, number) : fail(error, "truncated uint8");
      }
      case 0xcd: {
        uint16_t number = 0;
        return read_unsigned(number) ? set_integer(value, number) : fail(error, "truncated uint16");
      }
      case 0xce: {
        uint32_t number = 0;
        return read_unsigned(number) ? set_integer(value, number) : fail(error, "truncated uint32");
      }
      case 0xcf: {
        uint64_t number = 0;
        if (!read_unsigned(number)) return fail(error, "truncated uint64");
        if (number > static_cast<uint64_t>(std::numeric_limits<int64_t>::max()))
          return fail(error, "MessagePack integer exceeds signed metadata range");
        return set_integer(value, static_cast<int64_t>(number));
      }
      case 0xd0: {
        uint8_t number = 0;
        return read_unsigned(number) ? set_integer(value, static_cast<int8_t>(number)) : fail(error, "truncated int8");
      }
      case 0xd1: {
        uint16_t number = 0;
        return read_unsigned(number) ? set_integer(value, static_cast<int16_t>(number)) : fail(error, "truncated int16");
      }
      case 0xd2: {
        uint32_t number = 0;
        return read_unsigned(number) ? set_integer(value, static_cast<int32_t>(number)) : fail(error, "truncated int32");
      }
      case 0xd3: {
        uint64_t number = 0;
        return read_unsigned(number) ? set_integer(value, static_cast<int64_t>(number)) : fail(error, "truncated int64");
      }
      case 0xd9: {
        uint8_t size = 0;
        return read_unsigned(size) ? read_string(value, size, error) : fail(error, "truncated str8 size");
      }
      case 0xda: {
        uint16_t size = 0;
        return read_unsigned(size) ? read_string(value, size, error) : fail(error, "truncated str16 size");
      }
      case 0xdb: {
        uint32_t size = 0;
        return read_unsigned(size) ? read_string(value, size, error) : fail(error, "truncated str32 size");
      }
      case 0xdc: {
        uint16_t size = 0;
        return read_unsigned(size) ? read_array(value, size, error, depth) : fail(error, "truncated array16 size");
      }
      case 0xdd: {
        uint32_t size = 0;
        return read_unsigned(size) ? read_array(value, size, error, depth) : fail(error, "truncated array32 size");
      }
      case 0xde: {
        uint16_t size = 0;
        return read_unsigned(size) ? read_map(value, size, error, depth) : fail(error, "truncated map16 size");
      }
      case 0xdf: {
        uint32_t size = 0;
        return read_unsigned(size) ? read_map(value, size, error, depth) : fail(error, "truncated map32 size");
      }
      default: return fail(error, "unsupported MessagePack type in AMDGPU metadata");
    }
  }

  bool finished() const { return cursor_ == end_; }

private:
  template <typename T> bool read_unsigned(T &result) {
    if (static_cast<size_t>(end_ - cursor_) < sizeof(T)) return false;
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

  static bool set_integer(Value &value, int64_t number) {
    value = Value{};
    value.kind = ValueKind::integer;
    value.integer = number;
    return true;
  }

  bool read_string(Value &value, size_t size, std::string &error) {
    if (static_cast<size_t>(end_ - cursor_) < size) return fail(error, "truncated MessagePack string");
    value = Value{};
    value.kind = ValueKind::string;
    value.string.assign(reinterpret_cast<const char *>(cursor_), size);
    cursor_ += size;
    return true;
  }

  bool read_array(Value &value, size_t size, std::string &error, size_t depth) {
    if (size > kMaximumContainerEntries) return fail(error, "MessagePack array is unreasonably large");
    value = Value{};
    value.kind = ValueKind::array;
    value.array.reserve(size);
    for (size_t index = 0; index < size; ++index) {
      Value item;
      if (!read(item, error, depth + 1)) return false;
      value.array.push_back(std::move(item));
    }
    return true;
  }

  bool read_map(Value &value, size_t size, std::string &error, size_t depth) {
    if (size > kMaximumContainerEntries) return fail(error, "MessagePack map is unreasonably large");
    value = Value{};
    value.kind = ValueKind::map;
    value.map.reserve(size);
    for (size_t index = 0; index < size; ++index) {
      Value key;
      Value item;
      if (!read(key, error, depth + 1) || !read(item, error, depth + 1)) return false;
      value.map.emplace_back(std::move(key), std::move(item));
    }
    return true;
  }

  const uint8_t *cursor_;
  const uint8_t *end_;
};

const Value *map_value(const Value &map, std::string_view key) {
  if (map.kind != ValueKind::map) return nullptr;
  for (const auto &[candidate, value] : map.map)
    if (candidate.kind == ValueKind::string && candidate.string == key) return &value;
  return nullptr;
}

bool uint32_value(const Value *value, uint32_t &result) {
  if (value == nullptr || value->kind != ValueKind::integer || value->integer < 0 || value->integer > std::numeric_limits<uint32_t>::max())
    return false;
  result = static_cast<uint32_t>(value->integer);
  return true;
}

bool string_value(const Value *value, std::string &result) {
  if (value == nullptr || value->kind != ValueKind::string) return false;
  result = value->string;
  return true;
}

bool allocation_value_kind(std::string_view kind) {
  constexpr std::array<std::string_view, 6> pointer_kinds = {"global_buffer",        "by_reference",         "queue",
                                                             "hidden_printf_buffer", "hidden_default_queue", "hidden_completion_action"};
  return std::find(pointer_kinds.begin(), pointer_kinds.end(), kind) != pointer_kinds.end();
}

bool decode_kernel(const Value &value, CodeObjectKernelMetadata &kernel, std::string &error) {
  if (value.kind != ValueKind::map || !string_value(map_value(value, ".name"), kernel.name)
      || !string_value(map_value(value, ".symbol"), kernel.symbol)
      || !uint32_value(map_value(value, ".kernarg_segment_size"), kernel.kernarg_size)) {
    error = "AMDGPU kernel metadata is missing name, symbol, or kernarg size";
    return false;
  }
  const Value *arguments = map_value(value, ".args");
  if (arguments == nullptr) return kernel.kernarg_size == 0;
  if (arguments->kind != ValueKind::array) {
    error = "AMDGPU kernel argument metadata is not an array";
    return false;
  }
  kernel.fields.reserve(arguments->array.size());
  for (const Value &argument : arguments->array) {
    uint32_t offset = 0;
    uint32_t size = 0;
    std::string value_kind;
    if (argument.kind != ValueKind::map || !uint32_value(map_value(argument, ".offset"), offset)
        || !uint32_value(map_value(argument, ".size"), size) || !string_value(map_value(argument, ".value_kind"), value_kind) || size == 0
        || static_cast<uint64_t>(offset) + size > kernel.kernarg_size) {
      error = "invalid AMDGPU kernel argument metadata";
      return false;
    }
    const auto kind = allocation_value_kind(value_kind) ? GEMSTONE_HSA_ARGUMENT_ALLOCATION : GEMSTONE_HSA_ARGUMENT_SCALAR;
    if (kind == GEMSTONE_HSA_ARGUMENT_ALLOCATION && size != sizeof(uint64_t)) {
      error = "AMDGPU pointer kernarg is not 64 bits";
      return false;
    }
    kernel.fields.push_back({kind, offset, size});
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

bool checked_range(size_t offset, size_t size, size_t total) { return offset <= total && size <= total - offset; }

size_t align_four(size_t value) { return (value + 3U) & ~size_t{3U}; }

bool decode_note_range(const uint8_t *bytes, size_t size, std::vector<CodeObjectKernelMetadata> &kernels, std::string &error) {
  size_t offset = 0;
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

    Value root;
    MessagePackReader reader(description, description_size);
    if (!reader.read(root, error) || !reader.finished()) {
      if (error.empty()) error = "trailing AMDGPU MessagePack metadata";
      return false;
    }
    const Value *metadata_kernels = map_value(root, "amdhsa.kernels");
    if (metadata_kernels == nullptr || metadata_kernels->kind != ValueKind::array) {
      error = "AMDGPU metadata has no amdhsa.kernels array";
      return false;
    }
    std::vector<CodeObjectKernelMetadata> decoded;
    decoded.reserve(metadata_kernels->array.size());
    for (const Value &item : metadata_kernels->array) {
      CodeObjectKernelMetadata kernel;
      if (!decode_kernel(item, kernel, error)) return false;
      decoded.push_back(std::move(kernel));
    }
    kernels = std::move(decoded);
    return true;
  }
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

} // namespace gemstone::hsa
