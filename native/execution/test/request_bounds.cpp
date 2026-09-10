// SPDX-License-Identifier: Apache-2.0
#include <cassert>
#include <cstdlib>
#include <cstring>
#include <cstdint>
#include <vector>
#include <sys/wait.h>
#include <unistd.h>
#include "../gemstone_execution_rpc.h"

int main() {
  constexpr uint32_t limit = 128U * 1024U * 1024U;
  for (int test = 0; test < 8; ++test) {
    const pid_t child = fork();
    assert(child >= 0);
    if (child == 0) {
      setenv("GEMSTONE_RPC_TOKEN", "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef", 1);
      unsetenv("GEMSTONE_RPC_PORT");
      auto *client = gemstone_execution_client_create_from_environment();
      assert(client != nullptr);
      const auto *unreadable = reinterpret_cast<const uint8_t *>(uintptr_t{1});
      uint64_t identity = 0;
      int result = 0;
      if (test == 0) result = gemstone_execution_write_allocation(client, 1, 0, unreadable, limit);
      if (test == 1) result = gemstone_execution_load_program(client, GEMSTONE_PROGRAM_AMDGPU_CODE_OBJECT, unreadable, limit, &identity);
      if (test == 2) result = gemstone_execution_write_program_global(client, 1, "global", 0, unreadable, limit);
      if (test == 3) result = gemstone_execution_read_allocation(client, 1, 0, const_cast<uint8_t *>(unreadable), limit + 1);
      if (test == 4 || test == 5) {
        gemstone_kernel_argument arguments[2]{};
        for (auto &argument : arguments) {
          argument.kind = GEMSTONE_ARGUMENT_SCALAR;
          argument.scalar_bytes = unreadable;
          argument.scalar_size = limit / 2;
        }
        const gemstone_dimensions one{1, 1, 1};
        result = gemstone_execution_launch(client, 1, "kernel", one, one, one, 0, 0,
                                          arguments, test == 4 ? 2 : UINT32_MAX, &identity);
      }
      if (test == 6) {
        const gemstone_dimensions one{1, 1, 1};
        result = gemstone_execution_launch_with_relocations(client, 1, "kernel", one, one, one, 0, 0, nullptr, 0,
            reinterpret_cast<const gemstone_pointer_relocation *>(unreadable), UINT32_MAX, &identity);
      }
      if (test == 7) {
        std::vector<uint8_t> boundary(limit - 20U);
        result = gemstone_execution_write_allocation(client, 1, 0, boundary.data(), static_cast<uint32_t>(boundary.size()));
      }
      assert(result != 0);
      assert((std::strstr(gemstone_execution_client_error(client), "payload limit") != nullptr) == (test != 7));
      gemstone_execution_client_destroy(client);
      _exit(0);
    }
    int status = 0;
    assert(waitpid(child, &status, 0) == child);
    assert(WIFEXITED(status) && WEXITSTATUS(status) == 0);
  }
}
