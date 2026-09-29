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

/** Waits for a completion, copies its stable result records, and releases it.
 * The first transport/execution error is preserved even when release also
 * fails. Facades remain responsible for applying records to native memory.
 */
bool await_completion(Client &client, CompletionId completion, std::string &error, bool hostMemory = false);
bool completion_dirty_ranges(Client &client, CompletionId completion, std::vector<DirtyRange> &ranges, std::string &error);
bool collect_completion(Client &client, CompletionId completion, CompletionResult &result, std::string &error, bool hostMemory = false);

} // namespace gemstone::execution

#endif
