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

/** The calling thread's launch connection, kept across launches and apart from callers' shared clients, which stay
 * usable while a dispatch services host-memory requests. Null when the execution service is not configured.
 */
Client *launchClient();

/** Drops the calling thread's launch connection after a failure, so the next launch reconnects. */
void dropLaunchClient();

/** Launches and collects on the calling thread's launch connection. `launched` runs once the service accepts the launch. */
template <class Launched = decltype([] {})>
bool launchAndCollect(const Launch &launch, CompletionResult &result, std::string &error, Launched launched = {},
                      const std::function<void()> &synchronised = {}) {
  result.failure = FailureKind::Transport;
  auto *client = launchClient();
  if (!client) {
    error = "Gemstone execution RPC client is not configured";
    return false;
  }
  auto completion = client->call(launch);
  if (!completion) {
    dropLaunchClient();
    error = completion.error().detail;
    result.failure = completion.error().kind;
    return false;
  }
  launched();
  if (collectCompletion(*client, completion.value(), result, error, nullptr, synchronised)) return true;
  dropLaunchClient();
  return false;
}

} // namespace gemstone::execution
