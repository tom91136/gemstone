#pragma once

#include <cstddef>

#include "edge_cases.hpp"

namespace unified::stress {
inline constexpr size_t Iterations = 8;

template <class T, class Backend> void dependencyDiamond(Backend &backend, size_t size) {
  for (size_t iteration = 0; iteration < Iterations; ++iteration)
    edge::dependencyDiamond<T>(backend, size);
}

template <class T, class Backend> void primitiveBlockScan(Backend &backend, size_t size) {
  for (size_t iteration = 0; iteration < Iterations; ++iteration)
    edge::primitiveBlockScan<T>(backend, size);
}

template <class T, class Backend> void numericRoundtrip(Backend &backend, size_t size) {
  for (size_t iteration = 0; iteration < Iterations; ++iteration)
    edge::numericEdgeRoundtrip<T>(backend, size);
}
} // namespace unified::stress
