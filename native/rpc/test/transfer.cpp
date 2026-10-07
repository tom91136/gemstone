#include "transfer.hpp"

#include <cstdlib>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators.hpp>

#include "gemstone_execution_rpc.hpp"

#include "rpc_fixture.hpp"
#include <environment_keys.hpp>

TEST_CASE("chunked transfer round-trips allocation bytes") {
  gemstone_test::RpcFixture fixture(GENERATE(false, true));
  const gemstone_test::RpcEnvironment environment(fixture);

  auto client = gemstone::execution::Client::fromEnvironment();
  REQUIRE((client != nullptr));
  auto registered = client->call(gemstone::execution::RegisterAllocation{{8}});
  REQUIRE((static_cast<bool>(registered)));

  const std::vector<uint8_t> input{1, 2, 3, 4, 5, 6, 7, 8};
  REQUIRE((gemstone::execution::transferAllocation(*client, registered.value(), 0, input.data(), input.size(), true, 3)));

  std::vector<uint8_t> output(input.size(), 0);
  REQUIRE((gemstone::execution::transferAllocation(*client, registered.value(), 0, output.data(), output.size(), false, 3)));
  REQUIRE((output == input));
}

TEST_CASE("sparse writeback batches reads and leaves host gaps untouched") {
  using namespace gemstone::execution;
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  auto client = Client::fromEnvironment();
  REQUIRE(client);
  const auto allocation = client->call(RegisterAllocation{{16}});
  REQUIRE(allocation);
  REQUIRE(client->call(WriteAllocation{allocation.value(), 0, std::vector<uint8_t>(16, 42)}));
  std::vector<uint8_t> host(16, 99);
  const std::vector<DirtyRange> ranges{{allocation.value(), 1, 2}, {allocation.value(), 7, 1}, {allocation.value(), 12, 3}};
  REQUIRE(readDirtyRanges(*client, ranges, [&](const DirtyRange &range, const uint8_t *bytes) {
    std::memcpy(host.data() + range.offset.value, bytes, range.size.value);
    return true;
  }));
  CHECK(fixture.reads.load() == 1);
  CHECK(host == std::vector<uint8_t>{99, 42, 42, 99, 99, 99, 99, 42, 99, 99, 99, 99, 42, 42, 42, 99});
  CHECK_FALSE(readDirtyRanges(*client, ranges, [](const DirtyRange &, const uint8_t *) { return false; }));
  const auto reads = fixture.reads.load();
  CHECK_FALSE(readDirtyRanges(*client, {{allocation.value(), INT64_MAX, 1}}, [](const DirtyRange &, const uint8_t *) { return true; }));
  CHECK(fixture.reads.load() == reads);
  // A single interval can span multiple transport batches. Distinct allocations
  // must also remain separate even when their offsets happen to be adjacent.
  constexpr int32_t largeSize = 2 * 1024 * 1024 + 3;
  const auto large = client->call(RegisterAllocation{{largeSize}});
  REQUIRE(large);
  const std::vector<uint8_t> largeInput(largeSize, 17);
  REQUIRE(client->call(WriteAllocation{large.value(), 0, largeInput}));
  std::vector<uint8_t> largeOutput(largeSize, 0);
  REQUIRE(readDirtyRanges(*client, {{large.value(), 0, largeSize}, {allocation.value(), 0, 1}},
                          [&](const DirtyRange &range, const uint8_t *bytes) {
                            auto &destination = range.allocation.value == large.value().value ? largeOutput : host;
                            std::memcpy(destination.data() + range.offset.value, bytes, range.size.value);
                            return true;
                          }));
  CHECK(largeOutput == largeInput);
  CHECK(host.front() == 42);
  CHECK(fixture.reads.load() == reads + 4);
}

TEST_CASE("bounded dirty reads respect gap and payload limits without publishing gaps") {
  using namespace gemstone::execution;
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  auto client = Client::fromEnvironment();
  REQUIRE(client);
  constexpr int32_t batchBytes = 64 * 1024;
  const auto allocation = client->call(RegisterAllocation{{batchBytes + 1}});
  REQUIRE(allocation);
  REQUIRE(client->call(WriteAllocation{allocation.value(), 0, std::vector<uint8_t>(batchBytes + 1, 42)}));
  std::vector<uint8_t> host(batchBytes + 1, 99);
  const auto apply = [&](const DirtyRange &range, const uint8_t *bytes) {
    std::memcpy(host.data() + range.offset.value, bytes, range.size.value);
    return true;
  };
  const std::vector<DirtyRange> sparse{{allocation.value(), 1, 2}, {allocation.value(), 259, 1}, {allocation.value(), 517, 1}};
  REQUIRE(readDirtyRanges(*client, sparse, apply, 256, batchBytes));
  CHECK(fixture.reads.load() == 2); // The first gap is 256 bytes; the next is 257.
  for (size_t index = 0; index < host.size(); ++index)
    REQUIRE(host[index] == ((index == 1 || index == 2 || index == 259 || index == 517) ? 42 : 99));
  std::fill(host.begin(), host.end(), 99);
  std::vector<DirtyRange> spans;
  for (int32_t offset = 0; offset <= batchBytes; offset += 256)
    spans.push_back({allocation.value(), offset, 1});
  const auto reads = fixture.reads.load();
  REQUIRE(readDirtyRanges(*client, spans, apply, 256, batchBytes));
  CHECK(fixture.reads.load() == reads + 2);
  for (size_t index = 0; index < host.size(); ++index)
    REQUIRE(host[index] == (index % 256 == 0 ? 42 : 99));
  CHECK_FALSE(readDirtyRanges(*client, spans, apply, 256, 0));
  CHECK_FALSE(readDirtyRanges(*client, spans, apply, 256, TransferChunk + 1));
  CHECK(fixture.reads.load() == reads + 2);
}
