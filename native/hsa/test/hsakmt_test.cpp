#include <array>
#include <chrono>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <fstream>
#include <ios>
#include <iterator>
#include <utility>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <hsakmt/hsakmt.h>

#include "gemstone/hsa/aql.hpp"
#include "gemstone_execution_rpc.hpp"
#include <gemstone/bytes.hpp>
#include <gemstone/process.hpp>
#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include "code_object_fixture.hpp"
#include "hsakmt_test_support.hpp"
#include <environment_keys.hpp>

namespace {

struct alignas(8) SignalStorage {
  uint64_t kind = 0;
  int64_t value = 1;
  uint64_t eventMailbox = 0;
  uint32_t eventId = 0;
  uint32_t reserved = 0;

  [[nodiscard]] int64_t load() const { return __atomic_load_n(&value, __ATOMIC_ACQUIRE); }
};
static_assert(offsetof(SignalStorage, value) == 8);
static_assert(offsetof(SignalStorage, eventId) == 24);

using Packet = std::array<uint8_t, gemstone::hsa::AqlPacketSize>;

Packet kernelPacket(uint64_t kernelObject, uint64_t kernarg, SignalStorage &completion, uint16_t width = 1, uint32_t grid = 1) {
  Packet packet{};
  gemstone::writeNative<uint16_t>(packet.data(), 0, static_cast<uint16_t>(gemstone::hsa::PacketType::KernelDispatch));
  gemstone::writeNative<uint16_t>(packet.data(), 2, 1);
  gemstone::writeNative<uint16_t>(packet.data(), 4, width);
  gemstone::writeNative<uint16_t>(packet.data(), 6, 1);
  gemstone::writeNative<uint16_t>(packet.data(), 8, 1);
  gemstone::writeNative<uint32_t>(packet.data(), 12, grid);
  gemstone::writeNative<uint32_t>(packet.data(), 16, 1);
  gemstone::writeNative<uint32_t>(packet.data(), 20, 1);
  gemstone::writeNative<uint64_t>(packet.data(), 32, kernelObject);
  gemstone::writeNative<uint64_t>(packet.data(), 40, kernarg);
  gemstone::writeNative<uint64_t>(packet.data(), 56, reinterpret_cast<uint64_t>(&completion));
  return packet;
}

Packet barrierPacket(SignalStorage &completion) {
  Packet packet{};
  gemstone::writeNative<uint16_t>(packet.data(), 0, static_cast<uint16_t>(gemstone::hsa::PacketType::BarrierAnd));
  gemstone::writeNative<uint64_t>(packet.data(), 56, reinterpret_cast<uint64_t>(&completion));
  return packet;
}

int32_t word(const uint8_t *bytes, size_t offset) {
  int32_t value = 0;
  std::memcpy(&value, bytes + offset, sizeof(value));
  return value;
}

} // namespace

TEST_CASE("HSA KMT memory, events, and direct queues", "[hsa][kmt]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  REQUIRE((hsaKmtOpenKFD() == HSAKMT_STATUS_SUCCESS));
  REQUIRE((hsaKmtRuntimeEnable(nullptr, false) == HSAKMT_STATUS_NOT_SUPPORTED));
  HSAuint32 runtimeCapabilities = UINT32_MAX;
  REQUIRE((hsaKmtGetRuntimeCapabilities(&runtimeCapabilities) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((runtimeCapabilities == 0));

  // The fixture kernel takes an output pointer and a 32-bit seed: a 12-byte kernarg segment.
  const auto codeObject = gemstone::hsa::test::codeObjectFixture();
  {
    const gemstone::TemporaryDirectory directory("gemstone-hsakmt-XXXXXX");
    REQUIRE((directory));
    const auto dump = directory.file("rejected.co");
    const gemstone_test::Environment rejectedCodeObject(gemstone::environment::HsaRejectedCodeObject, dump.c_str());
    fixture.rejectNext(gemstone::execution::operation::LoadProgram);
    const std::array<gemstone::hsakmt::LoadedKernel, 1> rejected{{{.object = 0x123600, .name = "add", .kernargSize = 12}}};
    REQUIRE((!gemstone::hsakmt::installCodeObject(codeObject, rejected)));
    REQUIRE((gemstone::hsakmt::loaderError() == "LoadProgram rejected"));
    std::ifstream written(dump, std::ios::binary);
    REQUIRE((std::vector<uint8_t>(std::istreambuf_iterator<char>(written), {}) == codeObject));
    REQUIRE((fixture.programLoads.load() == 0));
  }
  const std::array<gemstone::hsakmt::LoadedKernel, 1> mismatched{{{.object = 0x123500, .name = "mismatched", .kernargSize = 20}}};
  REQUIRE((!gemstone::hsakmt::installCodeObject(codeObject, mismatched)));
  REQUIRE((gemstone::hsakmt::loaderError() == "execution manifest disagrees with loaded kernarg for kernel mismatched"));
  REQUIRE((fixture.programLoads.load() == 1 && fixture.programUnloads.load() == 1));
  const std::array<gemstone::hsakmt::LoadedKernel, 2> loaded{
      {{.object = 0x123300, .name = "internal_add", .kernargSize = 12}, {.object = 0x123400, .name = "add", .kernargSize = 12}}};
  REQUIRE((gemstone::hsakmt::installCodeObject(codeObject, loaded)));
  REQUIRE((fixture.programLoads.load() == 2 && fixture.lastProgramImage() == codeObject));
  // ROCr's symbol iterator also reports kernels from earlier code objects.
  // Repeated notifications must not reload or lose an existing binding.
  REQUIRE((gemstone::hsakmt::installCodeObject(codeObject, loaded)));
  REQUIRE((fixture.programLoads.load() == 2 && gemstone::hsakmt::loaderError().empty()));

  HsaVersionInfo version{};
  REQUIRE((hsaKmtGetVersion(&version) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((version.KernelInterfaceMajorVersion == 1));
  HsaSystemProperties system{};
  REQUIRE((hsaKmtAcquireSystemProperties(&system) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((system.NumNodes == 2));
  HsaNodeProperties cpu{};
  HsaNodeProperties gpu{};
  REQUIRE((hsaKmtGetNodeProperties(0, &cpu) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((hsaKmtGetNodeProperties(1, &gpu) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((cpu.NumCPUCores != 0 && cpu.NumFComputeCores == 0));
  REQUIRE((gpu.NumCPUCores == 0 && gpu.NumFComputeCores != 0));
  REQUIRE((gpu.Capability.ui32.DoorbellType == 2));

  HsaMemFlags flags{};
  flags.ui32.HostAccess = 1;
  constexpr uint64_t uploadChunk = uint64_t{64} * 1024;
  constexpr uint64_t primaryAllocationSize = 2 * uploadChunk;
  void *allocation = nullptr;
  const auto allocationsBefore = fixture.allocationCount();
  fixture.rejectNext(gemstone::execution::operation::RegisterAllocationAddress);
  REQUIRE((hsaKmtAllocMemory(0, primaryAllocationSize, flags, &allocation) != HSAKMT_STATUS_SUCCESS));
  REQUIRE((fixture.allocationCount() == allocationsBefore));
  REQUIRE((hsaKmtAllocMemory(0, primaryAllocationSize, flags, &allocation) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((allocation));
  fixture.checkAllocationAddress(allocation);
  auto *bytes = static_cast<uint8_t *>(allocation);
  void *unrelatedAllocation = nullptr;
  REQUIRE((hsaKmtAllocMemory(1, 4096, flags, &unrelatedAllocation) == HSAKMT_STATUS_SUCCESS));
  auto *unrelatedBytes = static_cast<uint8_t *>(unrelatedAllocation);
  unrelatedBytes[17] = 0x33;
  REQUIRE((hsaKmtMapMemoryToGPU(unrelatedAllocation, 4096, nullptr) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((fixture.writtenBytes(unrelatedAllocation) == 4096));
  uint64_t alternate = 1;
  REQUIRE((hsaKmtMapMemoryToGPU(allocation, primaryAllocationSize, &alternate) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((hsaKmtMapMemoryToGPU(bytes + 4096, 4096, nullptr) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((hsaKmtUnmapMemoryToGPU(bytes + 4096) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((hsaKmtUnmapMemoryToGPU(allocation) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((alternate == 0));
  HsaPointerInfo pointer{};
  REQUIRE((hsaKmtQueryPointerInfo(bytes + 17, &pointer) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((pointer.CPUAddress == allocation && pointer.SizeInBytes == primaryAllocationSize));

  HsaEventDescriptor descriptor{};
  HsaEvent *event = nullptr;
  REQUIRE((hsaKmtCreateEvent(&descriptor, true, false, &event) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((hsaKmtQueryEventState(event) == HSAKMT_STATUS_WAIT_TIMEOUT));
  REQUIRE((hsaKmtSetEvent(event) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((hsaKmtWaitOnEvent(event, HSA_EVENTTIMEOUT_IMMEDIATE) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((hsaKmtResetEvent(event) == HSAKMT_STATUS_SUCCESS));

  std::array<Packet, 8> ring{};
  for (auto &packet : ring)
    gemstone::writeNative<uint16_t>(packet.data(), 0, static_cast<uint16_t>(gemstone::hsa::PacketType::Invalid));
  HsaQueueResource queue{};
  REQUIRE((hsaKmtCreateQueueExt(1, HSA_QUEUE_COMPUTE_AQL, 100, HSA_QUEUE_PRIORITY_NORMAL, 0, ring.data(), sizeof(ring), event, &queue)
           == HSAKMT_STATUS_SUCCESS));
  REQUIRE((queue.Queue_DoorBell_aql && queue.Queue_write_ptr_aql && queue.Queue_read_ptr_aql));
  uint64_t packetIndex = 0;
  const auto submit = [&](const Packet &packet) {
    ring[packetIndex % ring.size()] = packet;
    __atomic_store_n(queue.Queue_write_ptr_aql, packetIndex + 1, __ATOMIC_RELEASE);
    // The same ordinary writable-memory store used by the ROCr AQL producer path.
    *queue.Queue_DoorBell_aql = packetIndex;
    return ++packetIndex;
  };
  const auto consumed = [&](uint64_t index) {
    return gemstone_test::waitUntil([&] { return __atomic_load_n(queue.Queue_read_ptr_aql, __ATOMIC_ACQUIRE) == index; },
                                    std::chrono::seconds(10));
  };
  const auto setKernarg = [&](size_t output, int32_t seed) {
    const uint64_t outputPointer = reinterpret_cast<uint64_t>(bytes + output);
    std::memcpy(bytes, &outputPointer, sizeof(outputPointer));
    std::memcpy(bytes + 8, &seed, sizeof(seed));
  };

  constexpr size_t outputOffset = 128;
  const int32_t poison = -1;
  std::memcpy(bytes + outputOffset, &poison, sizeof(poison));
  setKernarg(outputOffset, 42);
  fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = 42});
  SignalStorage completion;
  completion.eventMailbox = event->EventData.HWData2;
  completion.eventId = event->EventId;
  auto reads = fixture.reads.load();
  REQUIRE((consumed(submit(kernelPacket(0x123300, reinterpret_cast<uint64_t>(bytes), completion)))));
  REQUIRE((completion.load() == 0));
  // A physical AQL completion also raises the signal's KFD event, which wakes ROCr async handlers.
  REQUIRE((hsaKmtWaitOnEvent(event, HSA_EVENTTIMEOUT_IMMEDIATE) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((word(bytes, outputOffset) == 42));
  // Copy-back reads only the reported range and leaves unrelated native bytes alone.
  REQUIRE((fixture.reads.load() == reads + 1));
  REQUIRE((unrelatedBytes[17] == 0x33));

  // Completion copy-back refreshes the upload snapshot, so an unchanged host needs no upload.
  const auto primaryWrites = fixture.writtenBytes(allocation);
  SignalStorage repeated;
  REQUIRE((consumed(submit(kernelPacket(0x123400, reinterpret_cast<uint64_t>(bytes), repeated)))));
  REQUIRE((repeated.load() == 0));
  REQUIRE((fixture.writtenBytes(allocation) == primaryWrites));
  // Mapped GPU-node memory stays remote-authoritative instead of being refreshed per packet.
  REQUIRE((fixture.writtenBytes(unrelatedAllocation) == 4096));

  // One changed byte range uploads exactly its 64 KiB chunk before the next kernel.
  setKernarg(outputOffset, 91);
  fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = 91});
  SignalStorage changed;
  REQUIRE((consumed(submit(kernelPacket(0x123400, reinterpret_cast<uint64_t>(bytes), changed)))));
  REQUIRE((changed.load() == 0));
  REQUIRE((word(bytes, outputOffset) == 91));
  REQUIRE((fixture.writtenBytes(allocation) == primaryWrites + uploadChunk));

  SignalStorage rejected;
  REQUIRE((consumed(submit(kernelPacket(0xdeadbeef, 0, rejected)))));
  REQUIRE((rejected.load() == -2));

  // Synchronisation-only packets cannot observe allocation contents, so they upload nothing.
  const auto writesBeforeBarrier = fixture.writtenBytes(allocation);
  SignalStorage barrierCompletion;
  REQUIRE((consumed(submit(barrierPacket(barrierCompletion)))));
  REQUIRE((barrierCompletion.load() == 0));
  REQUIRE((fixture.writtenBytes(allocation) == writesBeforeBarrier));

  // A packet retains every allocation the service can observe until copy-back finishes.
  fixture.pauseNextCompletion();
  SignalStorage retained;
  const auto retainedIndex = submit(kernelPacket(0x123400, reinterpret_cast<uint64_t>(bytes), retained));
  REQUIRE((gemstone_test::waitUntil([&] { return fixture.completionEntered.load(); }, std::chrono::seconds(10))));
  REQUIRE((hsaKmtFreeMemory(allocation, primaryAllocationSize) == HSAKMT_STATUS_UNAVAILABLE));
  fixture.releasePausedCompletion();
  REQUIRE((consumed(retainedIndex)));
  REQUIRE((retained.load() == 0));

  // Across the group matrix the host keeps seeing the sentinel until the held completion is released.
  constexpr size_t matrixOutput = 4096;
  for (uint32_t const groups : {1U, 2U, 3U, 5U, 9U})
    for (uint32_t const width : {31U, 32U, 33U})
      for (uint32_t const tail : {0U, 1U})
        for (int32_t const epoch : {7, 29}) {
          const uint32_t count = (groups * width) - tail;
          INFO("groups=" << groups << " width=" << width << " tail=" << tail << " epoch=" << epoch);
          setKernarg(matrixOutput, epoch);
          const int32_t sentinel = -777;
          for (size_t index = 0; index <= count; ++index)
            std::memcpy(bytes + matrixOutput + (index * 4), &sentinel, 4);
          reads = fixture.reads.load();
          SignalStorage matrixCompletion;
          const auto packet =
              kernelPacket(0x123400, reinterpret_cast<uint64_t>(bytes), matrixCompletion, static_cast<uint16_t>(width), count);
          if (count < width) {
            // Pinned HSA 7.2.4 hsa.h requires grid_size_x >= workgroup_size_x. Keep a rejection control.
            REQUIRE((consumed(submit(packet))));
            REQUIRE((matrixCompletion.load() < 0));
            REQUIRE((fixture.reads.load() == reads));
            for (size_t index = 0; index <= count; ++index)
              REQUIRE((word(bytes, matrixOutput + (index * 4)) == sentinel));
            continue;
          }
          fixture.expect({.local = width, .groups = groups, .global = count, .offset = 0, .seed = static_cast<uint32_t>(epoch)});
          fixture.pauseNextCompletion();
          const auto index = submit(packet);
          REQUIRE((gemstone_test::waitUntil([&] { return fixture.completionEntered.load(); }, std::chrono::seconds(10))));
          REQUIRE((matrixCompletion.load() == 1));
          for (size_t element = 0; element <= count; ++element)
            REQUIRE((word(bytes, matrixOutput + (element * 4)) == sentinel));
          REQUIRE((fixture.reads.load() == reads));
          fixture.releasePausedCompletion();
          REQUIRE((consumed(index)));
          REQUIRE((matrixCompletion.load() == 0));
          for (size_t element = 0; element < count; ++element)
            REQUIRE((word(bytes, matrixOutput + (element * 4)) == epoch + static_cast<int32_t>(element)));
          REQUIRE((word(bytes, matrixOutput + (size_t{count} * 4)) == sentinel));
          REQUIRE((fixture.reads.load() == reads + groups));
          REQUIRE((unrelatedBytes[17] == 0x33));
        }

  REQUIRE((hsaKmtDestroyQueue(queue.QueueId) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((hsaKmtDestroyEvent(event) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((hsaKmtUnmapMemoryToGPU(unrelatedAllocation) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((hsaKmtFreeMemory(unrelatedAllocation, 4096) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((hsaKmtFreeMemory(allocation, primaryAllocationSize) == HSAKMT_STATUS_SUCCESS));
  REQUIRE((fixture.allocationCount() == 0));
  REQUIRE((hsaKmtReleaseSystemProperties() == HSAKMT_STATUS_SUCCESS));
  REQUIRE((hsaKmtCloseKFD() == HSAKMT_STATUS_SUCCESS));
  REQUIRE((fixture.programUnloads.load() == 2));
}

TEST_CASE("KFD GPU wave size follows the selected processor", "[hsa][kmt]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  REQUIRE((hsaKmtOpenKFD() == HSAKMT_STATUS_SUCCESS));
  for (const auto &[target, expected] : std::array<std::pair<const char *, uint32_t>, 6>{
           {{"gfx900", 64}, {"gfx90a", 64}, {"gfx1036", 32}, {"gfx1200", 32}, {"gfx1250", 32}, {"gfx1251", 32}}}) {
    const gemstone_test::Environment selected(gemstone::environment::TargetArch, target);
    INFO(target);
    HsaNodeProperties properties{};
    REQUIRE((hsaKmtGetNodeProperties(1, &properties) == HSAKMT_STATUS_SUCCESS));
    CHECK((properties.WaveFrontSize == expected));
  }
  REQUIRE((hsaKmtCloseKFD() == HSAKMT_STATUS_SUCCESS));
}
