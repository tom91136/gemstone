#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>

#include "api_thrust.hpp"
#include "template_stress.hpp"

#define DECLARE_EDGE_F32_COMPACT(name, check)                                                                                              \
  TEST_CASE("edge/f32/compact/" name, "[unified][compact-matrix][edge][f32]") {                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::compact_edge_sizes));                                                \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustEdgeCases backend;                                                                                             \
    check<float>(backend, size);                                                                                                           \
  }

DECLARE_EDGE_F32_COMPACT("composed_iterator_reduce", unified::edge::composed_iterator_reduce)
DECLARE_EDGE_F32_COMPACT("biased_index_64", unified::edge::biased_index_64)
DECLARE_EDGE_F32_COMPACT("segments_singleton", unified::edge::segments_singleton)
DECLARE_EDGE_F32_COMPACT("segments_giant", unified::edge::segments_giant)
DECLARE_EDGE_F32_COMPACT("segments_boundary", unified::edge::segments_boundary)
DECLARE_EDGE_F32_COMPACT("inplace_scan_canary", unified::edge::inplace_scan_canary)
DECLARE_EDGE_F32_COMPACT("stable_sort_payload", unified::edge::stable_sort_payload)
DECLARE_EDGE_F32_COMPACT("dependency_diamond", unified::edge::dependency_diamond)
DECLARE_EDGE_F32_COMPACT("numeric_edge_roundtrip", unified::edge::numeric_edge_roundtrip)
DECLARE_EDGE_F32_COMPACT("primitive_block_scan", unified::edge::primitive_block_scan)
DECLARE_EDGE_F32_COMPACT("runtime_error_recover", unified::edge::runtime_error_recover)

#undef DECLARE_EDGE_F32_COMPACT

#define DECLARE_EDGE_F32_FULL(name, check)                                                                                                 \
  TEST_CASE("edge/f32/full/" name, "[unified][full-matrix][edge][f32]") {                                                                  \
    const auto size = GENERATE(Catch::Generators::from_range(unified::edge_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustEdgeCases backend;                                                                                             \
    check<float>(backend, size);                                                                                                           \
  }

DECLARE_EDGE_F32_FULL("composed_iterator_reduce", unified::edge::composed_iterator_reduce)
DECLARE_EDGE_F32_FULL("biased_index_64", unified::edge::biased_index_64)
DECLARE_EDGE_F32_FULL("segments_singleton", unified::edge::segments_singleton)
DECLARE_EDGE_F32_FULL("segments_giant", unified::edge::segments_giant)
DECLARE_EDGE_F32_FULL("segments_boundary", unified::edge::segments_boundary)
DECLARE_EDGE_F32_FULL("inplace_scan_canary", unified::edge::inplace_scan_canary)
DECLARE_EDGE_F32_FULL("stable_sort_payload", unified::edge::stable_sort_payload)
DECLARE_EDGE_F32_FULL("dependency_diamond", unified::edge::dependency_diamond)
DECLARE_EDGE_F32_FULL("numeric_edge_roundtrip", unified::edge::numeric_edge_roundtrip)
DECLARE_EDGE_F32_FULL("primitive_block_scan", unified::edge::primitive_block_scan)
DECLARE_EDGE_F32_FULL("runtime_error_recover", unified::edge::runtime_error_recover)

#undef DECLARE_EDGE_F32_FULL

#define DECLARE_STRESS_F32_COMPACT(name, check)                                                                                            \
  TEST_CASE("stress/f32/compact/" name, "[unified][compact-matrix][stress][f32]") {                                                        \
    const auto size = GENERATE(Catch::Generators::from_range(unified::compact_stress_sizes));                                              \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustEdgeCases backend;                                                                                             \
    check<float>(backend, size);                                                                                                           \
  }

DECLARE_STRESS_F32_COMPACT("dependency_diamond", unified::stress::dependency_diamond)
DECLARE_STRESS_F32_COMPACT("primitive_block_scan", unified::stress::primitive_block_scan)
DECLARE_STRESS_F32_COMPACT("numeric_roundtrip", unified::stress::numeric_roundtrip)

#undef DECLARE_STRESS_F32_COMPACT

#define DECLARE_STRESS_F32_FULL(name, check)                                                                                               \
  TEST_CASE("stress/f32/full/" name, "[unified][full-matrix][stress][f32]") {                                                              \
    const auto size = GENERATE(Catch::Generators::from_range(unified::stress_sizes));                                                      \
    CAPTURE(size);                                                                                                                         \
    unified::backend::ThrustEdgeCases backend;                                                                                             \
    check<float>(backend, size);                                                                                                           \
  }

DECLARE_STRESS_F32_FULL("dependency_diamond", unified::stress::dependency_diamond)
DECLARE_STRESS_F32_FULL("primitive_block_scan", unified::stress::primitive_block_scan)
DECLARE_STRESS_F32_FULL("numeric_roundtrip", unified::stress::numeric_roundtrip)

#undef DECLARE_STRESS_F32_FULL
