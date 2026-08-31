#pragma region case: hsa-bootstrap
#pragma region do: {cxx} -std=c++17 -O2 -Wall -Wextra -Werror -Wno-pedantic -Wno-c++20-compat -I{hsa_include} {input} -L{rocr_lib} -Wl,-rpath,{rocr_lib} -lhsa-runtime64 -ldl -o {output}
#pragma region do: {output}
#pragma region requires: pass
// SPDX-License-Identifier: Apache-2.0
#include <algorithm>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <cstdlib>

#include "hsa.h"

namespace {

struct Agents {
  hsa_agent_t gpu{};
  uint32_t gpu_count = 0;
};

bool check(hsa_status_t status, const char *operation) {
  if (status == HSA_STATUS_SUCCESS) return true;
  const char *message = nullptr;
  hsa_status_string(status, &message);
  std::fprintf(stderr, "%s failed: %s (%u)\n", operation, message == nullptr ? "unknown HSA status" : message,
               static_cast<unsigned>(status));
  return false;
}

hsa_status_t collect_agent(hsa_agent_t agent, void *data) {
  auto &agents = *static_cast<Agents *>(data);
  hsa_device_type_t type = HSA_DEVICE_TYPE_CPU;
  const hsa_status_t status = hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type);
  if (status != HSA_STATUS_SUCCESS) return status;
  if (type == HSA_DEVICE_TYPE_GPU) {
    agents.gpu = agent;
    ++agents.gpu_count;
  }
  return HSA_STATUS_SUCCESS;
}

} // namespace

int main() {
  if (!check(hsa_init(), "hsa_init")) return 1;

  Agents agents{};
  if (!check(hsa_iterate_agents(collect_agent, &agents), "hsa_iterate_agents")) return 2;
  if (agents.gpu_count != 1) {
    std::fprintf(stderr, "expected one virtual GPU, found %u\n", agents.gpu_count);
    return 3;
  }

  char name[64]{};
  hsa_agent_feature_t features{};
  uint32_t queue_max = 0;
  if (!check(hsa_agent_get_info(agents.gpu, HSA_AGENT_INFO_NAME, name), "HSA_AGENT_INFO_NAME")
      || !check(hsa_agent_get_info(agents.gpu, HSA_AGENT_INFO_FEATURE, &features), "HSA_AGENT_INFO_FEATURE")
      || !check(hsa_agent_get_info(agents.gpu, HSA_AGENT_INFO_QUEUE_MAX_SIZE, &queue_max), "HSA_AGENT_INFO_QUEUE_MAX_SIZE"))
    return 4;
  const char *expected_target = std::getenv("GEMSTONE_AMDGPU_TARGET");
  if ((expected_target != nullptr && std::strcmp(name, expected_target) != 0)
      || (features & HSA_AGENT_FEATURE_KERNEL_DISPATCH) == 0 || queue_max < 64) {
    std::fprintf(stderr, "unexpected virtual agent: name=%s features=%u queue-max=%u\n", name, static_cast<unsigned>(features), queue_max);
    return 5;
  }

  hsa_queue_t *queue = nullptr;
  if (!check(hsa_queue_create(agents.gpu, std::min(queue_max, 64U), HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue),
             "hsa_queue_create"))
    return 6;
  if (queue == nullptr) return 7;

  // This is the stock public API. In ROCr's normal AQL signal implementation
  // it reaches the replacement KMT's Queue_DoorBell_aql with an ordinary CPU
  // store; no Gemstone producer shim or DTIF hook participates.
  hsa_signal_store_screlease(queue->doorbell_signal, 0);
  if (!check(hsa_queue_destroy(queue), "hsa_queue_destroy")) return 8;
  if (!check(hsa_shut_down(), "hsa_shut_down")) return 9;
  std::puts("pass");
  return 0;
}
