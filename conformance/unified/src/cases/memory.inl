#pragma once

#include <catch2/catch_test_macros.hpp>

#include "memory_support.hpp"

// The includer supplies execute(allocation, scenario).
#define UNIFIED_MEMORY_CASE(name, scenario, tags, ALLOCATION, ALLOCATION_TAGS)                                                             \
  TEST_CASE("memory/" ALLOCATION "/" name, "[unified][memory]" ALLOCATION_TAGS tags) {                                                     \
    try {                                                                                                                                  \
      execute(ALLOCATION, scenario);                                                                                                       \
      SUCCEED("memory scenario verified");                                                                                                 \
    } catch (const memory_patterns::Unsupported &error) {                                                                                  \
      SKIP(error.what());                                                                                                                  \
    }                                                                                                                                      \
  }

#define UNIFIED_MEMORY_CASES(ALLOCATION, ALLOCATION_TAGS)                                                                                  \
  UNIFIED_MEMORY_CASE("contiguous", 0, "", ALLOCATION, ALLOCATION_TAGS)                                                                    \
  UNIFIED_MEMORY_CASE("strided", 1, "", ALLOCATION, ALLOCATION_TAGS)                                                                       \
  UNIFIED_MEMORY_CASE("gather", 2, "", ALLOCATION, ALLOCATION_TAGS)                                                                        \
  UNIFIED_MEMORY_CASE("linked-4096", 3, "", ALLOCATION, ALLOCATION_TAGS)                                                                   \
  UNIFIED_MEMORY_CASE("same-queue-events", 4, "", ALLOCATION, ALLOCATION_TAGS)                                                             \
  UNIFIED_MEMORY_CASE("cross-queue-events", 5, "", ALLOCATION, ALLOCATION_TAGS)                                                            \
  UNIFIED_MEMORY_CASE("cpu-pointer-replacement", 6, "", ALLOCATION, ALLOCATION_TAGS)                                                       \
  UNIFIED_MEMORY_CASE("atomic-publication-32", 7, "", ALLOCATION, ALLOCATION_TAGS)                                                         \
  UNIFIED_MEMORY_CASE("fence-publication-32", 8, "", ALLOCATION, ALLOCATION_TAGS)                                                          \
  UNIFIED_MEMORY_CASE("atomic-return-values-32", 9, "", ALLOCATION, ALLOCATION_TAGS)                                                       \
  UNIFIED_MEMORY_CASE("failed-cas-32", 10, "", ALLOCATION, ALLOCATION_TAGS)                                                                \
  UNIFIED_MEMORY_CASE("atomic-publication-64", 11, "[full-matrix]", ALLOCATION, ALLOCATION_TAGS)                                           \
  UNIFIED_MEMORY_CASE("fence-publication-64", 12, "[full-matrix]", ALLOCATION, ALLOCATION_TAGS)                                            \
  UNIFIED_MEMORY_CASE("atomic-return-values-64", 13, "[full-matrix]", ALLOCATION, ALLOCATION_TAGS)                                         \
  UNIFIED_MEMORY_CASE("failed-cas-64", 14, "[full-matrix]", ALLOCATION, ALLOCATION_TAGS)
