// SPDX-License-Identifier: Apache-2.0
#include <cassert>
#include <cstdlib>
#include <cstring>

#include <hsa/hsa.h>
#include <hsa/hsa_ext_amd.h>

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
  uint16_t version_major = 0;
  assert(hsa_system_get_info(HSA_SYSTEM_INFO_VERSION_MAJOR, &version_major) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  uint32_t validation = 0;
  assert(hsa_executable_validate(hsa_executable_t{1}, &validation) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  volatile hsa_signal_value_t *signal_value = nullptr;
  assert(hsa_amd_signal_value_pointer(hsa_signal_t{1}, &signal_value) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_signal_async_handler(hsa_signal_t{1}, HSA_SIGNAL_CONDITION_EQ, 0, nullptr, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_memory_lock(nullptr, 1, nullptr, 0, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_memory_unlock(nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_pointer_info(nullptr, nullptr, nullptr, nullptr, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_profiling_async_copy_enable(false) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_profiling_get_async_copy_time(hsa_signal_t{1}, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_svm_attributes_set(nullptr, 0, nullptr, 0) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_register_system_event_handler(nullptr, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_shut_down() == HSA_STATUS_ERROR_NOT_INITIALIZED);
  return 0;
}
