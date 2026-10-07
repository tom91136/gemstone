#pragma once

#include <memory>
#include <string>
#include <vector>

#include "gemstone_execution_rpc.hpp"

namespace gemstone::execution {

struct CompletionResult {
  std::vector<DirtyRange> dirtyRanges;
  std::vector<PointerRelocation> pointerRelocations;
};

/** Reads all dirty-range pages without releasing the completion. */
bool completionDirtyRanges(Client &client, CompletionId completion, std::vector<DirtyRange> &ranges, std::string &error);

/** Waits while servicing host-memory requests; the caller retains the completion. */
bool awaitCompletion(Client &client, CompletionId completion, std::string &error);

/** Waits, copies stable result records and optional graphics statistics, then releases the completion.
 * The first transport/execution error is preserved even when release also
 * fails. Facades remain responsible for applying records to native memory.
 */
bool collectCompletion(Client &client, CompletionId completion, CompletionResult &result, std::string &error,
                       GraphicsStatistics *graphics = nullptr);

/** Launches and collects on a dedicated connection, so callers' shared clients stay usable while the dispatch
 * services host-memory requests. `launched` runs once the service accepts the launch.
 */
template <class Launched = decltype([] {})>
bool launchAndCollect(Launch launch, CompletionResult &result, std::string &error, Launched launched = {}) {
  const auto client = Client::fromEnvironment();
  if (!client) {
    error = "Gemstone execution RPC client is not configured";
    return false;
  }
  auto completion = client->call(launch);
  if (!completion) {
    error = completion.error().detail;
    return false;
  }
  launched();
  return collectCompletion(*client, completion.value(), result, error);
}

} // namespace gemstone::execution
