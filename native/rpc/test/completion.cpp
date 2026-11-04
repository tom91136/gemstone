#include "completion.hpp"

#include <atomic>
#include <cstdint>
#include <cstring>
#include <string>
#include <thread>
#include <vector>

#include <unistd.h>

#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators.hpp>

#include "gemstone_execution_rpc.hpp"

#include "rpc_fixture.hpp"
#include <sys/mman.h>
#include <sys/wait.h>

using namespace gemstone::execution;

TEST_CASE("Completion collection retrieves every bounded metadata page", "[rpc]") {
  enum class Statistics { Omit, Read, Fail };
  const auto statisticsMode = GENERATE(Statistics::Omit, Statistics::Read, Statistics::Fail);
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  auto client = Client::fromEnvironment();
  REQUIRE(client);
  for (const uint32_t count : {1U, MaximumElements, MaximumElements + 1}) {
    CAPTURE(count);
    fixture.expect({.local = 1, .groups = count, .global = count, .offset = 0, .seed = 0});
    auto allocation = client->call(RegisterAllocation{{static_cast<int64_t>(count) * 4}});
    REQUIRE(allocation);
    auto program = client->call(LoadProgram{.encoding = ProgramEncoding::SPIRVOpenCLCompute, .image = {3, 2, 35, 7}});
    REQUIRE(program);
    auto completion =
        client->call(Launch{.program = program.value(),
                            .kernelName = {"write"},
                            .groupSize = {.x = {1}, .y = {1}, .z = {1}},
                            .groupCount = {.x = {static_cast<int32_t>(count)}, .y = {1}, .z = {1}},
                            .globalSize = {.x = {count}, .y = {1}, .z = {1}},
                            .workDim = {1},
                            .globalOffset = {.x = {0}, .y = {0}, .z = {0}},
                            .privateSegmentBytes = {0},
                            .groupSegmentBytes = {0},
                            .arguments = {ArgumentMemory{.region = allocation.value(), .offset = {0}}, ArgumentScalar{{{0, 0, 0, 0}}}},
                            .pointerRelocations = {}});
    REQUIRE(completion);
    CompletionResult result;
    std::string error;
    GraphicsStatistics statistics{99}; // Collection must replace, or clear, a previous result.
    if (statisticsMode == Statistics::Fail) fixture.failNextGraphicsStatistics();
    const bool success =
        collectCompletion(*client, completion.value(), result, error, statisticsMode == Statistics::Omit ? nullptr : &statistics);
    if (statisticsMode == Statistics::Fail) {
      REQUIRE_FALSE(success);
      REQUIRE_FALSE(error.empty());
      REQUIRE(statistics.occlusionSamples.value == 0);
      REQUIRE(client->call(ReleaseAllocation{allocation.value()}));
      REQUIRE(client->call(UnloadProgram{program.value()}));
      fixture.check(); // Releasing the failed completion allows the next launch to proceed.
      continue;
    }
    REQUIRE(success);
    if (statisticsMode == Statistics::Read) REQUIRE(statistics.occlusionSamples.value == 17);

    REQUIRE(result.dirtyRanges.size() == count);
    for (uint32_t index = 0; index < count; ++index) {
      const auto &range = result.dirtyRanges[index];
      REQUIRE(range.allocation.value == allocation.value().value);
      REQUIRE(range.offset.value == static_cast<int64_t>(index) * 4);
      REQUIRE(range.size.value == 4);
    }
    REQUIRE(result.pointerRelocations.empty());
    REQUIRE(client->call(ReleaseAllocation{allocation.value()}));
    REQUIRE(client->call(UnloadProgram{program.value()}));
    fixture.check();
  }
}

TEST_CASE("Failures carry the kind the service reported", "[rpc]") {
  const auto kind = GENERATE(FailureKind::InvalidRequest, FailureKind::NotFound, FailureKind::Unsupported, FailureKind::Failed,
                             FailureKind::InvalidProgram, FailureKind::InvalidConfiguration, FailureKind::OutOfResources,
                             FailureKind::IllegalAddress, FailureKind::MisalignedAddress, FailureKind::IllegalInstruction,
                             FailureKind::Trapped, FailureKind::AssertionFailed, FailureKind::TimedOut);
  CAPTURE(static_cast<int>(kind));
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  auto client = Client::fromEnvironment();
  REQUIRE(client);

  fixture.rejectNext(operation::LoadProgram, kind);
  const auto rejected = client->call(LoadProgram{.encoding = ProgramEncoding::PTXText, .image = {'.'}});
  REQUIRE_FALSE(rejected);
  CHECK(rejected.error().kind == kind);
  CHECK(rejected.error().detail == "LoadProgram rejected");

  auto program = client->call(LoadProgram{.encoding = ProgramEncoding::SPIRVOpenCLCompute, .image = {3, 2, 35, 7}});
  REQUIRE(program);
  auto allocation = client->call(RegisterAllocation{{4}});
  REQUIRE(allocation);
  fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = 0});
  auto completion =
      client->call(Launch{.program = program.value(),
                          .kernelName = {"write"},
                          .groupSize = {.x = {1}, .y = {1}, .z = {1}},
                          .groupCount = {.x = {1}, .y = {1}, .z = {1}},
                          .globalSize = {.x = {1}, .y = {1}, .z = {1}},
                          .workDim = {1},
                          .globalOffset = {.x = {0}, .y = {0}, .z = {0}},
                          .privateSegmentBytes = {0},
                          .groupSegmentBytes = {0},
                          .arguments = {ArgumentMemory{.region = allocation.value(), .offset = {0}}, ArgumentScalar{{{0, 0, 0, 0}}}},
                          .pointerRelocations = {}});
  REQUIRE(completion);
  fixture.failNextCompletion(kind);
  CompletionResult result;
  std::string error;
  REQUIRE_FALSE(collectCompletion(*client, completion.value(), result, error));
  CHECK(result.failure == kind);
  CHECK(error == "completion failed");
  fixture.check();
}

TEST_CASE("Launches keep one connection per thread and process and replace a broken one", "[rpc]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  auto client = Client::fromEnvironment();
  REQUIRE(client);
  auto program = client->call(LoadProgram{.encoding = ProgramEncoding::SPIRVOpenCLCompute, .image = {3, 2, 35, 7}});
  REQUIRE(program);
  auto allocation = client->call(RegisterAllocation{{4}});
  REQUIRE(allocation);
  const auto launch = [&] {
    fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = 0});
    CompletionResult result;
    std::string error;
    return launchAndCollect(
        Launch{.program = program.value(),
               .kernelName = {"write"},
               .groupSize = {.x = {1}, .y = {1}, .z = {1}},
               .groupCount = {.x = {1}, .y = {1}, .z = {1}},
               .globalSize = {.x = {1}, .y = {1}, .z = {1}},
               .workDim = {1},
               .globalOffset = {.x = {0}, .y = {0}, .z = {0}},
               .privateSegmentBytes = {0},
               .groupSegmentBytes = {0},
               .arguments = {ArgumentMemory{.region = allocation.value(), .offset = {0}}, ArgumentScalar{{{0, 0, 0, 0}}}},
               .pointerRelocations = {}},
        result, error);
  };
  const unsigned before = fixture.accepted;
  REQUIRE(launch());
  REQUIRE(launch());
  CHECK(fixture.accepted - before == 1);

  fixture.dropNext(operation::Launch);
  CHECK_FALSE(launch());
  REQUIRE(launch());
  CHECK(fixture.accepted - before == 2);

  const auto child = fork();
  REQUIRE(child >= 0);
  if (child == 0) _exit(launchClient() && launchClient()->call(Ping{}) ? 0 : 1);
  int status = 0;
  REQUIRE(waitpid(child, &status, 0) == child);
  CHECK((WIFEXITED(status) && WEXITSTATUS(status) == 0));
  CHECK(fixture.accepted - before == 3);
  fixture.check();
}

TEST_CASE("Failures that never reach the service are transport failures", "[rpc]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  auto client = Client::fromEnvironment();
  REQUIRE(client);
  const auto invalid = client->call(RegisterAllocation{{0}});
  REQUIRE_FALSE(invalid);
  CHECK(invalid.error().kind == FailureKind::Transport);
}

TEST_CASE("Host byte copies cover unaligned ranges exactly", "[completion][host-memory]") {
  alignas(8) uint8_t storage[32];
  for (size_t index = 0; index < sizeof(storage); ++index)
    storage[index] = static_cast<uint8_t>((index * 7) + 1);
  for (size_t begin = 0; begin < 9; ++begin) {
    for (size_t size = 0; size + begin <= sizeof(storage); ++size) {
      const auto copied = copyHostBytes(reinterpret_cast<uintptr_t>(storage + begin), size);
      REQUIRE(copied == std::vector<uint8_t>(storage + begin, storage + begin + size));
    }
  }
}

TEST_CASE("Host byte copies never tear an aligned word", "[completion][host-memory]") {
  alignas(8) std::atomic<uint64_t> word{0};
  std::atomic<bool> stop{false};
  std::thread writer([&] {
    for (uint64_t flip = 0; !stop.load(std::memory_order_relaxed); ++flip)
      word.store(flip % 2 ? ~0ULL : 0ULL, std::memory_order_relaxed);
  });
  bool torn = false;
  for (int round = 0; round < 200000 && !torn; ++round) {
    const auto copied = copyHostBytes(reinterpret_cast<uintptr_t>(&word), sizeof(uint64_t));
    uint64_t value = 0;
    std::memcpy(&value, copied.data(), sizeof(value));
    torn = value != 0 && value != ~0ULL;
  }
  stop.store(true);
  writer.join();
  CHECK_FALSE(torn);
}

TEST_CASE("Readable extents stop at the first page the process cannot read", "[completion][host-memory]") {
  const auto page = static_cast<size_t>(sysconf(_SC_PAGESIZE));
  constexpr size_t pages = 6;
  auto *region = static_cast<uint8_t *>(mmap(nullptr, pages * page, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0));
  REQUIRE(region != MAP_FAILED);
  REQUIRE(munmap(region + (2 * page), page) == 0);
  REQUIRE(mprotect(region + (4 * page), page, PROT_NONE) == 0);
  const auto address = reinterpret_cast<uintptr_t>(region);
  CHECK(readableExtent(address, 2 * page) == 2 * page);
  CHECK(readableExtent(address + 16, page) == page);
  CHECK(readableExtent(address, 3 * page) == 2 * page);
  CHECK(readableExtent(address + page + 100, 4 * page) == page - 100);
  CHECK(readableExtent(address + (2 * page), page) == 0);
  CHECK(readableExtent(address + (2 * page) + 8, 8) == 0);
  CHECK(readableExtent(address + (3 * page), 3 * page) == page);
  CHECK(readableExtent(address + (4 * page), page) == 0);
  CHECK(readableExtent(address + (5 * page) - 4, 4) == 0);
  CHECK(readableExtent(address + (6 * page) - 4, 4) == 4);
  CHECK(readableExtent(address + page - 8, 0) == 0);
  CHECK(copyHostBytes(address, readableExtent(address, 3 * page)).size() == 2 * page);
  munmap(region, 2 * page);
  munmap(region + (3 * page), 3 * page);
}
