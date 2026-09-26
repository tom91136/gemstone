#include <cstdlib>
#include <string>

#include <catch2/catch_test_macros.hpp>

#include "client.hpp"
#include "rpc_fixture.hpp"

TEST_CASE("client manager distinguishes disabled, misconfigured, and ready states") {
  unsetenv("GEMSTONE_RPC_PORT");
  unsetenv("GEMSTONE_RPC_CLIENT");
  gemstone::execution::ClientManager disabled;
  REQUIRE((disabled.mode() == gemstone::execution::ClientMode::Disabled));
  REQUIRE((disabled.client() == nullptr));

  REQUIRE((setenv("GEMSTONE_RPC_PORT", "1", 1) == 0));
  gemstone::execution::ClientManager partial;
  REQUIRE((partial.mode() == gemstone::execution::ClientMode::Misconfigured));
  REQUIRE((partial.client() == nullptr));
  unsetenv("GEMSTONE_RPC_PORT");

  gemstone_test::RpcFixture fixture;
  const auto port = std::to_string(fixture.port());
  REQUIRE((setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0));
  REQUIRE((setenv("GEMSTONE_RPC_CLIENT", "42", 1) == 0));
  gemstone::execution::ClientManager ready;
  REQUIRE((ready.mode() == gemstone::execution::ClientMode::Ready));
  REQUIRE((ready.client() != nullptr));
  unsetenv("GEMSTONE_RPC_PORT");
  unsetenv("GEMSTONE_RPC_CLIENT");
}
