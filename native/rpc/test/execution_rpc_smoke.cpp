#include <chrono>
#include <cstdint>
#include <cstdlib>
#include <thread>
#include <utility>
#include <variant>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#include "gemstone_execution_rpc.hpp"

using namespace gemstone::execution;

TEST_CASE("execution RPC end-to-end smoke test", "[rpc][integration]") {
  auto memoryClient = Client::fromEnvironment();
  auto executionClient = Client::fromEnvironment();
  REQUIRE((memoryClient && executionClient));

  auto allocationResult = memoryClient->call(RegisterAllocation{{8}});
  REQUIRE((allocationResult));
  const auto allocation = allocationResult.value();
  REQUIRE((memoryClient->call(WriteAllocation{allocation, 0, {1, 2, 3, 4}})));

  auto programResult = executionClient->call(LoadProgram{.encoding = ProgramEncoding::SPIRVOpenCLCompute, .image = {3, 2, 35, 7}});
  REQUIRE((programResult));
  const auto program = programResult.value();
  auto catalogue = executionClient->call(DescribeProgram{program});
  REQUIRE((catalogue));
  REQUIRE((catalogue.value().kernels.size() == 1));
  REQUIRE((catalogue.value().kernels[0].name.value == "add"));
  REQUIRE((catalogue.value().kernels[0].linkageName.value == "add.kd"));

  std::vector<Argument> arguments{
      ArgumentMemory{.region = AllocationId{allocation.value}, .offset = {2}},
      ArgumentScalar{{{9, 8}}},
      ArgumentLocal{64},
  };
  auto completionResult = executionClient->call(Launch{
      .program = program,
      .kernelName = {"write"},
      .groupSize = {.x = {1}, .y = {1}, .z = {1}},
      .groupCount = {.x = {1}, .y = {1}, .z = {1}},
      .globalSize = GlobalDimensions{.x = {1}, .y = {1}, .z = {1}},
      .workDim = {3},
      .globalOffset = {.x = {0}, .y = {0}, .z = {0}},
      .privateSegmentBytes = {0},
      .groupSegmentBytes = {0},
      .arguments = std::move(arguments),
      .pointerRelocations = {},
  });
  REQUIRE((completionResult));
  const auto completion = completionResult.value();

  const auto completionDeadline = std::chrono::steady_clock::now() + std::chrono::seconds(10);
  for (;;) {
    auto status = memoryClient->call(QueryCompletion{completion});
    REQUIRE((status));
    if (std::holds_alternative<CompletionStatusPending>(status.value())) {
      REQUIRE((std::chrono::steady_clock::now() < completionDeadline));
      std::this_thread::sleep_for(std::chrono::microseconds(50));
      continue;
    }
    REQUIRE((std::holds_alternative<CompletionStatusSucceeded>(status.value())));
    break;
  }

  auto relocations = executionClient->call(CompletionPointerRelocations{.completion = completion});
  REQUIRE((relocations && relocations.value().empty()));
  auto dirtyRanges = memoryClient->call(CompletionDirtyRanges{.completion = completion, .offset = {0}});
  REQUIRE((dirtyRanges && dirtyRanges.value().size() == 1));
  const auto &dirty = dirtyRanges.value()[0];
  REQUIRE((dirty.allocation.value == allocation.value && dirty.offset.value == 0 && dirty.size.value == 8));

  auto bytes = memoryClient->call(ReadAllocation{.allocation = allocation, .offset = {0}, .size = {8}});
  REQUIRE((bytes));
  REQUIRE((bytes.value() == std::vector<uint8_t>({1, 2, 9, 8, 0, 0, 0, 0})));

  constexpr size_t largeSize = (16U * 1024U * 1024U) + 37U;
  std::vector<uint8_t> expected(largeSize);
  for (size_t index = 0; index < expected.size(); ++index)
    expected[index] = static_cast<uint8_t>(((index * 131U) + 17U) & 0xffU);
  auto largeAllocationResult = memoryClient->call(RegisterAllocation{{largeSize}});
  REQUIRE((largeAllocationResult));
  const auto largeAllocation = largeAllocationResult.value();
  REQUIRE((executionClient->call(WriteAllocation{largeAllocation, 0, expected})));
  for (unsigned round = 0; round < 4; ++round) {
    auto actual =
        executionClient->call(ReadAllocation{.allocation = largeAllocation, .offset = {0}, .size = {static_cast<int32_t>(largeSize)}});
    REQUIRE((actual && actual.value() == expected));
  }

  REQUIRE((executionClient->call(ReleaseCompletion{completion})));
  REQUIRE((memoryClient->call(UnloadProgram{program})));
  REQUIRE((executionClient->call(ReleaseAllocation{largeAllocation})));
  REQUIRE((memoryClient->call(ReleaseAllocation{allocation})));
}
