#ifndef GEMSTONE_EXECUTION_COMPLETION_HPP
#define GEMSTONE_EXECUTION_COMPLETION_HPP

#include <string>
#include <vector>

#include "gemstone_execution_rpc.hpp"

namespace gemstone::execution {

struct CompletionResult {
  std::vector<DirtyRange> dirty_ranges;
  std::vector<PointerRelocation> pointer_relocations;
};

/** Reads all dirty-range pages without releasing the completion (also used by the HSA executor). */
bool completion_dirty_ranges(Client &client, CompletionId completion, std::vector<DirtyRange> &ranges, std::string &error);

/** Waits, servicing host-memory requests when required; the caller retains the completion. */
bool await_completion(Client &client, CompletionId completion, std::string &error, bool hostMemory = false);

/** Waits once, copies stable result records and optional graphics statistics, then releases the completion.
 * The first transport/execution error is preserved even when release also
 * fails. Facades remain responsible for applying records to native memory.
 */
bool collect_completion(Client &client, CompletionId completion, CompletionResult &result, std::string &error, bool hostMemory = false,
                        GraphicsStatistics *graphics = nullptr);

} // namespace gemstone::execution

#endif
