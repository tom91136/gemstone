#ifndef GEMSTONE_HSA_EXECUTABLE_METADATA_HPP
#define GEMSTONE_HSA_EXECUTABLE_METADATA_HPP

#include <string>
#include <string_view>
#include <vector>

#include <hsa.h>
#include <stdint.h>

#include "gemstone/hsa/aql_execution.h"

namespace gemstone::hsa {

struct ExecutableKernelMetadata {
  std::string name;
  std::string symbol;
  uint32_t kernarg_size = 0;
  uint32_t explicit_argument_count = 0;
  std::vector<gemstone_hsa_kernarg_field> fields;
};

// Internal bridge for facades embedded with the HSA provider. The regular RPC
// path receives this metadata from the server catalog and copies it out under
// the executable lock; injected executors may still supply the local fallback.
bool copyExecutableKernelMetadata(hsa_executable_t executable, std::string_view name, ExecutableKernelMetadata &metadata);
bool copyExecutableKernelMetadata(hsa_executable_t executable, std::vector<ExecutableKernelMetadata> &metadata);

} // namespace gemstone::hsa

#endif
