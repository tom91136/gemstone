#include "completion.hpp"

#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators.hpp>

#include "rpc_fixture.hpp"
#include <environment_keys.hpp>

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
    fixture.expect({1, count, count, 0, 0});
    auto allocation = client->call(RegisterAllocation{AllocationKind::Shared, count * 4});
    REQUIRE(allocation);
    auto program = client->call(LoadProgram{"spirv-binary", {3, 2, 35, 7}});
    REQUIRE(program);
    auto completion = client->call(Launch{program.value(),
                                          "write",
                                          {1, 1, 1},
                                          {static_cast<int32_t>(count), 1, 1},
                                          {count, 1, 1},
                                          1,
                                          {0, 0, 0},
                                          0,
                                          0,
                                          {ArgumentMemory{allocation.value(), 0}, ArgumentScalar{{0, 0, 0, 0}}},
                                          {}});
    REQUIRE(completion);
    CompletionResult result;
    std::string error;
    GraphicsStatistics statistics{99}; // Collection must replace, or clear, a previous result.
    if (statisticsMode == Statistics::Fail) fixture.failNextGraphicsStatistics();
    const bool success =
        collect_completion(*client, completion.value(), result, error, false, statisticsMode == Statistics::Omit ? nullptr : &statistics);
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

    REQUIRE(result.dirty_ranges.size() == count);
    for (uint32_t index = 0; index < count; ++index) {
      const auto &range = result.dirty_ranges[index];
      REQUIRE(range.allocation.value == allocation.value().value);
      REQUIRE(range.offset.value == index * 4);
      REQUIRE(range.size.value == 4);
    }
    REQUIRE(result.pointer_relocations.empty());
    REQUIRE(client->call(ReleaseAllocation{allocation.value()}));
    REQUIRE(client->call(UnloadProgram{program.value()}));
    fixture.check();
  }
}
