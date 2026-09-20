#pragma once

#include <vector>

#include <stddef.h>

namespace gemstone {

template <typename T> bool tryResize(std::vector<T> &values, size_t size) noexcept {
  try {
    values.resize(size);
    return true;
  } catch (...) {
    return false;
  }
}

} // namespace gemstone
