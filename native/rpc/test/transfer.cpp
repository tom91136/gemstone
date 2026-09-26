#include "transfer.hpp"

#include <cstdlib>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#include "gemstone_execution_rpc.hpp"

#include "rpc_fixture.hpp"

TEST_CASE("chunked transfer round-trips allocation bytes") {
  gemstone_test::RpcFixture fixture;
  const auto port = std::to_string(fixture.port());
  REQUIRE((setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0));
  REQUIRE((setenv("GEMSTONE_RPC_CLIENT", "42", 1) == 0));

  auto client = gemstone::execution::Client::fromEnvironment();
  REQUIRE((client != nullptr));
  auto registered = client->call(gemstone::execution::RegisterAllocation{gemstone::execution::AllocationKind::Device, 8});
  REQUIRE((static_cast<bool>(registered)));

  const std::vector<uint8_t> input{1, 2, 3, 4, 5, 6, 7, 8};
  REQUIRE((gemstone::execution::transferAllocation(*client, registered.value(), 0, input.data(), input.size(), true, 3)));

  std::vector<uint8_t> output(input.size(), 0);
  REQUIRE((gemstone::execution::transferAllocation(*client, registered.value(), 0, output.data(), output.size(), false, 3)));
  REQUIRE((output == input));

  unsetenv("GEMSTONE_RPC_PORT");
  unsetenv("GEMSTONE_RPC_CLIENT");
}
