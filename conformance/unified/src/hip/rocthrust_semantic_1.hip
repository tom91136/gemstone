#include "test_support.hpp"

#define UNIFIED_API_ROCTHRUST 1
#include "backend_thrust.hpp"
#undef UNIFIED_API_ROCTHRUST
#include <cstddef>
#include <cstdint>

#include <catch2/catch_test_macros.hpp>

using namespace unified::rocthrust_support;
using TestType = int32_t;

// ---------------------------------------------------------------------------
// The device-side random semantics of rocThrust's `testing/random`, decomposed
// into one case per engine characteristic and one per distribution
// characteristic so a single regression names the exact instantiation.
// ---------------------------------------------------------------------------

inline constexpr size_t random_draws = 10000;
// Exact sequence validation keeps the upstream 10,000th-value requirement. Range
// and equality properties need a representative sample, not 70 separate
// 10,000-step device loops in every emulator run.
inline constexpr size_t random_property_draws = 256;

// Runs a nullary device functor once and returns its result.
template <class Functor> inline int32_t device_functor_result(Functor functor) {
  thrust::device_vector<int32_t> output(1, 0);
  thrust::generate(output.begin(), output.end(), functor);
  return output[0];
}

template <class Engine> struct EngineValidation {
  using result_type = typename Engine::result_type;
  result_type expected;
  UNIFIED_ROCTHRUST_HOST_DEVICE int32_t operator()() {
    Engine engine;
    engine.discard(random_draws - 1);
    return engine() == expected ? 1 : 0;
  }
};

template <class Engine> struct EngineMinimum {
  UNIFIED_ROCTHRUST_HOST_DEVICE int32_t operator()() {
    Engine engine;
    int32_t result = 1;
    for (size_t index = 0; index < random_property_draws; ++index)
      result &= (engine() >= Engine::min) ? 1 : 0;
    return result;
  }
};

template <class Engine> struct EngineMaximum {
  UNIFIED_ROCTHRUST_HOST_DEVICE int32_t operator()() {
    Engine engine;
    int32_t result = 1;
    for (size_t index = 0; index < random_property_draws; ++index)
      result &= (engine() <= Engine::max) ? 1 : 0;
    return result;
  }
};

template <class Engine> struct EngineEquality {
  UNIFIED_ROCTHRUST_HOST_DEVICE int32_t operator()() {
    int32_t result = 1;
    Engine first, second;
    result &= (first == second) ? 1 : 0;
    first.discard(random_property_draws);
    second.discard(random_property_draws);
    result &= (first == second) ? 1 : 0;
    Engine seeded_a(13), seeded_b(13);
    result &= (seeded_a == seeded_b) ? 1 : 0;
    Engine other_a(7), other_b(13);
    result &= !(other_a == other_b) ? 1 : 0;
    other_a.seed(13);
    result &= (other_a == other_b) ? 1 : 0;
    result &= !(first != second) ? 1 : 0;
    Engine unequal_a(13), unequal_b(13);
    unequal_a.discard(500);
    unequal_b.discard(random_property_draws / 10);
    result &= (unequal_a != unequal_b) ? 1 : 0;
    return result;
  }
};

template <class Distribution, class Engine> struct DistributionMinimum {
  Distribution distribution;
  UNIFIED_ROCTHRUST_HOST_DEVICE int32_t operator()() {
    Engine engine;
    int32_t result = 1;
    for (size_t index = 0; index < random_property_draws; ++index) {
      const auto value = distribution(engine);
      result &= (value >= distribution.min()) ? 1 : 0;
    }
    return result;
  }
};

template <class Distribution, class Engine> struct DistributionMaximum {
  Distribution distribution;
  UNIFIED_ROCTHRUST_HOST_DEVICE int32_t operator()() {
    Engine engine;
    int32_t result = 1;
    for (size_t index = 0; index < random_property_draws; ++index) {
      const auto value = distribution(engine);
      result &= (value <= distribution.max()) ? 1 : 0;
    }
    return result;
  }
};

TEST_CASE("algorithms/roc-thrust-semantic/random-engine-validation/i32/n11", "[unified][rocthrust][compact]") {
  using namespace thrust::random;
  const auto check = [](const char *name, int32_t value) {
    {
      INFO(fmt::format("random-engine-validation: {} 10000th value mismatch", name));
      REQUIRE((value == 1));
    }
  };
  check("ranlux24_base", device_functor_result(EngineValidation<ranlux24_base>{7937952u}));
  check("ranlux48_base", device_functor_result(EngineValidation<ranlux48_base>{192113843633948ull}));
  check("minstd_rand", device_functor_result(EngineValidation<minstd_rand>{399268537u}));
  check("minstd_rand0", device_functor_result(EngineValidation<minstd_rand0>{1043618065u}));
  check("taus88", device_functor_result(EngineValidation<taus88>{3535848941ull}));
  check("ranlux24", device_functor_result(EngineValidation<ranlux24>{9901578u}));
  check("ranlux48", device_functor_result(EngineValidation<ranlux48>{88229545517833ull}));
}

template <template <class> class Check> inline void check_all_engines(const char *what) {
  using namespace thrust::random;
  const auto check = [what](const char *name, int32_t value) {
    {
      INFO(fmt::format("{}: {} check failed", what, name));
      REQUIRE((value == 1));
    }
  };
  check("ranlux24_base", device_functor_result(Check<ranlux24_base>{}));
  check("ranlux48_base", device_functor_result(Check<ranlux48_base>{}));
  check("minstd_rand", device_functor_result(Check<minstd_rand>{}));
  check("minstd_rand0", device_functor_result(Check<minstd_rand0>{}));
  check("taus88", device_functor_result(Check<taus88>{}));
  check("ranlux24", device_functor_result(Check<ranlux24>{}));
  check("ranlux48", device_functor_result(Check<ranlux48>{}));
}

TEST_CASE("algorithms/roc-thrust-semantic/random-engine-min/i32/n11", "[unified][rocthrust][compact]") {
  check_all_engines<EngineMinimum>("random-engine-min");
}

TEST_CASE("algorithms/roc-thrust-semantic/random-engine-max/i32/n11", "[unified][rocthrust][compact]") {
  check_all_engines<EngineMaximum>("random-engine-max");
}

TEST_CASE("algorithms/roc-thrust-semantic/random-engine-equality/i32/n11", "[unified][rocthrust][compact]") {
  check_all_engines<EngineEquality>("random-engine-equality");
}

template <class Engine> inline void check_engine_save_restore(const char *name) {
  Engine engine;
  engine.discard(random_draws);
  std::stringstream buffer;
  buffer << engine;
  engine.discard(random_draws);
  Engine restored;
  buffer >> restored;
  restored.discard(random_draws);
  {
    INFO(fmt::format("random-engine-save-restore: {} state mismatch", name));
    REQUIRE((engine() == restored()));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/random-engine-save-restore/i32/n11", "[unified][rocthrust][compact]") {
  using namespace thrust::random;
  check_engine_save_restore<ranlux24_base>("ranlux24_base");
  check_engine_save_restore<ranlux48_base>("ranlux48_base");
  check_engine_save_restore<minstd_rand>("minstd_rand");
  check_engine_save_restore<minstd_rand0>("minstd_rand0");
  check_engine_save_restore<taus88>("taus88");
  check_engine_save_restore<ranlux24>("ranlux24");
  check_engine_save_restore<ranlux48>("ranlux48");
}

template <template <class, class> class Check> inline void check_all_distributions(const char *what) {
  using namespace thrust::random;
  using Engine = thrust::random::minstd_rand;
  // A device error is sticky in HIP, so every distribution is attempted and
  // the failing ones are named instead of hiding the remaining probes behind
  // the first failure.
  std::string failures;
  const auto check = [what, &failures](const char *name, auto run) {
    try {
      if (run() == 1) return;
      failures += fmt::format(" {} (out-of-range sample)", name);
    } catch (const std::exception &error) {
      failures += fmt::format(" {} ({})", name, error.what());
    }
  };
  check("uniform_int_distribution<int32_t>",
        [] { return device_functor_result(Check<uniform_int_distribution<int32_t>, Engine>{uniform_int_distribution<int32_t>()}); });
  check("uniform_int_distribution<uint32_t>",
        [] { return device_functor_result(Check<uniform_int_distribution<uint32_t>, Engine>{uniform_int_distribution<uint32_t>()}); });
  check("uniform_real_distribution<float>",
        [] { return device_functor_result(Check<uniform_real_distribution<float>, Engine>{uniform_real_distribution<float>()}); });
  check("uniform_real_distribution<double>",
        [] { return device_functor_result(Check<uniform_real_distribution<double>, Engine>{uniform_real_distribution<double>()}); });
  check("normal_distribution<float>",
        [] { return device_functor_result(Check<normal_distribution<float>, Engine>{normal_distribution<float>()}); });
  check("normal_distribution<double>",
        [] { return device_functor_result(Check<normal_distribution<double>, Engine>{normal_distribution<double>()}); });
  if (!failures.empty()) throw std::runtime_error(fmt::format("{}: failing distributions:{}", what, failures));
}

TEST_CASE("algorithms/roc-thrust-semantic/random-distribution-min/i32/n11", "[unified][rocthrust][compact]") {
  check_all_distributions<DistributionMinimum>("random-distribution-min");
}

TEST_CASE("algorithms/roc-thrust-semantic/random-distribution-max/i32/n11", "[unified][rocthrust][compact]") {
  check_all_distributions<DistributionMaximum>("random-distribution-max");
}

template <class Distribution> inline void check_distribution_save_restore(const char *name) {
  Distribution original(7, 13);
  std::stringstream buffer;
  buffer << original;
  Distribution restored;
  buffer >> restored;
  {
    INFO(fmt::format("random-distribution-save-restore: {} state mismatch", name));
    REQUIRE((original == restored));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/random-distribution-save-restore/i32/n11", "[unified][rocthrust][compact]") {
  using namespace thrust::random;
  check_distribution_save_restore<uniform_int_distribution<int32_t>>("uniform_int_distribution<int32_t>");
  check_distribution_save_restore<uniform_int_distribution<uint32_t>>("uniform_int_distribution<uint32_t>");
  check_distribution_save_restore<uniform_real_distribution<float>>("uniform_real_distribution<float>");
  check_distribution_save_restore<uniform_real_distribution<double>>("uniform_real_distribution<double>");
  check_distribution_save_restore<normal_distribution<float>>("normal_distribution<float>");
  check_distribution_save_restore<normal_distribution<double>>("normal_distribution<double>");
}

// ---------------------------------------------------------------------------
// rocThrust `testing/unittest_static_assert` port.  The upstream case depends
// on a device kernel publishing through a pointer that the host installed in a
// `__device__` global with hipMemcpyToSymbol, so that capability is the
// semantic unit here rather than the static-assert macro itself.
// ---------------------------------------------------------------------------

#if defined(__HIPCC__)
__device__ int32_t unified_device_global_marker = 77;
__device__ void *unified_device_global_slot = nullptr;

static __global__ void unified_publish_through_device_global(int32_t *marker) {
  if (marker != nullptr) *marker = unified_device_global_marker;
  auto *slot = static_cast<int32_t *>(unified_device_global_slot);
  if (slot != nullptr) *slot = 77;
}
#endif

TEST_CASE("algorithms/roc-thrust-semantic/device-global-symbol-roundtrip/i32/n11", "[unified][rocthrust][compact]") {
#if defined(__HIPCC__)
  int32_t *target = nullptr;
  int32_t *marker = nullptr;
  if (hipMalloc(reinterpret_cast<void **>(&target), sizeof(int32_t)) != hipSuccess
      || hipMalloc(reinterpret_cast<void **>(&marker), sizeof(int32_t)) != hipSuccess) {
    hipFree(target);
    hipFree(marker);
    throw std::runtime_error("device-global-symbol-roundtrip: hipMalloc failed");
  }
  int32_t zero = 0;
  if (hipMemcpy(target, &zero, sizeof(int32_t), hipMemcpyHostToDevice) != hipSuccess
      || hipMemcpy(marker, &zero, sizeof(int32_t), hipMemcpyHostToDevice) != hipSuccess) {
    hipFree(target);
    hipFree(marker);
    throw std::runtime_error("device-global-symbol-roundtrip: destination initialisation failed");
  }
  void *slot = target;
  if (hipMemcpyToSymbol(unified_device_global_slot, &slot, sizeof(slot)) != hipSuccess) {
    hipFree(target);
    hipFree(marker);
    throw std::runtime_error("device-global-symbol-roundtrip: hipMemcpyToSymbol failed");
  }
  unified_publish_through_device_global<<<1, 1>>>(marker);
  const hipError_t launch = hipDeviceSynchronize();
  int32_t published = 0;
  int32_t observed_marker = 0;
  const bool copied = launch == hipSuccess && hipMemcpy(&published, target, sizeof(int32_t), hipMemcpyDeviceToHost) == hipSuccess
                      && hipMemcpy(&observed_marker, marker, sizeof(int32_t), hipMemcpyDeviceToHost) == hipSuccess;
  void *cleared = nullptr;
  hipMemcpyToSymbol(unified_device_global_slot, &cleared, sizeof(cleared));
  hipFree(target);
  hipFree(marker);
  if (!copied) throw std::runtime_error("device-global-symbol-roundtrip: kernel launch or read-back failed");
  {
    INFO("device-global-symbol-roundtrip: kernel did not read the initialised "
         "device global");
    REQUIRE((observed_marker == 77));
  }
  {
    INFO("device-global-symbol-roundtrip: kernel did not publish through the "
         "device global");
    REQUIRE((published == 77));
  }
#else
  SKIP("device-global-symbol-roundtrip requires the HIP backend");
#endif
}

// ---------------------------------------------------------------------------
// rocThrust `testing/vector{,_insert}` port: device_vector insert/erase/
// resize semantics, each step mirrored through an exact std::vector
// reference.

TEST_CASE("algorithms/roc-thrust-semantic/vector-insert/i32/n11", "[unified][rocthrust][compact]") {

  const thrust::device_vector<TestType> source{0, 1, 2, 3, 4};
  {
    thrust::device_vector<TestType> value(3);
    thrust::sequence(value.begin(), value.end());
    value.reserve(10);
    value.insert(value.begin() + 1, source.begin(), source.end());
    {
      INFO("vector-insert: reserved range insert content mismatch");
      const auto &device_values = value;
      REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end()))
              == (std::vector<TestType>{TestType{0}, TestType{0}, TestType{1}, TestType{2}, TestType{3}, TestType{4}, TestType{1},
                                        TestType{2}}));
    }
    {
      INFO("vector-insert: reserved range insert size/capacity mismatch");
      REQUIRE((value.size() == 8 && value.capacity() == 10));
    }
  }
  {
    thrust::device_vector<TestType> value(5);
    thrust::sequence(value.begin(), value.end());
    value.reserve(10);
    value.insert(value.begin(), source.begin(), source.end());
    {
      INFO("vector-insert: exact-capacity range insert content mismatch");
      const auto &device_values = value;
      REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end()))
              == (std::vector<TestType>{TestType{0}, TestType{1}, TestType{2}, TestType{3}, TestType{4}, TestType{0}, TestType{1},
                                        TestType{2}, TestType{3}, TestType{4}}));
    }
  }
  {
    thrust::device_vector<TestType> value(5);
    thrust::sequence(value.begin(), value.end());
    value.insert(value.begin() + 1, 5, TestType{13});
    {
      INFO("vector-insert: reallocating fill insert content mismatch");
      const auto &device_values = value;
      REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end()))
              == (std::vector<TestType>{TestType{0}, TestType{13}, TestType{13}, TestType{13}, TestType{13}, TestType{13}, TestType{1},
                                        TestType{2}, TestType{3}, TestType{4}}));
    }
    {
      INFO("vector-insert: reallocating fill insert size mismatch");
      REQUIRE((value.size() == 10));
    }
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/vector-erase/i32/n11", "[unified][rocthrust][compact]") {

  thrust::device_vector<TestType> value{0, 1, 2, 3, 4, 5};
  value.erase(value.begin() + 1, value.begin() + 3);
  {
    INFO("vector-erase: range erase content mismatch");
    const auto &device_values = value;
    REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end()))
            == (std::vector<TestType>{TestType{0}, TestType{3}, TestType{4}, TestType{5}}));
  }
  value.erase(value.begin());
  {
    INFO("vector-erase: position erase content mismatch");
    const auto &device_values = value;
    REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end()))
            == (std::vector<TestType>{TestType{3}, TestType{4}, TestType{5}}));
  }
  value.erase(value.end() - 1, value.end());
  {
    INFO("vector-erase: tail erase content mismatch");
    const auto &device_values = value;
    REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end())) == (std::vector<TestType>{TestType{3}, TestType{4}}));
  }
  value.erase(value.begin() + 1, value.begin() + 1);
  {
    INFO("vector-erase: empty range erase content mismatch");
    const auto &device_values = value;
    REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end())) == (std::vector<TestType>{TestType{3}, TestType{4}}));
  }
  value.erase(value.begin() + 1);
  {
    INFO("vector-erase: final position erase content mismatch");
    const auto &device_values = value;
    REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end())) == (std::vector<TestType>{TestType{3}}));
  }
  value.erase(value.begin());
  {
    INFO("vector-erase: clearing the range left elements behind");
    REQUIRE((value.empty()));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/vector-resize/i32/n11", "[unified][rocthrust][compact]") {

  const thrust::device_vector<TestType> source{3, 1, 4, 1, 5};
  thrust::device_vector<TestType> value = source;
  value.resize(8);
  {
    INFO("vector-resize: growth size mismatch");
    REQUIRE((value.size() == 8));
  }
  value[7] = TestType{9};
  value.resize(5);
  {
    INFO("vector-resize: shrink content mismatch");
    const auto &device_values = value;
    REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end()))
            == (std::vector<TestType>{TestType{3}, TestType{1}, TestType{4}, TestType{1}, TestType{5}}));
  }
  value.resize(8, TestType{2});
  {
    INFO("vector-resize: padded growth content mismatch");
    const auto &device_values = value;
    REQUIRE(
        (std::vector<TestType>(device_values.begin(), device_values.end()))
        == (std::vector<TestType>{TestType{3}, TestType{1}, TestType{4}, TestType{1}, TestType{5}, TestType{2}, TestType{2}, TestType{2}}));
  }
  value.erase(value.begin() + 5, value.end());
  value.push_back(TestType{6});
  {
    INFO("vector-resize: push_back after erase mismatch");
    REQUIRE((value.size() == 6 && value.back() == TestType{6}));
  }
  value.pop_back();
  {
    INFO("vector-resize: pop_back size/back mismatch");
    REQUIRE((value.size() == 5 && value.back() == TestType{5}));
  }
  thrust::device_vector<TestType> other;
  other.swap(value);
  {
    INFO("vector-resize: swap sizes mismatch");
    REQUIRE((value.empty() && other.size() == 5));
  }
  other.shrink_to_fit();
  {
    INFO("vector-resize: shrink_to_fit capacity mismatch");
    REQUIRE((other.size() == other.capacity()));
  }
}

// ---------------------------------------------------------------------------
// rocThrust `testing/dereference` port: plain iterators and device pointers
// dereferenced inside device code through a hand-rolled copy kernel, since a
// library-side copy would dispatch instead of exercising the iterator's own
// operator*.  `testing/device_delete.cu` itself is marked KNOWN_FAILURE
// upstream, so only this brand of device dereference is live.
// ---------------------------------------------------------------------------

// ---------------------------------------------------------------------------
// rocThrust `testing/device_ptr` and `testing/device_reference` port.

TEST_CASE("algorithms/roc-thrust-semantic/device-pointer-manipulation/i32/n11", "[unified][rocthrust][compact]") {

  thrust::device_vector<TestType> data(5, TestType{0});
  thrust::device_ptr<TestType> begin = &data[0];
  const thrust::device_ptr<TestType> end = begin + 5;
  {
    INFO("device-pointer-manipulation: pointer range arithmetic mismatch");
    REQUIRE((end - begin == 5));
  }
  begin = begin + 1;
  *begin = TestType{10};
  begin = begin - 1;
  begin += 2;
  *begin = TestType{11};
  begin -= 2;
  begin++;
  *begin += TestType{1};
  const thrust::device_ptr<TestType> cast_back = thrust::device_pointer_cast(thrust::raw_pointer_cast(begin));
  {
    INFO("device-pointer-manipulation: device_pointer_cast roundtrip mismatch");
    REQUIRE((cast_back == begin));
  }
  {
    INFO("device-pointer-manipulation: raw pointer arithmetic mismatch");
    REQUIRE((thrust::raw_pointer_cast(end) == thrust::raw_pointer_cast(&data[0]) + 5));
  }
  const thrust::device_ptr<TestType> null_pointer = thrust::device_pointer_cast(static_cast<TestType *>(nullptr));
  {
    INFO("device-pointer-manipulation: null cast roundtrip mismatch");
    REQUIRE((thrust::raw_pointer_cast(null_pointer) == nullptr));
  }
  {
    INFO("device-pointer-manipulation: null conversion mismatch");
    REQUIRE((!static_cast<bool>(null_pointer)));
  }
  thrust::device_vector<TestType> output(5, TestType{0});
  thrust::copy(&data[0], &data[0] + 5, output.begin());
  {
    INFO("device-pointer-manipulation: copy through device_ptr mismatch");
    const auto &device_values = output;
    REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end()))
            == (std::vector<TestType>{TestType{0}, TestType{11}, TestType{11}, TestType{0}, TestType{0}}));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/device-reference/i32/n11", "[unified][rocthrust][compact]") {

  thrust::device_vector<TestType> value(2, TestType{0});
  thrust::device_reference<TestType> first = value[0];
  thrust::device_reference<TestType> second = value[1];
  first = TestType{13};
  second = first;
  {
    INFO("device-reference: assignment from device_reference mismatch");
    REQUIRE((second == TestType{13}));
  }
  ++first;
  {
    INFO("device-reference: prefix increment mismatch");
    REQUIRE((first == TestType{14} && value[0] == TestType{14}));
  }
  first += 5;
  {
    INFO("device-reference: addition-assignment mismatch");
    REQUIRE((first == TestType{19}));
  }
  first -= 4;
  {
    INFO("device-reference: subtraction-assignment mismatch");
    REQUIRE((first == TestType{15}));
  }
  first *= 3;
  {
    INFO("device-reference: multiply-assignment mismatch");
    REQUIRE((first == TestType{45}));
  }
  first /= 9;
  {
    INFO("device-reference: divide-assignment mismatch");
    REQUIRE((first == TestType{5}));
  }
  first %= 3;
  {
    INFO("device-reference: modulus-assignment mismatch");
    REQUIRE((first == TestType{2}));
  }
  first <<= 1;
  {
    INFO("device-reference: left-shift-assignment mismatch");
    REQUIRE((first == TestType{4}));
  }
  first >>= 1;
  {
    INFO("device-reference: right-shift-assignment mismatch");
    REQUIRE((first == TestType{2}));
  }
  first |= 5;
  {
    INFO("device-reference: OR-assignment mismatch");
    REQUIRE((first == TestType{7}));
  }
  first &= 5;
  {
    INFO("device-reference: AND-assignment mismatch");
    REQUIRE((first == TestType{5}));
  }
  first ^= 1;
  {
    INFO("device-reference: XOR-assignment mismatch");
    REQUIRE((first == TestType{4}));
  }
  first.swap(second);
  {
    INFO("device-reference: member swap mismatch");
    REQUIRE((value[0] == TestType{13} && value[1] == TestType{4}));
  }
  using std::swap;
  swap(first, second);
  {
    INFO("device-reference: ADL swap mismatch");
    REQUIRE((value[0] == TestType{4} && value[1] == TestType{13}));
  }
  const thrust::device_ptr<TestType> pointer = &value[0];
  thrust::device_reference<TestType> through_pointer(pointer);
  through_pointer += TestType{1};
  {
    INFO("device-reference: write through pointer-constructed reference "
         "mismatch");
    REQUIRE((value[0] == TestType{5}));
  }
}

// ---------------------------------------------------------------------------
// rocThrust `testing/memory` and `testing/universal_memory` port: raw device
// allocation roundtrips and universal (managed) storage, mirrored against an
// exact host reference.
// ---------------------------------------------------------------------------

template <class TestType> struct HoldsValue {
  UNIFIED_ROCTHRUST_HOST_DEVICE bool operator()(TestType value) const { return value == expected; }
  TestType expected;
};

TEST_CASE("algorithms/roc-thrust-semantic/memory-malloc-free/i32/n11", "[unified][rocthrust][compact]") {

  const ptrdiff_t count = 17;
  const thrust::device_system_tag tag{};
  const auto allocated = thrust::malloc<TestType>(tag, count);
  thrust::fill_n(allocated, count, TestType{13});
  {
    INFO("memory-malloc-free: raw allocation content mismatch");
    REQUIRE((thrust::all_of(allocated, allocated + count, HoldsValue<TestType>{TestType{13}})));
  }
  thrust::free(tag, allocated);
  const auto temporary = thrust::get_temporary_buffer<TestType>(tag, count);
  {
    INFO("memory-malloc-free: temporary buffer size mismatch");
    REQUIRE(((std::vector<ptrdiff_t>{count}) == (std::vector<ptrdiff_t>{temporary.second})));
  }
  thrust::fill_n(temporary.first, temporary.second, TestType{7});
  {
    INFO("memory-malloc-free: temporary buffer content mismatch");
    REQUIRE((thrust::all_of(temporary.first, temporary.first + temporary.second, HoldsValue<TestType>{TestType{7}})));
  }
  thrust::return_temporary_buffer(tag, temporary.first, temporary.second);
}
