#include "cases/async.inl"

#include <catch2/catch_template_test_macros.hpp>

#include "cases/hip_async.inl"
#include "reference_async.hpp"

#define REFERENCE_ASYNC(name, function, label, family)                                                                                     \
  TEMPLATE_TEST_CASE("reference " #label " async/" #name, "[unit][async]", int32_t, float) {                                               \
    const auto size = GENERATE(0, 1, 7, 33);                                                                                               \
    CAPTURE(size);                                                                                                                         \
    unified::test::ReferenceAsync backend;                                                                                                 \
    unified::family::function<TestType>(backend, size);                                                                                    \
  }

UNIFIED_ASYNC_CASES(REFERENCE_ASYNC, portable, async)
UNIFIED_HIP_ASYNC_CASES(REFERENCE_ASYNC, HIP, hip_async)
