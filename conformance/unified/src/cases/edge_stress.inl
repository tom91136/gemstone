#pragma once

#include "cases/grid.inl"
#include "stress_cases.hpp"

#define UNIFIED_EDGE_CASES(CASE, ...)                                                                                                      \
  CASE(composed_iterator_reduce, composedIteratorReduce, __VA_ARGS__)                                                                      \
  CASE(biased_index_64, biasedIndex64, __VA_ARGS__)                                                                                        \
  CASE(segments_singleton, segmentsSingleton, __VA_ARGS__)                                                                                 \
  CASE(segments_giant, segmentsGiant, __VA_ARGS__)                                                                                         \
  CASE(segments_boundary, segmentsBoundary, __VA_ARGS__)                                                                                   \
  CASE(inplace_scan_canary, inplaceScanCanary, __VA_ARGS__)                                                                                \
  CASE(stable_sort_payload, stableSortPayload, __VA_ARGS__)                                                                                \
  CASE(dependency_diamond, dependencyDiamond, __VA_ARGS__)                                                                                 \
  CASE(numeric_edge_roundtrip, numericEdgeRoundtrip, __VA_ARGS__)                                                                          \
  CASE(primitive_block_scan, primitiveBlockScan, __VA_ARGS__)                                                                              \
  CASE(runtime_error_recover, runtimeErrorRecover, __VA_ARGS__)

#define UNIFIED_STRESS_CASES(CASE, ...)                                                                                                    \
  CASE(dependency_diamond, dependencyDiamond, __VA_ARGS__)                                                                                 \
  CASE(primitive_block_scan, primitiveBlockScan, __VA_ARGS__)                                                                              \
  CASE(numeric_roundtrip, numericRoundtrip, __VA_ARGS__)

#define UNIFIED_EDGE_STRESS(T, TYPE, MATRIX)                                                                                               \
  UNIFIED_EDGE_CASES(UNIFIED_CASE, T, TYPE, MATRIX, edge, edge, EdgeSizes, EdgeCases)                                                      \
  UNIFIED_STRESS_CASES(UNIFIED_CASE, T, TYPE, MATRIX, stress, stress, StressSizes, EdgeCases)
