#include <cstdint>
#include <functional>
#include <random>
#include <type_traits>
#include <vector>

#include <mma.h>

#include "cuda/mma_support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <cuda_bf16.h>
#include <cuda_fp16.h>
#include <cuda_runtime_api.h>

#include "test_support.hpp"

using namespace aspartame;

// nvcuda::wmma for every fragment element type and shape, both operand layouts and global or shared fragments, checked
// against exact host products.
namespace {

enum class Kind { Half, BFloat16, TensorFloat32, Double, SignedByte, UnsignedByte, SignedNibble, UnsignedNibble, Bit };
enum class Bits { None, Xor, And };

#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 700
// The fragment element type of each kind, where the architecture declares it.
template <Kind> struct Fragment;
template <> struct Fragment<Kind::Half> {
  using type = __half;
};
  #if __CUDA_ARCH__ >= 720
template <> struct Fragment<Kind::SignedByte> {
  using type = signed char;
};
template <> struct Fragment<Kind::UnsignedByte> {
  using type = unsigned char;
};
  #endif
  #if __CUDA_ARCH__ >= 730
template <> struct Fragment<Kind::SignedNibble> {
  using type = nvcuda::wmma::experimental::precision::s4;
};
template <> struct Fragment<Kind::UnsignedNibble> {
  using type = nvcuda::wmma::experimental::precision::u4;
};
template <> struct Fragment<Kind::Bit> {
  using type = nvcuda::wmma::experimental::precision::b1;
};
  #endif
  #if __CUDA_ARCH__ >= 800
template <> struct Fragment<Kind::BFloat16> {
  using type = __nv_bfloat16;
};
template <> struct Fragment<Kind::TensorFloat32> {
  using type = nvcuda::wmma::precision::tf32;
};
template <> struct Fragment<Kind::Double> {
  using type = double;
};
  #endif
#endif

// One warp loads A, B and C, from global memory or a shared copy, and stores D = A * B + C row-major.
template <int Arch, Kind Element, class Memory, class Accumulator, int M, int N, int K, bool ColumnA, bool ColumnB, bool Shared,
          Bits Operation = Bits::None>
__global__ void product(const Memory *a, const Memory *b, const Accumulator *c, Accumulator *d, unsigned lda, unsigned ldb, unsigned ldc,
                        unsigned sizeA, unsigned sizeB) {
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 700
  using namespace nvcuda;
  if constexpr (__CUDA_ARCH__ >= Arch * 10 && (Operation != Bits::And || __CUDA_ARCH__ < 1000)) {
    using Type = typename Fragment<Element>::type;
    using LayoutA = std::conditional_t<ColumnA, wmma::col_major, wmma::row_major>;
    using LayoutB = std::conditional_t<ColumnB, wmma::col_major, wmma::row_major>;
    __shared__ alignas(128) unsigned char staging[16384];
    const Memory *left = a;
    const Memory *right = b;
    if constexpr (Shared) {
      auto *sa = reinterpret_cast<Memory *>(staging);
      auto *sb = reinterpret_cast<Memory *>(staging + 8192);
      for (unsigned i = threadIdx.x; i < sizeA; i += 32)
        sa[i] = a[i];
      for (unsigned i = threadIdx.x; i < sizeB; i += 32)
        sb[i] = b[i];
      __syncwarp();
      left = sa;
      right = sb;
    }
    wmma::fragment<wmma::matrix_a, M, N, K, Type, LayoutA> fa;
    wmma::fragment<wmma::matrix_b, M, N, K, Type, LayoutB> fb;
    wmma::fragment<wmma::accumulator, M, N, K, Accumulator> fc;
    wmma::load_matrix_sync(fa, left, lda);
    wmma::load_matrix_sync(fb, right, ldb);
    wmma::load_matrix_sync(fc, c, ldc, wmma::mem_row_major);
  #if __CUDA_ARCH__ >= 800
    if constexpr (Element == Kind::TensorFloat32) {
      for (int i = 0; i < fa.num_elements; ++i)
        fa.x[i] = wmma::__float_to_tf32(fa.x[i]);
      for (int i = 0; i < fb.num_elements; ++i)
        fb.x[i] = wmma::__float_to_tf32(fb.x[i]);
    }
  #endif
    if constexpr (Operation == Bits::None) wmma::mma_sync(fc, fa, fb, fc);
  #if __CUDA_ARCH__ >= 730
    else if constexpr (Operation == Bits::Xor)
      wmma::bmma_sync(fc, fa, fb, fc, wmma::experimental::bmmaBitOpXOR, wmma::experimental::bmmaAccumulateOpPOPC);
  #endif
  #if __CUDA_ARCH__ >= 800
    else if constexpr (Operation == Bits::And)
      wmma::bmma_sync(fc, fa, fb, fc, wmma::experimental::bmmaBitOpAND, wmma::experimental::bmmaAccumulateOpPOPC);
  #endif
    wmma::store_matrix_sync(d, fc, ldc, wmma::mem_row_major);
  } else {
    __trap();
  }
#else
  __trap();
#endif
}

// Values of A, B and C as exact doubles, laid out as the kernel reads them, and the exact D.
struct Problem {
  int m, n, k;
  bool columnA, columnB;
  unsigned lda, ldb, ldc;
  std::vector<double> a, b, c;
  [[nodiscard]] double left(int row, int depth) const { return columnA ? a[(depth * lda) + row] : a[(row * lda) + depth]; }
  [[nodiscard]] double right(int depth, int column) const { return columnB ? b[(column * ldb) + depth] : b[(depth * ldb) + column]; }
  [[nodiscard]] double expected(int row, int column) const {
    return iota(0, k)
           | fold_left(c[(row * ldc) + column], [&](double sum, int depth) { return sum + (left(row, depth) * right(depth, column)); });
  }
};

Problem problem(int m, int n, int k, bool columnA, bool columnB, int low, int high, int pad, std::mt19937 &random) {
  // padded leading dimensions, a whole number of 16 elements as every fragment type allows
  auto leading = [pad](int extent) { return static_cast<unsigned>((extent + pad + 15) / 16 * 16); };
  Problem result{m, n, k, columnA, columnB, leading(columnA ? m : k), leading(columnB ? k : n), leading(n), {}, {}, {}};
  std::uniform_int_distribution<int> pick(low, high);
  result.a.resize(static_cast<size_t>((columnA ? k : m)) * result.lda);
  result.b.resize(static_cast<size_t>((columnB ? n : k)) * result.ldb);
  result.c.resize(static_cast<size_t>(m) * result.ldc);
  for (auto &value : result.a)
    value = pick(random);
  for (auto &value : result.b)
    value = pick(random);
  for (auto &value : result.c)
    value = std::uniform_int_distribution<int>(-64, 64)(random);
  return result;
}

template <class Memory, class Accumulator, class Kernel>
std::vector<Accumulator> launch(const Problem &p, const std::vector<Memory> &a, const std::vector<Memory> &b,
                                const std::vector<Accumulator> &c, Kernel kernel, unsigned lda, unsigned ldb) {
  DeviceBuffer<Memory> da(a);
  DeviceBuffer<Memory> db(b);
  DeviceBuffer<Accumulator> dc(c);
  DeviceBuffer<Accumulator> dd(std::vector<Accumulator>(c.size()));
  kernel<<<1, 32>>>(da.data, db.data, dc.data, dd.data, lda, ldb, p.ldc, unsigned(a.size()), unsigned(b.size()));
  finish("wmma");
  return dd.download();
}

template <int Arch, Kind Element, class Memory, class Accumulator, int M, int N, int K, bool ColumnA, bool ColumnB, bool Shared>
bool floating(const char *name, const std::function<Memory(double)> &encode, const std::function<Accumulator(double)> &accumulate,
              const std::function<double(Accumulator)> &value, int range) {
  if (capability() < Arch) return false;
  std::mt19937 random(0x776d6dU + (M * 3) + (N * 5) + K);
  const auto p = problem(M, N, K, ColumnA, ColumnB, -range, range, 8, random);
  const auto a = p.a ^ map(encode);
  const auto b = p.b ^ map(encode);
  const auto c = p.c ^ map(accumulate);
  const auto d = launch(p, a, b, c, product<Arch, Element, Memory, Accumulator, M, N, K, ColumnA, ColumnB, Shared>, p.lda, p.ldb);
  for (int row = 0; row < M; ++row)
    for (int column = 0; column < N; ++column)
      if (value(d[(row * p.ldc) + column]) != p.expected(row, column))
        FAIL(
            fmt::format("{} D[{}][{}]: expected {}, got {}", name, row, column, p.expected(row, column), value(d[(row * p.ldc) + column])));
  return true;
}

} // namespace

TEST_CASE("runtime/cuda/wmma/half", "[unified][runtime][tensor][compact]") {
  bool ran = false;
  const auto encode = [](double v) { return __float2half(static_cast<float>(v)); };
  const auto single = [](double v) { return static_cast<float>(v); };
  const auto fromSingle = [](float v) { return static_cast<double>(v); };
  const auto fromHalf = [](__half v) { return static_cast<double>(__half2float(v)); };
  ran |= floating<70, Kind::Half, __half, float, 16, 16, 16, true, false, false>("m16n16k16 col.row f32", encode, single, fromSingle, 4);
  ran |= floating<70, Kind::Half, __half, __half, 32, 8, 16, false, true, true>("m32n8k16 row.col f16 shared", encode, encode, fromHalf, 4);
  ran |= floating<70, Kind::Half, __half, float, 8, 32, 16, false, false, false>("m8n32k16 row.row f32", encode, single, fromSingle, 4);
  ran |=
      floating<70, Kind::Half, __half, __half, 16, 16, 16, true, true, true>("m16n16k16 col.col f16 shared", encode, encode, fromHalf, 4);
  if (!ran) SKIP("the device runs none of these forms");
}

TEST_CASE("runtime/cuda/wmma/bfloat16-tf32-double", "[unified][runtime][tensor][compact]") {
  bool ran = false;
  const auto single = [](double v) { return static_cast<float>(v); };
  const auto fromSingle = [](float v) { return static_cast<double>(v); };
  ran |= floating<80, Kind::BFloat16, __nv_bfloat16, float, 16, 16, 16, false, true, false>(
      "bf16 m16n16k16", [](double v) { return __float2bfloat16(static_cast<float>(v)); }, single, fromSingle, 8);
  ran |= floating<80, Kind::BFloat16, __nv_bfloat16, float, 8, 32, 16, true, false, true>(
      "bf16 m8n32k16 shared", [](double v) { return __float2bfloat16(static_cast<float>(v)); }, single, fromSingle, 8);
  ran |= floating<80, Kind::TensorFloat32, float, float, 16, 16, 8, false, false, false>("tf32 m16n16k8", single, single, fromSingle, 16);
  ran |= floating<80, Kind::Double, double, double, 8, 8, 4, true, false, false>(
      "f64 m8n8k4", [](double v) { return v; }, [](double v) { return v; }, [](double v) { return v; }, 1000);
  if (!ran) SKIP("the device runs none of these forms");
}

namespace {
template <int Arch, Kind Element, class Memory, int M, int N, int K, bool ColumnA, bool ColumnB, bool Shared>
bool bytes(const char *name, int low, int high) {
  if (capability() < Arch) return false;
  std::mt19937 random(0x776d38U + M + (N * 3));
  auto p = problem(M, N, K, ColumnA, ColumnB, low, high, 16, random);
  for (auto &v : p.c)
    v = std::uniform_int_distribution<int>(-2000000, 2000000)(random);
  const auto a = p.a ^ map([](double v) { return static_cast<Memory>(v); });
  const auto b = p.b ^ map([](double v) { return static_cast<Memory>(v); });
  const auto c = p.c ^ map([](double v) { return static_cast<int>(v); });
  const auto d = launch(p, a, b, c, product<Arch, Element, Memory, int, M, N, K, ColumnA, ColumnB, Shared>, p.lda, p.ldb);
  for (int row = 0; row < M; ++row)
    for (int column = 0; column < N; ++column)
      if (d[(row * p.ldc) + column] != static_cast<int>(p.expected(row, column)))
        FAIL(fmt::format("{} D[{}][{}]: expected {}, got {}", name, row, column, p.expected(row, column), d[(row * p.ldc) + column]));
  return true;
}

} // namespace

TEST_CASE("runtime/cuda/wmma/integer", "[unified][runtime][tensor][compact]") {
  bool ran = false;
  ran |= bytes<75, Kind::SignedByte, signed char, 16, 16, 16, false, true, false>("s8 m16n16k16", -128, 127);
  ran |= bytes<75, Kind::UnsignedByte, unsigned char, 32, 8, 16, true, false, true>("u8 m32n8k16 shared", 0, 255);
  ran |= bytes<75, Kind::SignedByte, signed char, 8, 32, 16, false, false, false>("s8 m8n32k16", -128, 127);
  if (!ran) SKIP("the device runs none of these forms");
}

namespace {
// Sub-byte A (row-major) and B (column-major), packed little-endian: `bits` bits per element.
template <int Arch, Kind Element, Bits Operation> bool packed(const char *name, int bits, bool signedElements) {
  if (capability() < Arch) return false;
  constexpr int k = Operation == Bits::None ? 32 : 128;
  std::mt19937 random(0x776d34U + bits);
  int high = 1;
  int low = 0;
  if (bits != 1) {
    high = signedElements ? 7 : 15;
    low = signedElements ? -8 : 0;
  }
  auto p = problem(8, 8, k, false, true, low, high, 0, random);
  p.lda = p.ldb = k;
  for (auto &v : p.c)
    v = std::uniform_int_distribution<int>(-100000, 100000)(random);
  auto pack = [bits](const std::vector<double> &values) {
    std::vector<unsigned> words(values.size() * bits / 32, 0);
    for (size_t i = 0; i < values.size(); ++i)
      words[i * bits / 32] |= (static_cast<unsigned>(static_cast<int>(values[i])) & ((1U << bits) - 1U)) << (i * bits % 32);
    return words;
  };
  const auto c = p.c ^ map([](double v) { return static_cast<int>(v); });
  const auto d = launch(p, pack(p.a), pack(p.b), c, product<Arch, Element, unsigned, int, 8, 8, k, false, true, false, Operation>, k, k);
  for (int row = 0; row < 8; ++row)
    for (int column = 0; column < 8; ++column) {
      int64_t sum = static_cast<int64_t>(p.c[(row * p.ldc) + column]);
      for (int depth = 0; depth < k; ++depth) {
        const auto x = static_cast<int64_t>(p.left(row, depth));
        const auto y = static_cast<int64_t>(p.right(depth, column));
        if constexpr (Operation == Bits::Xor) sum += x ^ y;
        else if constexpr (Operation == Bits::And) sum += x & y;
        else sum += x * y;
      }
      if (d[(row * p.ldc) + column] != static_cast<int>(sum))
        FAIL(fmt::format("{} D[{}][{}]: expected {}, got {}", name, row, column, sum, d[(row * p.ldc) + column]));
    }
  return true;
}

} // namespace

TEST_CASE("runtime/cuda/wmma/sub-byte", "[unified][runtime][tensor][compact]") {
  bool ran = false;
  ran |= packed<75, Kind::SignedNibble, Bits::None>("s4 m8n8k32", 4, true);
  ran |= packed<75, Kind::UnsignedNibble, Bits::None>("u4 m8n8k32", 4, false);
  ran |= packed<75, Kind::Bit, Bits::Xor>("b1 m8n8k128 xor", 1, false);
  // nvcc rejects the single-bit AND product for compute capability 10.0 and later
  if (capability() < 100) ran |= packed<80, Kind::Bit, Bits::And>("b1 m8n8k128 and", 1, false);
  if (!ran) SKIP("the device runs none of these forms");
}
