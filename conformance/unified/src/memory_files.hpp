#pragma once
#include <cstdlib>
#include <string>
#include <string_view>
#include <vector>

#include "memory_image.hpp"
#if defined(UNIFIED_API_CUDA)
  #include "atomic_image.hpp"
  #include "atomic_sass_image.hpp"
  #include "contended_image.hpp"
  #include "contended_sass_image.hpp"
  #include "exchange_image.hpp"
  #include "exchange_sass_image.hpp"
  #include "fence_image.hpp"
  #include "fence_sass_image.hpp"
  #include "kernel_sass_image.hpp"
  #include "mma884_ptx_image.hpp"
  #include "mma884_sass_image.hpp"
#elif defined(UNIFIED_API_OPENCL) || defined(UNIFIED_API_LEVELZERO)
  #include "memory64_image.hpp"
#endif

inline std::vector<char> image(const char *name) {
  auto select = [&]() -> std::vector<char> {
#if defined(UNIFIED_API_CUDA)
    const char *code = std::getenv("CONFORMANCE_CUDA_CODE");
    if (code && std::string_view(code) == "sass") {
      if (std::string(name) == "mma884.ptx") return {mma884_sass_image.begin(), mma884_sass_image.end()};
      if (std::string(name) == "exchange.ptx") return {exchange_sass_image.begin(), exchange_sass_image.end()};
      if (std::string(name) == "atomic.ptx") return {atomic_sass_image.begin(), atomic_sass_image.end()};
      if (std::string(name) == "fence.ptx") return {fence_sass_image.begin(), fence_sass_image.end()};
      if (std::string(name) == "contended.ptx") return {contended_sass_image.begin(), contended_sass_image.end()};
      return {kernel_sass_image.begin(), kernel_sass_image.end()};
    }
    if (std::string(name) == "mma884.ptx") return {mma884_ptx_image.begin(), mma884_ptx_image.end()};
    if (std::string(name) == "exchange.ptx") return {exchange_image.begin(), exchange_image.end()};
    if (std::string(name) == "atomic.ptx") return {atomic_image.begin(), atomic_image.end()};
    if (std::string(name) == "fence.ptx") return {fence_image.begin(), fence_image.end()};
    if (std::string(name) == "contended.ptx") return {contended_image.begin(), contended_image.end()};
#elif defined(UNIFIED_API_OPENCL) || defined(UNIFIED_API_LEVELZERO)
    if (std::string(name) == "kernel64.spv") return {memory64_image.begin(), memory64_image.end()};
#endif
    return {memory_image.begin(), memory_image.end()};
  };
  auto bytes = select();
  bytes.push_back(0);
  return bytes;
}
