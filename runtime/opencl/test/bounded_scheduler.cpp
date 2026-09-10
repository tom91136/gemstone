#include <cassert>
#include <chrono>
#include <cstdlib>
#include <cstring>
#include <string>
#include <thread>
#include <vector>

#include <dirent.h>

#include "../../../native/execution/test/rpc_fixture.hpp"
#include <CL/cl.h>

namespace {
size_t thread_count() {
  DIR *directory = opendir("/proc/self/task");
  assert(directory != nullptr);
  size_t count = 0;
  while (const auto *entry = readdir(directory))
    if (entry->d_name[0] != '.') ++count;
  closedir(directory);
  return count;
}
} // namespace

int main() {
  gemstone_test::RpcFixture server;
  const auto port = std::to_string(server.port());
  assert(setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0);
  assert(setenv("GEMSTONE_RPC_TOKEN", gemstone_test::RpcFixture::token, 1) == 0);
  cl_platform_id platform = nullptr;
  cl_device_id device = nullptr;
  assert(clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS);
  assert(clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS);
  cl_int error = CL_SUCCESS;
  cl_context context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  assert(context != nullptr && error == CL_SUCCESS);
  cl_event gate = clCreateUserEvent(context, &error);
  assert(gate != nullptr && error == CL_SUCCESS);
  const size_t before = thread_count();
  std::vector<cl_command_queue> queues;
  std::vector<cl_event> events;
  for (size_t index = 0; index < 64; ++index) {
    auto queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
    assert(queue != nullptr && error == CL_SUCCESS);
    cl_event event = nullptr;
    assert(clEnqueueMarkerWithWaitList(queue, 1, &gate, &event) == CL_SUCCESS);
    queues.push_back(queue);
    events.push_back(event);
  }
  std::this_thread::sleep_for(std::chrono::milliseconds(20));
  assert(thread_count() <= before + 2);
  assert(clSetUserEventStatus(gate, CL_COMPLETE) == CL_SUCCESS);
  assert(clWaitForEvents(static_cast<cl_uint>(events.size()), events.data()) == CL_SUCCESS);
  for (auto event : events) assert(clReleaseEvent(event) == CL_SUCCESS);
  for (auto queue : queues) assert(clReleaseCommandQueue(queue) == CL_SUCCESS);
  assert(clReleaseEvent(gate) == CL_SUCCESS);
  assert(clReleaseContext(context) == CL_SUCCESS);
}
