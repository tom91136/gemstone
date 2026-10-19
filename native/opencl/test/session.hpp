#pragma once

#include <string_view>
#include <vector>

#include <CL/cl.h>
#include <aspartame/vector.hpp>
#include <catch2/catch_test_macros.hpp>

#include "../../rpc/test/rpc_fixture.hpp"

namespace gemstone_opencl_test {

inline cl_device_id gpu() {
  cl_platform_id platform = nullptr;
  cl_device_id device = nullptr;
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS));
  return device;
}

template <class T> T info(cl_device_id device, cl_device_info name) {
  T value{};
  REQUIRE((clGetDeviceInfo(device, name, sizeof(value), &value, nullptr) == CL_SUCCESS));
  return value;
}

inline bool hasFeature(cl_device_id device, std::string_view feature) {
  using namespace aspartame;
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_FEATURES, 0, nullptr, &size) == CL_SUCCESS));
  std::vector<cl_name_version> features(size / sizeof(cl_name_version));
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_FEATURES, size, features.data(), nullptr) == CL_SUCCESS));
  return features ^ exists([&](const cl_name_version &entry) { return entry.name == feature; });
}

struct Session {
  gemstone_test::RpcFixture server;
  gemstone_test::RpcEnvironment environment{server};
  cl_device_id device = gpu();
  cl_context context = nullptr;
  cl_command_queue queue = nullptr;
  Session() {
    cl_int error = CL_SUCCESS;
    context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
    REQUIRE((context && error == CL_SUCCESS));
    queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
    REQUIRE((queue && error == CL_SUCCESS));
  }
  ~Session() {
    clFinish(queue);
    clReleaseCommandQueue(queue);
    clReleaseContext(context);
  }
};

} // namespace gemstone_opencl_test
