#include "completion.hpp"

#include <catch2/catch_test_macros.hpp>

#include "rpc_fixture.hpp"
#include <environment_keys.hpp>

using namespace gemstone::execution;

TEST_CASE("Completion collection retrieves every bounded metadata page", "[rpc]") {
  gemstone_test::RpcFixture fixture;
  const auto endpoint = fixture.endpoint();
  REQUIRE(setenv(gemstone::environment::RpcEndpoint, endpoint.c_str(), 1) == 0);
  REQUIRE(setenv(gemstone::environment::RpcClient, "42", 1) == 0);
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
    REQUIRE(collect_completion(*client, completion.value(), result, error));
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
  unsetenv(gemstone::environment::RpcEndpoint);
  unsetenv(gemstone::environment::RpcClient);
}
