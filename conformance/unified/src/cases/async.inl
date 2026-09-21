// Include-twice declaration list for per-type portable async cases. The
// includer defines UNIFIED_ASYNC_TYPE, UNIFIED_ASYNC_NAME (string),
// UNIFIED_ASYNC_MATRIX (string), and UNIFIED_ASYNC_SIZES first.
#define UNIFIED_ASYNC_CASE(name, check)                                                                                                    \
  TEST_CASE("async/" UNIFIED_ASYNC_NAME "/" UNIFIED_ASYNC_MATRIX "/" name,                                                                 \
            "[unified][" UNIFIED_ASYNC_MATRIX "-matrix][async][" UNIFIED_ASYNC_NAME "]") {                                                \
    const auto size = GENERATE(Catch::Generators::from_range(UNIFIED_ASYNC_SIZES));                                                       \
    CAPTURE(size);                                                                                                                         \
    unified::backend::Async backend;                                                                                                       \
    check<UNIFIED_ASYNC_TYPE>(backend, unified::support::async_input_values<UNIFIED_ASYNC_TYPE>(size));                                    \
  }

UNIFIED_ASYNC_CASE("copy", unified::async::copy)
UNIFIED_ASYNC_CASE("reduce", unified::async::reduce)
UNIFIED_ASYNC_CASE("scan", unified::async::scan)
UNIFIED_ASYNC_CASE("sort", unified::async::sort)
UNIFIED_ASYNC_CASE("future_chain", unified::async::future_chain)
UNIFIED_ASYNC_CASE("event_chain", unified::async::event_chain)
UNIFIED_ASYNC_CASE("interleaved_streams", unified::async::interleaved_streams)

#undef UNIFIED_ASYNC_CASE
