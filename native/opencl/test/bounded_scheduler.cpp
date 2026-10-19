#include <chrono>
#include <cstdlib>
#include <thread>
#include <vector>

#include <CL/cl.h>
#include <catch2/catch_test_macros.hpp>

#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include "session.hpp"

TEST_CASE("OpenCL deferred work uses a bounded scheduler", "[opencl]") {
  gemstone_test::RpcFixture const server;
  const gemstone_test::RpcEnvironment environment(server);
  cl_device_id device = gemstone_opencl_test::gpu();
  cl_int error = CL_SUCCESS;
  cl_context context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));
  cl_event gate = clCreateUserEvent(context, &error);
  REQUIRE((gate && error == CL_SUCCESS));
  const size_t before = gemstone_test::processThreadCount();
  std::vector<cl_command_queue> queues;
  std::vector<cl_event> events;
  for (size_t index = 0; index < 64; ++index) {
    auto *queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
    REQUIRE((queue && error == CL_SUCCESS));
    cl_event event = nullptr;
    REQUIRE((clEnqueueMarkerWithWaitList(queue, 1, &gate, &event) == CL_SUCCESS));
    queues.push_back(queue);
    events.push_back(event);
  }
  std::this_thread::sleep_for(std::chrono::milliseconds(20));
  REQUIRE((gemstone_test::processThreadCount() <= before + 2));
  REQUIRE((clSetUserEventStatus(gate, CL_COMPLETE) == CL_SUCCESS));
  REQUIRE((clWaitForEvents(static_cast<cl_uint>(events.size()), events.data()) == CL_SUCCESS));
  for (auto *event : events)
    REQUIRE((clReleaseEvent(event) == CL_SUCCESS));
  for (auto *queue : queues)
    REQUIRE((clReleaseCommandQueue(queue) == CL_SUCCESS));
  REQUIRE((clReleaseEvent(gate) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}
