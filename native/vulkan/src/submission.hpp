#pragma once

#include <mutex>
#include <optional>
#include <vector>

#include "client.hpp"
#include "host_allocator.hpp"

namespace gemstone::vulkan {
// Own remote resources for one synchronous execution. Host-backed compute
// allocations are serviced by completion callbacks; graphics uses explicit copies.
class SubmissionResources {
  host::Scope arena{nullptr};
  std::unique_lock<std::mutex> executionLock;
  execution::ClientManager connection;
  execution::Client *client = connection.client();
  struct Allocation {
    uint8_t *data;
    size_t size;
    execution::AllocationId id;
    bool hostBacked;
  };
  std::vector<Allocation> allocations;
  std::optional<execution::ProgramId> program;
  bool okay = client != nullptr;

public:
  explicit SubmissionResources(std::mutex &mutex);
  ~SubmissionResources();
  SubmissionResources(const SubmissionResources &) = delete;
  SubmissionResources &operator=(const SubmissionResources &) = delete;
  execution::Client *get() const { return okay ? client : nullptr; }
  void fail() { okay = false; }
  execution::GraphicsRange memory(uint8_t *data, size_t size, bool hostBacked);
  std::optional<execution::ProgramId> load(const std::vector<uint8_t> &image);
  bool complete(execution::CompletionId completion, uint64_t *samples = nullptr);
};
} // namespace gemstone::vulkan
