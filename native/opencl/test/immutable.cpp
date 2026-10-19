#include <array>
#include <cstddef>
#include <cstdint>
#include <string>

#include <CL/cl.h>
#include <CL/cl_ext.h>
#include <catch2/catch_test_macros.hpp>

#include "session.hpp"

namespace {
struct Session : gemstone_opencl_test::Session {
  cl_int create(cl_mem_flags flags, void *host) const {
    cl_int error = CL_SUCCESS;
    auto *buffer = clCreateBuffer(context, flags, 16, host, &error);
    if (buffer) clReleaseMemObject(buffer);
    return error;
  }
};
} // namespace

TEST_CASE("OpenCL immutable buffers keep their initial contents and reject host writes", "[opencl][immutable]") {
  Session const session;
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS, 0, nullptr, &size) == CL_SUCCESS));
  std::string extensions(size, '\0');
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS, size, extensions.data(), nullptr) == CL_SUCCESS));
  CHECK((" " + extensions + " ").contains(" cl_ext_immutable_memory_objects "));

  std::array<int32_t, 4> initial{1, 2, 3, 4};
  CHECK(session.create(CL_MEM_IMMUTABLE_EXT, nullptr) == CL_INVALID_VALUE);
  CHECK(session.create(CL_MEM_IMMUTABLE_EXT | CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, initial.data()) == CL_INVALID_VALUE);
  CHECK(session.create(CL_MEM_IMMUTABLE_EXT | CL_MEM_HOST_WRITE_ONLY | CL_MEM_COPY_HOST_PTR, initial.data()) == CL_INVALID_VALUE);
  CHECK(session.create(CL_MEM_IMMUTABLE_EXT | CL_MEM_COPY_HOST_PTR | CL_MEM_ALLOC_HOST_PTR, initial.data()) == CL_SUCCESS);

  cl_int error = CL_SUCCESS;
  auto *buffer = clCreateBuffer(session.context, CL_MEM_IMMUTABLE_EXT | CL_MEM_COPY_HOST_PTR, sizeof(initial), initial.data(), &error);
  REQUIRE((buffer && error == CL_SUCCESS));
  auto *other = clCreateBuffer(session.context, CL_MEM_READ_WRITE, sizeof(initial), nullptr, &error);
  REQUIRE((other && error == CL_SUCCESS));
  cl_mem_flags flags = 0;
  REQUIRE((clGetMemObjectInfo(buffer, CL_MEM_FLAGS, sizeof(flags), &flags, nullptr) == CL_SUCCESS));
  CHECK((flags & CL_MEM_IMMUTABLE_EXT) != 0);

  const int32_t value = 9;
  const size_t origin[] = {0, 0, 0};
  const size_t region[] = {4, 1, 1};
  CHECK(clEnqueueWriteBuffer(session.queue, buffer, CL_TRUE, 0, 4, &value, 0, nullptr, nullptr) == CL_INVALID_OPERATION);
  CHECK(clEnqueueWriteBufferRect(session.queue, buffer, CL_TRUE, origin, origin, region, 0, 0, 0, 0, &value, 0, nullptr, nullptr)
        == CL_INVALID_OPERATION);
  CHECK(clEnqueueFillBuffer(session.queue, buffer, &value, sizeof(value), 0, 4, 0, nullptr, nullptr) == CL_INVALID_OPERATION);
  CHECK(clEnqueueCopyBuffer(session.queue, other, buffer, 0, 0, 4, 0, nullptr, nullptr) == CL_INVALID_OPERATION);
  for (const cl_map_flags map : {CL_MAP_WRITE, CL_MAP_WRITE_INVALIDATE_REGION}) {
    CHECK(clEnqueueMapBuffer(session.queue, buffer, CL_TRUE, map, 0, 4, 0, nullptr, nullptr, &error) == nullptr);
    CHECK(error == CL_INVALID_OPERATION);
  }

  std::array<int32_t, 4> read{};
  REQUIRE((clEnqueueReadBuffer(session.queue, buffer, CL_TRUE, 0, sizeof(read), read.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  CHECK(read == initial);
  REQUIRE((clEnqueueCopyBuffer(session.queue, buffer, other, 0, 0, sizeof(read), 0, nullptr, nullptr) == CL_SUCCESS));
  auto *mapped =
      static_cast<int32_t *>(clEnqueueMapBuffer(session.queue, buffer, CL_TRUE, CL_MAP_READ, 0, sizeof(read), 0, nullptr, nullptr, &error));
  REQUIRE((mapped && error == CL_SUCCESS));
  CHECK(mapped[2] == 3);
  REQUIRE((clEnqueueUnmapMemObject(session.queue, buffer, mapped, 0, nullptr, nullptr) == CL_SUCCESS));
  clReleaseMemObject(other);
  clReleaseMemObject(buffer);
}

TEST_CASE("OpenCL immutable images reject host writes and writable kernel arguments", "[opencl][immutable]") {
  Session session;
  std::array<uint8_t, 16> texels{};
  cl_image_desc desc{};
  desc.image_type = CL_MEM_OBJECT_IMAGE2D;
  desc.image_width = 2;
  desc.image_height = 2;
  const cl_image_format format{.image_channel_order = CL_RGBA, .image_channel_data_type = CL_UNORM_INT8};
  cl_int error = CL_SUCCESS;
  auto *image = clCreateImage(session.context, CL_MEM_IMMUTABLE_EXT | CL_MEM_USE_HOST_PTR, &format, &desc, texels.data(), &error);
  REQUIRE((image && error == CL_SUCCESS));
  const size_t origin[] = {0, 0, 0};
  const size_t region[] = {1, 1, 1};
  const float colour[4] = {1, 1, 1, 1};
  CHECK(clEnqueueWriteImage(session.queue, image, CL_TRUE, origin, region, 0, 0, texels.data(), 0, nullptr, nullptr)
        == CL_INVALID_OPERATION);
  CHECK(clEnqueueFillImage(session.queue, image, colour, origin, region, 0, nullptr, nullptr) == CL_INVALID_OPERATION);
  size_t rowPitch = 0;
  CHECK(clEnqueueMapImage(session.queue, image, CL_TRUE, CL_MAP_WRITE, origin, region, &rowPitch, nullptr, 0, nullptr, nullptr, &error)
        == nullptr);
  CHECK(error == CL_INVALID_OPERATION);

  int32_t values[4] = {};
  auto *buffer = clCreateBuffer(session.context, CL_MEM_IMMUTABLE_EXT | CL_MEM_USE_HOST_PTR, sizeof(values), values, &error);
  REQUIRE((buffer && error == CL_SUCCESS));
  session.server.programCatalogue({{.name = "k", .linkageName = "k"}}, {});
  session.server.kernelMetadata(40, 5,
                                {{.width = 8, .offset = 0, .kind = 2, .containsPointers = 0},
                                 {.width = 8, .offset = 8, .kind = 2, .containsPointers = 0},
                                 {.width = 8, .offset = 16, .kind = 2, .containsPointers = 0},
                                 {.width = 8, .offset = 24, .kind = 2, .containsPointers = 0},
                                 {.width = 8, .offset = 32, .kind = 2, .containsPointers = 0}});
  const char *source = R"CLC(
    kernel void k(global const int *in, constant int *table, global int *out, read_only image2d_t src, write_only image2d_t dst) {
      out[0] = in[0] + table[0] + (int)read_imagef(src, (int2)(0, 0)).x;
      write_imagef(dst, (int2)(0, 0), (float4)(0));
    })CLC";
  auto *program = clCreateProgramWithSource(session.context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(program, 1, &session.device, "-cl-std=CL3.0", nullptr, nullptr) == CL_SUCCESS));
  auto *kernel = clCreateKernel(program, "k", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  CHECK(clSetKernelArg(kernel, 0, sizeof(buffer), &buffer) == CL_SUCCESS);
  CHECK(clSetKernelArg(kernel, 1, sizeof(buffer), &buffer) == CL_SUCCESS);
  CHECK(clSetKernelArg(kernel, 2, sizeof(buffer), &buffer) == CL_INVALID_ARG_VALUE);
  CHECK(clSetKernelArg(kernel, 3, sizeof(image), &image) == CL_SUCCESS);
  CHECK(clSetKernelArg(kernel, 4, sizeof(image), &image) == CL_INVALID_ARG_VALUE);
  clReleaseKernel(kernel);
  clReleaseProgram(program);
  clReleaseMemObject(buffer);
  clReleaseMemObject(image);
}
