#pragma once

#include <algorithm>
#include <chrono>
#include <condition_variable>
#include <deque>
#include <functional>
#include <map>
#include <memory>
#include <mutex>
#include <optional>
#include <thread>
#include <utility>
#include <vector>

#include <stdint.h>

namespace gemstone {

/** Saturating steady-clock deadline `nanoseconds` from now. */
inline std::chrono::steady_clock::time_point deadlineAfter(uint64_t nanoseconds) {
  const auto now = std::chrono::steady_clock::now();
  const auto room = std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::steady_clock::time_point::max() - now).count();
  return nanoseconds >= static_cast<uint64_t>(room) ? std::chrono::steady_clock::time_point::max()
                                                    : now + std::chrono::nanoseconds(nanoseconds);
}

// A bounded pool executes independent streams. Completion handles retain event
// dependencies; legacy/default-stream ordering is explicit in the dependency graph.
// Workers start with the first submission.
class CommandQueue {
  struct Completion {
    bool done = false;
    bool join = false;
    int32_t status = 0;
    std::vector<std::shared_ptr<Completion>> dependencies;
  };

public:
  using Ticket = std::shared_ptr<Completion>;
  // Unordered work waits only for the last ordered submission; ordered work also waits for everything before it.
  enum class Order { Ordered, Unordered };

  explicit CommandQueue(int32_t exceptionStatus) : exceptionStatus(exceptionStatus) {}
  ~CommandQueue() { shutdown(); }

  /** Joins the workers and fails work whose dependencies remain pending. Later work runs on the thread that makes it
   * eligible.
   */
  void shutdown() {
    {
      std::lock_guard lock(mutex);
      stopping = true;
    }
    changed.notify_all();
    for (;;) {
      std::thread worker;
      {
        std::lock_guard lock(mutex);
        if (workers.empty()) break;
        worker = std::move(workers.back());
        workers.pop_back();
      }
      worker.join();
    }
    std::unique_lock lock(mutex);
    drainStopped(lock);
  }
  CommandQueue(const CommandQueue &) = delete;
  CommandQueue &operator=(const CommandQueue &) = delete;

  void configure(const void *stream, bool nonblocking) {
    stream = streamKey(stream);
    std::lock_guard lock(mutex);
    streams[stream].nonblocking = nonblocking;
  }
  Ticket submit(const void *stream, std::function<int32_t()> work, std::vector<Ticket> dependencies = {}, Order order = Order::Ordered) {
    std::unique_lock lock(mutex);
    auto ticket = submitLocked(streamKey(stream), std::move(work), std::move(dependencies), order);
    drainStopped(lock);
    return ticket;
  }
  Ticket snapshot(const void *stream) {
    stream = streamKey(stream);
    std::lock_guard lock(mutex);
    if (!stream) {
      auto result = std::make_shared<Completion>();
      result->join = true;
      for (const auto &[_, value] : streams)
        if (!value.nonblocking) frontier(value, result->dependencies);
      return result;
    }
    const auto found = streams.find(stream);
    if (found == streams.end()) return {};
    if (found->second.outstanding.empty()) return found->second.tail;
    auto result = std::make_shared<Completion>();
    result->join = true;
    frontier(found->second, result->dependencies);
    return result;
  }
  Ticket snapshotAll() {
    std::lock_guard lock(mutex);
    auto result = std::make_shared<Completion>();
    result->join = true;
    for (const auto &[_, value] : streams)
      frontier(value, result->dependencies);
    return result;
  }
  bool ready(const Ticket &ticket) {
    std::lock_guard lock(mutex);
    return readyLocked(ticket);
  }
  int32_t query(const Ticket &ticket, int32_t notReady) {
    std::lock_guard lock(mutex);
    return readyLocked(ticket) ? statusLocked(ticket, false) : notReady;
  }
  void forget(const void *stream) {
    stream = streamKey(stream);
    std::lock_guard lock(mutex);
    streams.erase(stream);
  }
  int32_t wait(const Ticket &ticket) {
    if (onWorker()) return 0; // Queued copies reuse synchronous copy implementations.
    return await(ticket);
  }
  /** Waits at most `nanoseconds`; `UINT64_MAX` waits indefinitely. Empty when the ticket is still pending. */
  std::optional<int32_t> waitFor(const Ticket &ticket, uint64_t nanoseconds) {
    if (onWorker() || nanoseconds == UINT64_MAX) return wait(ticket);
    std::unique_lock lock(mutex);
    if (!changed.wait_until(lock, deadlineAfter(nanoseconds), [&] { return readyLocked(ticket); })) return std::nullopt;
    return statusLocked(ticket, true);
  }
  int32_t waitAll() { return onWorker() ? 0 : wait(snapshotAll()); }
  bool onWorker() const { return activeQueue == this; }

  /** A ticket completed by the host rather than by queued work. */
  Ticket pending() { return std::make_shared<Completion>(); }
  void complete(const Ticket &ticket, int32_t status) {
    {
      std::unique_lock lock(mutex);
      ticket->status = status;
      ticket->done = true;
      drainStopped(lock);
    }
    changed.notify_all();
  }

  /** Runs a wait that may depend on later queued work; another worker covers for this one meanwhile. */
  template <class Wait> decltype(auto) blocking(Wait &&wait) {
    if (!onWorker()) return std::forward<Wait>(wait)();
    {
      std::lock_guard lock(mutex);
      if (!stopping && workers.size() - blocked <= Concurrency) workers.emplace_back([this] { run(); });
      ++blocked;
    }
    struct Unblock {
      CommandQueue &queue;
      ~Unblock() {
        std::lock_guard lock(queue.mutex);
        --queue.blocked;
      }
    } unblock{*this};
    return std::forward<Wait>(wait)();
  }
  /** Like `wait`, but a worker also waits while another covers for it. */
  int32_t waitCovered(const Ticket &ticket) {
    return blocking([&] { return await(ticket); });
  }

  void *createStream(bool nonblocking) {
    std::lock_guard lock(mutex);
    auto *stream = mintHandle();
    auto &created = streams[stream];
    created.created = true;
    created.nonblocking = nonblocking;
    return stream;
  }
  /** False for streams this queue did not create, including the special legacy and per-thread streams. */
  bool destroyStream(const void *stream) {
    std::lock_guard lock(mutex);
    const auto found = streams.find(stream);
    if (found == streams.end() || !found->second.created) return false;
    streams.erase(found);
    return true;
  }
  bool hasStream(const void *stream) {
    stream = streamKey(stream);
    if (!stream || stream == &perThreadStream()) return true;
    std::lock_guard lock(mutex);
    const auto found = streams.find(stream);
    return found != streams.end() && found->second.created;
  }

  /** Destroys every stream and event this queue created; queued work keeps its own references. */
  void reset() {
    std::lock_guard lock(mutex);
    std::erase_if(streams, [](const auto &entry) { return entry.second.created; });
    events.clear();
  }

  void *createEvent() {
    std::lock_guard lock(mutex);
    auto *event = mintHandle();
    events.emplace(event, Ticket{});
    return event;
  }
  bool destroyEvent(const void *event) {
    std::lock_guard lock(mutex);
    return events.erase(event) == 1;
  }
  bool recordEvent(const void *event, const void *stream) {
    std::unique_lock lock(mutex);
    const auto found = events.find(event);
    if (found == events.end()) return false;
    found->second = submitLocked(streamKey(stream), [] { return 0; }, {}, Order::Ordered);
    drainStopped(lock);
    return true;
  }
  /** The completion of the event's latest record; empty when the event is unknown. */
  std::optional<Ticket> eventTicket(const void *event) {
    std::lock_guard lock(mutex);
    const auto found = events.find(event);
    if (found == events.end()) return std::nullopt;
    return found->second;
  }
  bool waitEvent(const void *stream, const void *event) {
    std::unique_lock lock(mutex);
    const auto found = events.find(event);
    if (found == events.end()) return false;
    submitLocked(streamKey(stream), [] { return 0; }, {found->second}, Order::Ordered);
    drainStopped(lock);
    return true;
  }

  /** Workers started by the first submission; blocking waits add covering workers. */
  static constexpr unsigned Concurrency = 2;

private:
  struct Stream {
    Ticket tail;
    std::vector<Ticket> outstanding;
    bool nonblocking = false;
    bool created = false;
  };
  struct Job {
    Ticket ticket;
    std::function<int32_t()> work;
  };
  static const unsigned char &perThreadStream() {
    static thread_local const unsigned char perThread = 0;
    return perThread;
  }
  static const void *streamKey(const void *stream) {
    if (stream == reinterpret_cast<const void *>(uintptr_t{1})) return nullptr;
    if (stream == reinterpret_cast<const void *>(uintptr_t{2})) return &perThreadStream();
    return stream;
  }
  int32_t await(const Ticket &ticket) {
    std::unique_lock lock(mutex);
    changed.wait(lock, [&] { return readyLocked(ticket); });
    return statusLocked(ticket, true);
  }
  void *mintHandle() { return reinterpret_cast<void *>(static_cast<uintptr_t>(nextHandle++)); }
  static void frontier(const Stream &stream, std::vector<Ticket> &into) {
    if (stream.tail) into.push_back(stream.tail);
    into.insert(into.end(), stream.outstanding.begin(), stream.outstanding.end());
  }
  Ticket submitLocked(const void *stream, std::function<int32_t()> work, std::vector<Ticket> dependencies, Order order) {
    auto &target = streams[stream];
    if (order == Order::Ordered) frontier(target, dependencies);
    else if (target.tail) dependencies.push_back(target.tail);
    if (!stream) {
      for (const auto &[key, value] : streams)
        if (key && !value.nonblocking) frontier(value, dependencies);
    } else if (!target.nonblocking) frontier(streams[nullptr], dependencies);
    auto ticket = std::make_shared<Completion>();
    ticket->dependencies = std::move(dependencies);
    if (!stopping && workers.empty())
      for (unsigned i = 0; i < Concurrency; ++i)
        workers.emplace_back([this] { run(); });
    jobs.push_back({ticket, std::move(work)});
    if (order == Order::Ordered) {
      target.tail = ticket;
      target.outstanding.clear();
    } else {
      std::erase_if(target.outstanding, [](const Ticket &value) { return value->done; });
      target.outstanding.push_back(ticket);
    }
    changed.notify_all();
    return ticket;
  }
  static bool readyLocked(const Ticket &ticket) { return !ticket || ticket->done || (ticket->join && dependenciesReady(ticket)); }
  static bool dependenciesReady(const Ticket &ticket) {
    return std::all_of(ticket->dependencies.begin(), ticket->dependencies.end(), readyLocked);
  }
  static int32_t statusLocked(const Ticket &ticket, bool consume) {
    if (!ticket) return 0;
    auto status = ticket->status;
    for (const auto &dependency : ticket->dependencies) {
      const auto error = statusLocked(dependency, consume);
      if (status == 0) status = error;
    }
    if (consume) ticket->status = 0;
    return status;
  }
  std::deque<Job>::iterator eligibleLocked() {
    return std::find_if(jobs.begin(), jobs.end(), [](const Job &job) { return dependenciesReady(job.ticket); });
  }
  // Runs one taken job as a worker, releasing the lock meanwhile.
  void execute(Job job, std::unique_lock<std::mutex> &lock) {
    const int32_t inherited = statusLocked(job.ticket, false);
    lock.unlock();
    const auto *previous = std::exchange(activeQueue, this);
    int32_t status;
    try {
      status = job.work();
    } catch (...) {
      status = exceptionStatus;
    }
    job.work = {};
    activeQueue = previous;
    lock.lock();
    job.ticket->status = status == 0 ? inherited : status;
    job.ticket->done = true;
    job.ticket->dependencies.clear();
    lock.unlock();
    changed.notify_all();
    lock.lock();
  }
  // Without workers, eligible work runs on the calling thread and the rest can never start.
  void drainStopped(std::unique_lock<std::mutex> &lock) {
    if (!stopping || !workers.empty()) return;
    for (auto next = eligibleLocked(); next != jobs.end(); next = eligibleLocked()) {
      Job job = std::move(*next);
      jobs.erase(next);
      execute(std::move(job), lock);
    }
    auto abandoned = std::exchange(jobs, {});
    for (auto &job : abandoned) {
      job.ticket->status = exceptionStatus;
      job.ticket->done = true;
      job.ticket->dependencies.clear();
    }
    lock.unlock();
    changed.notify_all();
    abandoned.clear();
    lock.lock();
  }
  void run() {
    activeQueue = this;
    std::unique_lock lock(mutex);
    for (;;) {
      // Stopping abandons work that waits on host completions which never arrive.
      changed.wait(lock, [&] { return stopping || eligibleLocked() != jobs.end(); });
      const auto next = eligibleLocked();
      if (next == jobs.end()) return;
      Job job = std::move(*next);
      jobs.erase(next);
      execute(std::move(job), lock);
    }
  }
  inline static thread_local const CommandQueue *activeQueue = nullptr;
  const int32_t exceptionStatus;
  std::mutex mutex;
  std::condition_variable changed;
  std::deque<Job> jobs;
  std::map<const void *, Stream> streams;
  std::map<const void *, Ticket> events;
  uint64_t nextHandle = 0x400000000;
  unsigned blocked = 0;
  bool stopping = false;
  std::vector<std::thread> workers;
};

} // namespace gemstone
