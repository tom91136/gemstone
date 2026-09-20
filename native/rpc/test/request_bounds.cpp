#include <cstdlib>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#include "gemstone_execution_rpc.hpp"

using namespace gemstone::execution;

template <class T> void checkEncodingLimit(const Result<T> &result) {
  REQUIRE((!result));
  REQUIRE((result.error().detail.find("encoding limits") != std::string::npos));
}

TEST_CASE("execution RPC enforces wire encoding bounds", "[rpc]") {
  REQUIRE((setenv("GEMSTONE_RPC_CLIENT", "42", 1) == 0));
  REQUIRE((unsetenv("GEMSTONE_RPC_PORT") == 0));
  auto client = Client::fromEnvironment();
  REQUIRE((client));

  checkEncodingLimit(client->call(DescribeKernel{{1}, std::string(MaximumTextBytes + 1, 'x')}));

  Launch launch{{1}, "kernel", {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, 0, {0, 0, 0}, 0, 0, {}, {}};
  launch.arguments.resize(MaximumElements + 1, KernelArgumentLocal{1});
  checkEncodingLimit(client->call(launch));

  std::vector<uint8_t> boundary(MaximumPayload);
  checkEncodingLimit(client->call(WriteAllocation{{1}, 0, std::move(boundary)}));
}
