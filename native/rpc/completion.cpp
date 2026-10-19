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

#include "gemstone_execution_rpc.hpp"

namespace gemstone::execution {
namespace {

template <typename Function> decltype(auto) visitAtomicWidth(size_t width, Function &&function) {
  if (width == 1) return std::forward<Function>(function).template operator()<uint8_t>();
  if (width == 2) return std::forward<Function>(function).template operator()<uint16_t>();
  if (width == 4) return std::forward<Function>(function).template operator()<uint32_t>();
  return std::forward<Function>(function).template operator()<uint64_t>();
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

bool validAtomicWidth(int32_t width) { return width == 1 || width == 2 || width == 4 || width == 8; }

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

bool reject(const Error &cause, std::string &error, FailureKind *kind) {
  error = cause.detail;
  if (kind) *kind = cause.kind;
  return false;
}

bool reject(std::string detail, FailureKind cause, std::string &error, FailureKind *kind) {
  error = std::move(detail);
  if (kind) *kind = cause;
  return false;
}

FailureKind atomicFailure(uintptr_t address, int32_t width) {
  if (!address) return FailureKind::IllegalAddress;
  return validAtomicWidth(width) && address % width != 0 ? FailureKind::MisalignedAddress : FailureKind::Transport;
}

} // namespace

bool awaitCompletion(Client &client, CompletionId completion, std::string &error, FailureKind *kind) {
  for (;;) {
    const auto polled = client.call(PollHostMemory{completion});
    if (!polled) return reject(polled.error(), error, kind);
    const auto &batch = polled.value();
    // Only exact interpreter-requested ranges are touched. Never round to pages.
    std::vector<HostMemoryBytes> replies;
    for (const auto &range : batch.reads) {
      const auto address = static_cast<uintptr_t>(range.address.value);
      const auto size = static_cast<size_t>(range.size.value);
      if (!address || size > UINTPTR_MAX - address) return reject("invalid host read range", FailureKind::IllegalAddress, error, kind);
      const auto *begin = reinterpret_cast<const uint8_t *>(address);
      replies.push_back({.address = range.address, .bytes = std::vector<uint8_t>(begin, begin + size)});
    }
    for (const auto &range : batch.writes) {
      const auto address = static_cast<uintptr_t>(range.address.value);
      if (!address || range.bytes.size() > UINTPTR_MAX - address)
        return reject("invalid host write range", FailureKind::IllegalAddress, error, kind);
      const auto width = range.atomicWidth.value;
      if (width != 0) {
        if (!validAtomicWidth(width) || address % width || range.bytes.size() % width)
          return reject("invalid host atomic store range", atomicFailure(address, width), error, kind);
        for (size_t offset = 0; offset < range.bytes.size(); offset += width) {
          visitAtomicWidth(width, [&]<typename T>() {
            T value;
            std::memcpy(&value, range.bytes.data() + offset, sizeof(value));
            atomicStore<T>(address + offset, value, std::memory_order_seq_cst);
          });
        }
      } else {
        std::memcpy(reinterpret_cast<void *>(address), range.bytes.data(), range.bytes.size());
      }
    }
    if (batch.sync) {
      const auto &sync = *batch.sync;
      uint64_t observed = 0;
      if (sync.atomic) {
        const auto &request = *sync.atomic;
        const auto address = static_cast<uintptr_t>(request.address.value);
        observed = static_cast<uint64_t>(request.expected);
        const auto width = request.size.value;
        if (!address || !validAtomicWidth(width) || address % width != 0)
          return reject("invalid host atomic range", atomicFailure(address, width), error, kind);
        observed = visitAtomicWidth(
            width, [&]<typename T>() { return applyAtomicRequest<T>(address, request.operation, observed, request.desired); });
      }
      for (const auto &request : sync.atomicStores) {
        const auto address = static_cast<uintptr_t>(request.address.value);
        const auto width = request.size.value;
        if (!address || !validAtomicWidth(width) || address % width != 0)
          return reject("invalid host atomic store range", atomicFailure(address, width), error, kind);
        visitAtomicWidth(width, [&]<typename T>() { atomicStore<T>(address, request.desired, std::memory_order_relaxed); });
      }
      if (!sync.relaxed && !sync.atomic && sync.publish.empty()) std::atomic_thread_fence(std::memory_order_seq_cst);
      auto supplied =
          client.call(SupplyHostSync{.completion = completion, .ticket = sync.ticket, .observed = static_cast<int64_t>(observed)});
      if (!supplied) return reject(supplied.error(), error, kind);
    }
    if (batch.completed) return true;
    if (replies.empty()) continue;
    auto supplied = client.call(SupplyHostMemory{.completion = completion, .ranges = std::move(replies)});
    if (!supplied) return reject(supplied.error(), error, kind);
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

  result.failure = FailureKind::Transport;
  bool succeeded = awaitCompletion(client, completion, error, &result.failure);
  if (succeeded && graphics) {
    const auto statistics = client.call(ReadGraphicsStatistics{completion});
    succeeded = static_cast<bool>(statistics);
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
    } else {
      error = prints.error().detail;
    }
  }

  const auto released = client.call(ReleaseCompletion{completion});
  if (!released && error.empty()) error = released.error().detail;
  return succeeded && released;
}

} // namespace gemstone::execution
