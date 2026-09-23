#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>

#include "algorithm_cases.hpp"
#include "backend_thrust.hpp"

#define UNIFIED_ALGORITHM_TYPE double
#define UNIFIED_ALGORITHM_NAME "f64"
#define UNIFIED_ALGORITHM_MATRIX "full"
#define UNIFIED_ALGORITHM_SIZES unified::algorithm_sizes
#include "cases/algorithms.inl"
