#pragma once

#include <array>
#include <chrono>
#include <future>

#include <catch2/catch_test_macros.hpp>

namespace gemstone_test {

// The queued read must finish before a synchronous free retires its storage.
// A bounded callback gate makes the lifetime assertion independent of kernel speed.
template <class Allocate, class Copy, class Callback, class Free>
void queuedLifetime(Allocate allocate, Copy copy, Callback callback, Free free) {
  std::array<uint32_t, 32> source{}, observed{};
  for (unsigned i = 0; i < source.size(); ++i)
    source[i] = 71 + i;
  auto memory = allocate(sizeof(source));
  copy(memory, source.data(), sizeof(source), true);
  std::promise<void> release, started;
  struct Gate {
    std::shared_future<void> release;
    std::promise<void> &started;
  } gate{release.get_future().share(), started};
  auto reached = started.get_future();
  callback(
      [](void *data) {
        auto &gate = *static_cast<Gate *>(data);
        gate.started.set_value();
        gate.release.wait_for(std::chrono::seconds(5));
      },
      &gate);
  copy(observed.data(), memory, sizeof(source), false);
  const bool began = reached.wait_for(std::chrono::seconds(2)) == std::future_status::ready;
  std::promise<void> freeing;
  auto entered = freeing.get_future();
  auto freed = std::async(std::launch::async, [&] {
    freeing.set_value();
    return free(memory);
  });
  entered.wait();
  const bool waited = freed.wait_for(std::chrono::milliseconds(50)) == std::future_status::timeout;
  release.set_value();
  CHECK(freed.get() == 0);
  CHECK(began);
  CHECK(waited);
  CHECK(observed == source);
}

} // namespace gemstone_test
