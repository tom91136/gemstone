#pragma once
#include <algorithm>
#include <array>
#include <climits>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <functional>
#include <limits>
#include <stdexcept>
#include <string>
#include <string_view>
#include <type_traits>
#include <utility>
#include <vector>

#include <catch2/matchers/catch_matchers_templated.hpp>

#include <fmt/format.h>

#if defined(__CUDACC__) || defined(__HIPCC__)
  #define UNIFIED_HOST_DEVICE __host__ __device__
#else
  #define UNIFIED_HOST_DEVICE
#endif

namespace unified {
enum class ScalarType { i8, i32, i64, f16, f32, f64 };
struct MatrixShape {
  size_t m, n, k;
  friend bool operator==(const MatrixShape &a, const MatrixShape &b) { return a.m == b.m && a.n == b.n && a.k == b.k; }
};
inline constexpr std::array<size_t, 17> algorithm_sizes{0, 1, 7, 15, 31, 32, 33, 63, 64, 65, 127, 128, 129, 257, 1023, 1024, 4096};
inline constexpr std::array<size_t, 8> blas_sizes{1, 2, 7, 31, 32, 64, 257, 1024};
inline constexpr std::array<size_t, 13> edge_sizes{0, 1, 2, 7, 15, 31, 32, 33, 63, 64, 257, 1023, 1024};
// GEMM is cubic: 1024 already needs ~1.8e8 multiply-adds, which exhausts the
// emulator instruction budget. Cap the padded layouts at a prime so the
// non-power-of-two leading dimensions are still exercised.
inline constexpr std::array<size_t, 12> gemm_edge_sizes{0, 1, 2, 7, 15, 31, 32, 33, 63, 64, 257, 509};
inline constexpr std::array<size_t, 3> stress_sizes{257, 4096, 16384};
inline constexpr std::array<MatrixShape, 5> matrix_shapes{{{1, 1, 1}, {2, 2, 3}, {3, 5, 7}, {7, 5, 9}, {17, 13, 19}}};
// Compact and full workloads select separate native Catch cases. No environment-dependent inventory.
inline constexpr std::array<size_t, 9> compact_algorithm_sizes{0, 1, 31, 32, 33, 63, 64, 65, 257};
inline constexpr std::array<size_t, 4> compact_blas_sizes{1, 31, 64, 257};
inline constexpr std::array<size_t, 7> compact_edge_sizes{0, 1, 7, 31, 32, 33, 257};
inline constexpr std::array<size_t, 2> compact_stress_sizes{257, 4096};
inline constexpr std::array<MatrixShape, 3> compact_matrix_shapes{{{1, 1, 1}, {2, 2, 3}, {7, 5, 9}}};
inline constexpr std::array<size_t, 6> compact_hip_scan_sizes{0, 1, 7, 31, 32, 33};
inline constexpr std::array<size_t, 8> hip_scan_sizes{0, 1, 2, 7, 15, 31, 32, 33};
namespace support {
inline size_t checked_add(size_t left, size_t right, std::string_view where) {
  if (right > std::numeric_limits<size_t>::max() - left) throw std::invalid_argument(std::string(where) + " overflows size_t");
  return left + right;
}

inline size_t checked_multiply(size_t left, size_t right, std::string_view where) {
  if (left != 0 && right > std::numeric_limits<size_t>::max() / left) throw std::invalid_argument(std::string(where) + " overflows size_t");
  return left * right;
}

inline int checked_int(size_t value, std::string_view where) {
  if (value > static_cast<size_t>(INT_MAX)) throw std::invalid_argument(std::string(where) + " exceeds the vendor BLAS integer ABI");
  return static_cast<int>(value);
}

} // namespace support
} // namespace unified

namespace unified::support {

template <class T> struct Partitioned {
  std::vector<T> values;
  size_t boundary;
};

template <class T> struct KeyValues {
  std::vector<T> keys;
  std::vector<int32_t> values;
  friend bool operator==(const KeyValues &a, const KeyValues &b) { return a.keys == b.keys && a.values == b.values; }
};

template <class T> struct TwoRanges {
  std::vector<T> first;
  std::vector<T> second;
};

struct TwoPositions {
  size_t first;
  size_t second;
  friend bool operator==(TwoPositions a, TwoPositions b) { return a.first == b.first && a.second == b.second; }
};

} // namespace unified::support

namespace unified::support {

template <class T> struct Affine {
  UNIFIED_HOST_DEVICE T operator()(T value) const { return static_cast<T>(value * T{3} - T{2}); }
};

template <class T> struct Combine {
  UNIFIED_HOST_DEVICE T operator()(T left, T right) const { return static_cast<T>(left * T{2} + right * T{5}); }
};

template <class T> struct Square {
  UNIFIED_HOST_DEVICE T operator()(T value) const { return static_cast<T>(value * value); }
};

template <class T> struct Increment {
  UNIFIED_HOST_DEVICE void operator()(T &value) const { value = static_cast<T>(value + T{1}); }
};

template <class T> struct Constant {
  UNIFIED_HOST_DEVICE T operator()() const { return T{7}; }
};

template <class T> struct Plus {
  UNIFIED_HOST_DEVICE T operator()(T left, T right) const { return static_cast<T>(left + right); }
};

template <class T> struct IsEven {
  UNIFIED_HOST_DEVICE bool operator()(T value) const { return static_cast<int64_t>(value) % 2 == 0; }
};

template <class T> struct Less {
  UNIFIED_HOST_DEVICE bool operator()(T left, T right) const { return left < right; }
};

template <class T> struct Greater {
  UNIFIED_HOST_DEVICE bool operator()(T left, T right) const { return right < left; }
};

template <class T> struct ValueAt {
  UNIFIED_HOST_DEVICE T operator()(uint64_t index) const { return static_cast<T>(static_cast<int64_t>((index * 7 + 5) % 17) - 8); }
};

struct ReverseIndex {
  size_t size;
  UNIFIED_HOST_DEVICE size_t operator()(size_t index) const { return size - index - 1; }
};

template <class T> struct WidePairValue {
  UNIFIED_HOST_DEVICE int64_t operator()(T left, T right) const {
    const auto a = static_cast<int64_t>(left);
    const auto b = static_cast<int64_t>(right);
    return a * 3 + b * 5 + a * b * 7;
  }
};

template <class T> struct BiasedHash {
  UNIFIED_HOST_DEVICE uint64_t operator()(uint64_t index) const {
    auto value = index ^ (static_cast<uint64_t>(0x9e3779b97f4a7c15) + sizeof(T));
    value ^= value >> 30;
    value *= static_cast<uint64_t>(0xbf58476d1ce4e5b9);
    value ^= value >> 27;
    value *= static_cast<uint64_t>(0x94d049bb133111eb);
    return value ^ (value >> 31);
  }
};

struct Xor64 {
  UNIFIED_HOST_DEVICE uint64_t operator()(uint64_t left, uint64_t right) const { return left ^ right; }
};

template <class T> struct Identity {
  UNIFIED_HOST_DEVICE T operator()(T value) const { return value; }
};

template <class T> inline std::vector<T> algorithm_values(size_t size, size_t salt = 0) {
  std::vector<T> result(size);
  for (size_t index = 0; index < size; ++index) {
    const auto value = static_cast<int64_t>((index * 17 + salt * 11) % 23) - 11;
    result[index] = static_cast<T>(value);
  }
  return result;
}

template <class T> inline std::vector<T> duplicate_keys(size_t size) {
  std::vector<T> result(size);
  for (size_t index = 0; index < size; ++index)
    result[index] = static_cast<T>((index * 7 + 3) % 11);
  return result;
}

template <class T> inline std::vector<T> sorted_values(size_t size, size_t stride) {
  std::vector<T> result(size);
  if constexpr (std::is_same_v<T, int8_t>) {
    constexpr int64_t minimum = -48;
    constexpr size_t span = 96;
    const auto divisor = std::max<size_t>(1, (size * stride + span - 1) / span);
    for (size_t index = 0; index < size; ++index) {
      const auto rank = std::min<size_t>(span, (index * stride) / divisor);
      result[index] = static_cast<T>(minimum + static_cast<int64_t>(rank));
    }
  } else {
    for (size_t index = 0; index < size; ++index)
      result[index] = static_cast<T>((index * stride) / 2);
  }
  return result;
}

template <class T> inline std::vector<T> edge_values(size_t size) {
  std::vector<T> result(size);
  for (size_t index = 0; index < size; ++index)
    result[index] = ValueAt<T>{}(index);
  return result;
}

template <class T> inline std::vector<T> async_input_values(size_t size) {
  std::vector<T> result(size);
  for (size_t index = 0; index < size; ++index)
    result[index] = ValueAt<T>{}(index + 3);
  return result;
}

} // namespace unified::support

#undef UNIFIED_HOST_DEVICE

namespace unified {
// Catch matchers express domain comparisons; Catch owns assertions and diagnostics.
struct WithinTolerance : Catch::Matchers::MatcherGenericBase {
  long double tolerance;
  std::vector<long double> expected;
  std::vector<bool> active;
  WithinTolerance(long double tolerance, long double expected) : tolerance(tolerance), expected{expected} {}
  WithinTolerance(long double tolerance, std::vector<long double> expected, std::vector<bool> active = {})
      : tolerance(tolerance), expected(std::move(expected)), active(std::move(active)) {}
  bool match(long double actual) const {
    return std::isfinite(actual) && std::fabs(actual - expected.front()) <= tolerance * std::max(1.0L, std::fabs(expected.front()));
  }
  bool match(const std::vector<long double> &actual) const {
    if (actual.size() != expected.size() || (!active.empty() && active.size() != expected.size())) return false;
    for (size_t i = 0; i < actual.size(); ++i) {
      if (!active.empty() && !active[i]) {
        if (actual[i] != expected[i]) return false;
      } else if (!WithinTolerance(tolerance, expected[i]).match(actual[i])) return false;
    }
    return true;
  }
  std::string describe() const override {
    return fmt::format("matches BLAS values within relative/absolute tolerance {} and preserves inactive storage exactly",
                       static_cast<double>(tolerance));
  }
};
template <class T> struct KeyPermutation : Catch::Matchers::MatcherGenericBase {
  const std::vector<int32_t> &payload;
  const std::vector<T> &source;
  KeyPermutation(const std::vector<int32_t> &payload, const std::vector<T> &source) : payload(payload), source(source) {}
  bool match(const std::vector<T> &keys) const {
    if (keys.size() != source.size() || payload.size() != source.size()) return false;
    std::vector<bool> seen(source.size(), false);
    for (size_t i = 0; i < keys.size(); ++i) {
      if (payload[i] < 0 || static_cast<size_t>(payload[i]) >= source.size()) return false;
      const auto index = static_cast<size_t>(payload[i]);
      if (seen[index] || keys[i] != source[index]) return false;
      seen[index] = true;
    }
    return true;
  }
  std::string describe() const override { return "is a permutation preserving every key/payload association"; }
};
template <class T> struct BitwiseEqual : Catch::Matchers::MatcherGenericBase {
  const std::vector<T> &expected;
  explicit BitwiseEqual(const std::vector<T> &expected) : expected(expected) {}
  bool match(const std::vector<T> &actual) const {
    return actual.size() == expected.size()
           && (actual.empty() || std::memcmp(actual.data(), expected.data(), actual.size() * sizeof(T)) == 0);
  }
  std::string describe() const override { return "preserves every value's bits, including NaNs and signed zero"; }
};
} // namespace unified

namespace unified::backend::blas_detail {

template <class T, class Memory> class DeviceBuffer {
public:
  DeviceBuffer() = default;
  DeviceBuffer(T *data, size_t size) : data_(data), size_(size) {}
  DeviceBuffer(const DeviceBuffer &) = delete;
  DeviceBuffer &operator=(const DeviceBuffer &) = delete;
  DeviceBuffer(DeviceBuffer &&other) noexcept : data_(std::exchange(other.data_, nullptr)), size_(std::exchange(other.size_, 0)) {}
  DeviceBuffer &operator=(DeviceBuffer &&other) noexcept {
    if (this != &other) {
      release();
      data_ = std::exchange(other.data_, nullptr);
      size_ = std::exchange(other.size_, 0);
    }
    return *this;
  }
  ~DeviceBuffer() { release(); }

  T *data() { return data_; }
  const T *data() const { return data_; }
  size_t size() const { return size_; }

private:
  T *data_{};
  size_t size_{};

  void release() {
    if (data_) Memory::release(data_);
    data_ = nullptr;
    size_ = 0;
  }
};

} // namespace unified::backend::blas_detail

namespace unified::test_runtime {
// Release acquired API objects in reverse order, including on assertion failure.
class Cleanup {
  std::vector<std::function<void()>> actions_;

public:
  Cleanup() = default;
  Cleanup(const Cleanup &) = delete;
  Cleanup &operator=(const Cleanup &) = delete;
  template <class F> void add(F action) { actions_.emplace_back(std::move(action)); }
  ~Cleanup() noexcept {
    for (auto it = actions_.rbegin(); it != actions_.rend(); ++it) {
      try {
        (*it)();
      } catch (...) {
        // Preserve the original failure if cleanup itself throws.
      }
    }
  }
};

inline std::vector<int32_t> input(size_t size) {
  std::vector<int32_t> values(size);
  for (size_t i = 0; i < size; ++i)
    values[i] = static_cast<int32_t>(i * 37) - 511;
  return values;
}
} // namespace unified::test_runtime
