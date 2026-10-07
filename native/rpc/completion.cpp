#include "completion.hpp"

#include <atomic>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <iterator>
#include <limits>
#include <string>
#include <utility>
#include <vector>

namespace gemstone::execution {
namespace {

template <typename Function> decltype(auto) visitAtomicWidth(size_t width, Function &&function) {
  if (width == 1) return function.template operator()<uint8_t>();
  if (width == 2) return function.template operator()<uint16_t>();
  if (width == 4) return function.template operator()<uint32_t>();
  return function.template operator()<uint64_t>();
}

template <typename T>
uint64_t applyAtomicRequest(uintptr_t address, HostMemoryAtomicOperation operation, uint64_t expected, uint64_t desired) {
  auto &cell = *reinterpret_cast<T *>(address);
  auto reference = std::atomic_ref<T>(cell);
  if (operation == HostMemoryAtomicOperation::Load) return reference.load(std::memory_order_relaxed);
  if (operation == HostMemoryAtomicOperation::Store) {
    reference.store(static_cast<T>(desired), std::memory_order_relaxed);
    return expected;
  }
  auto observed = static_cast<T>(expected);
  reference.compare_exchange_strong(observed, static_cast<T>(desired), std::memory_order_seq_cst);
  return observed;
}

template <typename T> void atomicStore(uintptr_t address, uint64_t value, std::memory_order order) {
  auto &cell = *reinterpret_cast<T *>(address);
  std::atomic_ref<T>(cell).store(static_cast<T>(value), order);
}

// Completion records are immutable until ReleaseCompletion, so offsets can be retried safely.
template <class Request, class Record>
bool collectPages(Client &client, CompletionId completion, std::vector<Record> &records, std::string &error) {
  for (;;) {
    if (records.size() > static_cast<size_t>(std::numeric_limits<int32_t>::max())) {
      error = "completion metadata exceeds the offset range";
      return false;
    }
    auto page = client.call(Request{completion, static_cast<int32_t>(records.size())});
    if (!page) {
      error = page.error().detail;
      return false;
    }
    const auto count = page.value().size();
    records.insert(records.end(), std::make_move_iterator(page.value().begin()), std::make_move_iterator(page.value().end()));
    if (count < MaximumElements) return true;
  }
}

} // namespace

bool awaitCompletion(Client &client, CompletionId completion, std::string &error) {
  for (;;) {
    auto batch = client.call(PollHostMemory{completion});
    if (!batch) {
      error = batch.error().detail;
      return false;
    }
    // Only exact interpreter-requested ranges are touched. Never round to pages.
    std::vector<HostMemoryBytes> replies;
    for (const auto &range : batch.value().reads) {
      const auto address = static_cast<uintptr_t>(range.address.value);
      const auto size = static_cast<size_t>(range.size.value);
      if (!address || size > UINTPTR_MAX - address) {
        error = "invalid host read range";
        return false;
      }
      const auto *begin = reinterpret_cast<const uint8_t *>(address);
      replies.push_back({range.address, std::vector<uint8_t>(begin, begin + size)});
    }
    for (const auto &range : batch.value().writes) {
      const auto address = static_cast<uintptr_t>(range.address.value);
      if (!address || range.bytes.size() > UINTPTR_MAX - address) {
        error = "invalid host write range";
        return false;
      }
      const auto width = range.atomicWidth.value;
      if (width != 0) {
        if ((width != 1 && width != 2 && width != 4 && width != 8) || address % width || range.bytes.size() % width) {
          error = "invalid host atomic store range";
          return false;
        }
        for (size_t offset = 0; offset < range.bytes.size(); offset += width) {
          visitAtomicWidth(width, [&]<typename T>() {
            T value;
            std::memcpy(&value, range.bytes.data() + offset, sizeof(value));
            atomicStore<T>(address + offset, value, std::memory_order_seq_cst);
          });
        }
      } else std::memcpy(reinterpret_cast<void *>(address), range.bytes.data(), range.bytes.size());
    }
    if (batch.value().sync) {
      const auto &sync = *batch.value().sync;
      uint64_t observed = 0;
      if (sync.atomic) {
        const auto &request = *sync.atomic;
        const auto address = static_cast<uintptr_t>(request.address.value);
        observed = static_cast<uint64_t>(request.expected);
        const auto width = request.size.value;
        if (!address || (width != 1 && width != 2 && width != 4 && width != 8) || address % width != 0) {
          error = "invalid host atomic range";
          return false;
        }
        observed = visitAtomicWidth(
            width, [&]<typename T>() { return applyAtomicRequest<T>(address, request.operation, observed, request.desired); });
      }
      for (const auto &request : sync.atomicStores) {
        const auto address = static_cast<uintptr_t>(request.address.value);
        const auto width = request.size.value;
        if (!address || (width != 1 && width != 2 && width != 4 && width != 8) || address % width != 0) {
          error = "invalid host atomic store range";
          return false;
        }
        visitAtomicWidth(width, [&]<typename T>() { atomicStore<T>(address, request.desired, std::memory_order_relaxed); });
      }
      if (!sync.relaxed && !sync.atomic && sync.publish.empty()) std::atomic_thread_fence(std::memory_order_seq_cst);
      auto supplied = client.call(SupplyHostSync{completion, sync.ticket, static_cast<int64_t>(observed)});
      if (!supplied) {
        error = supplied.error().detail;
        return false;
      }
    }
    if (batch.value().completed) return true;
    if (replies.empty()) continue;
    auto supplied = client.call(SupplyHostMemory{completion, std::move(replies)});
    if (!supplied) {
      error = supplied.error().detail;
      return false;
    }
  }
}

bool completionDirtyRanges(Client &client, CompletionId completion, std::vector<DirtyRange> &ranges, std::string &error) {
  ranges.clear();
  error.clear();
  return collectPages<CompletionDirtyRanges>(client, completion, ranges, error);
}

bool collectCompletion(Client &client, CompletionId completion, CompletionResult &result, std::string &error,
                       GraphicsStatistics *graphics) {
  if (graphics) *graphics = {};
  result.dirtyRanges.clear();
  result.pointerRelocations.clear();
  error.clear();

  bool succeeded = awaitCompletion(client, completion, error);
  if (succeeded && graphics) {
    const auto statistics = client.call(ReadGraphicsStatistics{completion});
    succeeded = bool(statistics);
    if (succeeded) *graphics = statistics.value();
    else error = statistics.error().detail;
  }
  if (succeeded) succeeded = completionDirtyRanges(client, completion, result.dirtyRanges, error);
  if (succeeded) succeeded = collectPages<CompletionPointerRelocations>(client, completion, result.pointerRelocations, error);
  if (succeeded) {
    auto prints = client.call(CompletionPrints{completion});
    succeeded = static_cast<bool>(prints);
    if (succeeded) {
      if (!prints.value().empty()) std::fwrite(prints.value().data(), 1, prints.value().size(), stdout);
    } else error = prints.error().detail;
  }

  const auto released = client.call(ReleaseCompletion{completion});
  if (!released && error.empty()) error = released.error().detail;
  return succeeded && released;
}

} // namespace gemstone::execution
