#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>

#include "backend_thrust.hpp"
#include "async_cases.hpp"

#undef UNIFIED_ASYNC_TYPE
#undef UNIFIED_ASYNC_NAME
#undef UNIFIED_ASYNC_MATRIX
#undef UNIFIED_ASYNC_SIZES
#define UNIFIED_ASYNC_TYPE int8_t
#define UNIFIED_ASYNC_NAME "i8"
#define UNIFIED_ASYNC_MATRIX "full"
#define UNIFIED_ASYNC_SIZES unified::edge_sizes
#include "cases/async.inl"

#undef UNIFIED_ASYNC_TYPE
#undef UNIFIED_ASYNC_NAME
#undef UNIFIED_ASYNC_MATRIX
#undef UNIFIED_ASYNC_SIZES
#define UNIFIED_ASYNC_TYPE int32_t
#define UNIFIED_ASYNC_NAME "i32"
#define UNIFIED_ASYNC_MATRIX "compact"
#define UNIFIED_ASYNC_SIZES unified::compact_edge_sizes
#include "cases/async.inl"

#undef UNIFIED_ASYNC_MATRIX
#undef UNIFIED_ASYNC_SIZES
#define UNIFIED_ASYNC_MATRIX "full"
#define UNIFIED_ASYNC_SIZES unified::edge_sizes
#include "cases/async.inl"

#undef UNIFIED_ASYNC_TYPE
#undef UNIFIED_ASYNC_NAME
#undef UNIFIED_ASYNC_MATRIX
#undef UNIFIED_ASYNC_SIZES
#define UNIFIED_ASYNC_TYPE int64_t
#define UNIFIED_ASYNC_NAME "i64"
#define UNIFIED_ASYNC_MATRIX "full"
#define UNIFIED_ASYNC_SIZES unified::edge_sizes
#include "cases/async.inl"

#undef UNIFIED_ASYNC_TYPE
#undef UNIFIED_ASYNC_NAME
#undef UNIFIED_ASYNC_MATRIX
#undef UNIFIED_ASYNC_SIZES
#define UNIFIED_ASYNC_TYPE float
#define UNIFIED_ASYNC_NAME "f32"
#define UNIFIED_ASYNC_MATRIX "compact"
#define UNIFIED_ASYNC_SIZES unified::compact_edge_sizes
#include "cases/async.inl"

#undef UNIFIED_ASYNC_MATRIX
#undef UNIFIED_ASYNC_SIZES
#define UNIFIED_ASYNC_MATRIX "full"
#define UNIFIED_ASYNC_SIZES unified::edge_sizes
#include "cases/async.inl"

#undef UNIFIED_ASYNC_TYPE
#undef UNIFIED_ASYNC_NAME
#undef UNIFIED_ASYNC_MATRIX
#undef UNIFIED_ASYNC_SIZES
#define UNIFIED_ASYNC_TYPE double
#define UNIFIED_ASYNC_NAME "f64"
#define UNIFIED_ASYNC_MATRIX "full"
#define UNIFIED_ASYNC_SIZES unified::edge_sizes
#include "cases/async.inl"
