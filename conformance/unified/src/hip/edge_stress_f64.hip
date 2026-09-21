#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>

#include "backend_thrust.hpp"
#include "stress_cases.hpp"

#undef UNIFIED_EDGE_TYPE
#undef UNIFIED_EDGE_NAME
#undef UNIFIED_EDGE_MATRIX
#undef UNIFIED_EDGE_SIZES
#undef UNIFIED_STRESS_SIZES
#define UNIFIED_EDGE_TYPE double
#define UNIFIED_EDGE_NAME "f64"
#define UNIFIED_EDGE_MATRIX "full"
#define UNIFIED_EDGE_SIZES unified::edge_sizes
#define UNIFIED_STRESS_SIZES unified::stress_sizes
#include "cases/edge_stress.inl"
