#include "gemstone_execution_rpc.h"

#include <assert.h>
#include <stdint.h>

int main() {
  auto *client = gemstone_execution_client_create_from_environment();
  assert(client != nullptr);
  assert(gemstone_execution_register_allocation(client, 1, 8, GEMSTONE_ALLOCATION_SHARED) == 0);
  const uint8_t byte = 42;
  assert(gemstone_execution_write_allocation(client, 1, 0, &byte, 1) != 0);
  assert(gemstone_execution_release_allocation(client, 1) != 0);
  gemstone_execution_client_destroy(client);
  return 0;
}
