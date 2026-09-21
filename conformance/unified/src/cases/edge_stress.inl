// Include-twice declaration list for per-type edge and stress cases. The
// includer defines UNIFIED_EDGE_TYPE, UNIFIED_EDGE_NAME (string),
// UNIFIED_EDGE_MATRIX (string), UNIFIED_EDGE_SIZES, and UNIFIED_STRESS_SIZES
// before including this file.
#define UNIFIED_EDGE_CASE(name, check)                                                                                                     \
  TEST_CASE("edge/" UNIFIED_EDGE_NAME "/" UNIFIED_EDGE_MATRIX "/" name,                                                                    \
            "[unified][" UNIFIED_EDGE_MATRIX "-matrix][edge][" UNIFIED_EDGE_NAME "]") {                                                    \
    const auto size = GENERATE(Catch::Generators::from_range(UNIFIED_EDGE_SIZES));                                                         \
    CAPTURE(size);                                                                                                                         \
    unified::backend::EdgeCases backend;                                                                                                   \
    check<UNIFIED_EDGE_TYPE>(backend, size);                                                                                               \
  }

UNIFIED_EDGE_CASE("composed_iterator_reduce", unified::edge::composed_iterator_reduce)
UNIFIED_EDGE_CASE("biased_index_64", unified::edge::biased_index_64)
UNIFIED_EDGE_CASE("segments_singleton", unified::edge::segments_singleton)
UNIFIED_EDGE_CASE("segments_giant", unified::edge::segments_giant)
UNIFIED_EDGE_CASE("segments_boundary", unified::edge::segments_boundary)
UNIFIED_EDGE_CASE("inplace_scan_canary", unified::edge::inplace_scan_canary)
UNIFIED_EDGE_CASE("stable_sort_payload", unified::edge::stable_sort_payload)
UNIFIED_EDGE_CASE("dependency_diamond", unified::edge::dependency_diamond)
UNIFIED_EDGE_CASE("numeric_edge_roundtrip", unified::edge::numeric_edge_roundtrip)
UNIFIED_EDGE_CASE("primitive_block_scan", unified::edge::primitive_block_scan)
UNIFIED_EDGE_CASE("runtime_error_recover", unified::edge::runtime_error_recover)

#undef UNIFIED_EDGE_CASE

#define UNIFIED_STRESS_CASE(name, check)                                                                                                   \
  TEST_CASE("stress/" UNIFIED_EDGE_NAME "/" UNIFIED_EDGE_MATRIX "/" name,                                                                  \
            "[unified][" UNIFIED_EDGE_MATRIX "-matrix][stress][" UNIFIED_EDGE_NAME "]") {                                                  \
    const auto size = GENERATE(Catch::Generators::from_range(UNIFIED_STRESS_SIZES));                                                       \
    CAPTURE(size);                                                                                                                         \
    unified::backend::EdgeCases backend;                                                                                                   \
    check<UNIFIED_EDGE_TYPE>(backend, size);                                                                                               \
  }

UNIFIED_STRESS_CASE("dependency_diamond", unified::stress::dependency_diamond)
UNIFIED_STRESS_CASE("primitive_block_scan", unified::stress::primitive_block_scan)
UNIFIED_STRESS_CASE("numeric_roundtrip", unified::stress::numeric_roundtrip)

#undef UNIFIED_STRESS_CASE
