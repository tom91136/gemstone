#include "submission.hpp"

#include <cstdint>
#include <cstdio>
#include <cstring>
#include <mutex>
#include <optional>
#include <string>
#include <utility>
#include <vector>

#include <aspartame/vector.hpp>

#include "gemstone_execution_rpc.hpp"

#include "completion.hpp"
#include "transfer.hpp"

using namespace aspartame;

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
      return {.allocation = allocation.id, .offset = {static_cast<int64_t>(address - base)}, .size = {static_cast<int64_t>(size)}};
  }
  // Reserve before acquiring a remote resource so local allocation failure cannot leak it.
  allocations.reserve(allocations.size() + 1);
  const auto allocated = gx::registerAllocation(*client, size, data, hostBacked);
  if (!allocated) {
    fail();
    return {};
  }
  allocations.push_back({.data = data, .size = size, .id = *allocated, .hostBacked = hostBacked});
  if (!hostBacked) okay = gx::uploadAllocation(*client, *allocated, 0, data, size, gx::TransferChunk);
  return {.allocation = *allocated, .offset = {0}, .size = {static_cast<int64_t>(size)}};
}
std::optional<gx::ProgramId> SubmissionResources::load(const std::vector<uint8_t> &image) {
  if (!okay || program) return std::nullopt;
  auto loaded = client->call(gx::LoadProgram{.encoding = gx::ProgramEncoding::SPIRVVulkanCompute, .image = image});
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
    return allocations ^ find_cref([&](const auto &allocation) { return allocation.id.value == range.allocation.value; });
  };
  std::vector<gx::DirtyRange> copied;
  for (const auto &dirty : result.dirtyRanges) {
    const auto found = owner(dirty);
    if (!found || std::cmp_greater(dirty.offset.value, found->get().size)
        || static_cast<uint64_t>(dirty.size.value) > found->get().size - dirty.offset.value)
      return false;
    if (!found->get().hostBacked) copied.push_back(dirty);
  }
  return gx::readDirtyRanges(*client, copied, [&](const gx::DirtyRange &range, const uint8_t *bytes) {
    std::memcpy(owner(range)->get().data + range.offset.value, bytes, range.size.value);
    return true;
  });
}
} // namespace gemstone::vulkan
