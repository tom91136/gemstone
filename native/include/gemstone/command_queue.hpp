#pragma once

#include <algorithm>
#include <condition_variable>
#include <deque>
#include <functional>
#include <map>
#include <memory>
#include <mutex>
#include <thread>
#include <vector>

#include <stdint.h>

namespace gemstone {

// A bounded pool executes independent streams. Completion handles retain event
// dependencies; legacy/default-stream ordering is explicit in the dependency graph.
class CommandQueue {
  struct Completion {
    bool done = false;
    bool join = false;
    int32_t status = 0;
    std::vector<std::shared_ptr<Completion>> dependencies;
  };

public:
  using Ticket = std::shared_ptr<Completion>;
  explicit CommandQueue(int32_t exceptionStatus) : exceptionStatus_(exceptionStatus) {
    for (unsigned i = 0; i < 2; ++i)
      workers_.emplace_back([this] { run(); });
  }
  ~CommandQueue() {
    {
      std::lock_guard lock(mutex_);
      stopping_ = true;
    }
    changed_.notify_all();
    for (auto &worker : workers_)
      worker.join();
  }
  CommandQueue(const CommandQueue &) = delete;
  CommandQueue &operator=(const CommandQueue &) = delete;

  void configure(const void *stream, bool nonblocking) {
    stream = streamKey(stream);
    std::lock_guard lock(mutex_);
    streams_[stream].nonblocking = nonblocking;
  }
  Ticket submit(const void *stream, std::function<int32_t()> work, std::vector<Ticket> dependencies = {}) {
    stream = streamKey(stream);
    std::lock_guard lock(mutex_);
    auto &target = streams_[stream];
    if (target.tail) dependencies.push_back(target.tail);
    if (!stream) {
      for (const auto &[key, value] : streams_)
        if (key && !value.nonblocking && value.tail) dependencies.push_back(value.tail);
    } else if (!target.nonblocking && streams_[nullptr].tail) dependencies.push_back(streams_[nullptr].tail);
    auto ticket = std::make_shared<Completion>();
    ticket->dependencies = std::move(dependencies);
    jobs_.push_back({ticket, std::move(work)});
    target.tail = ticket;
    changed_.notify_all();
    return ticket;
  }
  Ticket snapshot(const void *stream) {
    stream = streamKey(stream);
    std::lock_guard lock(mutex_);
    if (!stream) {
      auto result = std::make_shared<Completion>();
      result->join = true;
      for (const auto &[_, value] : streams_)
        if (!value.nonblocking && value.tail) result->dependencies.push_back(value.tail);
      return result;
    }
    const auto found = streams_.find(stream);
    return found == streams_.end() ? Ticket{} : found->second.tail;
  }
  Ticket snapshotAll() {
    std::lock_guard lock(mutex_);
    auto result = std::make_shared<Completion>();
    result->join = true;
    for (const auto &[_, value] : streams_)
      if (value.tail) result->dependencies.push_back(value.tail);
    return result;
  }
  bool ready(const Ticket &ticket) {
    std::lock_guard lock(mutex_);
    return readyLocked(ticket);
  }
  int32_t query(const Ticket &ticket, int32_t notReady) {
    std::lock_guard lock(mutex_);
    return readyLocked(ticket) ? statusLocked(ticket, false) : notReady;
  }
  void forget(const void *stream) {
    stream = streamKey(stream);
    std::lock_guard lock(mutex_);
    streams_.erase(stream);
  }
  int32_t wait(const Ticket &ticket) {
    if (onWorker()) return 0; // Queued copies reuse synchronous copy implementations.
    std::unique_lock lock(mutex_);
    changed_.wait(lock, [&] { return readyLocked(ticket); });
    return statusLocked(ticket, true);
  }
  int32_t waitAll() { return onWorker() ? 0 : wait(snapshotAll()); }
  bool onWorker() const { return activeQueue_ == this; }

private:
  static const void *streamKey(const void *stream) {
    if (stream == reinterpret_cast<const void *>(uintptr_t{1})) return nullptr;
    if (stream == reinterpret_cast<const void *>(uintptr_t{2})) {
      static thread_local const unsigned char perThread = 0;
      return &perThread;
    }
    return stream;
  }
  struct Stream {
    Ticket tail;
    bool nonblocking = false;
  };
  struct Job {
    Ticket ticket;
    std::function<int32_t()> work;
  };
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
  void run() {
    activeQueue_ = this;
    for (;;) {
      Job job;
      int32_t inherited = 0;
      {
        std::unique_lock lock(mutex_);
        const auto eligible = [&] {
          return std::find_if(jobs_.begin(), jobs_.end(), [](const Job &job) { return dependenciesReady(job.ticket); });
        };
        changed_.wait(lock, [&] { return (stopping_ && jobs_.empty()) || eligible() != jobs_.end(); });
        const auto next = eligible();
        if (next == jobs_.end()) return;
        job = std::move(*next);
        jobs_.erase(next);
        inherited = statusLocked(job.ticket, false);
      }
      int32_t status;
      try {
        status = job.work();
      } catch (...) {
        status = exceptionStatus_;
      }
      job.work = {};
      {
        std::lock_guard lock(mutex_);
        job.ticket->status = status == 0 ? inherited : status;
        job.ticket->done = true;
        job.ticket->dependencies.clear();
      }
      changed_.notify_all();
    }
  }
  inline static thread_local const CommandQueue *activeQueue_ = nullptr;
  const int32_t exceptionStatus_;
  std::mutex mutex_;
  std::condition_variable changed_;
  std::deque<Job> jobs_;
  std::map<const void *, Stream> streams_;
  bool stopping_ = false;
  std::vector<std::thread> workers_;
};

} // namespace gemstone
