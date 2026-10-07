#pragma once
#include "memory_support.hpp"

namespace memory_patterns {
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

// CPU edits -> producer kernel -> event/signal -> consumer kernel -> host wait.
// Repeat over the same allocations, covering both one queue and two queues.
template <class Backend> int ordered(Backend &backend, bool crossQueue, unsigned passes) {
  AllocationScope allocations(backend);
  constexpr unsigned items = 128;
  auto *input = static_cast<uint32_t *>(allocations.allocate((items + 2) * sizeof(uint32_t)));
  auto *middle = static_cast<uint32_t *>(allocations.allocate((items + 2) * sizeof(uint32_t)));
  auto *output = static_cast<uint32_t *>(allocations.allocate((items + 2) * sizeof(uint32_t)));
  for (unsigned pass = 0; pass < passes; ++pass) {
    std::fill_n(middle, items + 2, 0xa5a5a5a5U);
    std::fill_n(output, items + 2, 0xa5a5a5a5U);
    for (unsigned i = 0; i < items; ++i)
      input[i + 1] = 1000 * pass + i;
    backend.ordered(input + 1, middle + 1, output + 1, items, crossQueue);
    for (unsigned i = 0; i < items; ++i) {
      require(middle[i + 1] == 1000 * pass + i + 1, "producer result was not visible after event wait");
      require(output[i + 1] == 1000 * pass + i + 2, "consumer ran with stale producer data");
    }
    require(middle[0] == 0xa5a5a5a5U && middle[items + 1] == 0xa5a5a5a5U && output[0] == 0xa5a5a5a5U && output[items + 1] == 0xa5a5a5a5U,
            "ordered kernel changed guards");
    std::cout << "verified queue dependency pass=" << pass << " cross-queue=" << (crossQueue) << std::endl;
  }
  allocations.release(output);
  allocations.release(middle);
  allocations.release(input);
  return 0;
}
template <class Backend> void accessPattern(Backend &backend, unsigned mode) {
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
    const auto start = std::chrono::steady_clock::now();
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
    const auto seconds = std::chrono::duration<double>(std::chrono::steady_clock::now() - start).count();
    std::cout << "verified " << name << " pass=" << pass << " seconds=" << seconds << std::endl;
  }
  allocations.release(indices);
  allocations.release(out);
  allocations.release(storage);
}

// A host worker performs the publication handshake while the caller waits for
// one kernel. This requires host/device atomics, not concurrent kernel progress.
template <class Backend> void pointerReplacement(Backend &backend) {
  AllocationScope allocations(backend);
  auto *table = static_cast<Node *>(allocations.allocate(sizeof(Node)));
  auto *first = static_cast<Node *>(allocations.allocate(sizeof(Node)));
  auto *second = static_cast<Node *>(allocations.allocate(sizeof(Node)));
  *table = {};
  *first = {};
  *second = {};
  first->value = 11;
  second->value = 29;
  std::atomic_bool cancelled{false};
  auto host = std::async(std::launch::async, [&] {
    const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(90);
    auto flag = std::atomic_ref<uint32_t>(table->value);
    while (flag.load(std::memory_order_acquire) != 1) {
      if (cancelled.load(std::memory_order_relaxed)) return;
      require(std::chrono::steady_clock::now() < deadline, "pointer publication did not progress");
      std::this_thread::yield();
    }
    table->next = second;
    flag.store(2, std::memory_order_release);
  });
  try {
    backend.launch(table, reinterpret_cast<uint32_t *>(first), reinterpret_cast<uint32_t *>(table), 19, 1);
  } catch (...) {
    cancelled.store(true, std::memory_order_relaxed);
    host.wait();
    throw;
  }
  host.get();
  const bool correct = table->next == second && table->padding[0] == 29;
  allocations.release(second);
  allocations.release(first);
  allocations.release(table);
  require(correct, "synchronised CPU pointer replacement was cached or overwritten");
}
template <class Word, class Backend> void atomicValues(Backend &backend) {
  AllocationScope allocations(backend);
  constexpr unsigned lanes = 128;
  auto *input = static_cast<Word *>(allocations.allocate(128));
  auto *output = static_cast<Word *>(allocations.allocate(lanes * sizeof(Word)));
  std::fill_n(input, 128 / sizeof(Word), Word{});
  std::fill_n(output, lanes, Word{});
  std::cout << "atomic-input " << reinterpret_cast<uintptr_t>(input) << " " << sizeof(Word) << std::endl;
  backend.launch(input, reinterpret_cast<uint32_t *>(output), reinterpret_cast<uint32_t *>(input), sizeof(Word) == 4 ? 9 : 10, lanes);
  require(input[2] == Word(lanes), "multi-workgroup RMW lost an update");
  std::sort(output, output + lanes);
  for (unsigned lane = 0; lane < lanes; ++lane)
    require(output[lane] == Word(lane), "multi-workgroup atomic returned a stale or duplicate old value");
  allocations.release(output);
  allocations.release(input);
  std::cout << "verified multi-workgroup atomics bits=" << sizeof(Word) * 8 << " lanes=128 groups=4" << std::endl;
}
// Both sides synchronise through the native atomic word during execution.
// Repeated payload changes verify visibility after every acquire.
template <class Word, class Backend> void atomicPublication(Backend &backend, bool fenced) {
  AllocationScope allocations(backend);
  auto *input = static_cast<Word *>(allocations.allocate(128));
  auto *output = static_cast<Word *>(allocations.allocate(32 * sizeof(Word)));
  std::fill_n(input, 128 / sizeof(Word), Word{});
  std::fill_n(output, 32, Word{});
  std::cout << "atomic-input " << reinterpret_cast<uintptr_t>(input) << " " << sizeof(Word) << std::endl;
  std::atomic_bool cancelled{false};
  auto host = std::async(std::launch::async, [&] {
    auto state = std::atomic_ref<Word>(input[0]);
    const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(90);
    std::exception_ptr failure;
    for (unsigned round = 0; round < 32; ++round) {
      input[1] = 73 + round;
      if (fenced) std::atomic_thread_fence(std::memory_order_release);
      state.store(1, fenced ? std::memory_order_relaxed : std::memory_order_release);
      while (state.load(fenced ? std::memory_order_relaxed : std::memory_order_acquire) != 2) {
        if (cancelled.load(std::memory_order_relaxed)) return;
        require(std::chrono::steady_clock::now() < deadline, "live host/device handshake timed out");
        std::this_thread::yield();
      }
      if (fenced) std::atomic_thread_fence(std::memory_order_acquire);
      try {
        require(input[1] == 2 * (73 + round), "GPU release did not publish payload");
        require(output[round] == 73 + round, "GPU acquire read stale CPU payload");
        require(std::atomic_ref<Word>(input[2]).load(std::memory_order_relaxed) == Word(round + 1), "CPU/GPU atomic RMW lost an update");
      } catch (...) {
        if (!failure) failure = std::current_exception();
      }
    }
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

// Publish through one atomic location and acknowledge through another. The
// compare-exchange must fail, return the published value and acquire its payload.
template <class Word, class Backend> int compareFailure(Backend &backend) {
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
template <class Backend> void runScenario(Backend &backend, unsigned scenario) {
  const bool wide = scenario >= 11;
  if (scenario < 4) accessPattern(backend, scenario);
  else if (scenario < 6) ordered(backend, scenario == 5, scenario == 5 ? 2 : 1);
  else if (scenario == 6) pointerReplacement(backend);
  else {
    auto atomic = [&]<class Word>() {
      switch ((scenario - 7) % 4) {
        case 0:
        case 1: atomicPublication<Word>(backend, (scenario - 7) % 4 == 1); break;
        case 2: atomicValues<Word>(backend); break;
        case 3: compareFailure<Word>(backend); break;
      }
    };
    if (wide) atomic.template operator()<uint64_t>();
    else atomic.template operator()<uint32_t>();
  }
}
} // namespace memory_patterns
