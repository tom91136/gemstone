#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>

#include "backend_sycl.hpp"
#include "algorithm_cases.hpp"

#define UNIFIED_ALGORITHM_TYPE float
#define UNIFIED_ALGORITHM_NAME "f32"
#define UNIFIED_ALGORITHM_MATRIX "compact"
#define UNIFIED_ALGORITHM_SIZES unified::compact_algorithm_sizes
#include "cases/algorithms.inl"

#undef UNIFIED_ALGORITHM_MATRIX
#undef UNIFIED_ALGORITHM_SIZES
#define UNIFIED_ALGORITHM_MATRIX "full"
#define UNIFIED_ALGORITHM_SIZES unified::algorithm_sizes
#include "cases/algorithms.inl"
