#include <cstdlib>
#include <string>

#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators.hpp>

#include "client.hpp"
#include "rpc_fixture.hpp"
#include <environment_keys.hpp>

TEST_CASE("client manager distinguishes disabled, misconfigured, and ready states") {
  unsetenv(gemstone::environment::RpcEndpoint);
  unsetenv(gemstone::environment::RpcClient);
  gemstone::execution::ClientManager disabled;
  REQUIRE((disabled.mode() == gemstone::execution::ClientMode::Disabled));
  REQUIRE((disabled.client() == nullptr));

  REQUIRE((setenv(gemstone::environment::RpcEndpoint, "tcp://127.0.0.1:1", 1) == 0));
  gemstone::execution::ClientManager partial;
  REQUIRE((partial.mode() == gemstone::execution::ClientMode::Misconfigured));
  REQUIRE((partial.client() == nullptr));
  unsetenv(gemstone::environment::RpcEndpoint);

  gemstone_test::RpcFixture fixture(GENERATE(false, true));
  const auto endpoint = fixture.endpoint();
  REQUIRE((setenv(gemstone::environment::RpcEndpoint, endpoint.c_str(), 1) == 0));
  REQUIRE((setenv(gemstone::environment::RpcClient, "42", 1) == 0));
  gemstone::execution::ClientManager ready;
  REQUIRE((ready.mode() == gemstone::execution::ClientMode::Ready));
  REQUIRE((ready.client() != nullptr));
  unsetenv(gemstone::environment::RpcEndpoint);
  unsetenv(gemstone::environment::RpcClient);
}

TEST_CASE("an established client never reconnects after losing its session") {
  std::unique_ptr<gemstone::execution::Client> client;
  {
    gemstone_test::RpcFixture fixture(GENERATE(false, true));
    const auto endpoint = fixture.endpoint();
    REQUIRE(setenv(gemstone::environment::RpcEndpoint, endpoint.c_str(), 1) == 0);
    REQUIRE(setenv(gemstone::environment::RpcClient, "42", 1) == 0);
    client = gemstone::execution::Client::fromEnvironment();
    REQUIRE(client);
    REQUIRE(client->call(gemstone::execution::Ping{}));
  }
  REQUIRE_FALSE(client->call(gemstone::execution::Ping{}));
  const auto lost = client->call(gemstone::execution::Ping{});
  REQUIRE_FALSE(lost);
  REQUIRE(lost.error().detail.find("session was lost") != std::string::npos);
  unsetenv(gemstone::environment::RpcEndpoint);
  unsetenv(gemstone::environment::RpcClient);
}
