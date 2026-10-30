#include <array>
#include <chrono>
#include <cstdint>
#include <cstring>
#include <thread>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <hsa/hsa.h>
#include <hsa/hsa_ext_amd.h>
#include <hsa/hsa_ext_image.h>

#include "conformance/device.hpp"

#include "images_image.hpp"
#include "test_support.hpp"
#include <fmt/format.h>

using namespace aspartame;

// The HSA images extension without samplers: images created over device memory, imported from and exported to the
// host, cleared and copied by the runtime, and loaded and stored by a kernel through their handles.
namespace hsa_images {

namespace {

void check(hsa_status_t status, const char *operation) {
  INFO(fmt::format("{} returned {:#x}", operation, static_cast<int>(status)));
  REQUIRE(status == HSA_STATUS_SUCCESS);
}

struct Regions {
  hsa_region_t kernarg{}, device{};
};

hsa_status_t findRegions(hsa_region_t region, void *data) {
  hsa_region_segment_t segment = HSA_REGION_SEGMENT_READONLY;
  hsa_region_global_flag_t flags{};
  bool allocatable = false;
  if (hsa_region_get_info(region, HSA_REGION_INFO_SEGMENT, &segment) != HSA_STATUS_SUCCESS || segment != HSA_REGION_SEGMENT_GLOBAL
      || hsa_region_get_info(region, HSA_REGION_INFO_GLOBAL_FLAGS, &flags) != HSA_STATUS_SUCCESS
      || hsa_region_get_info(region, HSA_REGION_INFO_RUNTIME_ALLOC_ALLOWED, &allocatable) != HSA_STATUS_SUCCESS || !allocatable)
    return HSA_STATUS_SUCCESS;
  auto &regions = *static_cast<Regions *>(data);
  if ((flags & HSA_REGION_GLOBAL_FLAG_KERNARG) != 0) regions.kernarg = region;
  else if (!regions.device.handle) regions.device = region;
  return HSA_STATUS_SUCCESS;
}

hsa_status_t findCpu(hsa_agent_t agent, void *data) {
  hsa_device_type_t type{};
  if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
  if (type == HSA_DEVICE_TYPE_CPU) *static_cast<hsa_agent_t *>(data) = agent;
  return HSA_STATUS_SUCCESS;
}

/** RGBA texels of 8-bit unsigned channels, as a kernel and the runtime see them. */
using Texel = std::array<uint8_t, 4>;

Texel transformed(const Texel &texel, uint32_t x, uint32_t y) {
  return {static_cast<uint8_t>(texel[1] + x), static_cast<uint8_t>(texel[0] ^ y), texel[3], texel[2]};
}

} // namespace

TEST_CASE("resources/hsa/image-extension", "[unified][resources]") {
  constexpr uint32_t width = 13;
  constexpr uint32_t height = 5;
  unified::test_runtime::Cleanup cleanup;
  check(hsa_init(), "init");
  cleanup.add([] { hsa_shut_down(); });
  const hsa_agent_t gpu{conformance::deviceSelection().nativeHandle};
  hsa_agent_t cpu{};
  check(hsa_iterate_agents(findCpu, &cpu), "agents");
  Regions host;
  Regions device;
  check(hsa_agent_iterate_regions(cpu, findRegions, &host), "host regions");
  check(hsa_agent_iterate_regions(gpu, findRegions, &device), "device regions");
  const hsa_region_t imageRegion = device.device.handle ? device.device : host.device;
  REQUIRE((host.kernarg.handle && imageRegion.handle));

  bool supported = false;
  uint16_t minor = 0;
  check(hsa_system_major_extension_supported(HSA_EXTENSION_IMAGES, 1, &minor, &supported), "extension query");
  REQUIRE(supported);
  // ROCr compares the major version with its internal table's, so it publishes no table for version 1; both export the
  // functions, which the table holds where there is one
  hsa_ext_images_1_00_pfn_t images{.hsa_ext_image_get_capability = hsa_ext_image_get_capability,
                                   .hsa_ext_image_data_get_info = hsa_ext_image_data_get_info,
                                   .hsa_ext_image_create = hsa_ext_image_create,
                                   .hsa_ext_image_destroy = hsa_ext_image_destroy,
                                   .hsa_ext_image_copy = hsa_ext_image_copy,
                                   .hsa_ext_image_import = hsa_ext_image_import,
                                   .hsa_ext_image_export = hsa_ext_image_export,
                                   .hsa_ext_image_clear = hsa_ext_image_clear,
                                   .hsa_ext_sampler_create = hsa_ext_sampler_create,
                                   .hsa_ext_sampler_destroy = hsa_ext_sampler_destroy};
  hsa_ext_images_1_00_pfn_t table{};
  if (hsa_system_get_major_extension_table(HSA_EXTENSION_IMAGES, 1, sizeof(table), &table) == HSA_STATUS_SUCCESS) {
    REQUIRE((table.hsa_ext_image_create && table.hsa_ext_image_import && table.hsa_ext_image_export));
    images = table;
  }

  const hsa_ext_image_format_t format{.channel_type = HSA_EXT_IMAGE_CHANNEL_TYPE_UNSIGNED_INT8,
                                      .channel_order = HSA_EXT_IMAGE_CHANNEL_ORDER_RGBA};
  uint32_t capabilities = 0;
  check(images.hsa_ext_image_get_capability(gpu, HSA_EXT_IMAGE_GEOMETRY_2D, &format, &capabilities), "capability");
  if (capabilities == HSA_EXT_IMAGE_CAPABILITY_NOT_SUPPORTED) SKIP("the GPU agent supports no 2D RGBA8 images");
  REQUIRE((capabilities & HSA_EXT_IMAGE_CAPABILITY_READ_ONLY) != 0);
  REQUIRE((capabilities & HSA_EXT_IMAGE_CAPABILITY_WRITE_ONLY) != 0);
  const hsa_ext_image_descriptor_t descriptor{
      .geometry = HSA_EXT_IMAGE_GEOMETRY_2D, .width = width, .height = height, .depth = 0, .array_size = 0, .format = format};
  hsa_ext_image_data_info_t info{};
  check(images.hsa_ext_image_data_get_info(gpu, &descriptor, HSA_ACCESS_PERMISSION_RW, &info), "data info");
  REQUIRE(info.size >= size_t(width) * height * 4);
  const auto image = [&](const char *name) {
    void *data = nullptr;
    check(hsa_memory_allocate(imageRegion, info.size + info.alignment, &data), name);
    cleanup.add([=] { hsa_memory_free(data); });
    const auto address = reinterpret_cast<uintptr_t>(data);
    void *aligned = reinterpret_cast<void *>((address + info.alignment - 1) / info.alignment * info.alignment);
    hsa_ext_image_t created{};
    check(images.hsa_ext_image_create(gpu, &descriptor, aligned, HSA_ACCESS_PERMISSION_RW, &created), name);
    cleanup.add([=] { images.hsa_ext_image_destroy(gpu, created); });
    return created;
  };
  const auto source = image("source image");
  const auto target = image("target image");

  const auto texels = iota(uint32_t{0}, width * height) | map([](uint32_t i) {
                        return Texel{static_cast<uint8_t>((i * 7) + 1), static_cast<uint8_t>(i * 3), static_cast<uint8_t>(0xf0 - i),
                                     static_cast<uint8_t>(i ^ 0x5a)};
                      })
                      | to_vector();
  // the runtime's copies may run on the device, so host data moves through system memory it reaches
  const auto staging = [&](size_t size) {
    void *result = nullptr;
    check(hsa_memory_allocate(host.kernarg, size, &result), "staging");
    cleanup.add([=] { hsa_memory_free(result); });
    return static_cast<uint8_t *>(result);
  };
  auto *upload = staging(texels.size() * sizeof(Texel));
  std::memcpy(upload, texels.data(), texels.size() * sizeof(Texel));
  const hsa_ext_image_region_t whole{.offset = {.x = 0, .y = 0, .z = 0}, .range = {.x = width, .y = height, .z = 1}};
  check(images.hsa_ext_image_import(gpu, upload, 0, 0, source, &whole), "import");
  const uint32_t fill[4] = {9, 8, 7, 6};
  check(images.hsa_ext_image_clear(gpu, target, fill, &whole), "clear");

  hsa_code_object_reader_t reader{};
  check(hsa_code_object_reader_create_from_memory(images_image.data(), images_image.size(), &reader), "reader");
  cleanup.add([=] { hsa_code_object_reader_destroy(reader); });
  hsa_profile_t profile{};
  check(hsa_agent_get_info(gpu, HSA_AGENT_INFO_PROFILE, &profile), "profile");
  hsa_executable_t executable{};
  check(hsa_executable_create_alt(profile, HSA_DEFAULT_FLOAT_ROUNDING_MODE_DEFAULT, nullptr, &executable), "executable");
  cleanup.add([=] { hsa_executable_destroy(executable); });
  check(hsa_executable_load_agent_code_object(executable, gpu, reader, nullptr, nullptr), "load");
  check(hsa_executable_freeze(executable, nullptr), "freeze");
  hsa_executable_symbol_t symbol{};
  check(hsa_executable_get_symbol_by_name(executable, "image_transform.kd", &gpu, &symbol), "symbol");
  uint64_t kernelObject = 0;
  uint32_t argumentSize = 0;
  uint32_t groupSize = 0;
  uint32_t privateSize = 0;
  check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &kernelObject), "kernel object");
  check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_KERNARG_SEGMENT_SIZE, &argumentSize), "argument size");
  check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_GROUP_SEGMENT_SIZE, &groupSize), "group size");
  check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_PRIVATE_SEGMENT_SIZE, &privateSize), "private size");
  REQUIRE(argumentSize >= 24);
  const auto allocate = [&](size_t size) {
    void *result = nullptr;
    check(hsa_memory_allocate(host.kernarg, size, &result), "allocate");
    cleanup.add([=] { hsa_memory_free(result); });
    return result;
  };
  auto *arguments = allocate(argumentSize);
  auto *dimensions = static_cast<uint32_t *>(allocate(64));
  std::memset(dimensions, 0xff, 64);
  std::memset(arguments, 0, argumentSize);
  std::memcpy(arguments, &source.handle, 8);
  std::memcpy(static_cast<char *>(arguments) + 8, &target.handle, 8);
  std::memcpy(static_cast<char *>(arguments) + 16, &dimensions, 8);

  hsa_signal_t completion{};
  check(hsa_signal_create(1, 0, nullptr, &completion), "completion signal");
  cleanup.add([=] { hsa_signal_destroy(completion); });
  hsa_queue_t *queue = nullptr;
  check(hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue), "queue");
  cleanup.add([=] { hsa_queue_destroy(queue); });
  const auto index = hsa_queue_add_write_index_relaxed(queue, 1);
  auto *packet = &static_cast<hsa_kernel_dispatch_packet_t *>(queue->base_address)[index & (queue->size - 1)];
  std::memset(packet, 0, sizeof(*packet));
  packet->setup = 2U << HSA_KERNEL_DISPATCH_PACKET_SETUP_DIMENSIONS;
  packet->workgroup_size_x = width;
  packet->workgroup_size_y = height;
  packet->workgroup_size_z = 1;
  packet->grid_size_x = width;
  packet->grid_size_y = height;
  packet->grid_size_z = 1;
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
  INFO(fmt::format("width {:#x}, height {:#x}", dimensions[0], dimensions[1]));
  REQUIRE((dimensions[0] == width && dimensions[1] == height));

  // the kernel transformed every texel; then a 4 by 2 block of the source is copied over the target at (6, 3), the
  // clear colour fills row 0 from x 10, and a 3 by 3 block is exported with a padded row pitch
  auto expected =
      iota(uint32_t{0}, width * height) | map([&](uint32_t i) { return transformed(texels[i], i % width, i / width); }) | to_vector();
  const hsa_dim3_t from{.x = 2, .y = 1, .z = 0};
  const hsa_dim3_t to{.x = 6, .y = 3, .z = 0};
  const hsa_dim3_t block{.x = 4, .y = 2, .z = 1};
  check(images.hsa_ext_image_copy(gpu, source, &from, target, &to, &block), "copy");
  for (uint32_t y = 0; y < block.y; ++y)
    for (uint32_t x = 0; x < block.x; ++x)
      expected[((to.y + y) * width) + to.x + x] = texels[((from.y + y) * width) + from.x + x];
  const hsa_ext_image_region_t corner{.offset = {.x = 10, .y = 0, .z = 0}, .range = {.x = 3, .y = 1, .z = 1}};
  check(images.hsa_ext_image_clear(gpu, target, fill, &corner), "region clear");
  for (uint32_t x = 10; x < width; ++x)
    expected[x] = {9, 8, 7, 6};
  auto *download = staging(expected.size() * sizeof(Texel));
  check(images.hsa_ext_image_export(gpu, target, download, 0, 0, &whole), "export");
  std::vector<Texel> actual(size_t{width} * height);
  std::memcpy(actual.data(), download, actual.size() * sizeof(Texel));
  for (uint32_t i = 0; i < actual.size(); ++i) {
    INFO(fmt::format("texel ({}, {})", i % width, i / width));
    REQUIRE(actual[i] == expected[i]);
  }
  constexpr size_t pitch = 5 * sizeof(Texel);
  auto *padded = staging(pitch * 3);
  std::memset(padded, 0xee, pitch * 3);
  const hsa_ext_image_region_t window{.offset = {.x = 5, .y = 2, .z = 0}, .range = {.x = 3, .y = 3, .z = 1}};
  check(images.hsa_ext_image_export(gpu, target, padded, pitch, 0, &window), "pitched export");
  for (uint32_t y = 0; y < 3; ++y)
    for (uint32_t x = 0; x < 5; ++x) {
      INFO(fmt::format("pitched texel ({}, {})", x, y));
      Texel found{};
      std::memcpy(found.data(), padded + (y * pitch) + (x * sizeof(Texel)), sizeof(Texel));
      REQUIRE(found == (x < 3 ? expected[((window.offset.y + y) * width) + window.offset.x + x] : Texel{0xee, 0xee, 0xee, 0xee}));
    }
}

} // namespace hsa_images
