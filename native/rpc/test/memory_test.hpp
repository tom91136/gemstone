#pragma once
#include <algorithm>
#include <atomic>
#include <chrono>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <filesystem>
#include <fstream>
#include <future>
#include <iostream>
#include <iterator>
#include <stdexcept>
#include <string>
#include <thread>
#include <vector>

namespace memory_patterns {
constexpr unsigned Count = 4096;
struct Node {
  Node *next;
  uint32_t value;
  uint32_t padding[13];
};
static_assert(sizeof(Node) == 64);
inline void require(bool ok, const char *message) {
  if (!ok) throw std::runtime_error(message);
}
inline void check(int status) {
  if (status != 0) throw std::runtime_error("API status " + std::to_string(status));
}
inline void *ordinary(size_t size) {
  auto *p = std::aligned_alloc(64, (size + 63) / 64 * 64);
  if (!p) throw std::bad_alloc();
  return p;
}

// Release partially constructed cases on assertion/API failure as well as success.
template <class Backend> struct AllocationScope {
  Backend &backend;
  std::vector<void *> pointers;
  explicit AllocationScope(Backend &backend) : backend(backend) {}
  void *allocate(size_t size) {
    auto *p = backend.allocate(size);
    pointers.push_back(p);
    return p;
  }
  void release(void *p) {
    backend.release(p);
    pointers.erase(std::find(pointers.begin(), pointers.end(), p));
  }
  ~AllocationScope() {
    for (auto *p : pointers) {
      try {
        backend.release(p);
      } catch (...) {
      }
    }
  }
};

inline std::vector<char> image(const std::filesystem::path &directory, const char *name) {
  std::ifstream input(directory / name, std::ios::binary);
  require(input.good(), "cannot open native memory fixture");
  std::vector<char> bytes((std::istreambuf_iterator<char>(input)), {});
  bytes.push_back(0);
  return bytes;
}
} // namespace memory_patterns
