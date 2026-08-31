// SPDX-License-Identifier: Apache-2.0
#include <cassert>
#include <cstdlib>
#include <cstring>

#include <hsa/hsa.h>

int main() {
  // The public provider must fail closed when no authenticated Gemstone
  // execution endpoint is configured; it must not fall back to a physical
  // ROCr installation in the process.
  unsetenv("GEMSTONE_RPC_PORT");
  unsetenv("GEMSTONE_RPC_TOKEN");
  assert(hsa_init() == HSA_STATUS_ERROR_NOT_INITIALIZED);

  const char *message = nullptr;
  assert(hsa_status_string(HSA_STATUS_ERROR_NOT_INITIALIZED, &message) == HSA_STATUS_SUCCESS);
  assert(message != nullptr && std::strcmp(message, "runtime not initialized") == 0);
  assert(hsa_shut_down() == HSA_STATUS_ERROR_NOT_INITIALIZED);
  return 0;
}
