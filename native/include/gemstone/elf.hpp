#pragma once

#include <algorithm>
#include <optional>

#include <elf.h>
#include <stddef.h>
#include <stdint.h>

#include <gemstone/bytes.hpp>

namespace gemstone {

/** Extent of an ELF64 little-endian image embedded without a length: the furthest byte covered by its header tables,
 * file-backed segments, and sections. Only bytes below `maximumSize` are read. Null when the image is malformed,
 * exceeds `maximumSize`, or `machine` is set and differs from `e_machine`.
 */
inline std::optional<size_t> elfImageSize(const void *image, size_t maximumSize = SIZE_MAX, uint16_t machine = EM_NONE) {
  if (!image || maximumSize < sizeof(Elf64_Ehdr)) return std::nullopt;
  const auto header = readNative<Elf64_Ehdr>(image);
  if (header.e_ident[EI_MAG0] != ELFMAG0 || header.e_ident[EI_MAG1] != ELFMAG1 || header.e_ident[EI_MAG2] != ELFMAG2
      || header.e_ident[EI_MAG3] != ELFMAG3 || header.e_ident[EI_CLASS] != ELFCLASS64 || header.e_ident[EI_DATA] != ELFDATA2LSB
      || (machine != EM_NONE && header.e_machine != machine) || header.e_ehsize < sizeof(Elf64_Ehdr)
      || (header.e_phnum != 0 && header.e_phentsize < sizeof(Elf64_Phdr))
      || (header.e_shnum != 0 && header.e_shentsize < sizeof(Elf64_Shdr)))
    return std::nullopt;
  uint64_t end = header.e_ehsize;
  const auto cover = [&](uint64_t offset, uint64_t size) {
    if (offset > maximumSize || size > maximumSize - offset) return false;
    end = std::max(end, offset + size);
    return true;
  };
  if ((header.e_phnum != 0 && !cover(header.e_phoff, static_cast<uint64_t>(header.e_phentsize) * header.e_phnum))
      || (header.e_shnum != 0 && !cover(header.e_shoff, static_cast<uint64_t>(header.e_shentsize) * header.e_shnum)))
    return std::nullopt;
  for (uint16_t index = 0; index < header.e_phnum; ++index) {
    const auto segment = readNative<Elf64_Phdr>(image, header.e_phoff + (static_cast<uint64_t>(index) * header.e_phentsize));
    if (!cover(segment.p_offset, segment.p_filesz)) return std::nullopt;
  }
  for (uint16_t index = 0; index < header.e_shnum; ++index) {
    const auto section = readNative<Elf64_Shdr>(image, header.e_shoff + (static_cast<uint64_t>(index) * header.e_shentsize));
    if (section.sh_type != SHT_NOBITS && !cover(section.sh_offset, section.sh_size)) return std::nullopt;
  }
  return static_cast<size_t>(end);
}

} // namespace gemstone
