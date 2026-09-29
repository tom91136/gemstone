#include "transfer.hpp"

#include <cstdlib>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators.hpp>

#include "gemstone_execution_rpc.hpp"

#include "completion.hpp"
#include "rpc_fixture.hpp"
#include <environment_keys.hpp>

TEST_CASE("chunked transfer round-trips allocation bytes") {
  gemstone_test::RpcFixture fixture(GENERATE(false, true));
  const auto endpoint = fixture.endpoint();
  REQUIRE((setenv(gemstone::environment::RpcEndpoint, endpoint.c_str(), 1) == 0));
  REQUIRE((setenv(gemstone::environment::RpcClient, "42", 1) == 0));

  auto client = gemstone::execution::Client::fromEnvironment();
  REQUIRE((client != nullptr));
  auto registered = client->call(gemstone::execution::RegisterAllocation{gemstone::execution::AllocationKind::Device, 8});
  REQUIRE((static_cast<bool>(registered)));

  const std::vector<uint8_t> input{1, 2, 3, 4, 5, 6, 7, 8};
  REQUIRE((gemstone::execution::transferAllocation(*client, registered.value(), 0, input.data(), input.size(), true, 3)));

  std::vector<uint8_t> output(input.size(), 0);
  REQUIRE((gemstone::execution::transferAllocation(*client, registered.value(), 0, output.data(), output.size(), false, 3)));
  REQUIRE((output == input));

  unsetenv(gemstone::environment::RpcEndpoint);
  unsetenv(gemstone::environment::RpcClient);
}

TEST_CASE("completion collection follows full dirty-range pages through the final short page") {
  using namespace gemstone::execution;
  const auto count = GENERATE(MaximumElements, MaximumElements + 3);
  gemstone_test::RpcFixture fixture;
  fixture.expect({1, count, count, 0, 0});
  const auto endpoint = fixture.endpoint();
  REQUIRE(setenv(gemstone::environment::RpcEndpoint, endpoint.c_str(), 1) == 0);
  REQUIRE(setenv(gemstone::environment::RpcClient, "42", 1) == 0);
  auto client = Client::fromEnvironment();
  REQUIRE(client);
  const auto allocation = client->call(RegisterAllocation{AllocationKind::Device, static_cast<int64_t>(count) * 4});
  REQUIRE(allocation);
  const auto program = client->call(LoadProgram{"spirv-binary", {1}});
  REQUIRE(program);
  const auto completion = client->call(Launch{program.value(),
                                              "write",
                                              {1, 1, 1},
                                              {static_cast<int32_t>(count), 1, 1},
                                              {count, 1, 1},
                                              1,
                                              {},
                                              0,
                                              0,
                                              {ArgumentMemory{allocation.value(), 0}, ArgumentScalar{{0, 0, 0, 0}}},
                                              {}});
  REQUIRE(completion);
  CompletionResult result;
  std::string error;
  REQUIRE(collect_completion(*client, completion.value(), result, error));
  REQUIRE(result.dirty_ranges.size() == count);
  for (size_t index = 0; index < count; ++index) {
    CHECK(result.dirty_ranges[index].allocation.value == allocation.value().value);
    CHECK(result.dirty_ranges[index].offset.value == static_cast<int64_t>(index * 4));
    CHECK(result.dirty_ranges[index].size.value == 4);
  }
  fixture.check();
  unsetenv(gemstone::environment::RpcEndpoint);
  unsetenv(gemstone::environment::RpcClient);
}

TEST_CASE("sparse writeback batches reads and leaves host gaps untouched") {
  using namespace gemstone::execution;
  gemstone_test::RpcFixture fixture;
  const auto endpoint = fixture.endpoint();
  REQUIRE(setenv(gemstone::environment::RpcEndpoint, endpoint.c_str(), 1) == 0);
  REQUIRE(setenv(gemstone::environment::RpcClient, "42", 1) == 0);
  auto client = Client::fromEnvironment();
  REQUIRE(client);
  const auto allocation = client->call(RegisterAllocation{AllocationKind::Device, 16});
  REQUIRE(allocation);
  REQUIRE(client->call(WriteAllocation{allocation.value(), 0, std::vector<uint8_t>(16, 42)}));
  std::vector<uint8_t> host(16, 99);
  const std::vector<DirtyRange> ranges{{allocation.value(), 1, 2}, {allocation.value(), 7, 1}, {allocation.value(), 12, 3}};
  REQUIRE(readDirtyRanges(*client, ranges, [&](const DirtyRange &range, const uint8_t *bytes) {
    std::memcpy(host.data() + range.offset.value, bytes, range.size.value);
    return true;
  }));
  CHECK(fixture.reads() == 1);
  CHECK(host == std::vector<uint8_t>{99, 42, 42, 99, 99, 99, 99, 42, 99, 99, 99, 99, 42, 42, 42, 99});
  CHECK_FALSE(readDirtyRanges(*client, ranges, [](const DirtyRange &, const uint8_t *) { return false; }));
  const auto reads = fixture.reads();
  CHECK_FALSE(readDirtyRanges(*client, {{allocation.value(), INT64_MAX, 1}}, [](const DirtyRange &, const uint8_t *) { return true; }));
  CHECK(fixture.reads() == reads);
  // A single interval can span multiple transport batches. Distinct allocations
  // must also remain separate even when their offsets happen to be adjacent.
  constexpr int32_t LargeSize = 2 * 1024 * 1024 + 3;
  const auto large = client->call(RegisterAllocation{AllocationKind::Device, LargeSize});
  REQUIRE(large);
  const std::vector<uint8_t> largeInput(LargeSize, 17);
  REQUIRE(client->call(WriteAllocation{large.value(), 0, largeInput}));
  std::vector<uint8_t> largeOutput(LargeSize, 0);
  REQUIRE(readDirtyRanges(*client, {{large.value(), 0, LargeSize}, {allocation.value(), 0, 1}},
                          [&](const DirtyRange &range, const uint8_t *bytes) {
                            auto &destination = range.allocation.value == large.value().value ? largeOutput : host;
                            std::memcpy(destination.data() + range.offset.value, bytes, range.size.value);
                            return true;
                          }));
  CHECK(largeOutput == largeInput);
  CHECK(host.front() == 42);
  CHECK(fixture.reads() == reads + 4);
  unsetenv(gemstone::environment::RpcEndpoint);
  unsetenv(gemstone::environment::RpcClient);
}
