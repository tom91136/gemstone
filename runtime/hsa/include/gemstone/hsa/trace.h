#ifndef GEMSTONE_HSA_TRACE_H
#define GEMSTONE_HSA_TRACE_H

#include <cstdlib>
#include <cstring>

namespace gemstone::hsa {
inline bool trace_enabled(const char *category) {
  const char *value = std::getenv("GEMSTONE_HSA_TRACE");
  if (value == nullptr) return false;
  const size_t wanted = std::strlen(category);
  while (*value != '\0') {
    while (*value == ',' || *value == ' ') ++value;
    const char *end = value;
    while (*end != '\0' && *end != ',') ++end;
    const size_t size = static_cast<size_t>(end - value);
    if ((size == wanted && std::memcmp(value, category, size) == 0) ||
        (size == 3 && std::memcmp(value, "all", 3) == 0))
      return true;
    value = end;
  }
  return false;
}
} // namespace gemstone::hsa

#endif
