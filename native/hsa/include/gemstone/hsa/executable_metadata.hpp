#pragma once

#include <string>
#include <string_view>
#include <vector>

#include <hsa.h>
#include <stdint.h>

#include "gemstone/hsa/aql_execution.hpp"

namespace gemstone::hsa {

struct ExecutableKernelMetadata {
  std::string name;
  std::string symbol;
  KernelLayout layout;
};

// Bridge for facades embedded with the HSA provider; copies the server-owned layout under the executable lock.
bool copyExecutableKernelMetadata(hsa_executable_t executable, std::string_view name, ExecutableKernelMetadata &metadata);
bool copyExecutableKernelMetadata(hsa_executable_t executable, std::vector<ExecutableKernelMetadata> &metadata);

} // namespace gemstone::hsa
