#include <catch2/catch_template_test_macros.hpp>
#include <catch2/generators/catch_generators_range.hpp>

#include "async_cases.hpp"
#include "reference_async.hpp"

#define DECLARE_REFERENCE_PORTABLE_ASYNC(name, check)                                                                                      \
  TEMPLATE_TEST_CASE("reference portable async/" name, "[unit][async]", int32_t, float) {                                                  \
    const auto size = GENERATE(0, 1, 7, 33);                                                                                               \
    CAPTURE(size);                                                                                                                         \
    unified::test::ReferenceAsync backend;                                                                                                 \
    check<TestType>(backend, unified::support::async_input_values<TestType>(size));                                                        \
  }

DECLARE_REFERENCE_PORTABLE_ASYNC("copy", unified::async::copy)
DECLARE_REFERENCE_PORTABLE_ASYNC("reduce", unified::async::reduce)
DECLARE_REFERENCE_PORTABLE_ASYNC("scan", unified::async::scan)
DECLARE_REFERENCE_PORTABLE_ASYNC("sort", unified::async::sort)
DECLARE_REFERENCE_PORTABLE_ASYNC("future_chain", unified::async::future_chain)
DECLARE_REFERENCE_PORTABLE_ASYNC("event_chain", unified::async::event_chain)
DECLARE_REFERENCE_PORTABLE_ASYNC("interleaved_streams", unified::async::interleaved_streams)

#undef DECLARE_REFERENCE_PORTABLE_ASYNC

#define DECLARE_REFERENCE_HIP_ASYNC(name, check)                                                                                           \
  TEMPLATE_TEST_CASE("reference HIP async/" name, "[unit][async]", int32_t, float) {                                                       \
    const auto size = GENERATE(0, 1, 7, 33);                                                                                               \
    CAPTURE(size);                                                                                                                         \
    unified::test::ReferenceAsync backend;                                                                                                 \
    check<TestType>(backend, size);                                                                                                        \
  }

DECLARE_REFERENCE_HIP_ASYNC("inclusive_counting", unified::hip_async::inclusive_counting)
DECLARE_REFERENCE_HIP_ASYNC("exclusive_counting", unified::hip_async::exclusive_counting)
DECLARE_REFERENCE_HIP_ASYNC("inclusive_discard", unified::hip_async::inclusive_discard)
DECLARE_REFERENCE_HIP_ASYNC("exclusive_discard", unified::hip_async::exclusive_discard)
DECLARE_REFERENCE_HIP_ASYNC("inclusive_stateful", unified::hip_async::inclusive_stateful)
DECLARE_REFERENCE_HIP_ASYNC("exclusive_stateful", unified::hip_async::exclusive_stateful)

#undef DECLARE_REFERENCE_HIP_ASYNC
