#include <cstdlib>
#include <memory>
#include <stdexcept>
#include <string>

#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators.hpp>

#include "gemstone/test_support.hpp"
#include "gemstone_execution_rpc.hpp"

#include "client.hpp"
#include "rpc_fixture.hpp"
#include <environment_keys.hpp>

TEST_CASE("client manager distinguishes disabled, misconfigured, and ready states") {
  const gemstone_test::Environment noEndpoint(gemstone::environment::RpcEndpoint, nullptr);
  const gemstone_test::Environment noClient(gemstone::environment::RpcClient, nullptr);
  gemstone::execution::ClientManager disabled;
  REQUIRE((disabled.mode() == gemstone::execution::ClientMode::Disabled));
  REQUIRE((disabled.client() == nullptr));

  {
    const gemstone_test::Environment endpoint(gemstone::environment::RpcEndpoint, "tcp://127.0.0.1:1");
    gemstone::execution::ClientManager partial;
    REQUIRE((partial.mode() == gemstone::execution::ClientMode::Misconfigured));
    REQUIRE((partial.client() == nullptr));
  }

  gemstone_test::RpcFixture const fixture(GENERATE(false, true));
  const gemstone_test::RpcEnvironment environment(fixture);
  gemstone::execution::ClientManager ready;
  REQUIRE((ready.mode() == gemstone::execution::ClientMode::Ready));
  REQUIRE((ready.client() != nullptr));
}

TEST_CASE("an established client never reconnects after losing its session") {
  std::unique_ptr<gemstone::execution::Client> client;
  {
    gemstone_test::RpcFixture const fixture(GENERATE(false, true));
    const gemstone_test::RpcEnvironment environment(fixture);
    client = gemstone::execution::Client::fromEnvironment();
    REQUIRE(client);
    REQUIRE(client->call(gemstone::execution::Ping{}));
  }
  REQUIRE_FALSE(client->call(gemstone::execution::Ping{}));
  const auto lost = client->call(gemstone::execution::Ping{});
  REQUIRE_FALSE(lost);
  REQUIRE(lost.error().detail.contains("session was lost"));
}

TEST_CASE("test environment restores absent and empty values after unwinding") {
  using gemstone_test::Environment;
  const auto *key = gemstone::environment::RpcEndpoint;
  const Environment absent(key, nullptr);
  {
    const Environment empty(key, "");
    const char *value = std::getenv(key);
    if (!value) FAIL("empty value is unset");
    REQUIRE(std::string(value).empty());
    CHECK_THROWS_AS(([&] {
                      const Environment configured(key, "tcp://127.0.0.1:1");
                      const char *configuredValue = std::getenv(key);
                      if (!configuredValue) FAIL("configured value is unset");
                      REQUIRE(std::string(configuredValue) == "tcp://127.0.0.1:1");
                      throw std::runtime_error("test scope unwinding");
                    }()),
                    std::runtime_error);
    const char *restored = std::getenv(key);
    if (!restored) FAIL("empty value is not restored");
    CHECK(std::string(restored).empty());
  }
  CHECK(std::getenv(key) == nullptr);
}
