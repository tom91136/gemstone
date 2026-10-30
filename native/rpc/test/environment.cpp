#include <cstdint>
#include <limits>
#include <optional>
#include <utility>

#include <catch2/catch_test_macros.hpp>

#include "gemstone_execution_rpc.hpp"
#include <gemstone/environment.hpp>
#include <gemstone/test_support.hpp>
#include <gemstone/trace.hpp>

#include "environment_keys.hpp"

namespace {
class Override {
  const char *name;
  std::optional<gemstone_test::Environment> value;

public:
  explicit Override(const char *name) : name(name), value(std::in_place, name, nullptr) {}
  void set(const char *raw) { value.emplace(name, raw); }
};
} // namespace

using namespace gemstone::environment;

TEST_CASE("environment integers reject overflow, signs and trailing text", "[rpc]") {
  for (const auto *raw : {"", "0", "00", "-1", "+1", " 1", "1 ", "1\n", "0x10", "1.5", "18446744073709551616"})
    CHECK_FALSE(positiveInteger(raw).has_value());
  CHECK(positiveInteger("00017") == 17);
  CHECK(positiveInteger("18446744073709551615") == std::numeric_limits<uint64_t>::max());
  Override limit(RpcTimeoutSeconds);
  CHECK(unsignedValue(RpcTimeoutSeconds, 7, 19) == 7);
  limit.set("19");
  CHECK(unsignedValue(RpcTimeoutSeconds, 7, 19) == 19);
  limit.set("20");
  CHECK(unsignedValue(RpcTimeoutSeconds, 7, 19) == 7);
}

TEST_CASE("booleans take CMake truth values and reject anything else", "[rpc]") {
  Override paging(SystemPagingOn);
  CHECK_FALSE(enabled(SystemPagingOn));
  CHECK_FALSE(invalid());
  for (const auto *raw : {"1", "true", "TRUE", "On", "y", "Yes"}) {
    paging.set(raw);
    CHECK(enabled(SystemPagingOn));
    CHECK_FALSE(invalid());
  }
  for (const auto *raw : {"0", "false", "FALSE", "Off", "n", "NO"}) {
    paging.set(raw);
    CHECK_FALSE(enabled(SystemPagingOn));
    CHECK_FALSE(invalid());
  }
  paging.set("");
  CHECK_FALSE(present(SystemPagingOn));
  CHECK_FALSE(invalid());
  for (const auto *raw : {"2", "enabled", " true", "false "}) {
    paging.set(raw);
    CHECK_FALSE(boolean(raw).has_value());
    CHECK(invalid().value_or("").starts_with(SystemPagingOn));
  }
}

TEST_CASE("enumerated settings reject unknown choices", "[rpc]") {
  Override mode(CudaCodeMode);
  mode.set("ptx");
  CHECK_FALSE(invalid());
  mode.set("cubin");
  CHECK(invalid() == "GEMSTONE_CUDA_CODE_MODE must be one of ptx sass, not 'cubin'");
}

TEST_CASE("HSA paging inherits RPC timeout and independently validates overrides", "[rpc]") {
  Override rpc(RpcTimeoutSeconds);
  Override hsa(HsaPagingTimeoutSeconds);
  CHECK(std::cmp_equal(rpcTimeoutSeconds(), RpcTimeoutSecondsDefault));
  CHECK(hsaPagingTimeoutSeconds() == rpcTimeoutSeconds());
  rpc.set("7");
  CHECK(rpcTimeoutSeconds() == 7);
  CHECK(hsaPagingTimeoutSeconds() == 7);
  hsa.set("13");
  CHECK(hsaPagingTimeoutSeconds() == 13);
  CHECK_FALSE(invalid());
  for (const auto *raw : {"0", "-1", "+1", "1 ", "2147484", "18446744073709551616"}) {
    hsa.set(raw);
    CHECK(invalid().value_or("").starts_with(HsaPagingTimeoutSeconds));
  }
  hsa.set(nullptr);
  rpc.set("2147484"); // First second count that overflows a signed 32-bit millisecond timeout.
  CHECK(invalid().value_or("").starts_with(RpcTimeoutSeconds));
}

TEST_CASE("trace scopes and categories match exactly", "[rpc]") {
  Override trace(TraceList);
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
  Override client(RpcClientId);
  for (const auto *raw : {"", "0", "-1", "+1", " 1", "1 ", "9223372036854775808", "18446744073709551616"}) {
    client.set(raw);
    CHECK_FALSE(gemstone::execution::Client::fromEnvironment());
  }
}
