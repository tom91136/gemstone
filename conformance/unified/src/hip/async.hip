#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>

#include "api_thrust.hpp"
#include "template_async.hpp"

#define DECLARE_ASYNC_I8_FULL(name, check)                                                                                                 \
  TEST_CASE("async/i8/full/" name, "[unified][full-matrix][async][i8]") {                                                                  \
    const auto size = GENERATE(Catch::Generators::from_range(unified::edge_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<int8_t>(backend, unified::support::async_input_values<int8_t>(size));                                                            \
  }

DECLARE_ASYNC_I8_FULL("copy", unified::async::copy)
DECLARE_ASYNC_I8_FULL("reduce", unified::async::reduce)
DECLARE_ASYNC_I8_FULL("scan", unified::async::scan)
DECLARE_ASYNC_I8_FULL("sort", unified::async::sort)
DECLARE_ASYNC_I8_FULL("future_chain", unified::async::future_chain)
DECLARE_ASYNC_I8_FULL("event_chain", unified::async::event_chain)
DECLARE_ASYNC_I8_FULL("interleaved_streams", unified::async::interleaved_streams)

#undef DECLARE_ASYNC_I8_FULL

#define DECLARE_ASYNC_I32_COMPACT(name, check)                                                                                             \
  TEST_CASE("async/i32/compact/" name, "[unified][compact-matrix][async][i32]") {                                                          \
    const auto size = GENERATE(Catch::Generators::from_range(unified::compact_edge_sizes));                                                \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<int32_t>(backend, unified::support::async_input_values<int32_t>(size));                                                          \
  }

DECLARE_ASYNC_I32_COMPACT("copy", unified::async::copy)
DECLARE_ASYNC_I32_COMPACT("reduce", unified::async::reduce)
DECLARE_ASYNC_I32_COMPACT("scan", unified::async::scan)
DECLARE_ASYNC_I32_COMPACT("sort", unified::async::sort)
DECLARE_ASYNC_I32_COMPACT("future_chain", unified::async::future_chain)
DECLARE_ASYNC_I32_COMPACT("event_chain", unified::async::event_chain)
DECLARE_ASYNC_I32_COMPACT("interleaved_streams", unified::async::interleaved_streams)

#undef DECLARE_ASYNC_I32_COMPACT

#define DECLARE_ASYNC_I32_FULL(name, check)                                                                                                \
  TEST_CASE("async/i32/full/" name, "[unified][full-matrix][async][i32]") {                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::edge_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<int32_t>(backend, unified::support::async_input_values<int32_t>(size));                                                          \
  }

DECLARE_ASYNC_I32_FULL("copy", unified::async::copy)
DECLARE_ASYNC_I32_FULL("reduce", unified::async::reduce)
DECLARE_ASYNC_I32_FULL("scan", unified::async::scan)
DECLARE_ASYNC_I32_FULL("sort", unified::async::sort)
DECLARE_ASYNC_I32_FULL("future_chain", unified::async::future_chain)
DECLARE_ASYNC_I32_FULL("event_chain", unified::async::event_chain)
DECLARE_ASYNC_I32_FULL("interleaved_streams", unified::async::interleaved_streams)

#undef DECLARE_ASYNC_I32_FULL

#define DECLARE_ASYNC_I64_FULL(name, check)                                                                                                \
  TEST_CASE("async/i64/full/" name, "[unified][full-matrix][async][i64]") {                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::edge_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<int64_t>(backend, unified::support::async_input_values<int64_t>(size));                                                          \
  }

DECLARE_ASYNC_I64_FULL("copy", unified::async::copy)
DECLARE_ASYNC_I64_FULL("reduce", unified::async::reduce)
DECLARE_ASYNC_I64_FULL("scan", unified::async::scan)
DECLARE_ASYNC_I64_FULL("sort", unified::async::sort)
DECLARE_ASYNC_I64_FULL("future_chain", unified::async::future_chain)
DECLARE_ASYNC_I64_FULL("event_chain", unified::async::event_chain)
DECLARE_ASYNC_I64_FULL("interleaved_streams", unified::async::interleaved_streams)

#undef DECLARE_ASYNC_I64_FULL

#define DECLARE_ASYNC_F32_COMPACT(name, check)                                                                                             \
  TEST_CASE("async/f32/compact/" name, "[unified][compact-matrix][async][f32]") {                                                          \
    const auto size = GENERATE(Catch::Generators::from_range(unified::compact_edge_sizes));                                                \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<float>(backend, unified::support::async_input_values<float>(size));                                                              \
  }

DECLARE_ASYNC_F32_COMPACT("copy", unified::async::copy)
DECLARE_ASYNC_F32_COMPACT("reduce", unified::async::reduce)
DECLARE_ASYNC_F32_COMPACT("scan", unified::async::scan)
DECLARE_ASYNC_F32_COMPACT("sort", unified::async::sort)
DECLARE_ASYNC_F32_COMPACT("future_chain", unified::async::future_chain)
DECLARE_ASYNC_F32_COMPACT("event_chain", unified::async::event_chain)
DECLARE_ASYNC_F32_COMPACT("interleaved_streams", unified::async::interleaved_streams)

#undef DECLARE_ASYNC_F32_COMPACT

#define DECLARE_ASYNC_F32_FULL(name, check)                                                                                                \
  TEST_CASE("async/f32/full/" name, "[unified][full-matrix][async][f32]") {                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::edge_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<float>(backend, unified::support::async_input_values<float>(size));                                                              \
  }

DECLARE_ASYNC_F32_FULL("copy", unified::async::copy)
DECLARE_ASYNC_F32_FULL("reduce", unified::async::reduce)
DECLARE_ASYNC_F32_FULL("scan", unified::async::scan)
DECLARE_ASYNC_F32_FULL("sort", unified::async::sort)
DECLARE_ASYNC_F32_FULL("future_chain", unified::async::future_chain)
DECLARE_ASYNC_F32_FULL("event_chain", unified::async::event_chain)
DECLARE_ASYNC_F32_FULL("interleaved_streams", unified::async::interleaved_streams)

#undef DECLARE_ASYNC_F32_FULL

#define DECLARE_ASYNC_F64_FULL(name, check)                                                                                                \
  TEST_CASE("async/f64/full/" name, "[unified][full-matrix][async][f64]") {                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::edge_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<double>(backend, unified::support::async_input_values<double>(size));                                                            \
  }

DECLARE_ASYNC_F64_FULL("copy", unified::async::copy)
DECLARE_ASYNC_F64_FULL("reduce", unified::async::reduce)
DECLARE_ASYNC_F64_FULL("scan", unified::async::scan)
DECLARE_ASYNC_F64_FULL("sort", unified::async::sort)
DECLARE_ASYNC_F64_FULL("future_chain", unified::async::future_chain)
DECLARE_ASYNC_F64_FULL("event_chain", unified::async::event_chain)
DECLARE_ASYNC_F64_FULL("interleaved_streams", unified::async::interleaved_streams)

#undef DECLARE_ASYNC_F64_FULL

#define DECLARE_HIP_ASYNC_I8_FULL(name, check)                                                                                             \
  TEST_CASE("hip_async/i8/full/" name, "[unified][full-matrix][async][i8]") {                                                              \
    const auto size = GENERATE(Catch::Generators::from_range(unified::hip_scan_sizes));                                                    \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<int8_t>(backend, size);                                                                                                          \
  }

DECLARE_HIP_ASYNC_I8_FULL("inclusive_counting", unified::hip_async::inclusive_counting)
DECLARE_HIP_ASYNC_I8_FULL("exclusive_counting", unified::hip_async::exclusive_counting)
DECLARE_HIP_ASYNC_I8_FULL("inclusive_discard", unified::hip_async::inclusive_discard)
DECLARE_HIP_ASYNC_I8_FULL("exclusive_discard", unified::hip_async::exclusive_discard)
DECLARE_HIP_ASYNC_I8_FULL("inclusive_stateful", unified::hip_async::inclusive_stateful)
DECLARE_HIP_ASYNC_I8_FULL("exclusive_stateful", unified::hip_async::exclusive_stateful)

#undef DECLARE_HIP_ASYNC_I8_FULL

#define DECLARE_HIP_ASYNC_I32_COMPACT(name, check)                                                                                         \
  TEST_CASE("hip_async/i32/compact/" name, "[unified][compact-matrix][async][i32]") {                                                      \
    const auto size = GENERATE(Catch::Generators::from_range(unified::compact_hip_scan_sizes));                                            \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<int32_t>(backend, size);                                                                                                         \
  }

DECLARE_HIP_ASYNC_I32_COMPACT("inclusive_counting", unified::hip_async::inclusive_counting)
DECLARE_HIP_ASYNC_I32_COMPACT("exclusive_counting", unified::hip_async::exclusive_counting)
DECLARE_HIP_ASYNC_I32_COMPACT("inclusive_discard", unified::hip_async::inclusive_discard)
DECLARE_HIP_ASYNC_I32_COMPACT("exclusive_discard", unified::hip_async::exclusive_discard)
DECLARE_HIP_ASYNC_I32_COMPACT("inclusive_stateful", unified::hip_async::inclusive_stateful)
DECLARE_HIP_ASYNC_I32_COMPACT("exclusive_stateful", unified::hip_async::exclusive_stateful)

#undef DECLARE_HIP_ASYNC_I32_COMPACT

#define DECLARE_HIP_ASYNC_I32_FULL(name, check)                                                                                            \
  TEST_CASE("hip_async/i32/full/" name, "[unified][full-matrix][async][i32]") {                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::hip_scan_sizes));                                                    \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<int32_t>(backend, size);                                                                                                         \
  }

DECLARE_HIP_ASYNC_I32_FULL("inclusive_counting", unified::hip_async::inclusive_counting)
DECLARE_HIP_ASYNC_I32_FULL("exclusive_counting", unified::hip_async::exclusive_counting)
DECLARE_HIP_ASYNC_I32_FULL("inclusive_discard", unified::hip_async::inclusive_discard)
DECLARE_HIP_ASYNC_I32_FULL("exclusive_discard", unified::hip_async::exclusive_discard)
DECLARE_HIP_ASYNC_I32_FULL("inclusive_stateful", unified::hip_async::inclusive_stateful)
DECLARE_HIP_ASYNC_I32_FULL("exclusive_stateful", unified::hip_async::exclusive_stateful)

#undef DECLARE_HIP_ASYNC_I32_FULL

#define DECLARE_HIP_ASYNC_I64_FULL(name, check)                                                                                            \
  TEST_CASE("hip_async/i64/full/" name, "[unified][full-matrix][async][i64]") {                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::hip_scan_sizes));                                                    \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<int64_t>(backend, size);                                                                                                         \
  }

DECLARE_HIP_ASYNC_I64_FULL("inclusive_counting", unified::hip_async::inclusive_counting)
DECLARE_HIP_ASYNC_I64_FULL("exclusive_counting", unified::hip_async::exclusive_counting)
DECLARE_HIP_ASYNC_I64_FULL("inclusive_discard", unified::hip_async::inclusive_discard)
DECLARE_HIP_ASYNC_I64_FULL("exclusive_discard", unified::hip_async::exclusive_discard)
DECLARE_HIP_ASYNC_I64_FULL("inclusive_stateful", unified::hip_async::inclusive_stateful)
DECLARE_HIP_ASYNC_I64_FULL("exclusive_stateful", unified::hip_async::exclusive_stateful)

#undef DECLARE_HIP_ASYNC_I64_FULL

#define DECLARE_HIP_ASYNC_F32_COMPACT(name, check)                                                                                         \
  TEST_CASE("hip_async/f32/compact/" name, "[unified][compact-matrix][async][f32]") {                                                      \
    const auto size = GENERATE(Catch::Generators::from_range(unified::compact_hip_scan_sizes));                                            \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<float>(backend, size);                                                                                                           \
  }

DECLARE_HIP_ASYNC_F32_COMPACT("inclusive_counting", unified::hip_async::inclusive_counting)
DECLARE_HIP_ASYNC_F32_COMPACT("exclusive_counting", unified::hip_async::exclusive_counting)
DECLARE_HIP_ASYNC_F32_COMPACT("inclusive_discard", unified::hip_async::inclusive_discard)
DECLARE_HIP_ASYNC_F32_COMPACT("exclusive_discard", unified::hip_async::exclusive_discard)
DECLARE_HIP_ASYNC_F32_COMPACT("inclusive_stateful", unified::hip_async::inclusive_stateful)
DECLARE_HIP_ASYNC_F32_COMPACT("exclusive_stateful", unified::hip_async::exclusive_stateful)

#undef DECLARE_HIP_ASYNC_F32_COMPACT

#define DECLARE_HIP_ASYNC_F32_FULL(name, check)                                                                                            \
  TEST_CASE("hip_async/f32/full/" name, "[unified][full-matrix][async][f32]") {                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::hip_scan_sizes));                                                    \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<float>(backend, size);                                                                                                           \
  }

DECLARE_HIP_ASYNC_F32_FULL("inclusive_counting", unified::hip_async::inclusive_counting)
DECLARE_HIP_ASYNC_F32_FULL("exclusive_counting", unified::hip_async::exclusive_counting)
DECLARE_HIP_ASYNC_F32_FULL("inclusive_discard", unified::hip_async::inclusive_discard)
DECLARE_HIP_ASYNC_F32_FULL("exclusive_discard", unified::hip_async::exclusive_discard)
DECLARE_HIP_ASYNC_F32_FULL("inclusive_stateful", unified::hip_async::inclusive_stateful)
DECLARE_HIP_ASYNC_F32_FULL("exclusive_stateful", unified::hip_async::exclusive_stateful)

#undef DECLARE_HIP_ASYNC_F32_FULL

#define DECLARE_HIP_ASYNC_F64_FULL(name, check)                                                                                            \
  TEST_CASE("hip_async/f64/full/" name, "[unified][full-matrix][async][f64]") {                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::hip_scan_sizes));                                                    \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustAsync backend;                                                                                                 \
    check<double>(backend, size);                                                                                                          \
  }

DECLARE_HIP_ASYNC_F64_FULL("inclusive_counting", unified::hip_async::inclusive_counting)
DECLARE_HIP_ASYNC_F64_FULL("exclusive_counting", unified::hip_async::exclusive_counting)
DECLARE_HIP_ASYNC_F64_FULL("inclusive_discard", unified::hip_async::inclusive_discard)
DECLARE_HIP_ASYNC_F64_FULL("exclusive_discard", unified::hip_async::exclusive_discard)
DECLARE_HIP_ASYNC_F64_FULL("inclusive_stateful", unified::hip_async::inclusive_stateful)
DECLARE_HIP_ASYNC_F64_FULL("exclusive_stateful", unified::hip_async::exclusive_stateful)

#undef DECLARE_HIP_ASYNC_F64_FULL
