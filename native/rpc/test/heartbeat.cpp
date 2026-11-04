#include <chrono>

#include <catch2/catch_test_macros.hpp>

#include "gemstone_execution_rpc.hpp"
#include <gemstone/test_support.hpp>

#include "rpc_fixture.hpp"
#include <environment_keys.hpp>

using namespace gemstone::execution;
using namespace std::chrono_literals;

namespace {
struct Window {
  gemstone_test::Environment seconds{gemstone::environment::RpcTimeoutSeconds, "1"};
};

template <class Function> std::chrono::milliseconds timed(Function function) {
  const auto begin = std::chrono::steady_clock::now();
  function();
  return std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::steady_clock::now() - begin);
}
} // namespace

TEST_CASE("execution RPC keeps waiting across heartbeats past the liveness window", "[rpc][heartbeat]") {
  const Window window;
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  auto client = Client::fromEnvironment();
  REQUIRE((client));
  fixture.stallNext(operation::Ping, 2500ms, 2500ms, true);
  bool succeeded = false;
  const auto elapsed = timed([&] { succeeded = static_cast<bool>(client->call(Ping{})); });
  CHECK(succeeded);
  CHECK(elapsed >= 2400ms);
  CHECK(static_cast<bool>(client->call(Ping{})));
}

TEST_CASE("execution RPC fails promptly when the server is silent", "[rpc][heartbeat]") {
  const Window window;
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  auto client = Client::fromEnvironment();
  REQUIRE((client));
  fixture.stallNext(operation::Ping, 8000ms, 0ms, false);
  bool succeeded = true;
  const auto elapsed = timed([&] { succeeded = static_cast<bool>(client->call(Ping{})); });
  CHECK_FALSE(succeeded);
  CHECK(elapsed < 4000ms);
  CHECK(client->error().contains("transport failed"));
}

TEST_CASE("execution RPC fails once heartbeats stop", "[rpc][heartbeat]") {
  const Window window;
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  auto client = Client::fromEnvironment();
  REQUIRE((client));
  fixture.stallNext(operation::Ping, 8000ms, 1500ms, false);
  bool succeeded = true;
  const auto elapsed = timed([&] { succeeded = static_cast<bool>(client->call(Ping{})); });
  CHECK_FALSE(succeeded);
  CHECK(elapsed >= 2000ms);
  CHECK(elapsed < 5000ms);
}
