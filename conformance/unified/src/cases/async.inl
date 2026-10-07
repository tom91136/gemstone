#pragma once

#include "async_cases.hpp"
#include "cases/grid.inl"

#define UNIFIED_ASYNC_CASES(CASE, ...)                                                                                                     \
  CASE(copy, copy, __VA_ARGS__)                                                                                                            \
  CASE(reduce, reduce, __VA_ARGS__)                                                                                                        \
  CASE(scan, scan, __VA_ARGS__)                                                                                                            \
  CASE(sort, sort, __VA_ARGS__)                                                                                                            \
  CASE(future_chain, futureChain, __VA_ARGS__)                                                                                             \
  CASE(event_chain, eventChain, __VA_ARGS__)                                                                                               \
  CASE(interleaved_streams, interleavedStreams, __VA_ARGS__)

#define UNIFIED_ASYNC(T, TYPE, MATRIX) UNIFIED_ASYNC_CASES(UNIFIED_CASE, T, TYPE, MATRIX, async, async, EdgeSizes, Async)
