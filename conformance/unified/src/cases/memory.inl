// Include once per allocation. The includer defines UNIFIED_MEMORY_ALLOCATION
// and UNIFIED_MEMORY_TAGS and supplies execute(kind, scenario).
#define UNIFIED_MEMORY_CASE(tags, name, scenario)                                                                                          \
  TEST_CASE("memory/" UNIFIED_MEMORY_ALLOCATION "/" name, "[unified][memory]" UNIFIED_MEMORY_TAGS tags) {                                  \
    try {                                                                                                                                  \
      execute(UNIFIED_MEMORY_ALLOCATION, scenario);                                                                                        \
      SUCCEED("memory scenario verified");                                                                                                 \
    } catch (const memory_patterns::Unsupported &error) {                                                                                  \
      SKIP(error.what());                                                                                                                  \
    }                                                                                                                                      \
  }

UNIFIED_MEMORY_CASE("", "contiguous", 0)
UNIFIED_MEMORY_CASE("", "strided", 1)
UNIFIED_MEMORY_CASE("", "gather", 2)
UNIFIED_MEMORY_CASE("", "linked-4096", 3)
UNIFIED_MEMORY_CASE("", "same-queue-events", 4)
UNIFIED_MEMORY_CASE("", "cross-queue-events", 5)
UNIFIED_MEMORY_CASE("", "cpu-pointer-replacement", 6)
UNIFIED_MEMORY_CASE("", "atomic-publication-32", 7)
UNIFIED_MEMORY_CASE("", "fence-publication-32", 8)
UNIFIED_MEMORY_CASE("", "atomic-return-values-32", 9)
UNIFIED_MEMORY_CASE("", "failed-cas-32", 10)
UNIFIED_MEMORY_CASE("[full-matrix]", "atomic-publication-64", 11)
UNIFIED_MEMORY_CASE("[full-matrix]", "fence-publication-64", 12)
UNIFIED_MEMORY_CASE("[full-matrix]", "atomic-return-values-64", 13)
UNIFIED_MEMORY_CASE("[full-matrix]", "failed-cas-64", 14)

#undef UNIFIED_MEMORY_CASE
