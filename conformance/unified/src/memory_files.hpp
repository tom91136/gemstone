#pragma once
#include <string>
#include <vector>

#include "memory_image.hpp"
#if defined(UNIFIED_API_CUDA)
  #include "atomic_image.hpp"
  #include "contended_image.hpp"
  #include "fence_image.hpp"
#elif defined(UNIFIED_API_OPENCL) || defined(UNIFIED_API_LEVELZERO)
  #include "memory64_image.hpp"
#endif
inline std::vector<char> image(const char *name) {
  auto select = [&]() -> std::vector<char> {
#if defined(UNIFIED_API_CUDA)
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
