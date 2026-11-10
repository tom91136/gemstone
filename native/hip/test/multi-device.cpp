#include <array>
#include <csignal>
#include <cstdint>
#include <cstring>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <hip/hip_runtime_api.h>

#include <gemstone/child_test.hpp>
#include <gemstone/test_support.hpp>

#include "../../hsa/test/code_object_fixture.hpp"
#include "../../rpc/test/rpc_fixture.hpp"
#include "offload_bundle.hpp"
#include <environment_keys.hpp>

extern "C" void **__hipRegisterFatBinary(const void *);
extern "C" void __hipRegisterVar(void **, void *, char *, char *, int, size_t, int, int);
extern "C" void __hipUnregisterFatBinary(void **);

namespace {

gemstone_test::RpcFixture fixture;
const gemstone_test::RpcEnvironment RpcEndpoint(fixture);
const gemstone_test::Environment DeviceCount(gemstone::environment::DeviceCount, "2");

size_t freeBytes() {
  size_t available = 0;
  size_t total = 0;
  REQUIRE(hipMemGetInfo(&available, &total) == hipSuccess);
  return available;
}

struct PeerLaunch {
  hipModule_t module{};
  hipFunction_t function{};
  void *output = nullptr;
  uint32_t seed = 21;

  /** Loads the kernel on device 1 and allocates its output on device 0. */
  PeerLaunch() {
    REQUIRE(hipSetDevice(0) == hipSuccess);
    REQUIRE(hipMalloc(&output, sizeof(uint32_t)) == hipSuccess);
    REQUIRE(hipSetDevice(1) == hipSuccess);
    const auto image = gemstone::hsa::test::codeObjectFixture(1, true, true);
    REQUIRE(hipModuleLoadData(&module, image.data()) == hipSuccess);
    REQUIRE(hipModuleGetFunction(&function, module, "add") == hipSuccess);
  }
  PeerLaunch(const PeerLaunch &) = delete;
  PeerLaunch &operator=(const PeerLaunch &) = delete;
  ~PeerLaunch() {
    (void)hipModuleUnload(module);
    (void)hipSetDevice(0);
    (void)hipFree(output);
  }
  hipError_t launch() {
    void *arguments[] = {&output, &seed};
    return hipModuleLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, nullptr, arguments, nullptr);
  }
};

struct IndirectPeerLaunch {
  static constexpr uint32_t Pointee = 77;
  hipModule_t module{};
  hipFunction_t function{};
  void *output = nullptr;
  void *table = nullptr;
  void *peer = nullptr;
  uint32_t seed = 31;

  /** Loads the kernel on device 1, which finds device 0's `peer` only through a pointer stored in its own `table`. */
  IndirectPeerLaunch() {
    REQUIRE(hipSetDevice(0) == hipSuccess);
    REQUIRE(hipMalloc(&peer, sizeof(uint32_t)) == hipSuccess);
    REQUIRE(hipMemcpy(peer, &Pointee, sizeof(Pointee), hipMemcpyHostToDevice) == hipSuccess);
    REQUIRE(hipSetDevice(1) == hipSuccess);
    REQUIRE(hipMalloc(&output, sizeof(uint32_t)) == hipSuccess);
    REQUIRE(hipMalloc(&table, sizeof(void *)) == hipSuccess);
    REQUIRE(hipMemcpy(table, &peer, sizeof(peer), hipMemcpyHostToDevice) == hipSuccess);
    const auto image = gemstone::hsa::test::codeObjectFixture(1, true, true);
    REQUIRE(hipModuleLoadData(&module, image.data()) == hipSuccess);
    REQUIRE(hipModuleGetFunction(&function, module, "add") == hipSuccess);
  }
  IndirectPeerLaunch(const IndirectPeerLaunch &) = delete;
  IndirectPeerLaunch &operator=(const IndirectPeerLaunch &) = delete;
  ~IndirectPeerLaunch() {
    (void)hipModuleUnload(module);
    (void)hipFree(output);
    (void)hipFree(table);
    (void)hipSetDevice(0);
    (void)hipFree(peer);
  }
  hipError_t launch() {
    fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = seed});
    fixture.expectIndirectInput(table, Pointee);
    void *arguments[] = {&output, &seed};
    return hipModuleLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, nullptr, arguments, nullptr);
  }
};

} // namespace

TEST_CASE("HIP enumerates the configured number of distinct devices", "[hip][multi-device]") {
  int count = 0;
  REQUIRE(hipGetDeviceCount(&count) == hipSuccess);
  CHECK(count == 2);
  hipDevice_t second = -1;
  REQUIRE(hipDeviceGet(&second, 1) == hipSuccess);
  CHECK(second == 1);
  CHECK(hipDeviceGet(&second, 2) == hipErrorInvalidDevice);
  CHECK(hipSetDevice(2) == hipErrorInvalidDevice);
  CHECK(hipSetDevice(-1) == hipErrorInvalidDevice);

  hipDeviceProp_tR0600 first{};
  hipDeviceProp_tR0600 other{};
  REQUIRE(hipGetDevicePropertiesR0600(&first, 0) == hipSuccess);
  REQUIRE(hipGetDevicePropertiesR0600(&other, 1) == hipSuccess);
  CHECK(std::string(first.name) == std::string(other.name));
  CHECK(first.pciBusID != other.pciBusID);
  CHECK(hipGetDevicePropertiesR0600(&other, 2) == hipErrorInvalidValue);

  int bus[2]{};
  REQUIRE(hipDeviceGetAttribute(&bus[0], hipDeviceAttributePciBusId, 0) == hipSuccess);
  REQUIRE(hipDeviceGetAttribute(&bus[1], hipDeviceAttributePciBusId, 1) == hipSuccess);
  CHECK(bus[0] != bus[1]);

  char busId[32]{};
  REQUIRE(hipDeviceGetPCIBusId(busId, sizeof(busId), 1) == hipSuccess);
  int found = -1;
  REQUIRE(hipDeviceGetByPCIBusId(&found, busId) == hipSuccess);
  CHECK(found == 1);
  CHECK(hipDeviceGetByPCIBusId(&found, "0000:09:00.0") == hipErrorInvalidValue);

  hipUUID uuids[2]{};
  REQUIRE(hipDeviceGetUuid(&uuids[0], 0) == hipSuccess);
  REQUIRE(hipDeviceGetUuid(&uuids[1], 1) == hipSuccess);
  CHECK(std::memcmp(uuids[0].bytes, uuids[1].bytes, sizeof(uuids[0].bytes)) != 0);
}

TEST_CASE("HIP allocations belong to the current device", "[hip][multi-device]") {
  REQUIRE(hipSetDevice(0) == hipSuccess);
  const auto untouched = freeBytes();
  REQUIRE(hipSetDevice(1) == hipSuccess);
  int device = -1;
  REQUIRE(hipGetDevice(&device) == hipSuccess);
  CHECK(device == 1);
  CHECK(freeBytes() == untouched);

  void *buffer = nullptr;
  constexpr size_t size = size_t{1} << 20;
  REQUIRE(hipMalloc(&buffer, size) == hipSuccess);
  CHECK(freeBytes() == untouched - size);
  hipPointerAttribute_t attributes{};
  REQUIRE(hipPointerGetAttributes(&attributes, buffer) == hipSuccess);
  CHECK(attributes.device == 1);
  REQUIRE(hipSetDevice(0) == hipSuccess);
  CHECK(freeBytes() == untouched);
  REQUIRE(hipPointerGetAttributes(&attributes, buffer) == hipSuccess);
  CHECK(attributes.device == 1);

  hipStream_t stream = nullptr;
  REQUIRE(hipSetDevice(1) == hipSuccess);
  REQUIRE(hipStreamCreate(&stream) == hipSuccess);
  REQUIRE(hipSetDevice(0) == hipSuccess);
  hipDevice_t owner = -1;
  REQUIRE(hipStreamGetDevice(stream, &owner) == hipSuccess);
  CHECK(owner == 1);
  CHECK(hipStreamDestroy(stream) == hipSuccess);

  REQUIRE(hipFree(buffer) == hipSuccess);
  CHECK(freeBytes() == untouched);
}

TEST_CASE("HIP peer access is directed and reports the runtime's errors", "[hip][multi-device]") {
  int can = -1;
  REQUIRE(hipDeviceCanAccessPeer(&can, 0, 1) == hipSuccess);
  CHECK(can == 1);
  REQUIRE(hipDeviceCanAccessPeer(&can, 1, 1) == hipSuccess);
  CHECK(can == 0);
  CHECK(hipDeviceCanAccessPeer(&can, 0, 2) == hipErrorInvalidDevice);
  int attribute = -1;
  REQUIRE(hipDeviceGetP2PAttribute(&attribute, hipDevP2PAttrAccessSupported, 0, 1) == hipSuccess);
  CHECK(attribute == 1);

  REQUIRE(hipSetDevice(0) == hipSuccess);
  CHECK(hipDeviceDisablePeerAccess(1) == hipErrorPeerAccessNotEnabled);
  CHECK(hipDeviceEnablePeerAccess(0, 0) == hipErrorInvalidDevice);
  CHECK(hipDeviceEnablePeerAccess(1, 1) == hipErrorInvalidValue);
  REQUIRE(hipDeviceEnablePeerAccess(1, 0) == hipSuccess);
  CHECK(hipDeviceEnablePeerAccess(1, 0) == hipErrorPeerAccessAlreadyEnabled);
  REQUIRE(hipSetDevice(1) == hipSuccess);
  CHECK(hipDeviceDisablePeerAccess(0) == hipErrorPeerAccessNotEnabled);
  REQUIRE(hipSetDevice(0) == hipSuccess);
  CHECK(hipDeviceDisablePeerAccess(1) == hipSuccess);
  CHECK(hipDeviceDisablePeerAccess(1) == hipErrorPeerAccessNotEnabled);
}

TEST_CASE("HIP copies bytes between devices", "[hip][multi-device]") {
  std::vector<uint8_t> input(4096);
  for (size_t index = 0; index < input.size(); ++index)
    input[index] = static_cast<uint8_t>((index * 7) + 3);

  REQUIRE(hipSetDevice(0) == hipSuccess);
  void *source = nullptr;
  REQUIRE(hipMalloc(&source, input.size()) == hipSuccess);
  REQUIRE(hipMemcpy(source, input.data(), input.size(), hipMemcpyHostToDevice) == hipSuccess);
  REQUIRE(hipSetDevice(1) == hipSuccess);
  void *destination = nullptr;
  REQUIRE(hipMalloc(&destination, input.size()) == hipSuccess);
  REQUIRE(hipMemcpyPeer(destination, 1, source, 0, input.size()) == hipSuccess);
  CHECK(hipMemcpyPeer(destination, 2, source, 0, input.size()) == hipErrorInvalidDevice);

  std::vector<uint8_t> output(input.size());
  REQUIRE(hipMemcpy(output.data(), destination, output.size(), hipMemcpyDeviceToHost) == hipSuccess);
  CHECK(output == input);

  REQUIRE(hipMemset(destination, 0, input.size()) == hipSuccess);
  REQUIRE(hipMemcpyPeerAsync(destination, 1, source, 0, input.size(), nullptr) == hipSuccess);
  REQUIRE(hipDeviceSynchronize() == hipSuccess);
  REQUIRE(hipMemcpy(output.data(), destination, output.size(), hipMemcpyDeviceToHost) == hipSuccess);
  CHECK(output == input);

  REQUIRE(hipFree(destination) == hipSuccess);
  REQUIRE(hipFree(source) == hipSuccess);
}

TEST_CASE("HIP modules load onto the current device", "[hip][multi-device]") {
  REQUIRE(hipSetDevice(0) == hipSuccess);
  const auto untouched = freeBytes();
  REQUIRE(hipSetDevice(1) == hipSuccess);
  CHECK(freeBytes() == untouched);

  hipModule_t module{};
  const auto image = gemstone::hip::test::offloadBundle(gemstone::hsa::test::variableKernelObjectFixture());
  fixture.programCatalogue({{.name = "add", .linkageName = "add.kd"}},
                           {{.name = "arbitrary_bss", .virtualAddress = 0x2000, .size = 32, .pointerOffsets = {}}});
  fixture.expectGlobalBinding("arbitrary_bss", 0, std::vector<uint8_t>(32));
  REQUIRE(hipModuleLoadData(&module, image.data()) == hipSuccess);
  CHECK(freeBytes() < untouched);
  REQUIRE(hipSetDevice(0) == hipSuccess);
  CHECK(freeBytes() == untouched);
  REQUIRE(hipModuleUnload(module) == hipSuccess);
}

TEST_CASE("HIP registered device variables live on the device that resolves them", "[hip][multi-device]") {
  const auto bundle = gemstone::hip::test::offloadBundle(gemstone::hsa::test::variableObjectFixture());
  const std::array<const void *, 3> wrapper{nullptr, bundle.data(), nullptr};
  void **const token = __hipRegisterFatBinary(wrapper.data());
  std::array<uint8_t, 8> host{};
  std::array<char, 16> name{"arbitrary_data"};
  __hipRegisterVar(token, host.data(), nullptr, name.data(), 0, host.size(), 0, 0);
  const void *const symbol = host.data();

  std::array<void *, 2> addresses{};
  for (const int device : {1, 0}) {
    CAPTURE(device);
    REQUIRE(hipSetDevice(device) == hipSuccess);
    fixture.programCatalogue({}, {{.name = "arbitrary_data", .virtualAddress = 0x1004, .size = 8, .pointerOffsets = {}}});
    fixture.expectGlobalBinding("arbitrary_data", 0, {5, 6, 7, 8, 9, 10, 11, 12});
    REQUIRE(hipGetSymbolAddress(&addresses[static_cast<size_t>(device)], symbol) == hipSuccess);
    hipPointerAttribute_t attributes{};
    REQUIRE(hipPointerGetAttributes(&attributes, addresses[static_cast<size_t>(device)]) == hipSuccess);
    CHECK(attributes.device == device);
  }
  CHECK(addresses[0] != addresses[1]);
  __hipUnregisterFatBinary(token);
  REQUIRE(hipSetDevice(0) == hipSuccess);
}

TEST_CASE("HIP kernels reach a peer device's memory once peer access is enabled", "[hip][multi-device]") {
  PeerLaunch session;
  REQUIRE(hipDeviceEnablePeerAccess(0, 0) == hipSuccess);
  fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = session.seed});
  CHECK(session.launch() == hipSuccess);
  CHECK(hipDeviceSynchronize() == hipSuccess);
  fixture.check();
  CHECK(hipDeviceDisablePeerAccess(0) == hipSuccess);
}

TEST_CASE("HIP ungranted peer launch child", "[.]") {
  PeerLaunch session;
  fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = session.seed});
  (void)session.launch();
  (void)hipDeviceSynchronize();
}

TEST_CASE("HIP kernels fault on a peer device's memory without peer access", "[hip][multi-device]") {
  const auto outcome = gemstone_test::runChildTest("HIP ungranted peer launch child");
  CHECK(outcome.signal == SIGABRT);
  CHECK(outcome.output.contains("inactivated due to async error"));
  CHECK(outcome.output.contains("cannot access an allocation"));
}

TEST_CASE("HIP kernels load a peer pointer stored in device memory once peer access is enabled", "[hip][multi-device]") {
  IndirectPeerLaunch session;
  REQUIRE(hipDeviceEnablePeerAccess(0, 0) == hipSuccess);
  CHECK(session.launch() == hipSuccess);
  CHECK(hipDeviceSynchronize() == hipSuccess);
  fixture.check();
  CHECK(hipDeviceDisablePeerAccess(0) == hipSuccess);
}

TEST_CASE("HIP ungranted indirect peer launch child", "[.]") {
  IndirectPeerLaunch session;
  (void)session.launch();
  (void)hipDeviceSynchronize();
}

TEST_CASE("HIP kernels fault on a peer pointer stored in device memory without peer access", "[hip][multi-device]") {
  const auto outcome = gemstone_test::runChildTest("HIP ungranted indirect peer launch child");
  CHECK(outcome.signal == SIGABRT);
  CHECK(outcome.output.contains("inactivated due to async error"));
  CHECK(outcome.output.contains("cannot access an allocation"));
}
