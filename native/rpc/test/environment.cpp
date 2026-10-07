#include <catch2/catch_test_macros.hpp>

#include "gemstone_execution_rpc.hpp"
#include <gemstone/environment.hpp>
#include <gemstone/trace.hpp>

namespace {
// Restore process state even when an assertion throws.
class Override {
  const char *name;
  std::optional<std::string> previous;

public:
  explicit Override(const char *name) : name(name) {
    if (const auto *value = std::getenv(name)) previous = value;
    unsetenv(name);
  }
  ~Override() {
    if (previous) setenv(name, previous->c_str(), 1);
    else unsetenv(name);
  }
  void set(const char *value) { REQUIRE(setenv(name, value, 1) == 0); }
};
} // namespace

using namespace gemstone::environment;

TEST_CASE("environment integers reject overflow, signs and trailing text", "[rpc]") {
  for (const auto *raw : {"", "0", "00", "-1", "+1", " 1", "1 ", "1\n", "0x10", "1.5", "18446744073709551616"})
    CHECK_FALSE(positiveInteger(raw).has_value());
  CHECK(positiveInteger("00017") == 17);
  CHECK(positiveInteger("18446744073709551615") == std::numeric_limits<uint64_t>::max());
  Override limit(OpenclMaxMemAllocSize);
  CHECK(unsignedValue(OpenclMaxMemAllocSize, 7, 19) == 7);
  limit.set("19");
  CHECK(unsignedValue(OpenclMaxMemAllocSize, 7, 19) == 19);
  limit.set("20");
  CHECK(unsignedValue(OpenclMaxMemAllocSize, 7, 19) == 7);
}

TEST_CASE("paging honours false and malformed values instead of presence", "[rpc]") {
  Override paging(SystemPaging);
  CHECK_FALSE(enabled(SystemPaging));
  for (const auto *raw : {"1", "true", "TRUE", "On"}) {
    paging.set(raw);
    CHECK(enabled(SystemPaging));
  }
  for (const auto *raw : {"0", "false", "FALSE", "Off"}) {
    paging.set(raw);
    CHECK_FALSE(enabled(SystemPaging, true));
  }
  for (const auto *raw : {"", "2", "yes", " true", "false "}) {
    paging.set(raw);
    CHECK_FALSE(boolean(raw).has_value());
    CHECK_FALSE(enabled(SystemPaging));
    CHECK(enabled(SystemPaging, true));
  }
}

TEST_CASE("HSA paging inherits RPC timeout and independently validates overrides", "[rpc]") {
  Override rpc(RpcTimeoutSeconds);
  Override hsa(HsaPagingTimeoutSeconds);
  CHECK(rpcTimeoutSeconds() == RpcTimeoutSecondsDefault);
  CHECK(hsaPagingTimeoutSeconds() == rpcTimeoutSeconds());
  rpc.set("7");
  CHECK(rpcTimeoutSeconds() == 7);
  CHECK(hsaPagingTimeoutSeconds() == 7);
  hsa.set("13");
  CHECK(hsaPagingTimeoutSeconds() == 13);
  for (const auto *raw : {"0", "-1", "+1", "1 ", "2147484", "18446744073709551616"}) {
    hsa.set(raw);
    CHECK(hsaPagingTimeoutSeconds() == 7);
  }
  rpc.set("2147484"); // First second count that overflows a signed 32-bit millisecond timeout.
  CHECK(rpcTimeoutSeconds() == RpcTimeoutSecondsDefault);
}

TEST_CASE("trace scopes and categories match exactly", "[rpc]") {
  Override trace(Trace);
  CHECK_FALSE(gemstone::traceEnabled("rpc", "memory"));
  for (const auto *raw : {"all", "rpc", "rpc.memory", "cuda, \trpc.memory\t ,perf"}) {
    trace.set(raw);
    CHECK(gemstone::traceEnabled("rpc", "memory"));
  }
  for (const auto *raw : {"", "rpc.memory.extra", "rpc.other", "rp", "RPC", "\nrpc", "rpc\r"}) {
    trace.set(raw);
    CHECK_FALSE(gemstone::traceEnabled("rpc", "memory"));
  }
  trace.set("rpc.memory");
  CHECK_FALSE(gemstone::traceEnabled("rpc"));
}

TEST_CASE("RPC client environment rejects invalid signed identities", "[rpc]") {
  Override client(RpcClient);
  for (const auto *raw : {"", "0", "-1", "+1", " 1", "1 ", "9223372036854775808", "18446744073709551616"}) {
    client.set(raw);
    CHECK_FALSE(gemstone::execution::Client::fromEnvironment());
  }
}
