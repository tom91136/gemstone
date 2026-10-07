#pragma once

#include "async_cases.hpp"
#include "cases/grid.inl"

#define UNIFIED_HIP_ASYNC_CASES(CASE, ...)                                                                                                 \
  CASE(inclusive_counting, inclusiveCounting, __VA_ARGS__)                                                                                 \
  CASE(exclusive_counting, exclusiveCounting, __VA_ARGS__)                                                                                 \
  CASE(inclusive_discard, inclusiveDiscard, __VA_ARGS__)                                                                                   \
  CASE(exclusive_discard, exclusiveDiscard, __VA_ARGS__)                                                                                   \
  CASE(inclusive_stateful, inclusiveStateful, __VA_ARGS__)                                                                                 \
  CASE(exclusive_stateful, exclusiveStateful, __VA_ARGS__)

#define UNIFIED_HIP_ASYNC(T, TYPE, MATRIX) UNIFIED_HIP_ASYNC_CASES(UNIFIED_CASE, T, TYPE, MATRIX, hip_async, async, HipScanSizes, Async)
