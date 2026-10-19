#pragma once
#include <cstddef>

#include <catch2/catch_test_macros.hpp>
#include <sycl/sycl.hpp>

#include "test_support.hpp"

namespace sycl_usm {

namespace detail {
template <class T> T *track(T *pointer, const char *what, sycl::queue &queue, unified::test_runtime::Cleanup &cleanup) {
  {
    INFO(what);
    REQUIRE(pointer != nullptr);
  }
  cleanup.add([pointer, &queue] { sycl::free(pointer, queue); });
  return pointer;
}
} // namespace detail

template <class T> T *shared(sycl::queue &queue, unified::test_runtime::Cleanup &cleanup, size_t count) {
  return detail::track(sycl::malloc_shared<T>(count, queue), "shared USM allocation failed", queue, cleanup);
}

template <class T> T *device(sycl::queue &queue, unified::test_runtime::Cleanup &cleanup, size_t count) {
  return detail::track(sycl::malloc_device<T>(count, queue), "device USM allocation failed", queue, cleanup);
}

} // namespace sycl_usm
