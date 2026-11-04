#include "completion.hpp"

#include <algorithm>
#include <array>
#include <atomic>
#include <cerrno>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <functional>
#include <iterator>
#include <limits>
#include <memory>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <unistd.h>

#ifdef __linux__
  #include <sys/uio.h>
#endif

#include "gemstone/environment.hpp"
#include "gemstone_execution_rpc.hpp"

#include <environment_keys.hpp>

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

static size_t pageSizeBytes() {
#ifdef __linux__
  static const auto page = static_cast<size_t>(sysconf(_SC_PAGESIZE));
  return page;
#else
  return 4096;
#endif
}

size_t readableExtent(uintptr_t address, size_t size) {
#ifdef __linux__
  const auto page = static_cast<uintptr_t>(pageSizeBytes());
  constexpr size_t batch = 256;
  size_t extent = 0;
  while (extent < size) {
    const auto at = address + extent;
    const auto first = at - (at % page);
    const auto pages = std::min(batch, (size - extent + (at % page) + page - 1) / page);
    std::array<iovec, batch> remote{};
    std::array<uint8_t, batch> sink{};
    for (size_t index = 0; index < pages; ++index)
      remote[index] = {.iov_base = reinterpret_cast<void *>(index == 0 ? at : first + (index * page)), .iov_len = 1};
    const iovec local{.iov_base = sink.data(), .iov_len = pages};
    const auto copied = process_vm_readv(getpid(), &local, 1, remote.data(), pages, 0);
    if (copied < 0 && errno != EFAULT) return size;
    const auto readable = copied < 0 ? size_t{0} : static_cast<size_t>(copied);
    if (readable < pages) return std::min(size, readable == 0 ? extent : first + (readable * page) - address);
    extent = std::min(size, first + (pages * page) - address);
  }
  return size;
#else
  (void)address;
  return size;
#endif
}

std::vector<uint8_t> copyHostBytes(uintptr_t address, size_t size) {
  std::vector<uint8_t> bytes(size);
  size_t offset = 0;
  while (offset < size) {
    const auto at = address + offset;
    size_t width = 8;
    while (width > 1 && (at % width != 0 || width > size - offset))
      width /= 2;
    visitAtomicWidth(width, [&]<typename T>() {
      const auto value = std::atomic_ref<T>(*reinterpret_cast<T *>(at)).load(std::memory_order_relaxed);
      std::memcpy(bytes.data() + offset, &value, sizeof(T));
      return 0;
    });
    offset += width;
  }
  return bytes;
}

bool awaitCompletion(Client &client, CompletionId completion, std::string &error, FailureKind *kind,
                     const std::function<void()> &synchronised) {
  for (;;) {
    const auto polled = client.call(PollHostMemory{completion});
    if (!polled) return reject(polled.error(), error, kind);
    const auto &batch = polled.value();
    // Only the ranges the server requests are touched, and only as far as they are readable; the server forgets the rest.
    std::vector<HostMemoryBytes> replies;
    for (const auto &range : batch.reads) {
      const auto address = static_cast<uintptr_t>(range.address.value);
      const auto size = static_cast<size_t>(range.size.value);
      if (!address || size > UINTPTR_MAX - address) return reject("invalid host read range", FailureKind::IllegalAddress, error, kind);
      const auto page = pageSizeBytes();
      size_t offset = 0;
      while (offset < size) {
        const auto readable = readableExtent(address + offset, size - offset);
        if (readable > 0)
          replies.push_back(
              {.address = {range.address.value + static_cast<int64_t>(offset)}, .bytes = copyHostBytes(address + offset, readable)});
        offset += readable;
        if (offset < size) offset += std::min(size - offset, page - ((address + offset) % page));
      }
    }
    // an empty reply lets the server make progress when no requested byte is readable
    if (!batch.reads.empty() && replies.empty()) replies.push_back({.address = batch.reads.front().address, .bytes = {}});
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
      if (synchronised) synchronised();
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

bool collectCompletion(Client &client, CompletionId completion, CompletionResult &result, std::string &error, GraphicsStatistics *graphics,
                       const std::function<void()> &synchronised) {
  if (graphics) *graphics = {};
  result.dirtyRanges.clear();
  result.pointerRelocations.clear();
  error.clear();

  result.failure = FailureKind::Transport;
  bool succeeded = awaitCompletion(client, completion, error, &result.failure, synchronised);
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
      const auto &text = prints.value().value;
      if (!text.empty()) std::fwrite(text.data(), 1, text.size(), stdout);
    } else {
      error = prints.error().detail;
    }
  }

  const auto released = client.call(ReleaseCompletion{completion});
  if (!released && error.empty()) error = released.error().detail;
  return succeeded && released;
}

namespace {

// a forked child must not speak on its parent's session
struct LaunchConnection {
  decltype(::getpid()) process = 0;
  std::string endpoint;
  std::string id;
  std::unique_ptr<Client> client;
  ~LaunchConnection();
};

std::string_view configured(const char *name) {
  const char *value = environment::value(name);
  return value ? value : "";
}

thread_local bool launchConnectionRetired = false;
// connections asked for after this thread's storage is torn down, as during process exit, are never closed
thread_local Client *lateConnection = nullptr;
thread_local LaunchConnection launchConnection;

LaunchConnection::~LaunchConnection() { launchConnectionRetired = true; }

} // namespace

Client *launchClient() {
  if (launchConnectionRetired) {
    if (!lateConnection) lateConnection = Client::fromEnvironment().release();
    return lateConnection;
  }
  const auto process = ::getpid();
  const auto endpoint = configured(environment::RpcEndpoint);
  const auto id = configured(environment::RpcClientId);
  auto &connection = launchConnection;
  if (!connection.client || connection.process != process || connection.endpoint != endpoint || connection.id != id) {
    connection.client = Client::fromEnvironment();
    connection.process = process;
    connection.endpoint = endpoint;
    connection.id = id;
  }
  return connection.client.get();
}

void dropLaunchClient() {
  if (launchConnectionRetired) {
    delete lateConnection;
    lateConnection = nullptr;
  } else {
    launchConnection.client.reset();
  }
}

} // namespace gemstone::execution
