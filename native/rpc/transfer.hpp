#ifndef GEMSTONE_EXECUTION_TRANSFER_HPP
#define GEMSTONE_EXECUTION_TRANSFER_HPP

#include <algorithm>
#include <cstdint>
#include <cstring>
#include <vector>

#include "gemstone_execution_rpc.hpp"

namespace gemstone::execution {

/** Largest allocation payload a single RPC message may carry. */
inline constexpr size_t TransferChunk = MaximumPayload / 2;

/** Copies an allocation range between the client and the execution service in
 * bounded chunks. `upload` writes client bytes to the service; otherwise the
 * service bytes are read back. Returns false on the first transport error.
 */
inline bool transferAllocation(Client &client, AllocationId allocation, uint64_t offset, const void *bytes, size_t size, bool upload,
                               size_t chunkLimit) {
  if (chunkLimit == 0) return false;
  const auto *source = static_cast<const uint8_t *>(bytes);
  while (size != 0) {
    const auto chunk = std::min(size, chunkLimit);
    if (upload) {
      if (!client.call(WriteAllocation{allocation, static_cast<int64_t>(offset), std::vector<uint8_t>(source, source + chunk)}))
        return false;
    } else {
      auto result = client.call(ReadAllocation{allocation, static_cast<int64_t>(offset), static_cast<int32_t>(chunk)});
      if (!result || result.value().size() != chunk) return false;
      std::memcpy(const_cast<uint8_t *>(source), result.value().data(), chunk);
    }
    source += chunk;
    offset += chunk;
    size -= chunk;
  }
  return true;
}

} // namespace gemstone::execution

#endif
