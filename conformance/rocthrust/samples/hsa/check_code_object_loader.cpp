#pragma region case: hsa-code-object-loader
#pragma region do: {cxx} -std=c++17 -O2 -Wall -Wextra -Werror -Wno-pedantic -Wno-c++20-compat -I{hsa_include} {input} -L{rocr_lib} -Wl,-rpath,{rocr_lib} -lhsa-runtime64 -ldl -o {output}
#pragma region do: {output} {hsa_vector_copy}
#pragma region requires: pass
// SPDX-License-Identifier: Apache-2.0
// The code object supplied to this program is built from ROCr's pinned,
// officially licensed vector_copy_kernel.cl fixture. All producer interaction
// remains through the stock public HSA API.
#include <cassert>
#include <cstdint>
#include <cstdio>
#include <fstream>
#include <iterator>
#include <vector>

#include <dlfcn.h>

#include "hsa.h"

namespace {

hsa_status_t find_gpu(hsa_agent_t agent, void *data) {
  hsa_device_type_t type = HSA_DEVICE_TYPE_CPU;
  if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
  if (type == HSA_DEVICE_TYPE_GPU) {
    *static_cast<hsa_agent_t *>(data) = agent;
    return HSA_STATUS_INFO_BREAK;
  }
  return HSA_STATUS_SUCCESS;
}

} // namespace

int main(int argc, char **argv) {
  assert(argc == 2);
  std::ifstream stream(argv[1], std::ios::binary);
  assert(stream.good());
  const std::vector<uint8_t> image((std::istreambuf_iterator<char>(stream)), std::istreambuf_iterator<char>());
  assert(!image.empty());

  assert(hsa_init() == HSA_STATUS_SUCCESS);
  hsa_agent_t gpu{};
  const hsa_status_t iterate = hsa_iterate_agents(find_gpu, &gpu);
  assert(iterate == HSA_STATUS_INFO_BREAK && gpu.handle != 0);

  hsa_code_object_reader_t reader{};
  assert(hsa_code_object_reader_create_from_memory(image.data(), image.size(), &reader) == HSA_STATUS_SUCCESS);
  hsa_executable_t executable{};
  assert(hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_DEFAULT, nullptr, &executable) == HSA_STATUS_SUCCESS);
  hsa_loaded_code_object_t loaded{};
  const hsa_status_t load = hsa_executable_load_agent_code_object(executable, gpu, reader, nullptr, &loaded);

  if (load != HSA_STATUS_SUCCESS) {
    using LoaderError = const char *(*)();
    const auto loader_error = reinterpret_cast<LoaderError>(dlsym(RTLD_DEFAULT, "gemstone_hsakmt_loader_error"));
    const char *detail = loader_error == nullptr ? "unavailable" : loader_error();
    std::fprintf(stderr,
                 "stock public HSA loader rejected the AMDGPU code object: "
                 "hsa_status_t=%d, virtual-driver-detail=%s\n",
                 static_cast<int>(load), detail);
    assert(hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS);
    assert(hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS);
    assert(hsa_shut_down() == HSA_STATUS_SUCCESS);
    return 2;
  }
  assert(hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS);
  assert(hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS);
  assert(hsa_shut_down() == HSA_STATUS_SUCCESS);
  std::puts("pass");
  return 0;
}
