#pragma region case: hsa-amd-async-memory-copy
#pragma region do: {cxx} -std=c++17 -O2 -Wall -Wextra -Werror -Wno-pedantic -Wno-c++20-compat -I{hsa_include} {input} -L{rocr_lib} -Wl,-rpath,{rocr_lib} -lhsa-runtime64 -o {output}
#pragma region do: {output}
#pragma region requires: pass

// Adapted from ROCR-Runtime rocrtst/samples/async_mem_copy.
// Copyright (c) 2017, Advanced Micro Devices, Inc. All rights reserved.
// See ../LICENSE-ROCR-NCSA.

#include "hsa.h"
#include "hsa_ext_amd.h"

#include <cassert>
#include <cstddef>
#include <cstdint>
#include <cstdio>

namespace {

struct AgentPair
{
    hsa_agent_t cpu{};
    hsa_agent_t gpu{};
};

struct PoolSelection
{
    hsa_amd_memory_pool_t pool{};
};

hsa_status_t collect_agent(hsa_agent_t agent, void* data)
{
    auto& pair = *static_cast<AgentPair*>(data);
    hsa_device_type_t type = HSA_DEVICE_TYPE_CPU;
    const hsa_status_t status = hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type);
    if(status != HSA_STATUS_SUCCESS)
        return status;
    if(type == HSA_DEVICE_TYPE_CPU)
        pair.cpu = agent;
    if(type == HSA_DEVICE_TYPE_GPU)
        pair.gpu = agent;
    return HSA_STATUS_SUCCESS;
}

hsa_status_t select_global_pool(hsa_amd_memory_pool_t pool, void* data)
{
    hsa_amd_segment_t segment = HSA_AMD_SEGMENT_GLOBAL;
    bool allocatable = false;
    if(hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_SEGMENT, &segment) != HSA_STATUS_SUCCESS
       || segment != HSA_AMD_SEGMENT_GLOBAL
       || hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_RUNTIME_ALLOC_ALLOWED, &allocatable)
              != HSA_STATUS_SUCCESS
       || !allocatable)
        return HSA_STATUS_SUCCESS;
    static_cast<PoolSelection*>(data)->pool = pool;
    return HSA_STATUS_INFO_BREAK;
}

void await(hsa_signal_t signal)
{
    const hsa_signal_value_t result = hsa_signal_wait_scacquire(
        signal, HSA_SIGNAL_CONDITION_LT, 1, UINT64_MAX, HSA_WAIT_STATE_BLOCKED);
    assert(result == 0);
}

} // namespace

int main()
{
    assert(hsa_init() == HSA_STATUS_SUCCESS);
    AgentPair agents{};
    assert(hsa_iterate_agents(collect_agent, &agents) == HSA_STATUS_SUCCESS);
    assert(agents.cpu.handle != 0 && agents.gpu.handle != 0);

    PoolSelection cpu_pool{};
    PoolSelection gpu_pool{};
    assert(hsa_amd_agent_iterate_memory_pools(agents.cpu, select_global_pool, &cpu_pool) == HSA_STATUS_INFO_BREAK);
    assert(hsa_amd_agent_iterate_memory_pools(agents.gpu, select_global_pool, &gpu_pool) == HSA_STATUS_INFO_BREAK);

    constexpr std::size_t bytes = 4096;
    void* source = nullptr;
    void* device = nullptr;
    void* result = nullptr;
    assert(hsa_amd_memory_pool_allocate(cpu_pool.pool, bytes, 0, &source) == HSA_STATUS_SUCCESS);
    assert(hsa_amd_memory_pool_allocate(gpu_pool.pool, bytes, 0, &device) == HSA_STATUS_SUCCESS);
    assert(hsa_amd_memory_pool_allocate(cpu_pool.pool, bytes, 0, &result) == HSA_STATUS_SUCCESS);
    assert(hsa_amd_memory_fill(source, 0x5aa55aa5U, bytes / sizeof(std::uint32_t)) == HSA_STATUS_SUCCESS);
    assert(hsa_amd_memory_fill(result, 0U, bytes / sizeof(std::uint32_t)) == HSA_STATUS_SUCCESS);

    const hsa_agent_t access[] = {agents.cpu, agents.gpu};
    assert(hsa_amd_agents_allow_access(2, access, nullptr, source) == HSA_STATUS_SUCCESS);
    assert(hsa_amd_agents_allow_access(2, access, nullptr, device) == HSA_STATUS_SUCCESS);
    assert(hsa_amd_agents_allow_access(2, access, nullptr, result) == HSA_STATUS_SUCCESS);

    hsa_signal_t completion{};
    assert(hsa_signal_create(1, 0, nullptr, &completion) == HSA_STATUS_SUCCESS);
    assert(hsa_amd_memory_async_copy(
               device, agents.gpu, source, agents.cpu, bytes, 0, nullptr, completion)
           == HSA_STATUS_SUCCESS);
    await(completion);
    hsa_signal_store_screlease(completion, 1);
    assert(hsa_amd_memory_async_copy(
               result, agents.cpu, device, agents.gpu, bytes, 0, nullptr, completion)
           == HSA_STATUS_SUCCESS);
    await(completion);

    const auto* values = static_cast<const std::uint32_t*>(result);
    for(std::size_t index = 0; index < bytes / sizeof(std::uint32_t); ++index)
        assert(values[index] == 0x5aa55aa5U);

    assert(hsa_signal_destroy(completion) == HSA_STATUS_SUCCESS);
    assert(hsa_amd_memory_pool_free(result) == HSA_STATUS_SUCCESS);
    assert(hsa_amd_memory_pool_free(device) == HSA_STATUS_SUCCESS);
    assert(hsa_amd_memory_pool_free(source) == HSA_STATUS_SUCCESS);
    assert(hsa_shut_down() == HSA_STATUS_SUCCESS);
    std::puts("pass");
    return 0;
}
