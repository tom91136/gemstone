#include "struct_alignment.hpp"

#include <algorithm>
#include <chrono>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <string>
#include <thread>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <hsa/hsa.h>

#include "conformance/device.hpp"

#include "struct_alignment_image.hpp"
#include "test_support.hpp"
#include <fmt/format.h>

namespace {
using namespace unified::struct_alignment;

void check(hsa_status_t status, const char *operation) {
  INFO(fmt::format("{} returned {}", operation, static_cast<int>(status)));
  REQUIRE(status == HSA_STATUS_SUCCESS);
}

hsa_status_t findCpu(hsa_agent_t agent, void *data) {
  hsa_device_type_t type = HSA_DEVICE_TYPE_GPU;
  if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
  if (type == HSA_DEVICE_TYPE_CPU) *static_cast<hsa_agent_t *>(data) = agent;
  return HSA_STATUS_SUCCESS;
}

hsa_status_t findKernarg(hsa_region_t region, void *data) {
  hsa_region_segment_t segment = HSA_REGION_SEGMENT_READONLY;
  hsa_region_global_flag_t flags{};
  if (hsa_region_get_info(region, HSA_REGION_INFO_SEGMENT, &segment) != HSA_STATUS_SUCCESS || segment != HSA_REGION_SEGMENT_GLOBAL
      || hsa_region_get_info(region, HSA_REGION_INFO_GLOBAL_FLAGS, &flags) != HSA_STATUS_SUCCESS)
    return HSA_STATUS_SUCCESS;
  if ((flags & HSA_REGION_GLOBAL_FLAG_KERNARG) != 0) {
    *static_cast<hsa_region_t *>(data) = region;
    return HSA_STATUS_INFO_BREAK;
  }
  return HSA_STATUS_SUCCESS;
}

template <class T> void run(const char *kernelName) {
  constexpr uint32_t workgroup = 64;
  unified::test_runtime::Cleanup cleanup;
  check(hsa_init(), "init");
  cleanup.add([] { hsa_shut_down(); });
  hsa_agent_t cpu{};
  check(hsa_iterate_agents(findCpu, &cpu), "agents");
  const hsa_agent_t gpu{conformance::deviceSelection().nativeHandle};
  hsa_region_t region{};
  const auto found = hsa_agent_iterate_regions(cpu, findKernarg, &region);
  REQUIRE(((found == HSA_STATUS_SUCCESS || found == HSA_STATUS_INFO_BREAK) && region.handle));

  hsa_code_object_reader_t reader{};
  check(hsa_code_object_reader_create_from_memory(struct_alignment_image.data(), struct_alignment_image.size(), &reader), "reader");
  cleanup.add([=] { hsa_code_object_reader_destroy(reader); });
  hsa_profile_t profile{};
  check(hsa_agent_get_info(gpu, HSA_AGENT_INFO_PROFILE, &profile), "profile");
  hsa_executable_t executable{};
  check(hsa_executable_create_alt(profile, HSA_DEFAULT_FLOAT_ROUNDING_MODE_DEFAULT, nullptr, &executable), "executable");
  cleanup.add([=] { hsa_executable_destroy(executable); });
  check(hsa_executable_load_agent_code_object(executable, gpu, reader, nullptr, nullptr), "load");
  check(hsa_executable_freeze(executable, nullptr), "freeze");
  hsa_executable_symbol_t symbol{};
  check(hsa_executable_get_symbol_by_name(executable, (std::string(kernelName) + ".kd").c_str(), &gpu, &symbol), "symbol");
  uint64_t kernelObject = 0;
  uint32_t argumentSize = 0;
  uint32_t groupSize = 0;
  uint32_t privateSize = 0;
  check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &kernelObject), "kernel object");
  check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_KERNARG_SEGMENT_SIZE, &argumentSize), "argument size");
  check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_GROUP_SEGMENT_SIZE, &groupSize), "group size");
  check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_PRIVATE_SEGMENT_SIZE, &privateSize), "private size");
  REQUIRE((kernelObject && argumentSize >= 28));

  const auto allocate = [&](size_t size) {
    void *result = nullptr;
    check(hsa_memory_allocate(region, size, &result), "allocate");
    cleanup.add([=] { hsa_memory_free(result); });
    return result;
  };
  const auto expected = fixture<T>();
  auto *image = static_cast<uint8_t *>(allocate(expected.input.size()));
  auto *out = static_cast<uint32_t *>(allocate(expected.words.size() * sizeof(uint32_t)));
  auto *arguments = static_cast<uint8_t *>(allocate(argumentSize));
  REQUIRE(reinterpret_cast<uintptr_t>(image) % BaseAlignment == 0);
  std::memcpy(image, expected.input.data(), expected.input.size());
  std::fill_n(out, expected.words.size(), 0xdeadbeefU);
  std::memset(arguments, 0, argumentSize);
  const auto offset = static_cast<uint32_t>(Fixture<T>::Offset);
  const uint32_t count = Count;
  std::memcpy(arguments, &image, sizeof(image));
  std::memcpy(arguments + 8, &offset, sizeof(offset));
  std::memcpy(arguments + 16, &out, sizeof(out));
  std::memcpy(arguments + 24, &count, sizeof(count));

  hsa_signal_t completion{};
  check(hsa_signal_create(1, 0, nullptr, &completion), "completion signal");
  cleanup.add([=] { hsa_signal_destroy(completion); });
  hsa_queue_t *queue = nullptr;
  check(hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue), "queue");
  cleanup.add([=] { hsa_queue_destroy(queue); });
  const auto index = hsa_queue_add_write_index_relaxed(queue, 1);
  auto *packet = &static_cast<hsa_kernel_dispatch_packet_t *>(queue->base_address)[index & (queue->size - 1)];
  std::memset(packet, 0, sizeof(*packet));
  packet->setup = 1U << HSA_KERNEL_DISPATCH_PACKET_SETUP_DIMENSIONS;
  packet->workgroup_size_x = workgroup;
  packet->workgroup_size_y = packet->workgroup_size_z = 1;
  packet->grid_size_x = Count;
  packet->grid_size_y = packet->grid_size_z = 1;
  packet->kernel_object = kernelObject;
  packet->kernarg_address = arguments;
  packet->completion_signal = completion;
  packet->group_segment_size = groupSize;
  packet->private_segment_size = privateSize;
  const uint16_t header = (HSA_PACKET_TYPE_KERNEL_DISPATCH << HSA_PACKET_HEADER_TYPE)
                          | (HSA_FENCE_SCOPE_SYSTEM << HSA_PACKET_HEADER_SCACQUIRE_FENCE_SCOPE)
                          | (HSA_FENCE_SCOPE_SYSTEM << HSA_PACKET_HEADER_SCRELEASE_FENCE_SCOPE);
  __atomic_store_n(&packet->header, header, __ATOMIC_RELEASE);
  hsa_signal_store_screlease(queue->doorbell_signal, static_cast<hsa_signal_value_t>(index));
  const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(30);
  while (hsa_signal_load_scacquire(completion) == 1 && std::chrono::steady_clock::now() < deadline)
    std::this_thread::yield();
  {
    INFO("HSA dispatch failed or timed out");
    REQUIRE(hsa_signal_load_scacquire(completion) == 0);
  }
  verify(expected, std::vector<uint8_t>(image, image + expected.input.size()), std::vector<uint32_t>(out, out + expected.words.size()));
}

} // namespace

TEST_CASE("memory/hsa/struct-alignment/packed", "[unified][memory]") { run<Packed>("pose_packed"); }

TEST_CASE("memory/hsa/struct-alignment/packed-aligned-2", "[unified][memory]") { run<Packed2>("pose_packed_aligned_2"); }

TEST_CASE("memory/hsa/struct-alignment/packed-aligned-4", "[unified][memory]") { run<Packed4>("pose_packed_aligned_4"); }

TEST_CASE("memory/hsa/struct-alignment/packed-aligned-8", "[unified][memory]") { run<Packed8>("pose_packed_aligned_8"); }

TEST_CASE("memory/hsa/struct-alignment/natural", "[unified][memory]") { run<Natural>("pose_natural"); }

TEST_CASE("memory/hsa/struct-alignment/aligned-16", "[unified][memory]") { run<Aligned16>("pose_aligned_16"); }

TEST_CASE("memory/hsa/struct-alignment/mixed-packed", "[unified][memory]") { run<MixedPacked>("pose_mixed_packed"); }

TEST_CASE("memory/hsa/struct-alignment/mixed-natural", "[unified][memory]") { run<MixedNatural>("pose_mixed_natural"); }
