#include "test_support.hpp"

#define UNIFIED_API_ROCTHRUST 1
#include "backend_thrust.hpp"
#undef UNIFIED_API_ROCTHRUST
#include <cstddef>
#include <cstdint>

#include <catch2/catch_test_macros.hpp>

using namespace unified::rocthrust_support;

// The device-side random semantics of rocThrust's `testing/random`, decomposed
// into one case per engine characteristic and one per distribution
// characteristic so a single regression names the exact instantiation.

inline constexpr size_t RandomDraws = 10000;
// Exact sequence validation keeps the upstream 10,000th-value requirement. Range
// and equality properties need a representative sample, not 70 separate
// 10,000-step device loops in every emulator run.
inline constexpr size_t RandomPropertyDraws = 256;

// Runs a nullary device functor once and returns its result.
template <class Functor> inline int32_t deviceFunctorResult(Functor functor) {
  thrust::device_vector<int32_t> output(1, 0);
  thrust::generate(output.begin(), output.end(), functor);
  return output[0];
}

template <class Engine> struct EngineValidation {
  using result_type = typename Engine::result_type;
  result_type expected;
  UNIFIED_ROCTHRUST_HOST_DEVICE int32_t operator()() {
    Engine engine;
    engine.discard(RandomDraws - 1);
    return engine() == expected ? 1 : 0;
  }
};

template <class Engine> struct EngineMinimum {
  UNIFIED_ROCTHRUST_HOST_DEVICE int32_t operator()() {
    Engine engine;
    int32_t result = 1;
    for (size_t index = 0; index < RandomPropertyDraws; ++index)
      result &= (engine() >= Engine::min) ? 1 : 0;
    return result;
  }
};

template <class Engine> struct EngineMaximum {
  UNIFIED_ROCTHRUST_HOST_DEVICE int32_t operator()() {
    Engine engine;
    int32_t result = 1;
    for (size_t index = 0; index < RandomPropertyDraws; ++index)
      result &= (engine() <= Engine::max) ? 1 : 0;
    return result;
  }
};

template <class Engine> struct EngineEquality {
  UNIFIED_ROCTHRUST_HOST_DEVICE int32_t operator()() {
    int32_t result = 1;
    Engine first, second;
    result &= (first == second) ? 1 : 0;
    first.discard(RandomPropertyDraws);
    second.discard(RandomPropertyDraws);
    result &= (first == second) ? 1 : 0;
    Engine seededA(13), seededB(13);
    result &= (seededA == seededB) ? 1 : 0;
    Engine otherA(7), otherB(13);
    result &= !(otherA == otherB) ? 1 : 0;
    otherA.seed(13);
    result &= (otherA == otherB) ? 1 : 0;
    result &= !(first != second) ? 1 : 0;
    Engine unequalA(13), unequalB(13);
    unequalA.discard(500);
    unequalB.discard(RandomPropertyDraws / 10);
    result &= (unequalA != unequalB) ? 1 : 0;
    return result;
  }
};

template <class Distribution, class Engine> struct DistributionMinimum {
  Distribution distribution;
  UNIFIED_ROCTHRUST_HOST_DEVICE int32_t operator()() {
    Engine engine;
    int32_t result = 1;
    for (size_t index = 0; index < RandomPropertyDraws; ++index) {
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
    for (size_t index = 0; index < RandomPropertyDraws; ++index) {
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
      REQUIRE(value == 1);
    }
  };
  check("ranlux24_base", deviceFunctorResult(EngineValidation<ranlux24_base>{7937952u}));
  check("ranlux48_base", deviceFunctorResult(EngineValidation<ranlux48_base>{192113843633948ull}));
  check("minstd_rand", deviceFunctorResult(EngineValidation<minstd_rand>{399268537u}));
  check("minstd_rand0", deviceFunctorResult(EngineValidation<minstd_rand0>{1043618065u}));
  check("taus88", deviceFunctorResult(EngineValidation<taus88>{3535848941ull}));
  check("ranlux24", deviceFunctorResult(EngineValidation<ranlux24>{9901578u}));
  check("ranlux48", deviceFunctorResult(EngineValidation<ranlux48>{88229545517833ull}));
}

template <template <class> class Check> inline void checkAllEngines(const char *what) {
  using namespace thrust::random;
  const auto check = [what](const char *name, int32_t value) {
    {
      INFO(fmt::format("{}: {} check failed", what, name));
      REQUIRE(value == 1);
    }
  };
  check("ranlux24_base", deviceFunctorResult(Check<ranlux24_base>{}));
  check("ranlux48_base", deviceFunctorResult(Check<ranlux48_base>{}));
  check("minstd_rand", deviceFunctorResult(Check<minstd_rand>{}));
  check("minstd_rand0", deviceFunctorResult(Check<minstd_rand0>{}));
  check("taus88", deviceFunctorResult(Check<taus88>{}));
  check("ranlux24", deviceFunctorResult(Check<ranlux24>{}));
  check("ranlux48", deviceFunctorResult(Check<ranlux48>{}));
}

TEST_CASE("algorithms/roc-thrust-semantic/random-engine-min/i32/n11", "[unified][rocthrust][compact]") {
  checkAllEngines<EngineMinimum>("random-engine-min");
}

TEST_CASE("algorithms/roc-thrust-semantic/random-engine-max/i32/n11", "[unified][rocthrust][compact]") {
  checkAllEngines<EngineMaximum>("random-engine-max");
}

TEST_CASE("algorithms/roc-thrust-semantic/random-engine-equality/i32/n11", "[unified][rocthrust][compact]") {
  checkAllEngines<EngineEquality>("random-engine-equality");
}

template <class Engine> inline void checkEngineSaveRestore(const char *name) {
  Engine engine;
  engine.discard(RandomDraws);
  std::stringstream buffer;
  buffer << engine;
  engine.discard(RandomDraws);
  Engine restored;
  buffer >> restored;
  restored.discard(RandomDraws);
  {
    INFO(fmt::format("random-engine-save-restore: {} state mismatch", name));
    REQUIRE(engine() == restored());
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/random-engine-save-restore/i32/n11", "[unified][rocthrust][compact]") {
  using namespace thrust::random;
  checkEngineSaveRestore<ranlux24_base>("ranlux24_base");
  checkEngineSaveRestore<ranlux48_base>("ranlux48_base");
  checkEngineSaveRestore<minstd_rand>("minstd_rand");
  checkEngineSaveRestore<minstd_rand0>("minstd_rand0");
  checkEngineSaveRestore<taus88>("taus88");
  checkEngineSaveRestore<ranlux24>("ranlux24");
  checkEngineSaveRestore<ranlux48>("ranlux48");
}

template <template <class, class> class Check> inline void checkAllDistributions(const char *what) {
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
        [] { return deviceFunctorResult(Check<uniform_int_distribution<int32_t>, Engine>{uniform_int_distribution<int32_t>()}); });
  check("uniform_int_distribution<uint32_t>",
        [] { return deviceFunctorResult(Check<uniform_int_distribution<uint32_t>, Engine>{uniform_int_distribution<uint32_t>()}); });
  check("uniform_real_distribution<float>",
        [] { return deviceFunctorResult(Check<uniform_real_distribution<float>, Engine>{uniform_real_distribution<float>()}); });
  check("uniform_real_distribution<double>",
        [] { return deviceFunctorResult(Check<uniform_real_distribution<double>, Engine>{uniform_real_distribution<double>()}); });
  check("normal_distribution<float>",
        [] { return deviceFunctorResult(Check<normal_distribution<float>, Engine>{normal_distribution<float>()}); });
  check("normal_distribution<double>",
        [] { return deviceFunctorResult(Check<normal_distribution<double>, Engine>{normal_distribution<double>()}); });
  if (!failures.empty()) throw std::runtime_error(fmt::format("{}: failing distributions:{}", what, failures));
}

TEST_CASE("algorithms/roc-thrust-semantic/random-distribution-min/i32/n11", "[unified][rocthrust][compact]") {
  checkAllDistributions<DistributionMinimum>("random-distribution-min");
}

TEST_CASE("algorithms/roc-thrust-semantic/random-distribution-max/i32/n11", "[unified][rocthrust][compact]") {
  checkAllDistributions<DistributionMaximum>("random-distribution-max");
}

template <class Distribution> inline void checkDistributionSaveRestore(const char *name) {
  Distribution original(7, 13);
  std::stringstream buffer;
  buffer << original;
  Distribution restored;
  buffer >> restored;
  {
    INFO(fmt::format("random-distribution-save-restore: {} state mismatch", name));
    REQUIRE(original == restored);
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/random-distribution-save-restore/i32/n11", "[unified][rocthrust][compact]") {
  using namespace thrust::random;
  checkDistributionSaveRestore<uniform_int_distribution<int32_t>>("uniform_int_distribution<int32_t>");
  checkDistributionSaveRestore<uniform_int_distribution<uint32_t>>("uniform_int_distribution<uint32_t>");
  checkDistributionSaveRestore<uniform_real_distribution<float>>("uniform_real_distribution<float>");
  checkDistributionSaveRestore<uniform_real_distribution<double>>("uniform_real_distribution<double>");
  checkDistributionSaveRestore<normal_distribution<float>>("normal_distribution<float>");
  checkDistributionSaveRestore<normal_distribution<double>>("normal_distribution<double>");
}

// rocThrust `testing/unittest_static_assert` port.  The upstream case depends
// on a device kernel publishing through a pointer that the host installed in a
// `__device__` global with hipMemcpyToSymbol, so that capability is the
// semantic unit here rather than the static-assert macro itself.

#if defined(__HIPCC__)
__device__ int32_t unifiedDeviceGlobalMarker = 77;
__device__ void *unifiedDeviceGlobalSlot = nullptr;

static __global__ void unifiedPublishThroughDeviceGlobal(int32_t *marker) {
  if (marker != nullptr) *marker = unifiedDeviceGlobalMarker;
  auto *slot = static_cast<int32_t *>(unifiedDeviceGlobalSlot);
  if (slot != nullptr) *slot = 77;
}
#endif

TEST_CASE("algorithms/roc-thrust-semantic/device-global-symbol-roundtrip/i32/n11", "[unified][rocthrust][compact]") {
#if defined(__HIPCC__)
  int32_t *target = nullptr;
  int32_t *marker = nullptr;
  if (hipMalloc(reinterpret_cast<void **>(&target), sizeof(int32_t)) != hipSuccess
      || hipMalloc(reinterpret_cast<void **>(&marker), sizeof(int32_t)) != hipSuccess) {
    (void)hipFree(target);
    (void)hipFree(marker);
    throw std::runtime_error("device-global-symbol-roundtrip: hipMalloc failed");
  }
  int32_t zero = 0;
  if (hipMemcpy(target, &zero, sizeof(int32_t), hipMemcpyHostToDevice) != hipSuccess
      || hipMemcpy(marker, &zero, sizeof(int32_t), hipMemcpyHostToDevice) != hipSuccess) {
    (void)hipFree(target);
    (void)hipFree(marker);
    throw std::runtime_error("device-global-symbol-roundtrip: destination initialisation failed");
  }
  void *slot = target;
  if (hipMemcpyToSymbol(unifiedDeviceGlobalSlot, &slot, sizeof(slot)) != hipSuccess) {
    (void)hipFree(target);
    (void)hipFree(marker);
    throw std::runtime_error("device-global-symbol-roundtrip: hipMemcpyToSymbol failed");
  }
  unifiedPublishThroughDeviceGlobal<<<1, 1>>>(marker);
  const hipError_t launch = hipDeviceSynchronize();
  int32_t published = 0;
  int32_t observedMarker = 0;
  const bool copied = launch == hipSuccess && hipMemcpy(&published, target, sizeof(int32_t), hipMemcpyDeviceToHost) == hipSuccess
                      && hipMemcpy(&observedMarker, marker, sizeof(int32_t), hipMemcpyDeviceToHost) == hipSuccess;
  void *cleared = nullptr;
  const auto reset = hipMemcpyToSymbol(unifiedDeviceGlobalSlot, &cleared, sizeof(cleared));
  const auto freedTarget = hipFree(target);
  const auto freedMarker = hipFree(marker);
  if (!copied) throw std::runtime_error("device-global-symbol-roundtrip: kernel launch or read-back failed");
  REQUIRE(reset == hipSuccess);
  REQUIRE(freedTarget == hipSuccess);
  REQUIRE(freedMarker == hipSuccess);
  {
    INFO("device-global-symbol-roundtrip: kernel did not read the initialised "
         "device global");
    REQUIRE(observedMarker == 77);
  }
  {
    INFO("device-global-symbol-roundtrip: kernel did not publish through the "
         "device global");
    REQUIRE(published == 77);
  }
#else
  SKIP("device-global-symbol-roundtrip requires the HIP backend");
#endif
}

// rocThrust `testing/vector{,_insert}` port: device_vector insert/erase/
// resize semantics, each step mirrored through an exact std::vector
// reference.

TEST_CASE("algorithms/roc-thrust-semantic/vector-insert/i32/n11", "[unified][rocthrust][compact]") {
  const thrust::device_vector<int32_t> source{0, 1, 2, 3, 4};
  {
    thrust::device_vector<int32_t> value(3);
    thrust::sequence(value.begin(), value.end());
    value.reserve(10);
    value.insert(value.begin() + 1, source.begin(), source.end());
    {
      INFO("vector-insert: reserved range insert content mismatch");
      const auto &deviceValues = value;
      REQUIRE((std::vector<int32_t>(deviceValues.begin(), deviceValues.end()))
              == (std::vector<int32_t>{int32_t{0}, int32_t{0}, int32_t{1}, int32_t{2}, int32_t{3}, int32_t{4}, int32_t{1}, int32_t{2}}));
    }
    {
      INFO("vector-insert: reserved range insert size/capacity mismatch");
      REQUIRE((value.size() == 8 && value.capacity() == 10));
    }
  }
  {
    thrust::device_vector<int32_t> value(5);
    thrust::sequence(value.begin(), value.end());
    value.reserve(10);
    value.insert(value.begin(), source.begin(), source.end());
    {
      INFO("vector-insert: exact-capacity range insert content mismatch");
      const auto &deviceValues = value;
      REQUIRE((std::vector<int32_t>(deviceValues.begin(), deviceValues.end()))
              == (std::vector<int32_t>{int32_t{0}, int32_t{1}, int32_t{2}, int32_t{3}, int32_t{4}, int32_t{0}, int32_t{1}, int32_t{2},
                                       int32_t{3}, int32_t{4}}));
    }
  }
  {
    thrust::device_vector<int32_t> value(5);
    thrust::sequence(value.begin(), value.end());
    value.insert(value.begin() + 1, 5, int32_t{13});
    {
      INFO("vector-insert: reallocating fill insert content mismatch");
      const auto &deviceValues = value;
      REQUIRE((std::vector<int32_t>(deviceValues.begin(), deviceValues.end()))
              == (std::vector<int32_t>{int32_t{0}, int32_t{13}, int32_t{13}, int32_t{13}, int32_t{13}, int32_t{13}, int32_t{1}, int32_t{2},
                                       int32_t{3}, int32_t{4}}));
    }
    {
      INFO("vector-insert: reallocating fill insert size mismatch");
      REQUIRE(value.size() == 10);
    }
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/vector-erase/i32/n11", "[unified][rocthrust][compact]") {
  thrust::device_vector<int32_t> value{0, 1, 2, 3, 4, 5};
  value.erase(value.begin() + 1, value.begin() + 3);
  {
    INFO("vector-erase: range erase content mismatch");
    const auto &deviceValues = value;
    REQUIRE(std::vector<int32_t>(deviceValues.begin(), deviceValues.end())
            == std::vector<int32_t>{int32_t{0}, int32_t{3}, int32_t{4}, int32_t{5}});
  }
  value.erase(value.begin());
  {
    INFO("vector-erase: position erase content mismatch");
    const auto &deviceValues = value;
    REQUIRE(std::vector<int32_t>(deviceValues.begin(), deviceValues.end()) == std::vector<int32_t>{int32_t{3}, int32_t{4}, int32_t{5}});
  }
  value.erase(value.end() - 1, value.end());
  {
    INFO("vector-erase: tail erase content mismatch");
    const auto &deviceValues = value;
    REQUIRE(std::vector<int32_t>(deviceValues.begin(), deviceValues.end()) == std::vector<int32_t>{int32_t{3}, int32_t{4}});
  }
  value.erase(value.begin() + 1, value.begin() + 1);
  {
    INFO("vector-erase: empty range erase content mismatch");
    const auto &deviceValues = value;
    REQUIRE(std::vector<int32_t>(deviceValues.begin(), deviceValues.end()) == std::vector<int32_t>{int32_t{3}, int32_t{4}});
  }
  value.erase(value.begin() + 1);
  {
    INFO("vector-erase: final position erase content mismatch");
    const auto &deviceValues = value;
    REQUIRE(std::vector<int32_t>(deviceValues.begin(), deviceValues.end()) == std::vector<int32_t>{int32_t{3}});
  }
  value.erase(value.begin());
  {
    INFO("vector-erase: clearing the range left elements behind");
    REQUIRE(value.empty());
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/vector-resize/i32/n11", "[unified][rocthrust][compact]") {
  const thrust::device_vector<int32_t> source{3, 1, 4, 1, 5};
  thrust::device_vector<int32_t> value = source;
  value.resize(8);
  {
    INFO("vector-resize: growth size mismatch");
    REQUIRE(value.size() == 8);
  }
  value[7] = int32_t{9};
  value.resize(5);
  {
    INFO("vector-resize: shrink content mismatch");
    const auto &deviceValues = value;
    REQUIRE(std::vector<int32_t>(deviceValues.begin(), deviceValues.end())
            == std::vector<int32_t>{int32_t{3}, int32_t{1}, int32_t{4}, int32_t{1}, int32_t{5}});
  }
  value.resize(8, int32_t{2});
  {
    INFO("vector-resize: padded growth content mismatch");
    const auto &deviceValues = value;
    REQUIRE(std::vector<int32_t>(deviceValues.begin(), deviceValues.end())
            == std::vector<int32_t>{int32_t{3}, int32_t{1}, int32_t{4}, int32_t{1}, int32_t{5}, int32_t{2}, int32_t{2}, int32_t{2}});
  }
  value.erase(value.begin() + 5, value.end());
  value.push_back(int32_t{6});
  {
    INFO("vector-resize: push_back after erase mismatch");
    REQUIRE((value.size() == 6 && value.back() == int32_t{6}));
  }
  value.pop_back();
  {
    INFO("vector-resize: pop_back size/back mismatch");
    REQUIRE((value.size() == 5 && value.back() == int32_t{5}));
  }
  thrust::device_vector<int32_t> other;
  other.swap(value);
  {
    INFO("vector-resize: swap sizes mismatch");
    REQUIRE((value.empty() && other.size() == 5));
  }
  other.shrink_to_fit();
  {
    INFO("vector-resize: shrink_to_fit capacity mismatch");
    REQUIRE(other.size() == other.capacity());
  }
}

// rocThrust `testing/device_ptr` and `testing/device_reference` port.

TEST_CASE("algorithms/roc-thrust-semantic/device-pointer-manipulation/i32/n11", "[unified][rocthrust][compact]") {
  thrust::device_vector<int32_t> data(5, int32_t{0});
  thrust::device_ptr<int32_t> begin = &data[0];
  const thrust::device_ptr<int32_t> end = begin + 5;
  {
    INFO("device-pointer-manipulation: pointer range arithmetic mismatch");
    REQUIRE(end - begin == 5);
  }
  begin = begin + 1;
  *begin = int32_t{10};
  begin = begin - 1;
  begin += 2;
  *begin = int32_t{11};
  begin -= 2;
  begin++;
  *begin += int32_t{1};
  const thrust::device_ptr<int32_t> castBack = thrust::device_pointer_cast(thrust::raw_pointer_cast(begin));
  {
    INFO("device-pointer-manipulation: device_pointer_cast roundtrip mismatch");
    REQUIRE(castBack == begin);
  }
  {
    INFO("device-pointer-manipulation: raw pointer arithmetic mismatch");
    REQUIRE(thrust::raw_pointer_cast(end) == thrust::raw_pointer_cast(&data[0]) + 5);
  }
  const thrust::device_ptr<int32_t> nullPointer = thrust::device_pointer_cast(static_cast<int32_t *>(nullptr));
  {
    INFO("device-pointer-manipulation: null cast roundtrip mismatch");
    REQUIRE(thrust::raw_pointer_cast(nullPointer) == nullptr);
  }
  {
    INFO("device-pointer-manipulation: null conversion mismatch");
    REQUIRE(!static_cast<bool>(nullPointer));
  }
  thrust::device_vector<int32_t> output(5, int32_t{0});
  thrust::copy(&data[0], &data[0] + 5, output.begin());
  {
    INFO("device-pointer-manipulation: copy through device_ptr mismatch");
    const auto &deviceValues = output;
    REQUIRE(std::vector<int32_t>(deviceValues.begin(), deviceValues.end())
            == std::vector<int32_t>{int32_t{0}, int32_t{11}, int32_t{11}, int32_t{0}, int32_t{0}});
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/device-reference/i32/n11", "[unified][rocthrust][compact]") {
  thrust::device_vector<int32_t> value(2, int32_t{0});
  thrust::device_reference<int32_t> first = value[0];
  thrust::device_reference<int32_t> second = value[1];
  first = int32_t{13};
  second = first;
  {
    INFO("device-reference: assignment from device_reference mismatch");
    REQUIRE(second == int32_t{13});
  }
  ++first;
  {
    INFO("device-reference: prefix increment mismatch");
    REQUIRE((first == int32_t{14} && value[0] == int32_t{14}));
  }
  first += 5;
  {
    INFO("device-reference: addition-assignment mismatch");
    REQUIRE(first == int32_t{19});
  }
  first -= 4;
  {
    INFO("device-reference: subtraction-assignment mismatch");
    REQUIRE(first == int32_t{15});
  }
  first *= 3;
  {
    INFO("device-reference: multiply-assignment mismatch");
    REQUIRE(first == int32_t{45});
  }
  first /= 9;
  {
    INFO("device-reference: divide-assignment mismatch");
    REQUIRE(first == int32_t{5});
  }
  first %= 3;
  {
    INFO("device-reference: modulus-assignment mismatch");
    REQUIRE(first == int32_t{2});
  }
  first <<= 1;
  {
    INFO("device-reference: left-shift-assignment mismatch");
    REQUIRE(first == int32_t{4});
  }
  first >>= 1;
  {
    INFO("device-reference: right-shift-assignment mismatch");
    REQUIRE(first == int32_t{2});
  }
  first |= 5;
  {
    INFO("device-reference: OR-assignment mismatch");
    REQUIRE(first == int32_t{7});
  }
  first &= 5;
  {
    INFO("device-reference: AND-assignment mismatch");
    REQUIRE(first == int32_t{5});
  }
  first ^= 1;
  {
    INFO("device-reference: XOR-assignment mismatch");
    REQUIRE(first == int32_t{4});
  }
  first.swap(second);
  {
    INFO("device-reference: member swap mismatch");
    REQUIRE((value[0] == int32_t{13} && value[1] == int32_t{4}));
  }
  using std::swap;
  swap(first, second);
  {
    INFO("device-reference: ADL swap mismatch");
    REQUIRE((value[0] == int32_t{4} && value[1] == int32_t{13}));
  }
  const thrust::device_ptr<int32_t> pointer = &value[0];
  thrust::device_reference<int32_t> throughPointer(pointer);
  throughPointer += int32_t{1};
  {
    INFO("device-reference: write through pointer-constructed reference "
         "mismatch");
    REQUIRE(value[0] == int32_t{5});
  }
}

// rocThrust `testing/memory` and `testing/universal_memory` port: raw device
// allocation roundtrips and universal (managed) storage, mirrored against an
// exact host reference.

template <class Value> struct HoldsValue {
  UNIFIED_ROCTHRUST_HOST_DEVICE bool operator()(Value value) const { return value == expected; }
  Value expected;
};

TEST_CASE("algorithms/roc-thrust-semantic/memory-malloc-free/i32/n11", "[unified][rocthrust][compact]") {
  const ptrdiff_t count = 17;
  const thrust::device_system_tag tag{};
  const auto allocated = thrust::malloc<int32_t>(tag, count);
  thrust::fill_n(allocated, count, int32_t{13});
  {
    INFO("memory-malloc-free: raw allocation content mismatch");
    REQUIRE(thrust::all_of(allocated, allocated + count, HoldsValue<int32_t>{int32_t{13}}));
  }
  thrust::free(tag, allocated);
  const auto temporary = thrust::get_temporary_buffer<int32_t>(tag, count);
  {
    INFO("memory-malloc-free: temporary buffer size mismatch");
    REQUIRE(std::vector<ptrdiff_t>{count} == std::vector<ptrdiff_t>{temporary.second});
  }
  thrust::fill_n(temporary.first, temporary.second, int32_t{7});
  {
    INFO("memory-malloc-free: temporary buffer content mismatch");
    REQUIRE(thrust::all_of(temporary.first, temporary.first + temporary.second, HoldsValue<int32_t>{int32_t{7}}));
  }
  thrust::return_temporary_buffer(tag, temporary.first, temporary.second);
}
