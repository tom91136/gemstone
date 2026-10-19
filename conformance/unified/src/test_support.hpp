#pragma once
#include <algorithm>
#include <array>
#include <climits>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <functional>
#include <limits>
#include <stdexcept>
#include <string>
#include <string_view>
#include <type_traits>
#include <utility>
#include <vector>

#include <aspartame/vector.hpp>
#include <aspartame/view.hpp>
#include <catch2/matchers/catch_matchers_templated.hpp>

#include <fmt/format.h>

#if defined(__CUDACC__) || defined(__HIPCC__)
  #define UNIFIED_HOST_DEVICE __host__ __device__
#else
  #define UNIFIED_HOST_DEVICE
#endif

namespace unified {
enum class ScalarType { F16, F32, F64 };
struct MatrixShape {
  size_t m, n, k;
  friend bool operator==(const MatrixShape &a, const MatrixShape &b) { return a.m == b.m && a.n == b.n && a.k == b.k; }
};
// Compact and full workloads select separate native Catch cases. No environment-dependent inventory.
namespace full {
inline constexpr std::array<size_t, 17> AlgorithmSizes{0, 1, 7, 15, 31, 32, 33, 63, 64, 65, 127, 128, 129, 257, 1023, 1024, 4096};
inline constexpr std::array<size_t, 8> BlasSizes{1, 2, 7, 31, 32, 64, 257, 1024};
inline constexpr std::array<size_t, 13> EdgeSizes{0, 1, 2, 7, 15, 31, 32, 33, 63, 64, 257, 1023, 1024};
// GEMM is cubic: 1024 already needs ~1.8e8 multiply-adds, which exhausts the
// emulator instruction budget. Cap the padded layouts at a prime so the
// non-power-of-two leading dimensions are still exercised.
inline constexpr std::array<size_t, 12> GemmEdgeSizes{0, 1, 2, 7, 15, 31, 32, 33, 63, 64, 257, 509};
inline constexpr std::array<size_t, 3> StressSizes{257, 4096, 16384};
inline constexpr std::array<MatrixShape, 5> MatrixShapes{
    {{.m = 1, .n = 1, .k = 1}, {.m = 2, .n = 2, .k = 3}, {.m = 3, .n = 5, .k = 7}, {.m = 7, .n = 5, .k = 9}, {.m = 17, .n = 13, .k = 19}}};
inline constexpr std::array<size_t, 8> HipScanSizes{0, 1, 2, 7, 15, 31, 32, 33};
} // namespace full
namespace compact {
inline constexpr std::array<size_t, 9> AlgorithmSizes{0, 1, 31, 32, 33, 63, 64, 65, 257};
inline constexpr std::array<size_t, 4> BlasSizes{1, 31, 64, 257};
inline constexpr std::array<size_t, 7> EdgeSizes{0, 1, 7, 31, 32, 33, 257};
inline constexpr auto &GemmEdgeSizes = EdgeSizes;
inline constexpr std::array<size_t, 2> StressSizes{257, 4096};
inline constexpr std::array<MatrixShape, 3> MatrixShapes{{{.m = 1, .n = 1, .k = 1}, {.m = 2, .n = 2, .k = 3}, {.m = 7, .n = 5, .k = 9}}};
inline constexpr std::array<size_t, 6> HipScanSizes{0, 1, 7, 31, 32, 33};
} // namespace compact
} // namespace unified

namespace unified::support {
inline size_t checkedAdd(size_t left, size_t right, std::string_view where) {
  if (right > std::numeric_limits<size_t>::max() - left) throw std::invalid_argument(std::string(where) + " overflows size_t");
  return left + right;
}

inline size_t checkedMultiply(size_t left, size_t right, std::string_view where) {
  if (left != 0 && right > std::numeric_limits<size_t>::max() / left) throw std::invalid_argument(std::string(where) + " overflows size_t");
  return left * right;
}

inline int checkedInt(size_t value, std::string_view where) {
  if (value > size_t{INT_MAX}) throw std::invalid_argument(std::string(where) + " exceeds the vendor BLAS integer ABI");
  return static_cast<int>(value);
}

inline uint32_t floatBits(float value) {
  uint32_t bits = 0;
  std::memcpy(&bits, &value, sizeof(bits));
  return bits;
}

inline float bitsFloat(uint32_t bits) {
  float value = NAN;
  std::memcpy(&value, &bits, sizeof(value));
  return value;
}

inline uint64_t doubleBits(double value) {
  uint64_t bits = 0;
  std::memcpy(&bits, &value, sizeof(bits));
  return bits;
}

inline double bitsDouble(uint64_t bits) {
  double value = NAN;
  std::memcpy(&value, &bits, sizeof(value));
  return value;
}

inline uint16_t bfloat16Bits(float value) { return static_cast<uint16_t>(floatBits(value) >> 16); }

inline bool isNan(uint32_t bits) { return (bits & 0x7fffffffU) > 0x7f800000U; }

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

template <class T> struct Affine {
  UNIFIED_HOST_DEVICE T operator()(T value) const { return static_cast<T>((value * T{3}) - T{2}); }
};

template <class T> struct Combine {
  UNIFIED_HOST_DEVICE T operator()(T left, T right) const { return static_cast<T>((left * T{2}) + (right * T{5})); }
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
  UNIFIED_HOST_DEVICE T operator()(uint64_t index) const { return static_cast<T>(static_cast<int64_t>(((index * 7) + 5) % 17) - 8); }
};

struct ReverseIndex {
  size_t size;
  UNIFIED_HOST_DEVICE size_t operator()(size_t index) const { return size - index - 1; }
};

template <class T> struct WidePairValue {
  UNIFIED_HOST_DEVICE int64_t operator()(T left, T right) const {
    const auto a = static_cast<int64_t>(left);
    const auto b = static_cast<int64_t>(right);
    return (a * 3) + (b * 5) + (a * b * 7);
  }
};

template <class T> struct BiasedHash {
  UNIFIED_HOST_DEVICE uint64_t operator()(uint64_t index) const {
    auto value = index ^ (uint64_t{0x9e3779b97f4a7c15} + sizeof(T));
    value ^= value >> 30;
    value *= uint64_t{0xbf58476d1ce4e5b9};
    value ^= value >> 27;
    value *= uint64_t{0x94d049bb133111eb};
    return value ^ (value >> 31);
  }
};

struct Xor64 {
  UNIFIED_HOST_DEVICE uint64_t operator()(uint64_t left, uint64_t right) const { return left ^ right; }
};

template <class T> struct Identity {
  UNIFIED_HOST_DEVICE T operator()(T value) const { return value; }
};

inline std::vector<int32_t> payload(size_t count, int32_t base = 0) {
  using namespace aspartame;
  return iota(size_t{0}, count) | map([=](size_t index) { return base + static_cast<int32_t>(index); }) | to_vector();
}

template <class T> inline std::vector<T> duplicateKeys(size_t size) {
  using namespace aspartame;
  return iota(size_t{0}, size) | map([](size_t index) { return static_cast<T>(((index * 7) + 3) % 11); }) | to_vector();
}

template <class T> inline std::vector<T> sortedValues(size_t size, size_t stride) {
  using namespace aspartame;
  if constexpr (std::is_same_v<T, int8_t>) {
    constexpr int64_t minimum = -48;
    constexpr size_t span = 96;
    const auto divisor = std::max<size_t>(1, ((size * stride) + span - 1) / span);
    return iota(size_t{0}, size) | map([&](size_t index) {
             const auto rank = std::min<size_t>(span, (index * stride) / divisor);
             return static_cast<T>(minimum + static_cast<int64_t>(rank));
           })
           | to_vector();
  } else {
    return iota(size_t{0}, size) | map([&](size_t index) {
             const auto value = index * stride / 2;
             return static_cast<T>(value);
           })
           | to_vector();
  }
}

template <class T> inline std::vector<T> asyncInputValues(size_t size) {
  using namespace aspartame;
  return iota(size_t{0}, size) | map([](size_t index) { return ValueAt<T>{}(index + 3); }) | to_vector();
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
  [[nodiscard]] bool match(long double actual) const {
    return std::isfinite(actual) && std::fabs(actual - expected.front()) <= tolerance * std::max(1.0L, std::fabs(expected.front()));
  }
  [[nodiscard]] bool match(const std::vector<long double> &actual) const {
    using namespace aspartame;
    if (actual.size() != expected.size() || (!active.empty() && active.size() != expected.size())) return false;
    return iota(size_t{0}, actual.size()) | forall([&](size_t i) {
             return !active.empty() && !active[i] ? actual[i] == expected[i] : WithinTolerance(tolerance, expected[i]).match(actual[i]);
           });
  }

private:
  [[nodiscard]] std::string describe() const override {
    return fmt::format("matches BLAS values within relative/absolute tolerance {} and preserves inactive storage exactly",
                       static_cast<double>(tolerance));
  }
};
template <class T> struct KeyPermutation : Catch::Matchers::MatcherGenericBase {
  const std::vector<int32_t> *payload;
  const std::vector<T> *source;
  KeyPermutation(const std::vector<int32_t> &payload, const std::vector<T> &source) : payload(&payload), source(&source) {}
  [[nodiscard]] bool match(const std::vector<T> &keys) const {
    if (keys.size() != source->size() || payload->size() != source->size()) return false;
    std::vector<bool> seen(source->size(), false);
    for (size_t i = 0; i < keys.size(); ++i) {
      if ((*payload)[i] < 0 || static_cast<size_t>((*payload)[i]) >= source->size()) return false;
      const auto index = static_cast<size_t>((*payload)[i]);
      if (seen[index] || keys[i] != (*source)[index]) return false;
      seen[index] = true;
    }
    return true;
  }

private:
  [[nodiscard]] std::string describe() const override { return "is a permutation preserving every key/payload association"; }
};
template <class T> struct BitwiseEqual : Catch::Matchers::MatcherGenericBase {
  const std::vector<T> *expected;
  explicit BitwiseEqual(const std::vector<T> &expected) : expected(&expected) {}
  [[nodiscard]] bool match(const std::vector<T> &actual) const {
    return actual.size() == expected->size()
           && (actual.empty() || std::memcmp(actual.data(), expected->data(), actual.size() * sizeof(T)) == 0);
  }

private:
  [[nodiscard]] std::string describe() const override { return "preserves every value's bits, including NaNs and signed zero"; }
};
} // namespace unified

namespace unified::backend::blas_detail {

template <class Buffer> void requireSize(const Buffer &buffer, size_t size) {
  if (buffer.size() < size) throw std::runtime_error("buffer is smaller than transfer");
}

template <class T, class Memory> class DeviceBuffer {
public:
  DeviceBuffer() = default;
  DeviceBuffer(T *data, size_t size) : storage(data), length(size) {}
  DeviceBuffer(const DeviceBuffer &) = delete;
  DeviceBuffer &operator=(const DeviceBuffer &) = delete;
  DeviceBuffer(DeviceBuffer &&other) noexcept : storage(std::exchange(other.storage, nullptr)), length(std::exchange(other.length, 0)) {}
  DeviceBuffer &operator=(DeviceBuffer &&other) noexcept {
    if (this != &other) {
      release();
      storage = std::exchange(other.storage, nullptr);
      length = std::exchange(other.length, 0);
    }
    return *this;
  }
  ~DeviceBuffer() { release(); }

  T *data() { return storage; }
  [[nodiscard]] const T *data() const { return storage; }
  [[nodiscard]] size_t size() const { return length; }

private:
  T *storage{};
  size_t length{};

  void release() {
    if (storage) Memory::release(storage);
    storage = nullptr;
    length = 0;
  }
};

} // namespace unified::backend::blas_detail

namespace unified::test_runtime {
// Release acquired API objects in reverse order, including on assertion failure.
class Cleanup {
  std::vector<std::function<void()>> actions;

public:
  Cleanup() = default;
  Cleanup(const Cleanup &) = delete;
  Cleanup &operator=(const Cleanup &) = delete;
  template <class F> void add(F action) { actions.emplace_back(std::move(action)); }
  ~Cleanup() noexcept {
    for (auto it = actions.rbegin(); it != actions.rend(); ++it) {
      try {
        (*it)();
      } catch (...) {
        // Preserve the original failure if cleanup itself throws.
        std::fputs("cleanup action failed\n", stderr);
      }
    }
  }
};

inline std::vector<int32_t> input(size_t size) {
  using namespace aspartame;
  return iota(size_t{0}, size) | map([](size_t i) { return static_cast<int32_t>(i * 37) - 511; }) | to_vector();
}
} // namespace unified::test_runtime
