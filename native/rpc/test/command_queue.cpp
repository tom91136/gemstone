#include <atomic>
#include <chrono>
#include <condition_variable>
#include <cstdint>
#include <future>
#include <memory>
#include <mutex>
#include <stdexcept>
#include <thread>

#include <catch2/catch_test_macros.hpp>

#include <gemstone/command_queue.hpp>

TEST_CASE("Queued completion retains arguments and orders streams without blocking submission", "[queue]") {
  gemstone::CommandQueue queue(999);
  std::promise<void> release;
  std::promise<void> started;
  auto gate = release.get_future().share();
  auto reached = started.get_future();
  auto owner = std::make_shared<int>(41);
  std::weak_ptr<int> const lifetime = owner;
  int streamA{};
  int streamB{};
  int result{};
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

TEST_CASE("Queued failures reach synchronisation and later work still completes", "[queue]") {
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
  int blocking{};
  int independent{};
  queue.configure(&blocking, false);
  queue.configure(&independent, true);
  std::promise<void> release;
  std::promise<void> ran;
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

TEST_CASE("Unordered work runs between barriers and host completions release dependants", "[queue]") {
  gemstone::CommandQueue queue(999);
  int stream{};
  queue.configure(&stream, true);
  const auto gate = gemstone::CommandQueue::pending();
  std::atomic_int sequence{0};
  const auto blocked =
      queue.submit(&stream, [&] { return sequence.fetch_add(1) == 1 ? 0 : 1; }, {gate}, gemstone::CommandQueue::Order::Unordered);
  const auto free = queue.submit(&stream, [&] { return sequence.fetch_add(1) == 0 ? 0 : 1; }, {}, gemstone::CommandQueue::Order::Unordered);
  CHECK(queue.waitFor(free, 2'000'000'000) == 0);
  CHECK_FALSE(queue.ready(blocked));
  CHECK_FALSE(queue.waitFor(queue.snapshot(&stream), 1'000'000).has_value());
  const auto barrier = queue.submit(&stream, [&] { return sequence.load() == 2 ? 0 : 1; });
  queue.complete(gate, 0);
  CHECK(queue.wait(barrier) == 0);
  CHECK(queue.ready(blocked));
  CHECK(sequence == 2);
}

TEST_CASE("Blocking waits on a worker let later queued work run", "[queue]") {
  gemstone::CommandQueue queue(999);
  int first{};
  int second{};
  int third{};
  std::mutex mutex;
  std::condition_variable changed;
  int signalled = 0;
  const auto wait = [&] {
    return queue.blocking([&] {
      std::unique_lock lock(mutex);
      return changed.wait_for(lock, std::chrono::seconds(5), [&] { return signalled != 0; }) ? 0 : 1;
    });
  };
  const auto a = queue.submit(&first, wait);
  const auto b = queue.submit(&second, wait);
  queue.submit(&third, [&] {
    {
      std::scoped_lock const lock(mutex);
      signalled = 1;
    }
    changed.notify_all();
    return 0;
  });
  CHECK(queue.wait(a) == 0);
  CHECK(queue.wait(b) == 0);
}

TEST_CASE("Streams and events created by the queue order work and reject unknown handles", "[queue]") {
  gemstone::CommandQueue queue(999);
  auto *stream = queue.createStream(true);
  auto *event = queue.createEvent();
  CHECK(queue.hasStream(stream));
  CHECK(queue.hasStream(nullptr));
  CHECK_FALSE(queue.hasStream(event));
  CHECK(queue.eventTicket(event) == gemstone::CommandQueue::Ticket{});
  std::promise<void> release;
  auto gate = release.get_future().share();
  queue.submit(stream, [gate] { return gate.wait_for(std::chrono::seconds(5)) == std::future_status::ready ? 0 : 1; });
  REQUIRE(queue.recordEvent(event, stream));
  int other{};
  queue.configure(&other, true);
  REQUIRE(queue.waitEvent(&other, event));
  const auto after = queue.submit(&other, [] { return 0; });
  const auto ticket = queue.eventTicket(event).value_or(nullptr);
  REQUIRE(ticket);
  CHECK_FALSE(queue.ready(ticket));
  CHECK_FALSE(queue.ready(after));
  release.set_value();
  CHECK(queue.wait(after) == 0);
  CHECK(queue.ready(ticket));
  CHECK(queue.destroyEvent(event));
  CHECK_FALSE(queue.destroyEvent(event));
  CHECK_FALSE(queue.recordEvent(event, stream));
  CHECK_FALSE(queue.waitEvent(stream, event));
  CHECK_FALSE(queue.eventTicket(event));
  CHECK(queue.destroyStream(stream));
  CHECK_FALSE(queue.destroyStream(stream));
  auto *survivor = queue.createStream(false);
  auto *dropped = queue.createEvent();
  queue.reset();
  CHECK_FALSE(queue.hasStream(survivor));
  CHECK_FALSE(queue.eventTicket(dropped));
  CHECK(queue.hasStream(nullptr));
  CHECK_FALSE(queue.destroyStream(nullptr));
  CHECK_FALSE(queue.hasStream(stream));
}
