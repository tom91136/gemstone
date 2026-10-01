#include "submission.hpp"

#include <algorithm>
#include <climits>
#include <cstdio>

#include "completion.hpp"
#include "transfer.hpp"

namespace gemstone::vulkan {
namespace gx = execution;
SubmissionResources::SubmissionResources(std::mutex &mutex) : executionLock(mutex) {}
SubmissionResources::~SubmissionResources() {
  if (!client) return;
  for (const auto &allocation : allocations)
    (void)client->call(gx::ReleaseAllocation{allocation.id});
  if (program) (void)client->call(gx::UnloadProgram{*program});
}
gx::GraphicsRange SubmissionResources::memory(uint8_t *data, size_t size, bool hostBacked) {
  if (!okay || !data || !size || size > INT32_MAX) {
    fail();
    return {};
  }
  const auto address = reinterpret_cast<uintptr_t>(data);
  for (const auto &allocation : allocations) {
    const auto base = reinterpret_cast<uintptr_t>(allocation.data);
    if (allocation.hostBacked == hostBacked && address >= base && address - base <= allocation.size
        && size <= allocation.size - (address - base))
      return {allocation.id, static_cast<int64_t>(address - base), static_cast<int64_t>(size)};
  }
  // Reserve before acquiring a remote resource so local allocation failure cannot leak it.
  allocations.reserve(allocations.size() + 1);
  auto allocated = client->call(gx::RegisterAllocation{gx::AllocationKind::Device, static_cast<int64_t>(size)});
  if (!allocated) {
    fail();
    return {};
  }
  allocations.push_back({data, size, allocated.value(), hostBacked});
  okay = bool(client->call(gx::RegisterAllocationAddress{allocated.value(), gx::GuestAddress{static_cast<int64_t>(address)}, hostBacked}));
  if (okay && !hostBacked) okay = gx::transferAllocation(*client, allocated.value(), 0, data, size, true, gx::TransferChunk);
  return {allocated.value(), 0, static_cast<int64_t>(size)};
}
std::optional<gx::ProgramId> SubmissionResources::load(const std::vector<uint8_t> &image) {
  if (!okay || program) return std::nullopt;
  auto loaded = client->call(gx::LoadProgram{"spirv-vulkan-binary", image});
  if (!loaded) {
    fail();
    return std::nullopt;
  }
  program = loaded.value();
  return program;
}
bool SubmissionResources::complete(gx::CompletionId completion, uint64_t *samples) {
  std::string error;
  gx::GraphicsStatistics statistics{};
  gx::CompletionResult result;
  if (!gx::collect_completion(*client, completion, result, error, true, samples ? &statistics : nullptr)) {
    std::fprintf(stderr, "Gemstone Vulkan execution: %s\n", error.c_str());
    return false;
  }
  if (samples) *samples += statistics.occlusionSamples.value;
  for (const auto &dirty : result.dirty_ranges) {
    const auto found = std::find_if(allocations.begin(), allocations.end(),
                                    [&](const auto &allocation) { return allocation.id.value == dirty.allocation.value; });
    if (found == allocations.end() || dirty.offset.value < 0 || dirty.size.value < 0
        || static_cast<uint64_t>(dirty.offset.value) > found->size
        || static_cast<uint64_t>(dirty.size.value) > found->size - dirty.offset.value)
      return false;
    if (!found->hostBacked
        && !gx::transferAllocation(*client, found->id, dirty.offset.value, found->data + dirty.offset.value, dirty.size.value, false,
                                   gx::TransferChunk))
      return false;
  }
  return true;
}
} // namespace gemstone::vulkan
