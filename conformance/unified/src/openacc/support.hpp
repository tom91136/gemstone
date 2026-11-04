#pragma once
#include <cmath>
#include <cstddef>
#include <numeric>
#include <type_traits>
#include <vector>

#include <openacc.h>

#include <catch2/catch_template_test_macros.hpp>
#include <catch2/catch_test_macros.hpp>

namespace unified::openacc {
inline constexpr int Count = 64;

inline size_t sz(int n) { return static_cast<size_t>(n); }

template <class T> std::vector<T> iotaScaled(int n, T scale = T{1}, T offset = T{0}) {
  std::vector<T> result(sz(n));
  for (int i = 0; i < n; ++i)
    result[sz(i)] = (static_cast<T>(i) * scale) + offset;
  return result;
}
} // namespace unified::openacc
