#include "transfer.hpp"

#include <algorithm>
#include <array>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <utility>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators.hpp>

#include "gemstone_execution_rpc.hpp"

#include "rpc_fixture.hpp"

TEST_CASE("chunked transfer round-trips allocation bytes") {
  gemstone_test::RpcFixture const fixture(GENERATE(false, true));
  const gemstone_test::RpcEnvironment environment(fixture);

  auto client = gemstone::execution::Client::fromEnvironment();
  REQUIRE((client != nullptr));
  auto registered = client->call(gemstone::execution::RegisterAllocation{{8}});
  REQUIRE((static_cast<bool>(registered)));

  const std::vector<uint8_t> input{1, 2, 3, 4, 5, 6, 7, 8};
  REQUIRE((gemstone::execution::uploadAllocation(*client, registered.value(), 0, input.data(), input.size(), 3)));

  std::vector<uint8_t> output(input.size(), 0);
  REQUIRE((gemstone::execution::downloadAllocation(*client, registered.value(), 0, output.data(), output.size(), 3)));
  REQUIRE((output == input));
}

TEST_CASE("sparse writeback batches reads and leaves host gaps untouched") {
  using namespace gemstone::execution;
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  auto client = Client::fromEnvironment();
  REQUIRE(client);
  const auto allocation = client->call(RegisterAllocation{{16}});
  REQUIRE(allocation);
  REQUIRE(client->call(WriteAllocation{allocation.value(), 0, std::vector<uint8_t>(16, 42)}));
  std::vector<uint8_t> host(16, 99);
  const std::vector<DirtyRange> ranges{{.allocation = allocation.value(), .offset = {1}, .size = {2}},
                                       {.allocation = allocation.value(), .offset = {7}, .size = {1}},
                                       {.allocation = allocation.value(), .offset = {12}, .size = {3}}};
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
  constexpr int32_t largeSize = (2 * 1024 * 1024) + 3;
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
  gemstone_test::RpcFixture const fixture;
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
  const std::vector<DirtyRange> sparse{{.allocation = allocation.value(), .offset = {1}, .size = {2}},
                                       {.allocation = allocation.value(), .offset = {259}, .size = {1}},
                                       {.allocation = allocation.value(), .offset = {517}, .size = {1}}};
  REQUIRE(readDirtyRanges(*client, sparse, apply, 256, batchBytes));
  CHECK(fixture.reads.load() == 2); // The first gap is 256 bytes; the next is 257.
  for (size_t index = 0; index < host.size(); ++index)
    REQUIRE(host[index] == ((index == 1 || index == 2 || index == 259 || index == 517) ? 42 : 99));
  std::fill(host.begin(), host.end(), 99);
  std::vector<DirtyRange> spans;
  for (int32_t offset = 0; offset <= batchBytes; offset += 256)
    spans.push_back({.allocation = allocation.value(), .offset = {offset}, .size = {1}});
  const auto reads = fixture.reads.load();
  REQUIRE(readDirtyRanges(*client, spans, apply, 256, batchBytes));
  CHECK(fixture.reads.load() == reads + 2);
  for (size_t index = 0; index < host.size(); ++index)
    REQUIRE(host[index] == (index % 256 == 0 ? 42 : 99));
  CHECK_FALSE(readDirtyRanges(*client, spans, apply, 256, 0));
  CHECK_FALSE(readDirtyRanges(*client, spans, apply, 256, TransferChunk + 1));
  CHECK(fixture.reads.load() == reads + 2);
}

TEST_CASE("rectangle transfers gather and scatter rows in bounded requests") {
  using namespace gemstone::execution;
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  auto client = Client::fromEnvironment();
  REQUIRE(client);
  constexpr size_t rowBytes = 5;
  constexpr size_t rowCount = 4;
  constexpr size_t sliceCount = 3;
  constexpr size_t rowPitch = 8;
  constexpr size_t slicePitch = 40;
  constexpr size_t offset = 3;
  constexpr size_t size = offset + (sliceCount * slicePitch);
  const auto allocation = client->call(RegisterAllocation{{static_cast<int64_t>(size)}});
  REQUIRE(allocation);
  REQUIRE(client->call(WriteAllocation{allocation.value(), 0, std::vector<uint8_t>(size, 7)}));
  const Rectangle rectangle{.offset = offset,
                            .rowBytes = rowBytes,
                            .rowCount = rowCount,
                            .sliceCount = sliceCount,
                            .rowPitch = rowPitch,
                            .slicePitch = slicePitch};

  constexpr size_t hostRowPitch = 6;
  constexpr size_t hostSlicePitch = 30;
  std::vector<uint8_t> host(sliceCount * hostSlicePitch, 0);
  for (size_t index = 0; index < host.size(); ++index)
    host[index] = static_cast<uint8_t>(index + 1);

  const auto expected = [&] {
    std::vector<uint8_t> bytes(size, 7);
    for (size_t slice = 0; slice < sliceCount; ++slice)
      for (size_t row = 0; row < rowCount; ++row)
        std::memcpy(bytes.data() + offset + (slice * slicePitch) + (row * rowPitch),
                    host.data() + (slice * hostSlicePitch) + (row * hostRowPitch), rowBytes);
    return bytes;
  }();

  // limits split the region into whole slices, then whole rows, then single rows, and finally row chunks
  for (const auto &[limit, requests] : {std::pair<size_t, unsigned>{1024, 1}, {40, 2}, {20, 3}, {10, 6}, {5, 12}, {4, 0}}) {
    REQUIRE(client->call(WriteAllocation{allocation.value(), 0, std::vector<uint8_t>(size, 7)}));
    const auto writes = fixture.rectangleWrites.load();
    REQUIRE(uploadRectangle(*client, allocation.value(), rectangle, host.data(), hostRowPitch, hostSlicePitch, limit));
    CHECK(fixture.rectangleWrites.load() - writes == requests);
    auto stored = client->call(ReadAllocation{.allocation = allocation.value(), .offset = 0, .size = {static_cast<int32_t>(size)}});
    REQUIRE(stored);
    CHECK(stored.value() == expected);

    std::vector<uint8_t> back(host.size(), 0xee);
    const auto reads = fixture.rectangleReads.load();
    REQUIRE(downloadRectangle(*client, allocation.value(), rectangle, back.data(), hostRowPitch, hostSlicePitch, limit));
    CHECK(fixture.rectangleReads.load() - reads == requests);
    for (size_t slice = 0; slice < sliceCount; ++slice)
      for (size_t row = 0; row < rowCount; ++row)
        for (size_t column = 0; column < hostRowPitch; ++column) {
          const auto index = (slice * hostSlicePitch) + (row * hostRowPitch) + column;
          CHECK(back[index] == (column < rowBytes ? host[index] : 0xee));
        }
  }

  CHECK_FALSE(uploadRectangle(*client, allocation.value(), rectangle, host.data(), hostRowPitch, hostSlicePitch, 0));
  CHECK_FALSE(uploadRectangle(*client, allocation.value(),
                              {.offset = offset,
                               .rowBytes = rowBytes,
                               .rowCount = rowCount,
                               .sliceCount = sliceCount,
                               .rowPitch = rowBytes - 1,
                               .slicePitch = slicePitch},
                              host.data(), hostRowPitch, hostSlicePitch, 1024));

  // pitches beyond the wire limit cannot be allocated in a test, so the block split is checked directly
  using Block = std::array<size_t, 4>;
  const auto blocksOf = [](const Rectangle &shape, size_t limit) {
    std::vector<Block> blocks;
    REQUIRE(detail::forEachRectangleBlock(shape, limit, [&](size_t slice, size_t row, size_t slices, size_t rows) {
      blocks.push_back({slice, row, slices, rows});
      return true;
    }));
    return blocks;
  };
  constexpr size_t wide = size_t{INT32_MAX} + 1;
  CHECK((blocksOf({.rowBytes = 4, .rowCount = 3, .sliceCount = 1, .rowPitch = INT32_MAX}, 1024) == std::vector<Block>{{0, 0, 1, 3}}));
  CHECK((blocksOf({.rowBytes = 4, .rowCount = 3, .sliceCount = 1, .rowPitch = wide}, 1024)
         == std::vector<Block>{{0, 0, 1, 1}, {0, 1, 1, 1}, {0, 2, 1, 1}}));
  CHECK((blocksOf({.rowBytes = 4, .rowCount = 3, .sliceCount = 2, .rowPitch = wide, .slicePitch = 3 * wide}, 1024)
         == std::vector<Block>{{0, 0, 1, 1}, {0, 1, 1, 1}, {0, 2, 1, 1}, {1, 0, 1, 1}, {1, 1, 1, 1}, {1, 2, 1, 1}}));
  CHECK((blocksOf({.rowBytes = 4, .rowCount = 1, .sliceCount = 2, .rowPitch = wide, .slicePitch = 8}, 1024)
         == std::vector<Block>{{0, 0, 2, 1}}));
  CHECK((blocksOf({.rowBytes = 4, .rowCount = 3, .sliceCount = 2, .rowPitch = 8, .slicePitch = wide}, 1024)
         == std::vector<Block>{{0, 0, 1, 3}, {1, 0, 1, 3}}));
  CHECK((blocksOf({.rowBytes = 4, .rowCount = 3, .sliceCount = 2, .rowPitch = 8, .slicePitch = wide}, 8)
         == std::vector<Block>{{0, 0, 1, 2}, {0, 2, 1, 1}, {1, 0, 1, 2}, {1, 2, 1, 1}}));
}
