#include "test_support.hpp"

#define UNIFIED_API_ROCTHRUST 1
#include "backend_thrust.hpp"
#undef UNIFIED_API_ROCTHRUST
#include <cstddef>
#include <cstdint>
#include <limits>
#include <new>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>

using namespace aspartame;
using namespace unified::rocthrust_support;

namespace {
template <class Value> inline void checkReduceLarge() {
  constexpr size_t size = 4096;
  const auto input =
      iota(size_t{0}, size) | map([](size_t index) -> Value { return static_cast<Value>(index % 17) - Value{8}; }) | to_vector();
  const thrust::device_vector<Value> source(input.begin(), input.end());
  const auto actual = thrust::reduce(source.begin(), source.end(), Value{}, thrust::plus<Value>{});
  const Value expected = input ^ sum();
  {
    INFO("reduce-large mismatch");
    REQUIRE(actual == expected);
  }
}

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/reduce-large/i32/n4096", "[unified][rocthrust][compact]") { checkReduceLarge<int32_t>(); }

namespace {
template <class Value> inline void checkStableSortByKeyLarge() {
  constexpr size_t size = 257;
  const auto keys = iota(size_t{0}, size) | map([](size_t index) { return static_cast<Value>(((index * 13) + 5) % 19); }) | to_vector();
  const auto values = iota(size_t{0}, size) | map([](size_t index) { return static_cast<int32_t>(index); }) | to_vector();
  const thrust::device_vector<Value> originalKeys(keys.begin(), keys.end());
  const thrust::device_vector<int32_t> originalValues(values.begin(), values.end());
  thrust::device_vector<Value> deviceKeys = originalKeys;
  thrust::device_vector<int32_t> deviceValues = originalValues;
  thrust::stable_sort_by_key(deviceKeys.begin(), deviceKeys.end(), deviceValues.begin());
  Value previous = deviceKeys.front();
  std::vector<int32_t> lastSeen(19, -1);
  for (size_t index = 0; index < size; ++index) {
    const auto key = deviceKeys[index];
    const auto value = deviceValues[index];
    {
      INFO("stable-sort-large keys are not sorted");
      REQUIRE((index == 0 || !(key < previous)));
    }
    {
      INFO("stable-sort-large equal-key order was not preserved");
      REQUIRE(value > lastSeen[static_cast<size_t>(key)]);
    }
    lastSeen[static_cast<size_t>(key)] = value;
    previous = key;
  }
}

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/stable-sort-by-key-large/i32/n257", "[unified][rocthrust][compact]") {
  checkStableSortByKeyLarge<int32_t>();
}

// rocThrust `testing/dereference` port: plain iterators and device pointers
// dereferenced inside device code through a hand-rolled copy kernel, since a
// library-side copy would dispatch instead of exercising the iterator's own
// operator*.  `testing/device_delete.cu` itself is marked KNOWN_FAILURE
// upstream, so only this brand of device dereference is live.

namespace {
#ifdef __HIPCC__
template <typename Source, typename Destination>
__global__ void unifiedDereferenceCopy(Source sourceFirst, Source sourceLast, Destination outputFirst) {
  while (sourceFirst != sourceLast)
    *(outputFirst++) = *(sourceFirst++);
}
#endif

template <class Value> inline void checkDereference() {
#ifdef __HIPCC__
  const std::vector<Value> input{Value{16}, Value{-23}, Value{5}, Value{101}, Value{-7}};
  thrust::device_vector<Value> source(input.begin(), input.end());
  thrust::device_vector<Value> output(5, Value{0});
  const auto run = [&](const std::string &where) {
    const hipError_t launch = hipDeviceSynchronize();
    {
      INFO(where + ": kernel launch failed");
      REQUIRE(launch == hipSuccess);
    }
    (void)hipGetLastError(); // Clear the previous error before exercising the iterator kernel.
  };
  {
    unifiedDereferenceCopy<<<1, 1>>>(source.begin(), source.end(), output.begin());
    run("dereference-device-vector-iterator");
    {
      INFO("dereference-device-vector-iterator");
      const auto &deviceValues = output;
      REQUIRE(std::vector<Value>(deviceValues.begin(), deviceValues.end()) == input);
    }
  }
  {
    thrust::fill(output.begin(), output.end(), Value{0});
    unifiedDereferenceCopy<<<1, 1>>>(&source[0], &source[0] + 5, output.begin());
    run("dereference-device-ptr");
    {
      INFO("dereference-device-ptr");
      const auto &deviceValues = output;
      REQUIRE(std::vector<Value>(deviceValues.begin(), deviceValues.end()) == input);
    }
  }
  {
    unifiedDereferenceCopy<<<1, 1>>>(thrust::make_counting_iterator<Value>(Value{1}), thrust::make_counting_iterator<Value>(Value{6}),
                                     output.begin());
    run("dereference-counting-iterator");
    {
      INFO("dereference-counting-iterator");
      const auto &deviceValues = output;
      REQUIRE(std::vector<Value>(deviceValues.begin(), deviceValues.end())
              == std::vector<Value>{Value{1}, Value{2}, Value{3}, Value{4}, Value{5}});
    }
  }
  {
    thrust::fill(output.begin(), output.end(), Value{0});
    unifiedDereferenceCopy<<<1, 1>>>(thrust::make_transform_iterator(thrust::make_counting_iterator<Value>(Value{1}), Negate<Value>{}),
                                     thrust::make_transform_iterator(thrust::make_counting_iterator<Value>(Value{6}), Negate<Value>{}),
                                     output.begin());
    run("dereference-transformed-counting-iterator");
    {
      INFO("dereference-transformed-counting-iterator");
      const auto &deviceValues = output;
      REQUIRE(std::vector<Value>(deviceValues.begin(), deviceValues.end())
              == std::vector<Value>{Value{-1}, Value{-2}, Value{-3}, Value{-4}, Value{-5}});
    }
  }
  {
    unifiedDereferenceCopy<<<1, 1>>>(thrust::make_transform_iterator(source.begin(), Square<Value>{}),
                                     thrust::make_transform_iterator(source.end(), Square<Value>{}), output.begin());
    run("dereference-transform-iterator");
    const auto expected = input ^ map([](Value value) -> Value { return value * value; });
    {
      INFO("dereference-transform-iterator");
      const auto &deviceValues = output;
      REQUIRE(std::vector<Value>(deviceValues.begin(), deviceValues.end()) == expected);
    }
  }
#else
  SKIP("dereference requires the HIP backend");
#endif
}

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/dereference/i32/n5", "[unified][rocthrust][compact]") { checkDereference<int32_t>(); }

TEST_CASE("algorithms/roc-thrust-semantic/device-delete/i32/n11", "[unified][rocthrust][compact]") {
  // The upstream test asserts that the object destructor runs on the
  // device after thrust::device_delete; rocThrust keeps that probe
  // commented out behind a KNOWN_FAILURE marker because the destructor is not
  // reliably invoked on this backend, so no direct port can verify more.
  SKIP("device-delete: the upstream destructor-invocation test is marked "
       "KNOWN_FAILURE on rocThrust");
}

namespace {
template <class Value> inline void checkUniversalVector() {
#ifdef __HIPCC__
  void *probe = nullptr;
  if (hipMallocManaged(&probe, sizeof(Value)) == hipSuccess) REQUIRE(hipFree(probe) == hipSuccess);
  else
    SKIP("universal-memory-vector: hipMallocManaged is unavailable on this "
         "device");
  thrust::universal_vector<Value> universal(11);
  thrust::sequence(universal.begin(), universal.end());
  thrust::universal_host_pinned_vector<Value> pinned(11);
  thrust::sequence(pinned.begin(), pinned.end());
  {
    INFO("universal-memory-vector");
    REQUIRE(std::vector<Value>(universal.begin(), universal.end()) == std::vector<Value>(pinned.begin(), pinned.end()));
  }
  {
    INFO("universal-memory-vector: sequence distance mismatch");
    REQUIRE(thrust::distance(universal.begin(), universal.end()) == 11);
  }
#else
  SKIP("universal-memory-vector requires the HIP backend");
#endif
}

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/universal-memory-vector/i32/n11", "[unified][rocthrust][compact]") {
  checkUniversalVector<int32_t>();
}

// rocThrust `testing/complex` and `testing/complex_transform` port: complex
// arithmetic and the complex math transforms, mirrored against std::complex
// host evaluation of the same expressions.

namespace {
template <class Number>
inline void requireAlmostEqual(const std::vector<Number> &actual, const std::vector<Number> &expected, Number tolerance,
                               const std::string &where) {
  {
    INFO(where + ": result size mismatch");
    REQUIRE(actual.size() == expected.size());
  }
  for (size_t index = 0; index < expected.size(); ++index) {
    INFO(fmt::format("{}[{}] mismatch (actual={}, expected={})", where, index, actual[index], expected[index]));
    REQUIRE(std::fabs(actual[index] - expected[index]) <= tolerance);
  }
}

template <class C> struct ComplexAbs {
  UNIFIED_ROCTHRUST_HOST_DEVICE C::value_type operator()(const C &value) const { return thrust::abs(value); }
};

template <class C> struct ComplexNorm {
  UNIFIED_ROCTHRUST_HOST_DEVICE C::value_type operator()(const C &value) const { return thrust::norm(value); }
};

template <class C> struct ComplexConjugate {
  UNIFIED_ROCTHRUST_HOST_DEVICE C operator()(const C &value) const { return thrust::conj(value); }
};

template <class C> struct ComplexSum {
  UNIFIED_ROCTHRUST_HOST_DEVICE C operator()(const C &left, const C &right) const { return left + right; }
};

template <class C> struct ComplexDifference {
  UNIFIED_ROCTHRUST_HOST_DEVICE C operator()(const C &left, const C &right) const { return left - right; }
};

template <class C> struct ComplexProduct {
  UNIFIED_ROCTHRUST_HOST_DEVICE C operator()(const C &left, const C &right) const { return left * right; }
};

template <class C> struct ComplexQuotient {
  UNIFIED_ROCTHRUST_HOST_DEVICE C operator()(const C &left, const C &right) const { return left / right; }
};

template <class C> struct ComplexExp {
  UNIFIED_ROCTHRUST_HOST_DEVICE C operator()(const C &value) const { return thrust::exp(value); }
};

template <class C> struct ComplexLog {
  UNIFIED_ROCTHRUST_HOST_DEVICE C operator()(const C &value) const { return thrust::log(value); }
};

template <class C> struct ComplexSquareRoot {
  UNIFIED_ROCTHRUST_HOST_DEVICE C operator()(const C &value) const { return thrust::sqrt(value); }
};

template <class C> struct ComplexSine {
  UNIFIED_ROCTHRUST_HOST_DEVICE C operator()(const C &value) const { return thrust::sin(value); }
};

template <class Number>
inline void requireComplexEqual(const std::vector<thrust::complex<Number>> &actual, const std::vector<std::complex<Number>> &expected,
                                Number tolerance, const std::string &where) {
  {
    INFO(where + ": result size mismatch");
    REQUIRE(actual.size() == expected.size());
  }
  for (size_t index = 0; index < expected.size(); ++index) {
    {
      INFO(fmt::format("{}.real[{}] mismatch (actual={}, expected={})", where, index, actual[index].real(), expected[index].real()));
      REQUIRE(std::fabs(actual[index].real() - expected[index].real()) <= tolerance);
    }
    {
      INFO(fmt::format("{}.imag[{}] mismatch (actual={}, expected={})", where, index, actual[index].imag(), expected[index].imag()));
      REQUIRE(std::fabs(actual[index].imag() - expected[index].imag()) <= tolerance);
    }
  }
}

template <class Number> inline std::vector<std::complex<Number>> toStd(const std::vector<thrust::complex<Number>> &values) {
  return {values.begin(), values.end()};
}

template <class Number> inline void checkComplexArithmetic() {
  using Complex = thrust::complex<Number>;
  using Std = std::complex<Number>;
  const std::vector<Complex> first{Complex(1.5, -2.0), Complex(-3.0, 4.0), Complex(0.5, 0.75), Complex(-1.25, -2.5), Complex(7.0, 0.0)};
  const std::vector<Complex> second{Complex(0.5, 1.0), Complex(-1.0, 0.5), Complex(2.0, 0.25), Complex(1.0, -1.0), Complex(0.0, 1.0)};
  const Number tolerance = static_cast<Number>(sizeof(Number) == sizeof(float) ? 1e-5 : 1e-9);
  const thrust::device_vector<Complex> a(first.begin(), first.end());
  const thrust::device_vector<Complex> b(second.begin(), second.end());
  const std::vector<Std> aStd = toStd(first);
  const std::vector<Std> bStd = toStd(second);

  const auto magnitudes = aStd ^ map([](const Std &value) { return std::abs(value); });
  const auto norms = aStd ^ map([](const Std &value) { return std::norm(value); });
  thrust::device_vector<Number> deviceMagnitudes(first.size());
  thrust::transform(a.begin(), a.end(), deviceMagnitudes.begin(), ComplexAbs<Complex>{});
  requireAlmostEqual(std::vector<Number>(deviceMagnitudes.begin(), deviceMagnitudes.end()), magnitudes, tolerance,
                     "complex-arithmetic.abs");
  thrust::device_vector<Number> deviceNorms(first.size());
  thrust::transform(a.begin(), a.end(), deviceNorms.begin(), ComplexNorm<Complex>{});
  requireAlmostEqual(std::vector<Number>(deviceNorms.begin(), deviceNorms.end()), norms, tolerance, "complex-arithmetic.norm");

  const auto conjugates = aStd ^ map([](const Std &value) { return std::conj(value); });
  thrust::device_vector<Complex> deviceConjugates(first.size());
  thrust::transform(a.begin(), a.end(), deviceConjugates.begin(), ComplexConjugate<Complex>{});
  requireComplexEqual(std::vector<Complex>(deviceConjugates.begin(), deviceConjugates.end()), conjugates, tolerance,
                      "complex-arithmetic.conj");

  const auto checkBinary = [&](auto functor, const std::string &where, const std::vector<Std> &expected) {
    thrust::device_vector<Complex> deviceScalars(first.size());
    thrust::transform(a.begin(), a.end(), b.begin(), deviceScalars.begin(), functor);
    requireComplexEqual(std::vector<Complex>(deviceScalars.begin(), deviceScalars.end()), expected, tolerance, where);
  };
  const auto pairs = aStd ^ zip(bStd);
  const auto sum = pairs ^ map([](const Std &left, const Std &right) { return left + right; });
  const auto difference = pairs ^ map([](const Std &left, const Std &right) { return left - right; });
  const auto product = pairs ^ map([](const Std &left, const Std &right) { return left * right; });
  const auto quotient = pairs ^ map([](const Std &left, const Std &right) { return left / right; });
  checkBinary(ComplexSum<Complex>{}, "complex-arithmetic-sum", sum);
  checkBinary(ComplexDifference<Complex>{}, "complex-arithmetic-difference", difference);
  checkBinary(ComplexProduct<Complex>{}, "complex-arithmetic-product", product);
  checkBinary(ComplexQuotient<Complex>{}, "complex-arithmetic-quotient", quotient);
}

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/complex-arithmetic/f64/n5", "[unified][rocthrust][compact]") { checkComplexArithmetic<double>(); }

namespace {
template <class Number> inline void checkComplexMathTransform() {
  using Complex = thrust::complex<Number>;
  using Std = std::complex<Number>;
  // Exercise all four quadrants, including the reciprocal branches.
  const std::vector<Complex> input{Complex(0.5, 0.25), Complex(-1.0, 0.5), Complex(0.75, -1.0), Complex(-1.25, -0.5), Complex(2.0, 1.0)};
  const Number tolerance = static_cast<Number>(sizeof(Number) == sizeof(float) ? 1e-5 : 1e-9);
  const thrust::device_vector<Complex> source(input.begin(), input.end());
  const auto checkUnary = [&](auto functor, const std::string &where) {
    thrust::device_vector<Complex> result(source.size());
    thrust::transform(source.begin(), source.end(), result.begin(), functor);
    const auto expected = input ^ map([&](const Complex &value) {
                            const Complex reference = functor(value);
                            return Std(reference.real(), reference.imag());
                          });
    requireComplexEqual(std::vector<Complex>(result.begin(), result.end()), expected, tolerance, where);
  };
  checkUnary(ComplexExp<Complex>{}, "complex-math-transform-exp");
  checkUnary(ComplexLog<Complex>{}, "complex-math-transform-log");
  checkUnary(ComplexSquareRoot<Complex>{}, "complex-math-transform-sqrt");
  checkUnary(ComplexSine<Complex>{}, "complex-math-transform-sin");
}

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/complex-math-transform/f32/n5", "[unified][rocthrust][compact]") {
  checkComplexMathTransform<float>();
}

namespace {
template <class Number> inline void checkComplexStdInterop() {
  const std::vector<std::complex<Number>> expected{std::complex<Number>(1.0, -2.0), std::complex<Number>(-3.0, 4.5),
                                                   std::complex<Number>(0.5, 0.25), std::complex<Number>(7.0, 0.0)};
  thrust::device_vector<thrust::complex<Number>> value(expected.begin(), expected.end());
  const std::vector<thrust::complex<Number>> actual(value.begin(), value.end());
  for (size_t index = 0; index < expected.size(); ++index) {
    {
      INFO(fmt::format("complex-std-interop.real[{}] mismatch", index));
      REQUIRE(actual[index].real() == expected[index].real());
    }
    {
      INFO(fmt::format("complex-std-interop.imag[{}] mismatch", index));
      REQUIRE(actual[index].imag() == expected[index].imag());
    }
  }
}

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/complex-std-interop/f32/n4", "[unified][rocthrust][compact]") { checkComplexStdInterop<float>(); }

// rocThrust `testing/sort_permutation_iterator` port: the strided range packs a
// permutation iterator over a counting/transform layer, and sorting that slice
// has to leave the untouched lanes untouched.

namespace {
template <class Iterator> class StridedRange {
public:
  using difference_type = thrust::iterator_difference<Iterator>::type;

  struct StrideFunctor {
    difference_type stride;

    UNIFIED_ROCTHRUST_HOST_DEVICE difference_type operator()(const difference_type &index) const { return stride * index; }
  };

  using Counting = thrust::counting_iterator<difference_type>;
  using Transformed = thrust::transform_iterator<StrideFunctor, Counting>;
  using Permuted = thrust::permutation_iterator<Iterator, Transformed>;

  StridedRange(Iterator first, Iterator last, difference_type stride) : first(first), last(last), stride(stride) {}

  [[nodiscard]] Permuted begin() const { return Permuted(first, Transformed(Counting(0), StrideFunctor{stride})); }
  [[nodiscard]] Permuted end() const { return begin() + ((last - first) + (stride - 1)) / stride; }

private:
  Iterator first;
  Iterator last;
  difference_type stride;
};

template <class Value> inline void checkSortPermutationIterator() {
  const std::vector<Value> input{Value{2}, Value{9}, Value{0}, Value{1}, Value{5}, Value{3}, Value{8}, Value{6}, Value{7}, Value{4}};
  const std::vector<Value> sorted{Value{0}, Value{9}, Value{2}, Value{1}, Value{5}, Value{3}, Value{7}, Value{6}, Value{8}, Value{4}};
  thrust::device_vector<Value> values(input.begin(), input.end());
  StridedRange<typename thrust::device_vector<Value>::iterator> strided(values.begin(), values.end(), 2);
  thrust::sort(strided.begin(), strided.end());
  {
    INFO("sort-permutation-iterator");
    const auto &deviceValues = values;
    REQUIRE(std::vector<Value>(deviceValues.begin(), deviceValues.end()) == sorted);
  }
  thrust::stable_sort(strided.begin(), strided.end());
  {
    INFO("sort-permutation-iterator-stable");
    const auto &deviceValues = values;
    REQUIRE(std::vector<Value>(deviceValues.begin(), deviceValues.end()) == sorted);
  }
}

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/sort-permutation-iterator/i32/n10", "[unified][rocthrust][compact]") {
  checkSortPermutationIterator<int32_t>();
}

namespace {
template <class Value> inline void checkSortByKeyPermutationIterator() {
  const std::vector<Value> keys{Value{2}, Value{9}, Value{0}, Value{1}, Value{5}, Value{3}, Value{8}, Value{6}, Value{7}, Value{4}};
  const std::vector<Value> values{Value{0}, Value{1}, Value{2}, Value{3}, Value{4}, Value{5}, Value{6}, Value{7}, Value{8}, Value{9}};
  const std::vector<Value> sortedKeys{Value{0}, Value{9}, Value{2}, Value{1}, Value{5}, Value{3}, Value{7}, Value{6}, Value{8}, Value{4}};
  const std::vector<Value> sortedValues{Value{2}, Value{1}, Value{0}, Value{3}, Value{4}, Value{5}, Value{8}, Value{7}, Value{6}, Value{9}};
  thrust::device_vector<Value> deviceKeys(keys.begin(), keys.end());
  thrust::device_vector<Value> deviceValues(values.begin(), values.end());
  StridedRange<typename thrust::device_vector<Value>::iterator> keyStrided(deviceKeys.begin(), deviceKeys.end(), 2);
  StridedRange<typename thrust::device_vector<Value>::iterator> valueStrided(deviceValues.begin(), deviceValues.end(), 2);
  thrust::sort_by_key(keyStrided.begin(), keyStrided.end(), valueStrided.begin());
  {
    INFO("sort-by-key-permutation-iterator keys");
    const auto &deviceValues = deviceKeys;
    REQUIRE(std::vector<Value>(deviceValues.begin(), deviceValues.end()) == sortedKeys);
  }
  {
    INFO("sort-by-key-permutation-iterator values");
    const auto &actualValues = deviceValues;
    REQUIRE(std::vector<Value>(actualValues.begin(), actualValues.end()) == sortedValues);
  }
}

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/sort-by-key-permutation-iterator/i32/n10", "[unified][rocthrust][compact]") {
  checkSortByKeyPermutationIterator<int32_t>();
}

// rocThrust `testing/reverse_iterator` port: copies and scans through reverse
// iterators with an exact host reference.

namespace {
template <class Value> inline void checkReverseIterator() {
  const std::vector<Value> source{Value{10}, Value{20}, Value{30}, Value{40}};
  const thrust::device_vector<Value> deviceSource(source.begin(), source.end());
  thrust::device_vector<Value> copied(4, Value{0});
  thrust::copy(thrust::make_reverse_iterator(deviceSource.end()), thrust::make_reverse_iterator(deviceSource.begin()), copied.begin());
  {
    INFO("reverse-iterator-copy");
    const auto &deviceValues = copied;
    REQUIRE(std::vector<Value>(deviceValues.begin(), deviceValues.end()) == std::vector<Value>{Value{40}, Value{30}, Value{20}, Value{10}});
  }

  const std::vector<Value> ascending{Value{0}, Value{1}, Value{2}, Value{3}, Value{4}};
  const thrust::device_vector<Value> deviceAscending(ascending.begin(), ascending.end());
  thrust::device_vector<Value> scanned(deviceAscending.size());
  thrust::exclusive_scan(thrust::make_reverse_iterator(deviceAscending.end()), thrust::make_reverse_iterator(deviceAscending.begin()),
                         scanned.begin());
  // Reversed inputs read 4,3,2,1,0; the exclusive scan keeps the running sum.
  {
    INFO("reverse-iterator-exclusive-scan");
    const auto &deviceValues = scanned;
    REQUIRE(std::vector<Value>(deviceValues.begin(), deviceValues.end())
            == std::vector<Value>{Value{0}, Value{4}, Value{7}, Value{9}, Value{10}});
  }
}

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/reverse-iterator/i32/n5", "[unified][rocthrust][compact]") { checkReverseIterator<int32_t>(); }

// rocThrust `testing/tabulate_output_iterator` port.

namespace {
template <class Iterator> struct TabulatePayload {
  Iterator output;

  template <class Index, class Value> UNIFIED_ROCTHRUST_HOST_DEVICE void operator()(Index index, Value value) const {
    output[index] = thrust::get<0>(value);
  }
};

template <class Tuple> struct SelectEveryNth {
  size_t stride;

  UNIFIED_ROCTHRUST_HOST_DEVICE bool operator()(const Tuple &value) const { return thrust::get<1>(value) % stride == 0; }
};

template <class Value> inline void checkTabulateOutputIterator() {
  constexpr size_t count = 17;
  constexpr size_t stride = 3;
  thrust::device_vector<Value> input(count);
  thrust::sequence(input.begin(), input.end(), Value{1});
  thrust::device_vector<Value> output(count, Value{42});
  const auto tabulate =
      thrust::make_tabulate_output_iterator(TabulatePayload<typename thrust::device_vector<Value>::iterator>{output.begin()});
  const auto zipped = thrust::make_zip_iterator(thrust::make_tuple(input.begin(), thrust::make_counting_iterator<size_t>(0)));
  const auto end = thrust::copy_if(zipped, zipped + count, tabulate, SelectEveryNth<thrust::tuple<Value, size_t>>{stride});
  const auto selected = static_cast<size_t>(thrust::distance(tabulate, end));
  {
    INFO("tabulate-output-iterator: selected count mismatch");
    REQUIRE(selected == (count + stride - 1) / stride);
  }
  std::vector<Value> expected(count, Value{42});
  for (size_t index = 0; index < selected; ++index)
    expected[index] = static_cast<Value>((index * stride) + 1);
  {
    INFO("tabulate-output-iterator");
    const auto &deviceValues = output;
    REQUIRE(std::vector<Value>(deviceValues.begin(), deviceValues.end()) == expected);
  }
}

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/tabulate-output-iterator/i32/n17", "[unified][rocthrust][compact]") {
  checkTabulateOutputIterator<int32_t>();
}

// Allocation failure must leave prior allocations valid and permit subsequent use.
// A request larger than the address space fails without exhausting physical RAM.

TEST_CASE("algorithms/roc-thrust-semantic/out-of-memory-recovery/i32/n11", "[unified][rocthrust][compact]") {
  const thrust::device_vector<int32_t> before(11, 7);
  REQUIRE_THROWS_AS(thrust::device_vector<int32_t>(std::numeric_limits<size_t>::max() / sizeof(int32_t)), std::bad_alloc);
  REQUIRE(std::vector<int32_t>(before.begin(), before.end()) == std::vector<int32_t>(11, 7));
  const thrust::device_vector<int32_t> after(11, 9);
  REQUIRE(std::vector<int32_t>(after.begin(), after.end()) == std::vector<int32_t>(11, 9));
}

// rocThrust `testing/async_for_each`, `testing/async_reduce{,_into}` port,
// compared against a host-side reference of the same operations.

namespace {
template <class Value> struct Halve {
  UNIFIED_ROCTHRUST_HOST_DEVICE void operator()(Value &value) const { value /= Value{2}; }
};

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/async-for-each/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<int32_t> input{int32_t{32}, int32_t{14}, int32_t{8}, int32_t{64}, int32_t{2}, int32_t{100}};
  thrust::device_vector<int32_t> device(input.begin(), input.end());
  std::vector<int32_t> host(input.begin(), input.end());
  thrust::for_each(host.begin(), host.end(), Halve<int32_t>{});
  UNIFIED_THRUST_ASYNC_BEGIN
  auto future = thrust::async::for_each(device.begin(), device.end(), Halve<int32_t>{});
  UNIFIED_THRUST_ASYNC_END
  future.wait();
  {
    INFO("async-for-each");
    REQUIRE(std::vector<int32_t>(device.begin(), device.end()) == host);
  }
  UNIFIED_THRUST_ASYNC_BEGIN
  auto viaPolicy = thrust::async::for_each(thrust::device, device.begin(), device.end(), Halve<int32_t>{});
  UNIFIED_THRUST_ASYNC_END
  viaPolicy.wait();
  for (auto &value : host)
    value /= int32_t{2};
  {
    INFO("async-for-each-policy");
    REQUIRE(std::vector<int32_t>(device.begin(), device.end()) == host);
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/async-reduce/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<int32_t> input{int32_t{1}, int32_t{-3}, int32_t{5}, int32_t{2}, int32_t{0}, int32_t{-2}};
  const thrust::device_vector<int32_t> device(input.begin(), input.end());
  const int32_t expected = input ^ sum();
  UNIFIED_THRUST_ASYNC_BEGIN
  auto defaultFuture = thrust::async::reduce(device.begin(), device.end());
  UNIFIED_THRUST_ASYNC_END
  const int32_t defaultValue = defaultFuture.get();
  {
    INFO("async-reduce");
    REQUIRE(defaultValue == expected);
  }
  UNIFIED_THRUST_ASYNC_BEGIN
  auto policyFuture = thrust::async::reduce(thrust::device, device.begin(), device.end());
  UNIFIED_THRUST_ASYNC_END {
    INFO("async-reduce-policy");
    REQUIRE(policyFuture.get() == expected);
  }
  UNIFIED_THRUST_ASYNC_BEGIN
  auto customFuture = thrust::async::reduce(device.begin(), device.end(), int32_t{7}, unified::algorithms::Plus<int32_t>{});
  UNIFIED_THRUST_ASYNC_END {
    INFO("async-reduce-custom");
    REQUIRE(customFuture.get() == expected + int32_t{7});
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/async-reduce-into/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<int32_t> input{int32_t{3}, int32_t{-4}, int32_t{10}, int32_t{1}, int32_t{2}};
  thrust::device_vector<int32_t> device(input.begin(), input.end());
  thrust::device_vector<int32_t> output(1, int32_t{0});
  UNIFIED_THRUST_ASYNC_BEGIN
  auto done = thrust::async::reduce_into(device.begin(), device.end(), output.begin());
  UNIFIED_THRUST_ASYNC_END
  done.wait();
  const int32_t expected = input ^ sum();
  {
    INFO("async-reduce-into");
    REQUIRE(output[0] == expected);
  }
}
