#include <array>
#include <cstddef>
#include <cstdint>
#include <string>
#include <string_view>
#include <utility>
#include <variant>
#include <vector>

#include <CL/cl.h>
#include <aspartame/vector.hpp>
#include <catch2/catch_test_macros.hpp>

#include "gemstone_pipe_layout.hpp"

#include "rpc/gemstone_execution_rpc.hpp"
#include "session.hpp"

using namespace aspartame;

using gemstone_opencl_test::gpu;
using gemstone_opencl_test::hasFeature;
using gemstone_opencl_test::info;

namespace {
struct Session : gemstone_opencl_test::Session {
  cl_int create(cl_mem_flags flags, cl_uint packetSize, cl_uint packets, const cl_pipe_properties *properties = nullptr) const {
    cl_int error = CL_SUCCESS;
    auto *pipe = clCreatePipe(context, flags, packetSize, packets, properties, &error);
    if (pipe) clReleaseMemObject(pipe);
    return error;
  }
};
} // namespace

TEST_CASE("OpenCL advertises pipes with the required limits", "[opencl][pipes]") {
  auto *const device = gpu();
  CHECK(info<cl_bool>(device, CL_DEVICE_PIPE_SUPPORT) == CL_TRUE);
  CHECK(info<cl_uint>(device, CL_DEVICE_MAX_PIPE_ARGS) >= 16);
  CHECK(info<cl_uint>(device, CL_DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS) >= 1);
  CHECK(info<cl_uint>(device, CL_DEVICE_PIPE_MAX_PACKET_SIZE) >= 1024);
  CHECK(hasFeature(device, "__opencl_c_pipes"));
}

TEST_CASE("OpenCL pipes report their shape and reject invalid descriptions and host access", "[opencl][pipes]") {
  Session const session;
  cl_int error = CL_SUCCESS;
  auto *pipe = clCreatePipe(session.context, 0, 12, 5, nullptr, &error);
  REQUIRE((pipe && error == CL_SUCCESS));
  cl_uint packetSize = 0;
  cl_uint packets = 0;
  REQUIRE((clGetPipeInfo(pipe, CL_PIPE_PACKET_SIZE, sizeof(packetSize), &packetSize, nullptr) == CL_SUCCESS));
  REQUIRE((clGetPipeInfo(pipe, CL_PIPE_MAX_PACKETS, sizeof(packets), &packets, nullptr) == CL_SUCCESS));
  CHECK(packetSize == 12);
  CHECK(packets == 5);
  size_t size = 1;
  REQUIRE((clGetPipeInfo(pipe, CL_PIPE_PROPERTIES, 0, nullptr, &size) == CL_SUCCESS));
  CHECK(size == 0);
  cl_mem_object_type type = 0;
  cl_mem_flags flags = 0;
  REQUIRE((clGetMemObjectInfo(pipe, CL_MEM_TYPE, sizeof(type), &type, nullptr) == CL_SUCCESS));
  REQUIRE((clGetMemObjectInfo(pipe, CL_MEM_FLAGS, sizeof(flags), &flags, nullptr) == CL_SUCCESS));
  CHECK(type == CL_MEM_OBJECT_PIPE);
  CHECK(flags == (CL_MEM_READ_WRITE | CL_MEM_HOST_NO_ACCESS));
  std::array<uint8_t, 4> bytes{};
  CHECK(clEnqueueReadBuffer(session.queue, pipe, CL_TRUE, 0, bytes.size(), bytes.data(), 0, nullptr, nullptr) == CL_INVALID_MEM_OBJECT);

  const auto maximum = info<cl_uint>(session.device, CL_DEVICE_PIPE_MAX_PACKET_SIZE);
  CHECK(session.create(0, 0, 4) == CL_INVALID_PIPE_SIZE);
  CHECK(session.create(0, maximum + 1, 4) == CL_INVALID_PIPE_SIZE);
  CHECK(session.create(0, 4, 0) == CL_INVALID_PIPE_SIZE);
  CHECK(session.create(CL_MEM_USE_HOST_PTR, 4, 4) == CL_INVALID_VALUE);
  CHECK(session.create(CL_MEM_HOST_READ_ONLY, 4, 4) == CL_INVALID_VALUE);
  const cl_pipe_properties properties[] = {0x1234, 0, 0};
  CHECK(session.create(0, 4, 4, properties) == CL_INVALID_VALUE);
  clReleaseMemObject(pipe);
}

TEST_CASE("OpenCL pipe kernel arguments pass the bounded pipe object", "[opencl][pipes]") {
  Session session;
  session.server.opaqueLaunches();
  session.server.programCatalogue({{.name = "groups", .linkageName = "groups"}}, {});
  session.server.kernelMetadata(
      16, 2, {{.width = 8, .offset = 0, .kind = 1, .containsPointers = 0}, {.width = 8, .offset = 8, .kind = 1, .containsPointers = 0}});
  cl_int error = CL_SUCCESS;
  auto *program =
      clCreateProgramWithIL(session.context, gemstone_test::RpcFixture::Spirv, sizeof(gemstone_test::RpcFixture::Spirv), &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(program, 1, &session.device, "", nullptr, nullptr) == CL_SUCCESS));
  auto *kernel = clCreateKernel(program, "groups", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  auto *writer = clCreatePipe(session.context, 0, 8, 6, nullptr, &error);
  REQUIRE((writer && error == CL_SUCCESS));
  auto *reader = clCreatePipe(session.context, 0, 4, 3, nullptr, &error);
  REQUIRE((reader && error == CL_SUCCESS));
  REQUIRE((clSetKernelArg(kernel, 0, sizeof(writer), &writer) == CL_SUCCESS));
  REQUIRE((clSetKernelArg(kernel, 1, sizeof(reader), &reader) == CL_SUCCESS));
  const size_t global = 1;
  REQUIRE((clEnqueueNDRangeKernel(session.queue, kernel, 1, nullptr, &global, nullptr, 0, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clFinish(session.queue) == CL_SUCCESS));
  const auto launch = session.server.lastLaunch();
  REQUIRE(launch.arguments.size() == 2);
  const auto *first = std::get_if<gemstone::execution::ArgumentMemory>(launch.arguments.data());
  const auto *second = std::get_if<gemstone::execution::ArgumentMemory>(&launch.arguments[1]);
  REQUIRE(first);
  REQUIRE(second);
  namespace layout = gemstone::spirv::PipeLayout;
  CHECK((first->range && std::cmp_equal(first->range->value, layout::Packets + (8 * 6))));
  CHECK((second->range && std::cmp_equal(second->range->value, layout::Packets + (4 * 3))));
  clReleaseMemObject(reader);
  clReleaseMemObject(writer);
  clReleaseKernel(kernel);
  clReleaseProgram(program);
}

TEST_CASE("OpenCL C compiles pipe kernels", "[opencl][pipes]") {
  Session const session;
  const char *source = R"CLC(
    #ifndef __opencl_c_pipes
    #error pipes are not enabled
    #endif
    kernel void producer(write_only pipe int out, global const int *src) {
      int value = src[get_global_id(0)];
      write_pipe(out, &value);
      reserve_id_t reserved = work_group_reserve_write_pipe(out, get_local_size(0));
      if (is_valid_reserve_id(reserved)) {
        write_pipe(out, reserved, get_local_id(0), &value);
        work_group_commit_write_pipe(out, reserved);
      }
    }
    kernel void consumer(read_only pipe int in, global int *dst) {
      int value = -1;
      dst[get_global_id(0)] = read_pipe(in, &value) == 0 ? value : (int)get_pipe_num_packets(in);
    })CLC";
  cl_int error = CL_SUCCESS;
  auto *program = clCreateProgramWithSource(session.context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  const auto build = clBuildProgram(program, 1, &session.device, "-cl-std=CL3.0", nullptr, nullptr);
  size_t size = 0;
  clGetProgramBuildInfo(program, session.device, CL_PROGRAM_BUILD_LOG, 0, nullptr, &size);
  std::string log(size, '\0');
  clGetProgramBuildInfo(program, session.device, CL_PROGRAM_BUILD_LOG, size, log.data(), nullptr);
  INFO(log);
  CHECK(build == CL_SUCCESS);
  clReleaseProgram(program);
}

TEST_CASE("OpenCL pipe arguments report the pipe type qualifier", "[opencl][pipes]") {
  Session session;
  session.server.programCatalogue({{.name = "k", .linkageName = "k"}}, {});
  session.server.kernelMetadata(
      16, 2, {{.width = 8, .offset = 0, .kind = 1, .containsPointers = 0}, {.width = 8, .offset = 8, .kind = 2, .containsPointers = 0}});
  const char *source = "__kernel void k(read_only pipe int in, __global int *out) { read_pipe(in, out); }";
  cl_int error = CL_SUCCESS;
  auto *program = clCreateProgramWithSource(session.context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(program, 1, &session.device, "-cl-std=CL3.0 -cl-kernel-arg-info", nullptr, nullptr) == CL_SUCCESS));
  auto *kernel = clCreateKernel(program, "k", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  cl_kernel_arg_type_qualifier qualifier = 0;
  REQUIRE((clGetKernelArgInfo(kernel, 0, CL_KERNEL_ARG_TYPE_QUALIFIER, sizeof(qualifier), &qualifier, nullptr) == CL_SUCCESS));
  CHECK(qualifier == CL_KERNEL_ARG_TYPE_PIPE);
  clReleaseKernel(kernel);
  clReleaseProgram(program);
}
