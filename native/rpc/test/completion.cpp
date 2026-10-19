#include "completion.hpp"

#include <cstdint>
#include <string>

#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators.hpp>

#include "gemstone_execution_rpc.hpp"

#include "rpc_fixture.hpp"

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

TEST_CASE("Failures that never reach the service are transport failures", "[rpc]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  auto client = Client::fromEnvironment();
  REQUIRE(client);
  const auto invalid = client->call(RegisterAllocation{{0}});
  REQUIRE_FALSE(invalid);
  CHECK(invalid.error().kind == FailureKind::Transport);
}
