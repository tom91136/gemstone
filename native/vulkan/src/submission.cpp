#include "submission.hpp"

#include <algorithm>
#include <climits>
#include <cstdio>
#include <cstring>

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
  const auto allocated = gx::registerAllocation(*client, size, data, hostBacked);
  if (!allocated) {
    fail();
    return {};
  }
  allocations.push_back({data, size, *allocated, hostBacked});
  if (!hostBacked) okay = gx::transferAllocation(*client, *allocated, 0, data, size, true, gx::TransferChunk);
  return {*allocated, 0, static_cast<int64_t>(size)};
}
std::optional<gx::ProgramId> SubmissionResources::load(const std::vector<uint8_t> &image) {
  if (!okay || program) return std::nullopt;
  auto loaded = client->call(gx::LoadProgram{gx::ProgramEncoding::SPIRVVulkanCompute, image});
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
  if (!gx::collectCompletion(*client, completion, result, error, samples ? &statistics : nullptr)) {
    std::fprintf(stderr, "Gemstone Vulkan execution: %s\n", error.c_str());
    return false;
  }
  if (samples) *samples += statistics.occlusionSamples.value;
  const auto owner = [&](const gx::DirtyRange &range) {
    return std::find_if(allocations.begin(), allocations.end(),
                        [&](const auto &allocation) { return allocation.id.value == range.allocation.value; });
  };
  std::vector<gx::DirtyRange> copied;
  for (const auto &dirty : result.dirtyRanges) {
    const auto found = owner(dirty);
    if (found == allocations.end() || static_cast<uint64_t>(dirty.offset.value) > found->size
        || static_cast<uint64_t>(dirty.size.value) > found->size - dirty.offset.value)
      return false;
    if (!found->hostBacked) copied.push_back(dirty);
  }
  return gx::readDirtyRanges(*client, copied, [&](const gx::DirtyRange &range, const uint8_t *bytes) {
    std::memcpy(owner(range)->data + range.offset.value, bytes, range.size.value);
    return true;
  });
}
} // namespace gemstone::vulkan
