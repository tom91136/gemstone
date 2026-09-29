#include <cstdlib>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#include "gemstone_execution_rpc.hpp"

#include <environment_keys.hpp>

using namespace gemstone::execution;

template <class T> void checkEncodingLimit(const Result<T> &result) {
  REQUIRE((!result));
  REQUIRE((result.error().detail.find("encoding limits") != std::string::npos));
}

TEST_CASE("execution RPC enforces wire encoding bounds", "[rpc]") {
  REQUIRE((setenv(gemstone::environment::RpcClient, "42", 1) == 0));
  REQUIRE((unsetenv(gemstone::environment::RpcEndpoint) == 0));
  auto client = Client::fromEnvironment();
  REQUIRE((client));

  std::vector<uint8_t> boundary(MaximumPayload);
  checkEncodingLimit(client->call(WriteAllocation{{1}, 0, std::move(boundary)}));
}
