#pragma once

#include <cstddef>

#include "template_edge.hpp"

namespace unified::stress {
inline constexpr size_t iterations = 8;

template <class T, class Backend> void dependency_diamond(Backend &backend, size_t size) {
  for (size_t iteration = 0; iteration < iterations; ++iteration)
    edge::dependency_diamond<T>(backend, size);
}

template <class T, class Backend> void primitive_block_scan(Backend &backend, size_t size) {
  for (size_t iteration = 0; iteration < iterations; ++iteration)
    edge::primitive_block_scan<T>(backend, size);
}

template <class T, class Backend> void numeric_roundtrip(Backend &backend, size_t size) {
  for (size_t iteration = 0; iteration < iterations; ++iteration)
    edge::numeric_edge_roundtrip<T>(backend, size);
}
} // namespace unified::stress
