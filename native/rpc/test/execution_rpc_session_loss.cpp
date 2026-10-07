#include <string>

#include <catch2/catch_test_macros.hpp>

#include "gemstone_execution_rpc.hpp"

using namespace gemstone::execution;

TEST_CASE("execution RPC reports session loss", "[rpc][integration]") {
  auto client = Client::fromEnvironment();
  REQUIRE((client));
  auto allocation = client->call(RegisterAllocation{{8}});
  REQUIRE((allocation));
  REQUIRE((!client->call(WriteAllocation{allocation.value(), 0, {42}})));
  auto release = client->call(ReleaseAllocation{allocation.value()});
  REQUIRE((!release));
  REQUIRE((release.error().detail.find("session was lost") != std::string::npos));
}
