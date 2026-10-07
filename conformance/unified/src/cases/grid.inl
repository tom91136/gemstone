#pragma once

#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>

// Every type runs the full matrix; i32 and f32 also run the compact matrix.
#define UNIFIED_GRID_I8(CASES) CASES(int8_t, i8, full)
#define UNIFIED_GRID_I32(CASES) CASES(int32_t, i32, compact) CASES(int32_t, i32, full)
#define UNIFIED_GRID_I64(CASES) CASES(int64_t, i64, full)
#define UNIFIED_GRID_F32(CASES) CASES(float, f32, compact) CASES(float, f32, full)
#define UNIFIED_GRID_F64(CASES) CASES(double, f64, full)
#define UNIFIED_GRID(CASES)                                                                                                                \
  UNIFIED_GRID_I8(CASES) UNIFIED_GRID_I32(CASES) UNIFIED_GRID_I64(CASES) UNIFIED_GRID_F32(CASES) UNIFIED_GRID_F64(CASES)
#define UNIFIED_BLAS_GRID(CASES) CASES(F32, f32, compact) CASES(F32, f32, full) CASES(F64, f64, full) CASES(F16, f16, full)

// FAMILY names both the case namespace and the test name prefix; SIZES selects unified::<matrix>::<sizes>.
#define UNIFIED_CASE(name, function, T, TYPE, MATRIX, FAMILY, TAG, SIZES, BACKEND)                                                         \
  TEST_CASE(#FAMILY "/" #TYPE "/" #MATRIX "/" #name, "[unified][" #MATRIX "-matrix][" #TAG "][" #TYPE "]") {                               \
    const auto size = GENERATE(Catch::Generators::from_range(unified::MATRIX::SIZES));                                                     \
    CAPTURE(size);                                                                                                                         \
    unified::backend::BACKEND backend;                                                                                                     \
    if (!backend.template supports<T>(#name, size)) SKIP(#name " is unsupported for this type/device");                                    \
    unified::FAMILY::function<T>(backend, size);                                                                                           \
  }
