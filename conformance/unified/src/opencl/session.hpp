#pragma once
#include <array>
#include <cstddef>
#include <cstdint>
#include <initializer_list>
#include <string>
#include <vector>

#include <CL/cl.h>
#include <aspartame/vector.hpp>
#include <catch2/catch_test_macros.hpp>

#include "conformance/device.hpp"

#include "test_support.hpp"
#include <fmt/format.h>

namespace unified::opencl {
using Shape = std::array<size_t, 3>;

inline void check(cl_int status, const char *operation) {
  INFO(fmt::format("{} returned {}", operation, status));
  REQUIRE(status == CL_SUCCESS);
}

template <class T> void arg(cl_kernel kernel, cl_uint index, const T &value, const char *name) {
  check(clSetKernelArg(kernel, index, sizeof(T), &value), name);
}

inline void args(cl_kernel kernel, std::initializer_list<cl_mem> values) {
  cl_uint index = 0;
  for (cl_mem value : values)
    arg(kernel, index++, value, "argument");
}

enum class Capability {
  NonUniformWorkGroups,
  WorkGroupCollectives,
  GenericAddressSpace,
  ProgramScopeGlobals,
  Fp64,
  Images,
  ReadWriteImages,
  ImageWrites3D,
  Image2DFromBuffer,
  DepthImages,
  Pipes
};

class Session {
  unified::test_runtime::Cleanup cleanup;

public:
  cl_device_id device = reinterpret_cast<cl_device_id>(static_cast<uintptr_t>(conformance::deviceSelection().nativeHandle));
  cl_context context{};
  cl_command_queue queue{};
  std::string standard;

  Session() {
    size_t size = 0;
    check(clGetDeviceInfo(device, CL_DEVICE_VERSION, 0, nullptr, &size), "device version size");
    std::string version(size, '\0');
    check(clGetDeviceInfo(device, CL_DEVICE_VERSION, size, version.data(), nullptr), "device version");
    if (version.rfind("OpenCL 1.", 0) == 0) SKIP("OpenCL C 2.0 features require an OpenCL 2.0 or later device");
    standard = version.rfind("OpenCL 2.", 0) == 0 ? "-cl-std=CL2.0" : "-cl-std=CL3.0";
    cl_int status = CL_SUCCESS;
    context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &status);
    check(status, "context");
    cleanup.add([context = context] { clReleaseContext(context); });
    queue = clCreateCommandQueueWithProperties(context, device, nullptr, &status);
    check(status, "queue");
    cleanup.add([queue = queue] { clReleaseCommandQueue(queue); });
  }

  [[nodiscard]] bool cl20() const { return standard == "-cl-std=CL2.0"; }

  // OpenCL 2.x mandates these features; OpenCL 3.0 reports each one.
  void require(Capability capability) const {
    const auto flag = [&](cl_device_info name) { return cl20() || deviceFlag(name); };
    switch (capability) {
      case Capability::NonUniformWorkGroups:
        if (!flag(CL_DEVICE_NON_UNIFORM_WORK_GROUP_SUPPORT)) SKIP("non-uniform work-groups unavailable");
        break;
      case Capability::WorkGroupCollectives:
        if (!flag(CL_DEVICE_WORK_GROUP_COLLECTIVE_FUNCTIONS_SUPPORT)) SKIP("work-group collective functions unavailable");
        break;
      case Capability::GenericAddressSpace:
        if (!flag(CL_DEVICE_GENERIC_ADDRESS_SPACE_SUPPORT)) SKIP("generic address space unavailable");
        break;
      case Capability::ProgramScopeGlobals: {
        size_t limit = cl20() ? 1 : 0;
        if (!limit) check(clGetDeviceInfo(device, CL_DEVICE_MAX_GLOBAL_VARIABLE_SIZE, sizeof(limit), &limit, nullptr), "global limit");
        if (limit == 0) SKIP("program-scope global variables unavailable");
        break;
      }
      case Capability::Images:
        if (!deviceFlag(CL_DEVICE_IMAGE_SUPPORT)) SKIP("images unavailable");
        break;
      case Capability::ReadWriteImages: {
        cl_uint arguments = cl20() ? 1 : 0;
        if (!arguments)
          check(clGetDeviceInfo(device, CL_DEVICE_MAX_READ_WRITE_IMAGE_ARGS, sizeof(arguments), &arguments, nullptr), "read-write images");
        if (arguments == 0) SKIP("read-write images unavailable");
        break;
      }
      case Capability::Pipes:
        if (!flag(CL_DEVICE_PIPE_SUPPORT)) SKIP("pipes unavailable");
        break;
      case Capability::Image2DFromBuffer:
        if (!extension("cl_khr_image2d_from_buffer")) SKIP("2D images from buffers unavailable");
        break;
      case Capability::DepthImages:
        if (!extension("cl_khr_depth_images")) SKIP("depth images unavailable");
        break;
      case Capability::ImageWrites3D:
        if (!cl20() && !feature("__opencl_c_3d_image_writes")) SKIP("3D image writes unavailable");
        break;
      case Capability::Fp64: {
        cl_device_fp_config config = 0;
        check(clGetDeviceInfo(device, CL_DEVICE_DOUBLE_FP_CONFIG, sizeof(config), &config, nullptr), "fp64 configuration");
        if (config == 0) SKIP("fp64 unavailable");
        break;
      }
    }
  }

  [[nodiscard]] bool deviceFlag(cl_device_info name) const {
    cl_bool value = CL_FALSE;
    check(clGetDeviceInfo(device, name, sizeof(value), &value, nullptr), "device flag");
    return value == CL_TRUE;
  }

  bool extension(const char *name) const {
    size_t size = 0;
    check(clGetDeviceInfo(device, CL_DEVICE_EXTENSIONS, 0, nullptr, &size), "extensions size");
    std::string extensions(size, '\0');
    check(clGetDeviceInfo(device, CL_DEVICE_EXTENSIONS, size, extensions.data(), nullptr), "extensions");
    return (" " + extensions + " ").find(std::string(" ") + name + " ") != std::string::npos;
  }

  bool feature(const char *name) const {
    size_t size = 0;
    check(clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_FEATURES, 0, nullptr, &size), "feature size");
    std::vector<cl_name_version> features(size / sizeof(cl_name_version));
    check(clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_FEATURES, size, features.data(), nullptr), "features");
    using namespace aspartame;
    return features ^ exists([&](const cl_name_version &entry) { return std::string(entry.name) == name; });
  }

  cl_mem image(cl_mem_flags flags, cl_image_format format, cl_image_desc desc, void *host = nullptr) {
    cl_int status = CL_SUCCESS;
    auto *memory = clCreateImage(context, flags, &format, &desc, host, &status);
    check(status, "image");
    cleanup.add([=] { clReleaseMemObject(memory); });
    return memory;
  }

  cl_sampler sampler(cl_bool normalised, cl_addressing_mode addressing, cl_filter_mode filter) {
    const cl_sampler_properties properties[] = {
        CL_SAMPLER_NORMALIZED_COORDS, normalised, CL_SAMPLER_ADDRESSING_MODE, addressing, CL_SAMPLER_FILTER_MODE, filter, 0};
    cl_int status = CL_SUCCESS;
    auto *value = clCreateSamplerWithProperties(context, properties, &status);
    check(status, "sampler");
    cleanup.add([=] { clReleaseSampler(value); });
    return value;
  }

  void readImage(cl_mem image, const size_t *origin, const size_t *region, void *host, const char *name) const {
    check(clEnqueueReadImage(queue, image, CL_TRUE, origin, region, 0, 0, host, 0, nullptr, nullptr), name);
  }

  void requireGroup(const Shape &local) const {
    size_t limit = 0;
    check(clGetDeviceInfo(device, CL_DEVICE_MAX_WORK_GROUP_SIZE, sizeof(limit), &limit, nullptr), "work-group limit");
    if (local[0] * local[1] * local[2] > limit) SKIP("work-group exceeds the device limit");
  }

  cl_kernel kernel(const char *source, const std::string &options, const char *name) {
    cl_int status = CL_SUCCESS;
    auto *program = clCreateProgramWithSource(context, 1, &source, nullptr, &status);
    check(status, "source program");
    cleanup.add([=] { clReleaseProgram(program); });
    const auto all = standard + " " + options;
    if (clBuildProgram(program, 1, &device, all.c_str(), nullptr, nullptr) != CL_SUCCESS) {
      size_t size = 0;
      clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, 0, nullptr, &size);
      std::string log(size, '\0');
      clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, size, log.data(), nullptr);
      FAIL("OpenCL build failed with " << all << ": " << log);
    }
    auto *kernel = clCreateKernel(program, name, &status);
    check(status, "kernel");
    cleanup.add([=] { clReleaseKernel(kernel); });
    return kernel;
  }

  template <class T> cl_mem buffer(const std::vector<T> &values) {
    cl_int status = CL_SUCCESS;
    auto memory = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, values.size() * sizeof(T),
                                 const_cast<T *>(values.data()), &status);
    check(status, "buffer");
    cleanup.add([=] { clReleaseMemObject(memory); });
    return memory;
  }

  template <class T> std::vector<T> read(cl_mem memory, size_t count) {
    std::vector<T> values(count);
    check(clEnqueueReadBuffer(queue, memory, CL_TRUE, 0, count * sizeof(T), values.data(), 0, nullptr, nullptr), "download");
    return values;
  }

  void run(cl_kernel kernel, cl_uint dimensions, const Shape &global, const Shape *local, const Shape &offset = {0, 0, 0}) const {
    check(clEnqueueNDRangeKernel(queue, kernel, dimensions, offset.data(), global.data(), local ? local->data() : nullptr, 0, nullptr,
                                 nullptr),
          "dispatch");
    check(clFinish(queue), "completion");
  }
};

} // namespace unified::opencl
