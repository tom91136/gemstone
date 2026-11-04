#pragma once

#include <functional>
#include <memory>
#include <string>
#include <vector>

#include "gemstone_execution_rpc.hpp"

namespace gemstone::execution {

struct CompletionResult {
  std::vector<DirtyRange> dirtyRanges;
  std::vector<PointerRelocation> pointerRelocations;
  /** What the service reported for a failed dispatch; `Transport` when the failure did not come from it. */
  FailureKind failure = FailureKind::Transport;
};

/** Reads all dirty-range pages without releasing the completion. */
bool completionDirtyRanges(Client &client, CompletionId completion, std::vector<DirtyRange> &ranges, std::string &error);

/** The length of the longest prefix of the range whose pages this process can read; the whole range when that cannot be probed. */
size_t readableExtent(uintptr_t address, size_t size);

/** Copies host bytes, reading each naturally aligned word with one relaxed atomic load so concurrent host stores never tear. */
std::vector<uint8_t> copyHostBytes(uintptr_t address, size_t size);

/** Waits while servicing host-memory requests; the caller retains the completion. A failure's kind is stored in `kind`.
 * `synchronised` runs whenever the dispatch asks the host to take part in synchronisation, such as an atomic access.
 */
bool awaitCompletion(Client &client, CompletionId completion, std::string &error, FailureKind *kind = nullptr,
                     const std::function<void()> &synchronised = {});

/** Waits, copies stable result records and optional graphics statistics, then releases the completion.
 * The first transport/execution error is preserved even when release also
 * fails. Facades remain responsible for applying records to native memory.
 */
bool collectCompletion(Client &client, CompletionId completion, CompletionResult &result, std::string &error,
                       GraphicsStatistics *graphics = nullptr, const std::function<void()> &synchronised = {});

/** Launches and collects on a dedicated connection, so callers' shared clients stay usable while the dispatch
 * services host-memory requests. `launched` runs once the service accepts the launch.
 */
template <class Launched = decltype([] {})>
bool launchAndCollect(const Launch &launch, CompletionResult &result, std::string &error, Launched launched = {},
                      const std::function<void()> &synchronised = {}) {
  result.failure = FailureKind::Transport;
  const auto client = Client::fromEnvironment();
  if (!client) {
    error = "Gemstone execution RPC client is not configured";
    return false;
  }
  auto completion = client->call(launch);
  if (!completion) {
    error = completion.error().detail;
    result.failure = completion.error().kind;
    return false;
  }
  launched();
  return collectCompletion(*client, completion.value(), result, error, nullptr, synchronised);
}

} // namespace gemstone::execution
