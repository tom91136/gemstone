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
#include <stdexcept>
#include <string>
#include <thread>
#include <vector>

namespace memory_patterns {
struct Unsupported : std::runtime_error {
  using std::runtime_error::runtime_error;
};
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

} // namespace memory_patterns
