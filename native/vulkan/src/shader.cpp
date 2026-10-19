#include "shader.hpp"

#include <cstdint>
#include <cstdio>
#include <optional>
#include <utility>
#include <vector>

#include "gemstone_execution_rpc.hpp"
#include <gemstone/trace.hpp>

#include "client.hpp"
#include "host_allocator.hpp"

namespace gemstone::vulkan {
std::optional<execution::PreparedShader> prepareShader(const std::vector<uint8_t> &image, const char *entry,
                                                       execution::ShaderStageKind kind, const VkSpecializationInfo *specialization) {
  if (!entry || !*entry) return std::nullopt;
  // Transport buffers must outlive neither this call nor its ordinary host arena.
  host::Scope const rpcScope(nullptr);
  execution::ClientManager connection;
  auto *client = connection.client();
  if (!client) return std::nullopt;
  std::vector<execution::SpecializationConstant> constants;
  if (specialization) {
    const auto &spec = *specialization;
    if ((spec.mapEntryCount && !spec.pMapEntries) || (spec.dataSize && !spec.pData)) return std::nullopt;
    constants.reserve(spec.mapEntryCount);
    for (uint32_t index = 0; index < spec.mapEntryCount; ++index) {
      const auto &entry = spec.pMapEntries[index];
      if (entry.offset > spec.dataSize || entry.size > spec.dataSize - entry.offset) return std::nullopt;
      std::vector<uint8_t> value;
      if (entry.size) {
        const auto *begin = static_cast<const uint8_t *>(spec.pData) + entry.offset;
        value.assign(begin, begin + entry.size);
      }
      constants.push_back({.specId = execution::Binary32{static_cast<int64_t>(entry.constantID)}, .value = std::move(value)});
    }
  }
  auto reply = client->call(execution::PrepareShader{.image = image, .entry = {entry}, .stage = kind, .constants = constants});
  if (!reply) {
    if (gemstone::traceEnabled("vulkan"))
      std::fprintf(stderr, "Gemstone Vulkan: prepare shader %s: %s\n", entry, reply.error().detail.c_str());
    return std::nullopt;
  }
  const auto &prepared = reply.value();
  for (const auto &descriptor : prepared.descriptors)
    if (std::cmp_greater_equal(descriptor.set.value, MaxBoundDescriptorSets)) return std::nullopt;
  // The retained generated value belongs to the pipeline's allocation arena.
  host::Scope const resultScope(rpcScope.previous);
  return prepared;
}
} // namespace gemstone::vulkan
