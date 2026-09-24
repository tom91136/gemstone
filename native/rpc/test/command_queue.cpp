#include <atomic>
#include <chrono>
#include <future>
#include <memory>
#include <stdexcept>

#include <catch2/catch_test_macros.hpp>

#include <gemstone/command_queue.hpp>

TEST_CASE("Queued completion retains arguments and orders streams without blocking submission", "[queue]") {
  gemstone::CommandQueue queue(999);
  std::promise<void> release, started;
  auto gate = release.get_future().share();
  auto reached = started.get_future();
  auto owner = std::make_shared<int>(41);
  std::weak_ptr<int> lifetime = owner;
  int streamA{}, streamB{}, result{};
  const auto first = queue.submit(&streamA, [owner, gate, &started, &result] {
    started.set_value();
    if (gate.wait_for(std::chrono::seconds(5)) != std::future_status::ready) return 1;
    result = *owner;
    return 0;
  });
  owner.reset();
  const auto second = queue.submit(&streamB,
                                   [&result] {
                                     ++result;
                                     return 0;
                                   },
                                   {first});
  const bool began = reached.wait_for(std::chrono::seconds(2)) == std::future_status::ready;
  const bool pending = !queue.ready(first) && !queue.ready(second);
  const bool retained = !lifetime.expired();
  auto wait = std::async(std::launch::async, [&] { return queue.wait(second); });
  const bool blocked = wait.wait_for(std::chrono::milliseconds(25)) == std::future_status::timeout;
  release.set_value(); // Always unblock before an assertion can unwind captured storage.
  CHECK(wait.get() == 0);
  CHECK(began);
  CHECK(pending);
  CHECK(retained);
  CHECK(blocked);
  CHECK(result == 42);
  CHECK(lifetime.expired());
  CHECK(queue.snapshot(&streamA) == first);
  CHECK(queue.snapshot(&streamB) == second);
  CHECK(queue.ready(second));
}

TEST_CASE("Queued failures reach synchronization and later work still completes", "[queue]") {
  gemstone::CommandQueue queue(999);
  queue.submit(nullptr, []() -> int32_t { throw std::runtime_error("execution failed"); });
  std::atomic_int completed{0};
  queue.submit(nullptr, [&] {
    ++completed;
    return 0;
  });
  while (!queue.ready(queue.snapshotAll()))
    std::this_thread::yield();
  CHECK(queue.query(queue.snapshotAll(), 600) == 999);
  CHECK(queue.waitAll() == 999);
  CHECK(completed == 1);
  CHECK(queue.waitAll() == 0);
  queue.submit(nullptr, [&] { return queue.waitAll(); });
  CHECK(queue.waitAll() == 0);
}

TEST_CASE("Legacy stream dependencies leave nonblocking streams independently runnable", "[queue]") {
  gemstone::CommandQueue queue(999);
  int blocking{}, independent{};
  queue.configure(&blocking, false);
  queue.configure(&independent, true);
  std::promise<void> release, ran;
  auto gate = release.get_future().share();
  auto progressed = ran.get_future();
  std::atomic_int sequence{0};
  queue.submit(&blocking, [gate, &sequence] {
    if (gate.wait_for(std::chrono::seconds(5)) != std::future_status::ready) return 1;
    sequence.store(1);
    return 0;
  });
  const auto legacy = queue.submit(nullptr, [&sequence] { return sequence.exchange(2) == 1 ? 0 : 2; });
  queue.submit(&independent, [&ran] {
    ran.set_value();
    return 0;
  });
  const bool concurrent = progressed.wait_for(std::chrono::seconds(2)) == std::future_status::ready;
  const bool pending = !queue.ready(legacy);
  release.set_value();
  CHECK(queue.waitAll() == 0);
  CHECK(concurrent);
  CHECK(pending);
  CHECK(sequence == 2);
}
