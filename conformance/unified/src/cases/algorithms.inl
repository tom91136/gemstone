#pragma once

#include "algorithm_cases.hpp"
#include "cases/grid.inl"

#define UNIFIED_ALGORITHM_CASES(CASE, ...)                                                                                                 \
  CASE(copy, copy, __VA_ARGS__)                                                                                                            \
  CASE(copy_if, copyIf, __VA_ARGS__)                                                                                                       \
  CASE(copy_n, copyN, __VA_ARGS__)                                                                                                         \
  CASE(uninitialized_copy, uninitializedCopy, __VA_ARGS__)                                                                                 \
  CASE(uninitialized_fill, uninitializedFill, __VA_ARGS__)                                                                                 \
  CASE(fill, fill, __VA_ARGS__)                                                                                                            \
  CASE(generate, generate, __VA_ARGS__)                                                                                                    \
  CASE(for_each, forEach, __VA_ARGS__)                                                                                                     \
  CASE(transform_unary, transformUnary, __VA_ARGS__)                                                                                       \
  CASE(transform_binary, transformBinary, __VA_ARGS__)                                                                                     \
  CASE(replace_if, replaceIf, __VA_ARGS__)                                                                                                 \
  CASE(replace_copy_if, replaceCopyIf, __VA_ARGS__)                                                                                        \
  CASE(remove_if, removeIf, __VA_ARGS__)                                                                                                   \
  CASE(remove_copy_if, removeCopyIf, __VA_ARGS__)                                                                                          \
  CASE(reverse, reverse, __VA_ARGS__)                                                                                                      \
  CASE(reverse_copy, reverseCopy, __VA_ARGS__)                                                                                             \
  CASE(shuffle, shuffle, __VA_ARGS__)                                                                                                      \
  CASE(swap_ranges, swapRanges, __VA_ARGS__)                                                                                               \
  CASE(reduce, reduce, __VA_ARGS__)                                                                                                        \
  CASE(transform_reduce, transformReduce, __VA_ARGS__)                                                                                     \
  CASE(inner_product, innerProduct, __VA_ARGS__)                                                                                           \
  CASE(inclusive_scan, inclusiveScan, __VA_ARGS__)                                                                                         \
  CASE(exclusive_scan, exclusiveScan, __VA_ARGS__)                                                                                         \
  CASE(transform_inclusive_scan, transformInclusiveScan, __VA_ARGS__)                                                                      \
  CASE(transform_exclusive_scan, transformExclusiveScan, __VA_ARGS__)                                                                      \
  CASE(adjacent_difference, adjacentDifference, __VA_ARGS__)                                                                               \
  CASE(sort, sort, __VA_ARGS__)                                                                                                            \
  CASE(stable_sort, stableSort, __VA_ARGS__)                                                                                               \
  CASE(sort_descending, sortDescending, __VA_ARGS__)                                                                                       \
  CASE(sort_by_key, sortByKey, __VA_ARGS__)                                                                                                \
  CASE(stable_sort_by_key, stableSortByKey, __VA_ARGS__)                                                                                   \
  CASE(unique, unique, __VA_ARGS__)                                                                                                        \
  CASE(unique_copy, uniqueCopy, __VA_ARGS__)                                                                                               \
  CASE(count, count, __VA_ARGS__)                                                                                                          \
  CASE(count_if, countIf, __VA_ARGS__)                                                                                                     \
  CASE(find, find, __VA_ARGS__)                                                                                                            \
  CASE(find_if, findIf, __VA_ARGS__)                                                                                                       \
  CASE(all_of, allOf, __VA_ARGS__)                                                                                                         \
  CASE(any_of, anyOf, __VA_ARGS__)                                                                                                         \
  CASE(none_of, noneOf, __VA_ARGS__)                                                                                                       \
  CASE(equal, equal, __VA_ARGS__)                                                                                                          \
  CASE(mismatch, mismatch, __VA_ARGS__)                                                                                                    \
  CASE(lower_bound, lowerBound, __VA_ARGS__)                                                                                               \
  CASE(upper_bound, upperBound, __VA_ARGS__)                                                                                               \
  CASE(binary_search, binarySearch, __VA_ARGS__)                                                                                           \
  CASE(equal_range, equalRange, __VA_ARGS__)                                                                                               \
  CASE(merge, merge, __VA_ARGS__)                                                                                                          \
  CASE(partition, partition, __VA_ARGS__)                                                                                                  \
  CASE(stable_partition, stablePartition, __VA_ARGS__)                                                                                     \
  CASE(partition_copy, partitionCopy, __VA_ARGS__)                                                                                         \
  CASE(is_partitioned, isPartitioned, __VA_ARGS__)                                                                                         \
  CASE(is_partitioned_false, isPartitionedFalse, __VA_ARGS__)                                                                              \
  CASE(partition_point, partitionPoint, __VA_ARGS__)                                                                                       \
  CASE(set_union, setUnion, __VA_ARGS__)                                                                                                   \
  CASE(set_intersection, setIntersection, __VA_ARGS__)                                                                                     \
  CASE(set_difference, setDifference, __VA_ARGS__)                                                                                         \
  CASE(set_symmetric_difference, setSymmetricDifference, __VA_ARGS__)                                                                      \
  CASE(min_element, minElement, __VA_ARGS__)                                                                                               \
  CASE(max_element, maxElement, __VA_ARGS__)                                                                                               \
  CASE(minmax_element, minmaxElement, __VA_ARGS__)                                                                                         \
  CASE(is_sorted, isSorted, __VA_ARGS__)                                                                                                   \
  CASE(is_sorted_false, isSortedFalse, __VA_ARGS__)                                                                                        \
  CASE(is_sorted_until, isSortedUntil, __VA_ARGS__)                                                                                        \
  CASE(rotate, rotate, __VA_ARGS__)                                                                                                        \
  CASE(rotate_copy, rotateCopy, __VA_ARGS__)                                                                                               \
  CASE(includes, includes, __VA_ARGS__)                                                                                                    \
  CASE(includes_false, includesFalse, __VA_ARGS__)                                                                                         \
  CASE(gather, gather, __VA_ARGS__)                                                                                                        \
  CASE(scatter, scatter, __VA_ARGS__)                                                                                                      \
  CASE(reduce_by_key, reduceByKey, __VA_ARGS__)                                                                                            \
  CASE(inclusive_scan_by_key, inclusiveScanByKey, __VA_ARGS__)                                                                             \
  CASE(exclusive_scan_by_key, exclusiveScanByKey, __VA_ARGS__)                                                                             \
  CASE(unique_by_key, uniqueByKey, __VA_ARGS__)                                                                                            \
  CASE(merge_by_key, mergeByKey, __VA_ARGS__)                                                                                              \
  CASE(partial_sort, partialSort, __VA_ARGS__)                                                                                             \
  CASE(nth_element, nthElement, __VA_ARGS__)                                                                                               \
  CASE(inplace_merge, inplaceMerge, __VA_ARGS__)

#define UNIFIED_ALGORITHMS(T, TYPE, MATRIX)                                                                                                \
  UNIFIED_ALGORITHM_CASES(UNIFIED_CASE, T, TYPE, MATRIX, algorithms, algorithm, AlgorithmSizes, Algorithms)
