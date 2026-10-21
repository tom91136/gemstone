#include "gemstone/hsa/aql_execution.hpp"

#include <array>
#include <chrono>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <memory>
#include <string>
#include <thread>
#include <utility>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#include "gemstone/hsa/aql.hpp"
#include "gemstone/hsa/doorbell_monitor.hpp"
#include "gemstone_execution_rpc.hpp"
#include <gemstone/bytes.hpp>
#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include "transfer.hpp"

namespace {

using namespace gemstone::hsa;
using gemstone::execution::AllocationId;
using gemstone::execution::DirtyRange;
using gemstone::execution::FailureKind;
using gemstone::execution::ParameterKind;
using Packet = std::array<uint8_t, AqlPacketSize>;

struct RocrSignal {
  uint64_t kind = 0;
  alignas(8) int64_t value;

  explicit RocrSignal(int64_t initial = 1) : value(initial) {}
  uint64_t handle() { return reinterpret_cast<uint64_t>(this); }
  [[nodiscard]] int64_t load() const { return __atomic_load_n(&value, __ATOMIC_ACQUIRE); }
  void store(int64_t next) { __atomic_store_n(&value, next, __ATOMIC_RELEASE); }
};
static_assert(offsetof(RocrSignal, value) == 8);

const std::array<uint8_t, 4> Image{{0x7f, 'E', 'L', 'F'}};

struct Harness {
  gemstone_test::RpcFixture fixture;
  gemstone_test::RpcEnvironment environment{fixture};
  std::unique_ptr<gemstone::execution::Client> client = gemstone::execution::Client::fromEnvironment();
  std::unique_ptr<AqlRuntime> runtime = AqlRuntime::fromEnvironment(std::make_unique<RocrSignals>());

  Harness() {
    REQUIRE(client);
    REQUIRE(runtime);
    fixture.opaqueLaunches();
  }

  uint64_t allocate(const void *base, size_t size) const {
    const auto id = gemstone::execution::registerAllocation(*client, size, base);
    if (!id) FAIL("allocation registration failed");
    REQUIRE(runtime->registerAllocation(reinterpret_cast<uint64_t>(base), size, static_cast<uint64_t>(id->value)));
    return static_cast<uint64_t>(id->value);
  }

  void install(uint64_t object, const std::string &name) const {
    const std::array<KernelSymbol, 1> symbols{{{.object = object, .name = name}}};
    REQUIRE(runtime->installCodeObject(Image, symbols));
  }
};

Packet kernelPacket(uint64_t kernelObject, uint64_t kernarg, uint64_t completion) {
  Packet packet{};
  gemstone::writeNative<uint16_t>(packet.data(), 0, static_cast<uint16_t>(PacketType::KernelDispatch));
  gemstone::writeNative<uint16_t>(packet.data(), 2, 2);
  gemstone::writeNative<uint16_t>(packet.data(), 4, 8);
  gemstone::writeNative<uint16_t>(packet.data(), 6, 4);
  gemstone::writeNative<uint16_t>(packet.data(), 8, 1);
  gemstone::writeNative<uint32_t>(packet.data(), 12, 17);
  gemstone::writeNative<uint32_t>(packet.data(), 16, 8);
  gemstone::writeNative<uint32_t>(packet.data(), 20, 1);
  gemstone::writeNative<uint32_t>(packet.data(), 24, 96);
  gemstone::writeNative<uint32_t>(packet.data(), 28, 128);
  gemstone::writeNative<uint64_t>(packet.data(), 32, kernelObject);
  gemstone::writeNative<uint64_t>(packet.data(), 40, kernarg);
  gemstone::writeNative<uint64_t>(packet.data(), 56, completion);
  return packet;
}

Packet barrierPacket(PacketType type, std::array<uint64_t, 5> dependencies, uint64_t completion) {
  Packet packet{};
  gemstone::writeNative<uint16_t>(packet.data(), 0, static_cast<uint16_t>(type));
  for (size_t index = 0; index < dependencies.size(); ++index)
    gemstone::writeNative<uint64_t>(packet.data(), 8 + (index * 8), dependencies[index]);
  gemstone::writeNative<uint64_t>(packet.data(), 56, completion);
  return packet;
}

Packet pm4IbPacket(uint64_t completion) {
  Packet packet{};
  gemstone::writeNative<uint16_t>(packet.data(), 0, static_cast<uint16_t>(PacketType::VendorSpecific));
  gemstone::writeNative<uint16_t>(packet.data(), 2, 1);
  gemstone::writeNative<uint32_t>(packet.data(), 4, 0xc0023f00);
  gemstone::writeNative<uint32_t>(packet.data(), 20, 0x0a);
  gemstone::writeNative<uint64_t>(packet.data(), 56, completion);
  return packet;
}

template <size_t N> struct Ring {
  std::array<Packet, N> packets{};
  std::unique_ptr<AqlQueue> queue;

  Ring(DoorbellMonitor &monitor, AqlRuntime &runtime)
      : queue(monitor.registerQueue(packets.data(), N,
                                    [&runtime](uint64_t, uint64_t, const void *packet) { return runtime.consume(packet); })) {
    for (auto &slot : packets)
      gemstone::writeNative<uint16_t>(slot.data(), 0, static_cast<uint16_t>(PacketType::Invalid));

    REQUIRE(queue);
  }

  void submit(uint64_t index, const Packet &packet) {
    packets[index % N] = packet;
    __atomic_store_n(queue->writeIndex(), index + 1, __ATOMIC_RELEASE);
    __atomic_store_n(queue->doorbell(), index, __ATOMIC_RELEASE);
  }
  [[nodiscard]] uint64_t read() const { return __atomic_load_n(queue->readIndex(), __ATOMIC_ACQUIRE); }
  [[nodiscard]] bool waitRead(uint64_t index) const {
    return gemstone_test::waitUntil([&] { return read() == index; }, std::chrono::seconds(10));
  }
};

} // namespace

TEST_CASE("AQL packet decoding rejects malformed packets", "[hsa][aql]") {
  std::array<uint8_t, 16> kernarg{};
  auto packet = kernelPacket(9, reinterpret_cast<uint64_t>(kernarg.data()), 11);
  AqlPacket decoded;
  REQUIRE(decodeAqlPacket(packet.data(), packet.size(), decoded) == DecodeStatus::Success);
  REQUIRE(decoded.type == PacketType::KernelDispatch);
  REQUIRE(decoded.dispatch.dimensions == 2);
  REQUIRE(decoded.dispatch.gridSize[0] == 17);
  REQUIRE(decoded.dispatch.kernelObject == 9);
  REQUIRE(decoded.completionSignal == 11);

  REQUIRE(decodeAqlPacket(packet.data(), 63, decoded) == DecodeStatus::Truncated);
  gemstone::writeNative<uint16_t>(packet.data(), 10, 1);
  REQUIRE(decodeAqlPacket(packet.data(), packet.size(), decoded) == DecodeStatus::ReservedBits);

  auto pm4 = pm4IbPacket(12);
  REQUIRE(decodeAqlPacket(pm4.data(), pm4.size(), decoded) == DecodeStatus::Success);
  REQUIRE(decoded.type == PacketType::VendorSpecific);
  REQUIRE(decoded.indirectBufferCommands[0] == 0xc0023f00);
  REQUIRE(decoded.completionSignal == 12);
  gemstone::writeNative<uint16_t>(pm4.data(), 2, 2);
  REQUIRE(decodeAqlPacket(pm4.data(), pm4.size(), decoded) == DecodeStatus::UnsupportedType);
}

TEST_CASE("AQL PM4 indirect-buffer synchronisation completes", "[hsa][aql]") {
  Harness harness;
  RocrSignal completion;
  const auto packet = pm4IbPacket(completion.handle());
  REQUIRE(harness.runtime->consume(packet.data()) == PacketResult::Accepted);
  REQUIRE(completion.load() == 0);
  REQUIRE(harness.fixture.launches.load() == 0);
}

TEST_CASE("AQL malformed dispatch fails its completion signal", "[hsa][aql]") {
  Harness harness;
  RocrSignal completion;
  auto packet = kernelPacket(77, 0, completion.handle());
  // Claims one dimension but retains a four-wide Y workgroup.
  gemstone::writeNative<uint16_t>(packet.data(), 2, 1);
  REQUIRE(harness.runtime->consume(packet.data()) == PacketResult::Accepted);
  REQUIRE(completion.load() == -7);
  REQUIRE(harness.fixture.launches.load() == 0);
  REQUIRE(harness.runtime->lastError() == "dispatch dimensions are invalid");
}

TEST_CASE("AQL failure codes distinguish malformed packets", "[hsa][aql]") {
  Harness harness;
  RocrSignal grid;
  RocrSignal reserved;
  auto smallGrid = kernelPacket(77, 0, grid.handle());
  gemstone::writeNative<uint32_t>(smallGrid.data(), 12, 7);
  REQUIRE(harness.runtime->consume(smallGrid.data()) == PacketResult::Accepted);
  REQUIRE(grid.load() == -8);
  auto reservedBits = kernelPacket(77, 0, reserved.handle());
  gemstone::writeNative<uint16_t>(reservedBits.data(), 10, 1);
  REQUIRE(harness.runtime->consume(reservedBits.data()) == PacketResult::Accepted);
  REQUIRE(reserved.load() == -6);
  auto unsignalled = kernelPacket(77, 0, 0);
  gemstone::writeNative<uint16_t>(unsignalled.data(), 2, 1);
  REQUIRE(harness.runtime->consume(unsignalled.data()) == PacketResult::Fatal);
}

TEST_CASE("AQL missing kernels do not re-enter the runtime lock", "[hsa][aql]") {
  Harness harness;
  REQUIRE(!harness.runtime->bindGlobal(999, "missing", 1, 0));
  REQUIRE(harness.runtime->lastError() == "global belongs to an unknown kernel object");
  RocrSignal completion;
  const auto packet = kernelPacket(999, 0, completion.handle());
  REQUIRE(harness.runtime->consume(packet.data()) == PacketResult::Accepted);
  REQUIRE(completion.load() == -2);
  REQUIRE(harness.runtime->lastError() == "kernel object is not installed");
}

TEST_CASE("AQL ROCr signal operations use the stock value word", "[hsa][aql]") {
  RocrSignal signal(2);
  RocrSignals operations;
  int64_t loaded = 0;
  REQUIRE(operations.load(signal.handle(), loaded));
  REQUIRE(loaded == 2);
  REQUIRE(operations.complete(signal.handle()));
  REQUIRE(signal.load() == 1);
  REQUIRE(operations.fail(signal.handle(), -7));
  REQUIRE(signal.load() == -7);
}

TEST_CASE("AQL execution queues dispatch and complete", "[hsa][aql]") {
  Harness harness;
  // An opaque by-value C++ functor holding a pointer and a scalar, then a separate
  // scalar. Padding between and after the fields stays in the kernarg buffer.
  harness.fixture.kernelMetadata(
      24, 2, {{.width = 12, .offset = 0, .kind = 1, .containsPointers = 1}, {.width = 4, .offset = 16, .kind = 1, .containsPointers = 0}});
  harness.install(77, "add_one");

  // The gap keeps another registered buffer from starting where the one-past pointer below lands.
  struct {
    std::array<uint8_t, 32> allocation{};
    std::array<uint8_t, 16> gap{};
    std::array<uint8_t, 64> unrelated{};
    std::array<uint8_t, 24> kernarg{};
  } buffers;
  auto &allocation = buffers.allocation;
  auto &unrelated = buffers.unrelated;
  auto &kernarg = buffers.kernarg;
  // A one-past pointer is a legal C/C++ loop bound and must remain relocatable.
  const uint64_t pointer = reinterpret_cast<uint64_t>(allocation.data() + allocation.size());
  std::memcpy(kernarg.data(), &pointer, sizeof(pointer));
  const auto allocationId = harness.allocate(allocation.data(), allocation.size());
  const auto kernargId = harness.allocate(kernarg.data(), kernarg.size());
  harness.allocate(unrelated.data(), unrelated.size());
  harness.fixture.opaqueLaunches({DirtyRange{.allocation = AllocationId{static_cast<int64_t>(allocationId)}, .offset = {8}, .size = {4}}});

  DoorbellMonitor monitor;
  Ring<4> ring(monitor, *harness.runtime);
  RocrSignal completion;
  ring.submit(0, kernelPacket(77, reinterpret_cast<uint64_t>(kernarg.data()), completion.handle()));
  REQUIRE(ring.waitRead(1));
  REQUIRE(completion.load() == 0);
  const auto launch = harness.fixture.lastLaunch();
  REQUIRE(launch.kernelName.value == "add_one");
  REQUIRE((launch.groupSize.x.value == 8 && launch.groupSize.y.value == 4 && launch.groupSize.z.value == 1));
  REQUIRE((launch.groupCount.x.value == 3 && launch.groupCount.y.value == 2 && launch.groupCount.z.value == 1));
  REQUIRE((launch.globalSize.x.value == 17 && launch.globalSize.y.value == 8 && launch.globalSize.z.value == 1));
  REQUIRE((launch.privateSegmentBytes.value == 96 && launch.groupSegmentBytes.value == 128));
  REQUIRE(launch.arguments.empty());
  REQUIRE((launch.argumentBuffer && std::cmp_equal(launch.argumentBuffer->region.value, kernargId)
           && launch.argumentBuffer->offset.value == 0));
  REQUIRE(launch.pointerRelocations.size() == 1);
  const auto &relocation = launch.pointerRelocations.front();
  REQUIRE(std::cmp_equal(std::get<gemstone::execution::RelocationSourceMemory>(relocation.source).region.value, kernargId));
  REQUIRE(relocation.sourceOffset.value == 0);
  REQUIRE((std::cmp_equal(relocation.target.value, allocationId) && std::cmp_equal(relocation.targetOffset.value, allocation.size())));

  const auto dirty = harness.runtime->takeDirtyRanges();
  REQUIRE(dirty.size() == 1);
  REQUIRE((std::cmp_equal(dirty[0].allocation.value, allocationId) && dirty[0].offset.value == 8 && dirty[0].size.value == 4));
  REQUIRE(harness.runtime->takeDirtyRanges().empty());

  RocrSignal missing;
  ring.submit(1, kernelPacket(999, reinterpret_cast<uint64_t>(kernarg.data()), missing.handle()));
  REQUIRE(ring.waitRead(2));
  REQUIRE(missing.load() == -2);

  harness.fixture.failNextCompletion();
  RocrSignal failed;
  ring.submit(2, kernelPacket(77, reinterpret_cast<uint64_t>(kernarg.data()), failed.handle()));
  REQUIRE(ring.waitRead(3));
  REQUIRE(failed.load() == -3);
  REQUIRE(harness.runtime->lastError() == "add_one: completion failed");
  REQUIRE(harness.runtime->takeDirtyRanges().empty());

  ring.queue.reset();
  harness.runtime.reset();
  REQUIRE(harness.fixture.programUnloads.load() == 1);
}

TEST_CASE("AQL dispatch failures publish the code for the failure kind", "[hsa][aql]") {
  Harness harness;
  harness.fixture.kernelMetadata(
      24, 2, {{.width = 12, .offset = 0, .kind = 1, .containsPointers = 1}, {.width = 4, .offset = 16, .kind = 1, .containsPointers = 0}});
  harness.install(77, "add_one");
  std::array<uint8_t, 32> allocation{};
  std::array<uint8_t, 24> kernarg{};
  const uint64_t pointer = reinterpret_cast<uint64_t>(allocation.data());
  std::memcpy(kernarg.data(), &pointer, sizeof(pointer));
  const auto allocationId = harness.allocate(allocation.data(), allocation.size());
  harness.allocate(kernarg.data(), kernarg.size());
  harness.fixture.opaqueLaunches({DirtyRange{.allocation = AllocationId{static_cast<int64_t>(allocationId)}, .offset = {0}, .size = {4}}});

  const struct {
    FailureKind kind;
    int64_t code;
  } cases[]{
      {.kind = FailureKind::InvalidProgram, .code = static_cast<int64_t>(PacketFailure::InvalidCodeObject)},
      {.kind = FailureKind::InvalidConfiguration, .code = static_cast<int64_t>(PacketFailure::InvalidDimensions)},
      {.kind = FailureKind::OutOfResources, .code = static_cast<int64_t>(PacketFailure::OutOfResources)},
      {.kind = FailureKind::IllegalAddress, .code = static_cast<int64_t>(PacketFailure::InvalidMemoryReference)},
      {.kind = FailureKind::MisalignedAddress, .code = static_cast<int64_t>(PacketFailure::InvalidMemoryReference)},
      {.kind = FailureKind::IllegalInstruction, .code = static_cast<int64_t>(PacketFailure::IllegalInstruction)},
      {.kind = FailureKind::Trapped, .code = static_cast<int64_t>(PacketFailure::ExecutionFailed)},
      {.kind = FailureKind::AssertionFailed, .code = static_cast<int64_t>(PacketFailure::ExecutionFailed)},
      {.kind = FailureKind::TimedOut, .code = static_cast<int64_t>(PacketFailure::ExecutionFailed)},
      {.kind = FailureKind::Unsupported, .code = static_cast<int64_t>(PacketFailure::ExecutionFailed)},
      {.kind = FailureKind::Failed, .code = static_cast<int64_t>(PacketFailure::ExecutionFailed)},
  };
  DoorbellMonitor monitor;
  Ring<4> ring(monitor, *harness.runtime);
  uint64_t index = 0;
  for (const auto &[kind, code] : cases) {
    CAPTURE(static_cast<int>(kind));
    harness.fixture.failNextCompletion(kind);
    RocrSignal failed;
    ring.submit(index, kernelPacket(77, reinterpret_cast<uint64_t>(kernarg.data()), failed.handle()));
    REQUIRE(ring.waitRead(++index));
    CHECK(failed.load() == code);
  }
}

TEST_CASE("AQL kernels build ahead of dispatch and report what the service rejected", "[hsa][aql]") {
  Harness harness;
  harness.fixture.kernelMetadata(8, 0, {});
  harness.install(77, "add_one");
  CHECK(!harness.runtime->build(77));
  CHECK(harness.fixture.kernelBuilds.load() == 1);

  harness.fixture.rejectNext(gemstone::execution::operation::BuildKernel, FailureKind::InvalidProgram);
  CHECK(harness.runtime->build(77).value_or(gemstone::execution::Error{}).kind == FailureKind::InvalidProgram);
  CHECK(harness.fixture.kernelBuilds.load() == 1);
  CHECK(harness.runtime->build(999).value_or(gemstone::execution::Error{}).kind == FailureKind::NotFound);
}

TEST_CASE("AQL code objects bind multiple kernel objects", "[hsa][aql]") {
  Harness harness;
  const std::array<KernelSymbol, 2> symbols{{{.object = 41, .name = "first"}, {.object = 42, .name = "second"}}};
  REQUIRE(harness.runtime->installCodeObject(Image, symbols));
  REQUIRE((harness.fixture.programLoads.load() == 1 && harness.fixture.programUnloads.load() == 0));
  REQUIRE(!harness.runtime->installCodeObject(Image, symbols));
  REQUIRE(harness.fixture.programUnloads.load() == 1);
  REQUIRE(harness.runtime->removeKernel(41));
  REQUIRE(harness.fixture.programUnloads.load() == 1);
  REQUIRE(harness.runtime->removeKernel(42));
  REQUIRE(harness.fixture.programUnloads.load() == 2);
  REQUIRE(!harness.runtime->removeKernel(42));
  harness.runtime.reset();
  REQUIRE(harness.fixture.programUnloads.load() == 2);
}

TEST_CASE("AQL kernel layouts come from the service manifest", "[hsa][aql]") {
  Harness harness;
  harness.fixture.kernelMetadata(8, 1, {{.width = 8, .offset = 0, .kind = 2, .containsPointers = 0}});
  harness.install(41, "server_kernel");
  const auto layout = harness.runtime->layout(41);
  if (!layout) FAIL("kernel has no layout");
  REQUIRE((layout->kernargSize == 8 && layout->explicitArgumentCount == 1 && layout->fields.size() == 1));
  REQUIRE((layout->fields[0].kind == ParameterKind::Allocation && layout->fields[0].offset == 0 && layout->fields[0].size == 8));

  std::array<uint8_t, 8> allocation{};
  std::array<uint8_t, 8> kernarg{};
  const uint64_t pointer = reinterpret_cast<uint64_t>(allocation.data());
  std::memcpy(kernarg.data(), &pointer, sizeof(pointer));
  const auto allocationId = harness.allocate(allocation.data(), allocation.size());
  harness.allocate(kernarg.data(), kernarg.size());
  RocrSignal completion;
  const auto packet = kernelPacket(41, reinterpret_cast<uint64_t>(kernarg.data()), completion.handle());
  REQUIRE(harness.runtime->consume(packet.data()) == PacketResult::Accepted);
  REQUIRE(completion.load() == 0);
  const auto relocations = harness.fixture.lastLaunch().pointerRelocations;
  REQUIRE(relocations.size() == 1);
  REQUIRE((std::cmp_equal(relocations[0].target.value, allocationId) && relocations[0].targetOffset.value == 0));

  harness.fixture.kernelMetadata(8, 1, {{.width = 8, .offset = 0, .kind = 3, .containsPointers = 0}});
  const std::array<KernelSymbol, 1> local{{{.object = 43, .name = "local_kernel"}}};
  REQUIRE(!harness.runtime->installCodeObject(Image, local));
  REQUIRE(harness.runtime->lastError() == "execution service returned invalid AMDGPU kernel metadata");
}

TEST_CASE("AQL allocation-backed kernargs retain zero-sized metadata buffers", "[hsa][aql]") {
  Harness harness;
  harness.fixture.kernelMetadata(0, 0, {});
  harness.install(77, "implicit");
  std::array<uint8_t, 96> storage{};
  const uint64_t base = reinterpret_cast<uint64_t>(storage.data());
  const auto id = harness.allocate(storage.data(), storage.size());
  RocrSignal completion;
  auto packet = kernelPacket(77, base + 16, completion.handle());
  REQUIRE(harness.runtime->consume(packet.data()) == PacketResult::Accepted);
  REQUIRE(completion.load() == 0);
  REQUIRE(harness.fixture.launches.load() == 1);
  const auto buffer = harness.fixture.lastLaunch().argumentBuffer;
  REQUIRE((buffer && std::cmp_equal(buffer->region.value, id) && buffer->offset.value == 16));

  RocrSignal onePast;
  packet = kernelPacket(77, base + storage.size(), onePast.handle());
  REQUIRE(harness.runtime->consume(packet.data()) == PacketResult::Accepted);
  REQUIRE(onePast.load() == -5);
  REQUIRE(harness.runtime->unregisterAllocation(base));
  RocrSignal unregistered;
  packet = kernelPacket(77, base + 16, unregistered.handle());
  REQUIRE(harness.runtime->consume(packet.data()) == PacketResult::Accepted);
  REQUIRE(unregistered.load() == -5);
  REQUIRE(harness.fixture.launches.load() == 1);
}

TEST_CASE("AQL dispatches from a cooperative queue launch cooperatively", "[hsa][aql]") {
  Harness harness;
  harness.fixture.kernelMetadata(0, 0, {});
  harness.install(77, "grid_sync");
  std::array<uint8_t, 16> storage{};
  harness.allocate(storage.data(), storage.size());
  for (const bool cooperative : {false, true}) {
    RocrSignal completion;
    const auto packet = kernelPacket(77, reinterpret_cast<uint64_t>(storage.data()), completion.handle());
    REQUIRE(harness.runtime->consume(packet.data(), cooperative) == PacketResult::Accepted);
    REQUIRE(completion.load() == 0);
    REQUIRE(harness.fixture.lastLaunch().cooperative == cooperative);
  }
}

TEST_CASE("AQL OpenMP sentinel preserves ABI without pointer resolution", "[hsa][aql]") {
  Harness harness;
  harness.fixture.kernelMetadata(
      16, 2, {{.width = 8, .offset = 0, .kind = 2, .containsPointers = 0}, {.width = 8, .offset = 8, .kind = 1, .containsPointers = 0}});
  harness.install(77, "openmp_kernel");
  std::array<uint8_t, 16> kernarg{};
  const uint64_t sentinel = UINT64_MAX;
  std::memcpy(kernarg.data(), &sentinel, sizeof(sentinel));
  const auto kernargId = harness.allocate(kernarg.data(), kernarg.size());
  RocrSignal completion;
  const auto packet = kernelPacket(77, reinterpret_cast<uint64_t>(kernarg.data()), completion.handle());
  REQUIRE(harness.runtime->consume(packet.data()) == PacketResult::Accepted);
  REQUIRE(completion.load() == 0);
  const auto launch = harness.fixture.lastLaunch();
  REQUIRE(launch.pointerRelocations.empty());
  REQUIRE((launch.argumentBuffer && std::cmp_equal(launch.argumentBuffer->region.value, kernargId)));
}

TEST_CASE("AQL barriers wait and complete", "[hsa][aql]") {
  Harness harness;
  DoorbellMonitor monitor;
  Ring<2> ring(monitor, *harness.runtime);
  RocrSignal satisfied(0);
  RocrSignal pending(1);
  RocrSignal completion(1);
  ring.submit(0, barrierPacket(PacketType::BarrierAnd, {satisfied.handle(), pending.handle(), 0, 0, 0}, completion.handle()));
  std::this_thread::sleep_for(std::chrono::milliseconds(5));
  REQUIRE(ring.read() == 0);
  pending.store(0);
  REQUIRE(ring.waitRead(1));
  REQUIRE(completion.load() == 0);

  RocrSignal unsatisfied(1);
  RocrSignal ready(0);
  RocrSignal either(1);
  ring.submit(1, barrierPacket(PacketType::BarrierOr, {unsatisfied.handle(), ready.handle(), 0, 0, 0}, either.handle()));
  REQUIRE(ring.waitRead(2));
  REQUIRE(either.load() == 0);
}

TEST_CASE("AQL waiting barriers do not block another queue", "[hsa][aql]") {
  Harness harness;
  DoorbellMonitor monitor;
  Ring<2> waiting(monitor, *harness.runtime);
  Ring<2> ready(monitor, *harness.runtime);
  RocrSignal dependency(1);
  RocrSignal waitingCompletion(1);
  RocrSignal readyCompletion(1);
  waiting.submit(0, barrierPacket(PacketType::BarrierAnd, {dependency.handle(), 0, 0, 0, 0}, waitingCompletion.handle()));
  ready.submit(0, barrierPacket(PacketType::BarrierAnd, {0, 0, 0, 0, 0}, readyCompletion.handle()));
  REQUIRE(ready.waitRead(1));
  REQUIRE(waiting.read() == 0);
  REQUIRE(readyCompletion.load() == 0);
  dependency.store(0);
  REQUIRE(waiting.waitRead(1));
  REQUIRE(waitingCompletion.load() == 0);
}
