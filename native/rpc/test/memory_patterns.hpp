#pragma once
#include "memory_test.hpp"

namespace memory_patterns {
inline bool concurrencyMode() { return std::getenv("GEMSTONE_MEMORY_CONCURRENCY_DIRECTORY") != nullptr; }
inline std::string allocationKind(const char *fallback) {
  const auto *kind = std::getenv("GEMSTONE_MEMORY_ALLOCATION_KIND");
  return kind && std::string(kind) != "default" ? kind : fallback;
}

template <class Backend> void ordinaryAccess(Backend &backend, unsigned mode) {
  AllocationScope allocations(backend);
  const char *name = mode == 0 ? "contiguous" : mode == 1 ? "strided" : mode == 2 ? "gather" : "linked";
  const size_t inputSize = mode == 3 ? Count * sizeof(Node) : Count * 17 * sizeof(uint32_t);
  auto *storage = static_cast<unsigned char *>(allocations.allocate(inputSize + 128));
  auto *input = storage + 64; // Every kernel sees an interior pointer, including first use.
  auto *out = static_cast<uint32_t *>(allocations.allocate((Count + 2) * sizeof(uint32_t)));
  auto *indices = static_cast<uint32_t *>(allocations.allocate(Count * sizeof(uint32_t)));
  std::memset(storage, 0x5a, inputSize + 128);
  for (unsigned i = 0; i < Count; ++i)
    indices[i] = ((i * 2053) % Count) * 17;
  for (unsigned pass = 0; pass != 2; ++pass) {
    std::fill_n(out, Count + 2, 0xa5a5a5a5U);
    if (mode == 3) {
      auto *nodes = reinterpret_cast<Node *>(input);
      for (unsigned i = 0; i < Count; ++i) {
        auto &node = nodes[(i * 2053) % Count];
        node.next = i + 1 < Count ? &nodes[((i + 1) * 2053) % Count] : nullptr;
        node.value = i + 1 + pass;
      }
    } else {
      auto *values = reinterpret_cast<uint32_t *>(input);
      for (unsigned i = 0; i < Count * 17; ++i)
        values[i] = i * 3 + 7 + pass;
    }
    std::cout << "start " << name << " nodes/items=" << Count << " pass=" << pass << std::endl;
    backend.launch(input, out + 1, indices, mode, mode == 3 ? 1 : Count);
    if (mode == 3) require(out[1] == Count * (Count + 1) / 2 + Count * pass, "linked sum mismatch");
    else
      for (unsigned i = 0; i < Count; ++i) {
        const unsigned index = mode == 0 ? i : mode == 1 ? i * 17 : indices[i];
        require(out[i + 1] == index * 3 + 8 + pass, "regular/gather mismatch");
      }
    require(out[0] == 0xa5a5a5a5U && out[Count + 1] == 0xa5a5a5a5U, "output guard changed");
    for (unsigned i = 0; i < 64; ++i)
      require(storage[i] == 0x5a && storage[inputSize + 64 + i] == 0x5a, "input guard changed");
    std::cout << "verified " << name << " pass=" << pass << std::endl;
  }
  allocations.release(indices);
  allocations.release(out);
  allocations.release(storage);
}

// Publish through one atomic location and acknowledge through another. The
// compare-exchange must fail, return the published value and acquire its payload.
template <class Backend> int compareTraffic(Backend &backend) {
  using Word = uint32_t;
  AllocationScope allocations(backend);
  auto *input = static_cast<Word *>(allocations.allocate(128));
  auto *output = static_cast<Word *>(allocations.allocate(96 * sizeof(Word)));
  std::fill_n(input, 128 / sizeof(Word), Word{});
  std::fill_n(output, 96, Word{});
  std::cout << "atomic-input " << reinterpret_cast<uintptr_t>(input) << " " << sizeof(Word) << std::endl;
  std::atomic_bool cancelled{false};
  auto host = std::async(std::launch::async, [&] {
    auto published = std::atomic_ref<Word>(input[0]);
    auto acknowledged = std::atomic_ref<Word>(input[2]);
    const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(90);
    std::exception_ptr failure;
    for (unsigned round = 0; round < 32; ++round) {
      // High bits exercise unsigned values across the full word width.
      const Word value = (Word{1} << (sizeof(Word) * 8 - 1)) + 73 + round;
      const Word token = (Word{1} << (sizeof(Word) * 8 - 1)) + round + 1;
      input[1] = value;
      published.store(token, std::memory_order_release);
      while (acknowledged.load(std::memory_order_acquire) != token) {
        if (cancelled.load(std::memory_order_relaxed)) return;
        require(std::chrono::steady_clock::now() < deadline, "compare-exchange handshake stopped progressing");
        std::this_thread::yield();
      }
      try {
        require(output[3 * round] == value, "failed acquire CAS read a stale payload");
        require(output[3 * round + 1] == token, "failed CAS did not return the observed value");
        require(output[3 * round + 2] == 0, "mismatched compare-exchange reported success");
        require(published.load(std::memory_order_relaxed) == token, "failed CAS changed its target");
      } catch (...) {
        if (!failure) failure = std::current_exception();
      }
    }
    if (failure) std::rethrow_exception(failure);
  });
  try {
    backend.launch(input, reinterpret_cast<uint32_t *>(output), reinterpret_cast<uint32_t *>(input), sizeof(Word) == 4 ? 13 : 14, 1);
  } catch (...) {
    cancelled.store(true, std::memory_order_relaxed);
    host.wait();
    throw;
  }
  host.get();
  allocations.release(output);
  allocations.release(input);
  std::cout << "verified failed compare-exchange bits=" << sizeof(Word) * 8 << " rounds=32 atomic-locations=2" << std::endl;
  return 0;
}
// File rendezvous with a test-only RuntimeBackend wrapper. No timing-based races.
inline void checkpoint(const std::filesystem::path &directory, unsigned launch, const char *phase, const auto &action) {
  const auto suffix = std::to_string(launch) + "-" + phase;
  const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(30);
  while (!std::filesystem::exists(directory / ("device-" + suffix))) {
    std::ifstream failure(directory / ("failure-" + std::to_string(launch)));
    if (failure.good()) {
      std::string message;
      std::getline(failure, message);
      throw std::runtime_error(message);
    }
    require(std::chrono::steady_clock::now() < deadline, "device checkpoint timed out");
    std::this_thread::sleep_for(std::chrono::milliseconds(1));
  }
  std::exception_ptr failure;
  try {
    action();
  } catch (...) {
    failure = std::current_exception();
  }
  std::ofstream ready(directory / ("host-" + suffix));
  require(ready.good(), "cannot release device checkpoint");
  ready.close();
  if (failure) std::rethrow_exception(failure);
}
// Two CPU threads update the native counter while each device CAS is paused.
template <class Word> void forceCollisions(const std::filesystem::path &directory, unsigned launch, unsigned operation, Word &counter) {
  for (unsigned attempt = 0; attempt < 2; ++attempt) {
    const auto phase = "counter-" + std::to_string(operation) + "-" + std::to_string(attempt);
    checkpoint(directory, launch, phase.c_str(), [&] {
      const auto increment = [&] {
        for (unsigned add = 0; add < 1024; ++add)
          std::atomic_ref<Word>(counter).fetch_add(1, std::memory_order_relaxed);
      };
      std::thread other(increment);
      increment();
      other.join();
    });
  }
}
template <class Word> void publishCounter(const std::filesystem::path &directory, unsigned launch, Word *input) {
  std::ofstream counter(directory / ("counter-" + std::to_string(launch)));
  counter << reinterpret_cast<uintptr_t>(input + 2);
  require(counter.good(), "cannot publish contention address");
}
template <class Word, class Backend> void multiLaneAtomicCase(Backend &backend, unsigned launch) {
  const std::filesystem::path directory(std::getenv("GEMSTONE_MEMORY_CONCURRENCY_DIRECTORY"));
  constexpr Word step = 4097;
  AllocationScope allocations(backend);
  constexpr unsigned Lanes = 128;
  auto *input = static_cast<Word *>(allocations.allocate(128));
  auto *output = static_cast<Word *>(allocations.allocate(Lanes * sizeof(Word)));
  std::fill_n(input, 128 / sizeof(Word), Word{});
  std::fill_n(output, Lanes, Word{});
  std::cout << "atomic-input " << reinterpret_cast<uintptr_t>(input) << " " << sizeof(Word) << std::endl;
  auto host = std::async(std::launch::async, [&] {
    publishCounter(directory, launch, input);
    checkpoint(directory, launch, "before", [] {});
    for (unsigned operation = 0; operation < Lanes; ++operation)
      forceCollisions(directory, launch, operation, input[2]);
    checkpoint(directory, launch, "after", [] {});
  });
  backend.launch(input, reinterpret_cast<uint32_t *>(output), reinterpret_cast<uint32_t *>(input), sizeof(Word) == 4 ? 9 : 10, Lanes);
  host.get();
  require(input[2] == Word(Lanes) * step, "multi-workgroup RMW lost an update");
  std::sort(output, output + Lanes);
  for (unsigned lane = 0; lane < Lanes; ++lane)
    require(output[lane] == Word(lane) * step + step - 1, "multi-workgroup atomic returned a stale or duplicate old value");
  allocations.release(output);
  allocations.release(input);
  std::cout << "verified multi-workgroup atomics bits=" << sizeof(Word) * 8 << " lanes=128 groups=4" << std::endl;
}
template <class Word, class Backend> void atomicCase(Backend &backend, unsigned launch) {
  const std::filesystem::path directory(std::getenv("GEMSTONE_MEMORY_CONCURRENCY_DIRECTORY"));
  const bool fenced = std::getenv("GEMSTONE_MEMORY_FENCE_PROBE") != nullptr;
  constexpr Word step = 4097;
  AllocationScope allocations(backend);
  auto *input = static_cast<Word *>(allocations.allocate(128));
  auto *output = static_cast<Word *>(allocations.allocate(32 * sizeof(Word)));
  std::fill_n(input, 128 / sizeof(Word), Word{});
  std::fill_n(output, 32, Word{});
  std::cout << "atomic-input " << reinterpret_cast<uintptr_t>(input) << " " << sizeof(Word) << std::endl;
  std::atomic_bool cancelled{false};
  auto host = std::async(std::launch::async, [&] {
    publishCounter(directory, launch, input);
    checkpoint(directory, launch, "before", [] {});
    auto state = std::atomic_ref<Word>(input[0]);
    const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(90);
    std::exception_ptr failure;
    for (unsigned round = 0; round < 32; ++round) {
      input[1] = 73 + round;
      if (fenced) std::atomic_thread_fence(std::memory_order_release);
      state.store(1, fenced ? std::memory_order_relaxed : std::memory_order_release);
      forceCollisions(directory, launch, round, input[2]);
      while (state.load(fenced ? std::memory_order_relaxed : std::memory_order_acquire) != 2) {
        if (cancelled.load(std::memory_order_relaxed)) return;
        require(std::chrono::steady_clock::now() < deadline, "live host/device handshake timed out");
        std::this_thread::yield();
      }
      if (fenced) std::atomic_thread_fence(std::memory_order_acquire);
      try {
        require(input[1] == 2 * (73 + round), "GPU release did not publish payload");
        require(output[round] == 73 + round, "GPU acquire read stale CPU payload");
        require(std::atomic_ref<Word>(input[2]).load(std::memory_order_relaxed) == Word(round + 1) * step,
                "CPU/GPU atomic RMW lost an update");
      } catch (...) {
        if (!failure) failure = std::current_exception();
      }
    }
    checkpoint(directory, launch, "after", [] {});
    if (failure) std::rethrow_exception(failure);
  });
  try {
    backend.launch(input, reinterpret_cast<uint32_t *>(output), reinterpret_cast<uint32_t *>(input),
                   (sizeof(Word) == 4 ? 5 : 6) + (fenced ? 2 : 0), 1);
  } catch (...) {
    cancelled.store(true, std::memory_order_relaxed);
    host.wait();
    throw;
  }
  host.get();
  allocations.release(output);
  allocations.release(input);
  std::cout << "verified live system atomics bits=" << sizeof(Word) * 8 << " rounds=32 fences=" << fenced << std::endl;
}

template <class Backend> int atomicProbe(Backend &backend) {
  const char *bits = std::getenv("GEMSTONE_MEMORY_ATOMIC_BITS");
  if (std::getenv("GEMSTONE_MEMORY_MULTILANE_PROBE")) {
    if (bits && std::string(bits) == "64") multiLaneAtomicCase<uint64_t>(backend, 0);
    else multiLaneAtomicCase<uint32_t>(backend, 0);
    return 0;
  }
  if (bits && std::string(bits) == "64") atomicCase<uint64_t>(backend, 0);
  else if (bits && std::string(bits) == "32") atomicCase<uint32_t>(backend, 0);
  else {
    atomicCase<uint32_t>(backend, 0);
    atomicCase<uint64_t>(backend, 1);
  }
  return 0;
}
template <class Backend> int concurrent(Backend &backend) {
  if (!backend.concurrentCapabilities()) {
    std::cout << "implementation concurrency probe beyond advertised allocation capabilities" << std::endl;
  }
  const std::filesystem::path directory(std::getenv("GEMSTONE_MEMORY_CONCURRENCY_DIRECTORY"));
  auto *input = static_cast<uint32_t *>(backend.allocate(128));
  auto *storage = static_cast<unsigned char *>(backend.allocate(65536 + 128));
  unsigned launch = 0;
  for (unsigned layout = 0; layout < 3; ++layout) {
    // Neighbouring words, byte holes within a word, and a 64 KiB transfer boundary.
    const unsigned offset = layout == 0 ? 64 : layout == 1 ? 65 : 65535;
    auto *output = storage + offset;
    const unsigned hostOffset = layout == 0 ? 4 : 1;
    for (unsigned pass = 0; pass < 2; ++pass, ++launch) {
      std::memset(storage, 0x5a, 65536 + 128);
      std::fill_n(input, 32, 0U);
      input[0] = 41 + pass;
      auto host = std::async(std::launch::async, [&] {
        for (const char *phase : {"before", "after"})
          checkpoint(directory, launch, phase, [&] {
            require(input[0] == 41 + pass, "concurrent shared read changed");
            input[1] = 201 + pass; // Disjoint from the kernel read, same allocation/page.
            output[hostOffset] = static_cast<unsigned char>(101 + pass);
          });
      });
      backend.launch(input, reinterpret_cast<uint32_t *>(output), input, layout == 0 ? 0 : 4, 1);
      host.get();
      require(input[1] == 201 + pass, "CPU input update overwritten");
      require(output[hostOffset] == 101 + pass, "CPU disjoint output update overwritten");
      if (layout == 0) {
        uint32_t value{};
        std::memcpy(&value, output, sizeof(value));
        require(value == 42 + pass, "concurrent kernel word result");
      } else {
        require(output[0] == 42 + pass && output[2] == 43 + pass, "concurrent kernel byte results");
      }
      require(output[-1] == 0x5a && output[8] == 0x5a, "concurrent guard overwritten");
      std::cout << "verified concurrency layout=" << layout << " pass=" << pass << std::endl;
    }
  }
  backend.release(storage);
  backend.release(input);
  return 0;
}
// The submitting host thread must be able to publish a flag that the kernel awaits.
// A watchdog completes the handshake on a blocking implementation, so the red is bounded.
template <class Word, class Backend> int submission(Backend &backend) {
  require(backend.concurrentCapabilities(), "submission probe requires concurrent host storage");
  auto *input = static_cast<Word *>(backend.allocate(128));
  auto *output = static_cast<Word *>(backend.allocate(32 * sizeof(Word)));
  std::fill_n(input, 128 / sizeof(Word), Word{});
  std::fill_n(output, 32, Word{});
  std::cout << "atomic-input " << reinterpret_cast<uintptr_t>(input) << " " << sizeof(Word) << std::endl;
  auto handshake = [&] {
    auto state = std::atomic_ref<Word>(input[0]);
    const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(25);
    for (unsigned round = 0; round < 32; ++round) {
      input[1] = 73 + round;
      state.store(1, std::memory_order_release);
      while (state.load(std::memory_order_acquire) != 2) {
        require(std::chrono::steady_clock::now() < deadline, "post-submit host handshake stopped progressing");
        std::this_thread::yield();
      }
      require(input[1] == 2 * (73 + round) && output[round] == 73 + round, "post-submit publication was stale");
    }
  };
  std::atomic_bool returned{false};
  auto rescue = std::async(std::launch::async, [&] {
    const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(2);
    while (!returned.load(std::memory_order_acquire)) {
      if (std::chrono::steady_clock::now() >= deadline) {
        handshake();
        return true;
      }
      std::this_thread::yield();
    }
    return false;
  });
  backend.submit(input, reinterpret_cast<uint32_t *>(output), reinterpret_cast<uint32_t *>(input), sizeof(Word) == 4 ? 5 : 6, 1);
  returned.store(true, std::memory_order_release);
  const bool blocked = rescue.get();
  if (!blocked) handshake();
  backend.finish();
  require(input[2] == 32, "post-submit RMW result");
  backend.release(output);
  backend.release(input);
  require(!blocked, "kernel submission blocked on a host flag written after return");
  std::cout << "verified asynchronous submission bits=" << sizeof(Word) * 8 << " rounds=32" << std::endl;
  return 0;
}
// Hold the first kernel in a host handshake while a second queue must run.
// Rescue the first after two seconds so serialization produces a bounded red.
template <class Word, class Backend> int overlap(Backend &backend) {
  require(backend.concurrentCapabilities(), "overlap probe requires concurrent host storage");
  Word *input[2], *output[2];
  for (unsigned i = 0; i < 2; ++i) {
    input[i] = static_cast<Word *>(backend.allocate(128));
    output[i] = static_cast<Word *>(backend.allocate(32 * sizeof(Word)));
    std::fill_n(input[i], 128 / sizeof(Word), Word{});
    std::fill_n(output[i], 32, Word{});
    std::cout << "atomic-input " << reinterpret_cast<uintptr_t>(input[i]) << " " << sizeof(Word) << std::endl;
  }
  auto round = [&](unsigned i, unsigned n) {
    input[i][1] = 71 + i + n;
    auto flag = std::atomic_ref<Word>(input[i][0]);
    flag.store(1, std::memory_order_release);
    const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(25);
    while (flag.load(std::memory_order_acquire) != 2) {
      require(std::chrono::steady_clock::now() < deadline, "independent queue stopped progressing");
      std::this_thread::yield();
    }
    require(input[i][1] == 2 * (71 + i + n) && output[i][n] == 71 + i + n, "overlapping dispatch payload mismatch");
  };
  backend.overlapSubmit(0, input[0], reinterpret_cast<uint32_t *>(output[0]), sizeof(Word) == 4 ? 5 : 6);
  round(0, 0);
  // The consumer has cached the old payload and is still running. Only the
  // producer kernel supplies the next payload and release flag on a green run.
  std::atomic_ref<Word>(input[0][0]).store(0, std::memory_order_release);
  std::atomic_bool secondProgress{false};
  auto rescue = std::async(std::launch::async, [&] {
    const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(2);
    while (!secondProgress.load(std::memory_order_acquire)) {
      if (std::chrono::steady_clock::now() >= deadline) {
        for (unsigned n = 1; n < 32; ++n)
          round(0, n);
        return true;
      }
      std::this_thread::yield();
    }
    return false;
  });
  backend.overlapSubmit(1, input[0], reinterpret_cast<uint32_t *>(output[1]), sizeof(Word) == 4 ? 11 : 12);
  const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(25);
  while (std::atomic_ref<Word>(input[0][0]).load(std::memory_order_acquire) != 2) {
    require(std::chrono::steady_clock::now() < deadline, "producer kernel did not release the waiting consumer");
    std::this_thread::yield();
  }
  secondProgress.store(true, std::memory_order_release);
  const bool serialized = rescue.get();
  if (!serialized) {
    require(output[0][1] == 74 && input[0][1] == 148, "consumer retained a stale payload after producer release");
    for (unsigned n = 2; n < 32; ++n)
      round(0, n);
  }
  backend.overlapFinish();
  for (unsigned i = 0; i < 2; ++i) {
    if (i == 0) require(input[i][2] == 32, "overlapping dispatch counter mismatch");
    else require(output[i][0] == 74, "producer kernel did not finish");
    backend.release(output[i]);
    backend.release(input[i]);
  }
  require(!serialized, "second queue only progressed after the first kernel completed");
  std::cout << "verified overlapping queues bits=" << sizeof(Word) * 8 << " rounds=32 kernel-publication=1" << std::endl;
  return 0;
}
// Same input graph, sparse targets, guard bytes and CPU edits for every API/ISA.
// A dependent pointer chain cannot be replaced by indexing into the source array.
// Actual concurrent kernels, with flags providing deterministic ordering. No speed gate.
template <class Backend> int reviewCases(Backend &backend) {
  auto wait = [](uint32_t &word, uint32_t value) {
    const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(25);
    while (std::atomic_ref<uint32_t>(word).load(std::memory_order_acquire) != value) {
      require(std::chrono::steady_clock::now() < deadline, "memory review handshake stopped progressing");
      std::this_thread::yield();
    }
  };
  for (bool mixed : {false, true}) {
    auto *host = static_cast<uint32_t *>(backend.allocate(64));
    std::fill_n(host, 16, 0U);
    host[2] = 7;
    auto *device = static_cast<uint32_t *>(backend.allocateDevice(8));
    uint32_t words[2]{};
    backend.deviceWrite(device, words, sizeof(words));
    backend.overlapSubmit(0, host, device, mixed ? 17 : 15);
    wait(host[0], 1);
    backend.overlapSubmit(1, host, mixed ? device : device + 1, mixed ? 18 : 16);
    wait(host[1], 2);
    if (!mixed) std::atomic_ref<uint32_t>(host[0]).store(3, std::memory_order_release);
    backend.overlapFinish();
    backend.deviceRead(words, device, sizeof(words));
    const bool correct = mixed ? host[3] == 73 && host[4] == 7 : words[0] == 1 && words[1] == 2;
    backend.releaseDevice(device);
    backend.release(host);
    require(correct, mixed ? "device flag failed to order shared payload" : "overlapping disjoint device writes were lost");
    std::cout << "verified " << (mixed ? "mixed backing publication" : "disjoint device writes") << std::endl;
  }
  auto *control = static_cast<Node *>(backend.allocate(sizeof(Node)));
  auto *first = static_cast<Node *>(backend.allocate(sizeof(Node)));
  auto *second = static_cast<Node *>(backend.allocate(sizeof(Node)));
  auto *table = static_cast<Node *>(backend.allocateDevice(sizeof(Node)));
  *control = {};
  *first = {};
  *second = {};
  control->next = first;
  first->next = second;
  first->value = 11;
  second->value = 29;
  Node initial{};
  backend.deviceWrite(table, &initial, sizeof(initial));
  backend.overlapSubmit(0, control, reinterpret_cast<uint32_t *>(table), 21);
  wait(control->value, 1);
  backend.overlapSubmit(1, control, reinterpret_cast<uint32_t *>(table), 22);
  backend.overlapFinish();
  Node result{};
  backend.deviceRead(&result, table, sizeof(result));
  const bool deviceCorrect = control->padding[0] == 29 && result.next == second;
  backend.releaseDevice(table);
  backend.release(second);
  backend.release(first);
  backend.release(control);
  require(deviceCorrect, "synchronized device pointer replacement was cached or replayed");
  std::cout << "verified synchronized device pointer replacement" << std::endl;
  return 0;
}

template <class Backend> int run(Backend &backend) {
  if (std::getenv("GEMSTONE_MEMORY_REVIEW_PROBE")) return reviewCases(backend);
  if (std::getenv("GEMSTONE_MEMORY_COMPARE_PROBE")) {
    require(backend.concurrentCapabilities(), "compare-exchange integration check requires concurrent host storage");
    return compareTraffic(backend);
  }
  if (std::getenv("GEMSTONE_MEMORY_OVERLAP_PROBE")) {
    const auto *bits = std::getenv("GEMSTONE_MEMORY_ATOMIC_BITS");
    return bits && std::string(bits) == "64" ? overlap<uint64_t>(backend) : overlap<uint32_t>(backend);
  }
  if (std::getenv("GEMSTONE_MEMORY_SUBMISSION_PROBE")) {
    const auto *bits = std::getenv("GEMSTONE_MEMORY_ATOMIC_BITS");
    return bits && std::string(bits) == "64" ? submission<uint64_t>(backend) : submission<uint32_t>(backend);
  }
  if (std::getenv("GEMSTONE_MEMORY_ATOMIC_PROBE")) return atomicProbe(backend);
  if (concurrencyMode()) return concurrent(backend);
  if (std::getenv("GEMSTONE_MEMORY_RPC_ONLY")) {
    AllocationScope allocations(backend);
    auto *input = static_cast<uint32_t *>(allocations.allocate(128));
    auto *output = static_cast<uint32_t *>(allocations.allocate(128));
    for (unsigned pass = 0; pass < 2; ++pass) {
      input[1] = 41 + pass;
      output[1] = 0;
      backend.launch(input + 1, output + 1, input + 1, 0, 1);
      require(output[1] == input[1] + 1, "RPC dispatch did not publish its write");
      std::cout << "verified host-memory transport pass=" << pass << std::endl;
    }
  } else {
    for (unsigned mode = 0; mode != 4; ++mode)
      ordinaryAccess(backend, mode);
  }
  return 0;
}
} // namespace memory_patterns
