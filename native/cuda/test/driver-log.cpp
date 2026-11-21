#include <algorithm>
#include <cstddef>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

extern "C" CUresult cuCtxCreate_v2(CUcontext *context, unsigned flags, CUdevice device);

namespace {

gemstone_test::RpcFixture fixture;
const std::string Endpoint = fixture.endpoint();
const gemstone_test::Environment RpcEndpoint(gemstone::environment::RpcEndpoint, Endpoint.c_str());
const gemstone_test::Environment RpcClient(gemstone::environment::RpcClientId, "42");

constexpr char FailedAllocation[] = "Returning 1 (CUDA_ERROR_INVALID_VALUE) from cuMemAlloc_v2";

struct Message {
  CUlogLevel level;
  std::string text;
};

void CUDA_CB collect(void *data, CUlogLevel level, char *message, size_t length) {
  static_cast<std::vector<Message> *>(data)->push_back({.level = level, .text = std::string(message, length)});
}

bool delivered(const std::vector<Message> &messages, const std::string &text) {
  return std::any_of(messages.begin(), messages.end(),
                     [&](const Message &message) { return message.level == CU_LOG_LEVEL_ERROR && message.text == text; });
}

void initialise() {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
}

CUresult failingAllocation() {
  CUdeviceptr device = 0;
  return cuMemAlloc_v2(&device, 0);
}

} // namespace

TEST_CASE("driver log rejects a null callback", "[cuda][driver-log]") {
  CUlogsCallbackHandle handle = nullptr;
  CHECK(cuLogsRegisterCallback(nullptr, nullptr, &handle) == CUDA_ERROR_INVALID_VALUE);
}

TEST_CASE("driver log delivers failures until unregistered", "[cuda][driver-log]") {
  initialise();
  std::vector<Message> messages;
  CUlogsCallbackHandle handle = nullptr;
  REQUIRE(cuLogsRegisterCallback(collect, &messages, &handle) == CUDA_SUCCESS);

  CHECK(failingAllocation() == CUDA_ERROR_INVALID_VALUE);
  CHECK(delivered(messages, FailedAllocation));

  REQUIRE(cuLogsUnregisterCallback(handle) == CUDA_SUCCESS);
  const auto count = messages.size();
  CHECK(failingAllocation() == CUDA_ERROR_INVALID_VALUE);
  CHECK(messages.size() == count);
}

TEST_CASE("driver log rejects unknown and repeated unregistration", "[cuda][driver-log]") {
  initialise();
  std::vector<Message> messages;
  CUlogsCallbackHandle handle = nullptr;
  REQUIRE(cuLogsRegisterCallback(collect, &messages, &handle) == CUDA_SUCCESS);
  REQUIRE(cuLogsUnregisterCallback(handle) == CUDA_SUCCESS);
  CHECK(cuLogsUnregisterCallback(handle) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuLogsUnregisterCallback(nullptr) == CUDA_ERROR_INVALID_VALUE);
}

TEST_CASE("driver log dumps every message without an iterator", "[cuda][driver-log]") {
  initialise();
  CHECK(failingAllocation() == CUDA_ERROR_INVALID_VALUE);

  std::vector<char> buffer(25600);
  size_t size = buffer.size();
  REQUIRE(cuLogsDumpToMemory(nullptr, buffer.data(), &size, 0) == CUDA_SUCCESS);
  CHECK(std::string(buffer.data(), size).contains(FailedAllocation));
}

TEST_CASE("driver log dumps messages after the current iterator once", "[cuda][driver-log]") {
  initialise();
  CUlogIterator iterator = 0;
  REQUIRE(cuLogsCurrent(&iterator, 0) == CUDA_SUCCESS);
  CHECK(failingAllocation() == CUDA_ERROR_INVALID_VALUE);

  std::vector<char> buffer(25600);
  size_t size = buffer.size();
  REQUIRE(cuLogsDumpToMemory(&iterator, buffer.data(), &size, 0) == CUDA_SUCCESS);
  CHECK(std::string(buffer.data(), size).contains(FailedAllocation));

  size = buffer.size();
  REQUIRE(cuLogsDumpToMemory(&iterator, buffer.data(), &size, 0) == CUDA_SUCCESS);
  CHECK(size == 0);
}
