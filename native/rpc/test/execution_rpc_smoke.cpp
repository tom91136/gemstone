#include <chrono>
#include <cstdlib>
#include <thread>
#include <variant>
#include <vector>

#include <stdint.h>

#include <catch2/catch_test_macros.hpp>

#include "gemstone_execution_rpc.hpp"

using namespace gemstone::execution;

TEST_CASE("execution RPC end-to-end smoke test", "[rpc][integration]") {
  auto memoryClient = Client::fromEnvironment();
  auto executionClient = Client::fromEnvironment();
  REQUIRE((memoryClient && executionClient));

  auto allocationResult = memoryClient->call(RegisterAllocation{AllocationKind::Shared, 8});
  REQUIRE((allocationResult));
  const auto allocation = allocationResult.value();
  REQUIRE((memoryClient->call(WriteAllocation{allocation, 0, {1, 2, 3, 4}})));

  auto programResult = executionClient->call(LoadProgram{"spirv-binary", {3, 2, 35, 7}});
  REQUIRE((programResult));
  const auto program = programResult.value();
  auto catalog = executionClient->call(DescribeProgram{program});
  REQUIRE((catalog));
  REQUIRE((catalog.value().kernels.size() == 1));
  REQUIRE((catalog.value().kernels[0].name.value == "add"));
  REQUIRE((catalog.value().kernels[0].linkageName.value == "add.kd"));

  std::vector<Argument> arguments{
      ArgumentMemory{AllocationId{allocation.value}, 2},
      ArgumentScalar{{9, 8}},
      ArgumentLocal{64},
  };
  auto completionResult = executionClient->call(Launch{
      program,
      "write",
      {1, 1, 1},
      {1, 1, 1},
      GlobalDimensions{1, 1, 1},
      3,
      {0, 0, 0},
      0,
      0,
      std::move(arguments),
      {},
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

  auto relocations = executionClient->call(CompletionPointerRelocations{completion});
  REQUIRE((relocations && relocations.value().empty()));
  auto dirtyRanges = memoryClient->call(CompletionDirtyRanges{completion, 0});
  REQUIRE((dirtyRanges && dirtyRanges.value().size() == 1));
  const auto &dirty = dirtyRanges.value()[0];
  REQUIRE((dirty.allocation.value == allocation.value && dirty.offset.value == 0 && dirty.size.value == 8));

  auto bytes = memoryClient->call(ReadAllocation{allocation, 0, 8});
  REQUIRE((bytes));
  REQUIRE((bytes.value() == std::vector<uint8_t>({1, 2, 9, 8, 0, 0, 0, 0})));

  constexpr size_t largeSize = 16U * 1024U * 1024U + 37U;
  std::vector<uint8_t> expected(largeSize);
  for (size_t index = 0; index < expected.size(); ++index)
    expected[index] = static_cast<uint8_t>((index * 131U + 17U) & 0xffU);
  auto largeAllocationResult = memoryClient->call(RegisterAllocation{AllocationKind::Shared, largeSize});
  REQUIRE((largeAllocationResult));
  const auto largeAllocation = largeAllocationResult.value();
  REQUIRE((executionClient->call(WriteAllocation{largeAllocation, 0, expected})));
  for (unsigned round = 0; round < 4; ++round) {
    auto actual = executionClient->call(ReadAllocation{largeAllocation, 0, static_cast<int32_t>(largeSize)});
    REQUIRE((actual && actual.value() == expected));
  }

  REQUIRE((executionClient->call(ReleaseCompletion{completion})));
  REQUIRE((memoryClient->call(UnloadProgram{program})));
  REQUIRE((executionClient->call(ReleaseAllocation{largeAllocation})));
  REQUIRE((memoryClient->call(ReleaseAllocation{allocation})));
}
