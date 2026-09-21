// Include-twice declaration list for per-type HIP async cases. The includer
// defines UNIFIED_HIP_ASYNC_TYPE, UNIFIED_HIP_ASYNC_NAME (string),
// UNIFIED_HIP_ASYNC_MATRIX (string), and UNIFIED_HIP_ASYNC_SIZES first.
#define UNIFIED_HIP_ASYNC_CASE(name, check)                                                                                                \
  TEST_CASE("hip_async/" UNIFIED_HIP_ASYNC_NAME "/" UNIFIED_HIP_ASYNC_MATRIX "/" name,                                                     \
            "[unified][" UNIFIED_HIP_ASYNC_MATRIX "-matrix][async][" UNIFIED_HIP_ASYNC_NAME "]") {                                         \
    const auto size = GENERATE(Catch::Generators::from_range(UNIFIED_HIP_ASYNC_SIZES));                                                   \
    CAPTURE(size);                                                                                                                         \
    unified::backend::Async backend;                                                                                                       \
    check<UNIFIED_HIP_ASYNC_TYPE>(backend, size);                                                                                          \
  }

UNIFIED_HIP_ASYNC_CASE("inclusive_counting", unified::hip_async::inclusive_counting)
UNIFIED_HIP_ASYNC_CASE("exclusive_counting", unified::hip_async::exclusive_counting)
UNIFIED_HIP_ASYNC_CASE("inclusive_discard", unified::hip_async::inclusive_discard)
UNIFIED_HIP_ASYNC_CASE("exclusive_discard", unified::hip_async::exclusive_discard)
UNIFIED_HIP_ASYNC_CASE("inclusive_stateful", unified::hip_async::inclusive_stateful)
UNIFIED_HIP_ASYNC_CASE("exclusive_stateful", unified::hip_async::exclusive_stateful)

#undef UNIFIED_HIP_ASYNC_CASE
