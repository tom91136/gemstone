#include <array>
#include <cstddef>
#include <cstdint>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <CL/cl.h>
#include <CL/cl_ext.h>
#include <aspartame/vector.hpp>
#include <catch2/catch_test_macros.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include "session.hpp"

using namespace aspartame;

using gemstone_opencl_test::gpu;
using gemstone_opencl_test::hasFeature;
using gemstone_opencl_test::info;

namespace {
struct Session {
  gemstone_test::RpcFixture server;
  gemstone_test::RpcEnvironment environment{server};
  cl_device_id device = gpu();
  cl_context context = nullptr;
  cl_command_queue queue = nullptr;
  uint32_t *memory = nullptr;
  Session() {
    server.opaqueLaunches();
    cl_int error = CL_SUCCESS;
    context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
    REQUIRE((context && error == CL_SUCCESS));
    queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
    REQUIRE((queue && error == CL_SUCCESS));
    memory = static_cast<uint32_t *>(clSVMAlloc(context, CL_MEM_READ_WRITE, 4096, 16));
    REQUIRE((memory));
  }
  ~Session() {
    clFinish(queue);
    clSVMFree(context, memory);
    clReleaseCommandQueue(queue);
    clReleaseContext(context);
  }
  cl_int build(cl_program program, const char *options) const {
    const auto status = clBuildProgram(program, 1, &device, options, nullptr, nullptr);
    size_t size = 0;
    clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, 0, nullptr, &size);
    std::string log(size, '\0');
    clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, size, log.data(), nullptr);
    UNSCOPED_INFO("build log for " << (options ? options : "") << ": " << log);
    return status;
  }
  cl_program source(const char *text, const char *options) const {
    cl_int error = CL_SUCCESS;
    auto *program = clCreateProgramWithSource(context, 1, &text, nullptr, &error);
    REQUIRE((program && error == CL_SUCCESS));
    REQUIRE((build(program, options) == CL_SUCCESS));
    return program;
  }
  cl_program il(const char *options) {
    server.programCatalogue({{.name = "groups", .linkageName = "groups"}}, {});
    cl_int error = CL_SUCCESS;
    auto *program = clCreateProgramWithIL(context, gemstone_test::RpcFixture::Spirv, sizeof(gemstone_test::RpcFixture::Spirv), &error);
    REQUIRE((program && error == CL_SUCCESS));
    REQUIRE((build(program, options) == CL_SUCCESS));
    return program;
  }
  cl_kernel kernel(cl_program program, const char *name) const {
    cl_int error = CL_SUCCESS;
    auto *kernel = clCreateKernel(program, name, &error);
    REQUIRE((kernel && error == CL_SUCCESS));
    const uint32_t seed = 0;
    REQUIRE((clSetKernelArgSVMPointer(kernel, 0, memory) == CL_SUCCESS));
    REQUIRE((clSetKernelArg(kernel, 1, sizeof(seed), &seed) == CL_SUCCESS));
    return kernel;
  }
  cl_int enqueue(cl_kernel kernel, cl_uint dimensions, const size_t *offset, const size_t *global, const size_t *local) const {
    const auto status = clEnqueueNDRangeKernel(queue, kernel, dimensions, offset, global, local, 0, nullptr, nullptr);
    REQUIRE((clFinish(queue) == CL_SUCCESS));
    return status;
  }
};

constexpr const char *StoreSource =
    "__kernel void store(__global uint *out, uint seed) { out[get_global_id(1) * get_global_size(0) + get_global_id(0)] = seed; }";

void requireGeometry(const auto &launch, std::array<int64_t, 3> size, std::array<int64_t, 3> count, std::array<int64_t, 3> global) {
  CHECK((launch.groupSize.x.value == size[0]));
  CHECK((launch.groupSize.y.value == size[1]));
  CHECK((launch.groupSize.z.value == size[2]));
  CHECK((launch.groupCount.x.value == count[0]));
  CHECK((launch.groupCount.y.value == count[1]));
  CHECK((launch.groupCount.z.value == count[2]));
  CHECK((launch.globalSize.x.value == global[0]));
  CHECK((launch.globalSize.y.value == global[1]));
  CHECK((launch.globalSize.z.value == global[2]));
}
} // namespace

TEST_CASE("OpenCL advertises non-uniform work-groups", "[opencl][capability]") {
  CHECK((info<cl_bool>(gpu(), CL_DEVICE_NON_UNIFORM_WORK_GROUP_SUPPORT) == CL_TRUE));
}

TEST_CASE("OpenCL advertises work-group collective functions", "[opencl][capability]") {
  auto *const device = gpu();
  CHECK((info<cl_bool>(device, CL_DEVICE_WORK_GROUP_COLLECTIVE_FUNCTIONS_SUPPORT) == CL_TRUE));
  CHECK(hasFeature(device, "__opencl_c_work_group_collective_functions"));
}

TEST_CASE("OpenCL advertises the generic address space", "[opencl][capability]") {
  auto *const device = gpu();
  CHECK((info<cl_bool>(device, CL_DEVICE_GENERIC_ADDRESS_SPACE_SUPPORT) == CL_TRUE));
  CHECK(hasFeature(device, "__opencl_c_generic_address_space"));
}

TEST_CASE("OpenCL advertises program-scope global variables", "[opencl][capability]") {
  auto *const device = gpu();
  size_t limit = 0;
  size_t preferred = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_MAX_GLOBAL_VARIABLE_SIZE, sizeof(limit), &limit, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_GLOBAL_VARIABLE_PREFERRED_TOTAL_SIZE, sizeof(preferred), &preferred, nullptr) == CL_SUCCESS));
  CHECK(limit >= 65536);
  CHECK(preferred >= limit);
  CHECK(hasFeature(device, "__opencl_c_program_scope_global_variables"));
}

TEST_CASE("OpenCL reports no passed conformance version", "[opencl][capability]") {
  std::array<char, 32> version{};
  REQUIRE((clGetDeviceInfo(gpu(), CL_DEVICE_LATEST_CONFORMANCE_VERSION_PASSED, version.size(), version.data(), nullptr) == CL_SUCCESS));
  CHECK((std::string(version.data()) == "v0000-01-01-00"));
}

TEST_CASE("OpenCL IL programs launch partial work-groups with the exact global size", "[opencl][capability]") {
  Session session;
  auto *program = session.il("");
  auto *kernel = session.kernel(program, "groups");
  const size_t offset[] = {3, 5, 0};
  const size_t global[] = {100, 7, 3};
  const size_t local[] = {16, 2, 2};
  REQUIRE(session.enqueue(kernel, 3, offset, global, local) == CL_SUCCESS);
  const auto launch = session.server.lastLaunch();
  requireGeometry(launch, {16, 2, 2}, {7, 4, 2}, {100, 7, 3});
  CHECK((launch.workDim.value == 3));
  CHECK((launch.globalOffset.x.value == 3));
  CHECK((launch.globalOffset.y.value == 5));
  clReleaseKernel(kernel);
  clReleaseProgram(program);
}

TEST_CASE("OpenCL uniform work-group builds reject partial work-groups", "[opencl][capability]") {
  Session session;
  auto *program = session.il("-cl-uniform-work-group-size");
  auto *kernel = session.kernel(program, "groups");
  const size_t global = 100;
  const size_t local = 32;
  const size_t divisible = 96;
  CHECK(session.enqueue(kernel, 1, nullptr, &global, &local) == CL_INVALID_WORK_GROUP_SIZE);
  CHECK(session.enqueue(kernel, 1, nullptr, &divisible, &local) == CL_SUCCESS);
  CHECK(session.enqueue(kernel, 1, nullptr, &global, nullptr) == CL_SUCCESS);
  const auto launch = session.server.lastLaunch();
  CHECK((global % static_cast<size_t>(launch.groupSize.x.value) == 0));
  CHECK((launch.globalSize.x.value == static_cast<int64_t>(launch.groupSize.x.value) * launch.groupCount.x.value));
  clReleaseKernel(kernel);
  clReleaseProgram(program);
}

TEST_CASE("OpenCL C 1.x programs keep work-groups uniform", "[opencl][capability]") {
  Session session;
  session.server.programCatalogue({{.name = "store", .linkageName = "store"}}, {});
  for (const char *options : {"", "-cl-std=CL1.2"}) {
    auto *program = session.source(StoreSource, options);
    auto *kernel = session.kernel(program, "store");
    const size_t global[] = {100, 9};
    const size_t local[] = {16, 4};
    CHECK(session.enqueue(kernel, 2, nullptr, global, local) == CL_INVALID_WORK_GROUP_SIZE);
    REQUIRE(session.enqueue(kernel, 2, nullptr, global, nullptr) == CL_SUCCESS);
    const auto launch = session.server.lastLaunch();
    CHECK((launch.globalSize.x.value == static_cast<int64_t>(launch.groupSize.x.value) * launch.groupCount.x.value));
    CHECK((launch.globalSize.y.value == static_cast<int64_t>(launch.groupSize.y.value) * launch.groupCount.y.value));
    CHECK((launch.globalSize.x.value == 100));
    CHECK((launch.globalSize.y.value == 9));
    clReleaseKernel(kernel);
    clReleaseProgram(program);
  }
}

TEST_CASE("OpenCL C 3.0 programs launch partial work-groups", "[opencl][capability]") {
  Session session;
  session.server.programCatalogue({{.name = "store", .linkageName = "store"}}, {});
  auto *program = session.source(StoreSource, "-cl-std=CL3.0");
  auto *kernel = session.kernel(program, "store");
  const size_t global[] = {100, 9};
  const size_t local[] = {16, 4};
  REQUIRE(session.enqueue(kernel, 2, nullptr, global, local) == CL_SUCCESS);
  requireGeometry(session.server.lastLaunch(), {16, 4, 1}, {7, 3, 1}, {100, 9, 1});
  clReleaseKernel(kernel);
  clReleaseProgram(program);
}

TEST_CASE("OpenCL C compiles work-group collective functions", "[opencl][capability]") {
  Session const session;
  const char *source = R"CLC(
    #ifndef __opencl_c_work_group_collective_functions
    #error work-group collective functions are not enabled
    #endif
    __kernel void collectives(__global long *out, uint seed) {
      size_t i = get_global_linear_id();
      int value = (int)(i ^ seed);
      out[i] = work_group_reduce_add(value) + work_group_scan_inclusive_min(value) + work_group_scan_exclusive_max((ulong)value)
               + work_group_broadcast(value, 0) + work_group_all(value > 0) + work_group_any(value == 3)
               + (long)work_group_reduce_max((float)value);
    })CLC";
  cl_int error = CL_SUCCESS;
  auto *program = clCreateProgramWithSource(session.context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  CHECK((session.build(program, "-cl-std=CL3.0") == CL_SUCCESS));
  clReleaseProgram(program);
}

TEST_CASE("OpenCL C compiles generic address space pointers", "[opencl][capability]") {
  Session const session;
  const char *source = R"CLC(
    #ifndef __opencl_c_generic_address_space
    #error the generic address space is not enabled
    #endif
    int bump(int *value) { return ++*value; }
    __kernel void pointers(__global int *out, uint seed) {
      __local int shared[1];
      int own = (int)seed;
      shared[0] = 0;
      int *pointers[] = {out, shared, &own};
      int total = 0;
      for (int i = 0; i < 3; ++i)
        total += bump(pointers[i]) + (to_global(pointers[i]) != 0) * 10 + (to_local(pointers[i]) != 0) * 100
                 + (to_private(pointers[i]) != 0) * 1000 + (int)get_fence(pointers[i]);
      out[1] = total;
    })CLC";
  cl_int error = CL_SUCCESS;
  auto *program = clCreateProgramWithSource(session.context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  CHECK((session.build(program, "-cl-std=CL3.0") == CL_SUCCESS));
  clReleaseProgram(program);
}

TEST_CASE("OpenCL builds bind every program-scope global for the program's lifetime", "[opencl][capability]") {
  Session session;
  session.server.programCatalogue({{.name = "groups", .linkageName = "groups"}},
                                  {{.name = "counter", .virtualAddress = 0, .size = 4, .pointerOffsets = {}},
                                   {.name = "table", .virtualAddress = 0, .size = 16, .pointerOffsets = {}}});
  session.server.expectGlobalBinding("counter", 0, {5, 0, 0, 0});
  session.server.expectGlobalBinding("table", 0, {1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4, 0, 0, 0});
  const auto before = session.server.allocationCount();
  cl_int error = CL_SUCCESS;
  auto *program =
      clCreateProgramWithIL(session.context, gemstone_test::RpcFixture::Spirv, sizeof(gemstone_test::RpcFixture::Spirv), &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE(session.build(program, "") == CL_SUCCESS);
  session.server.checkGlobalBindings();
  CHECK(session.server.allocationCount() == before + 2);
  size_t total = 0;
  REQUIRE((clGetProgramBuildInfo(program, session.device, CL_PROGRAM_BUILD_GLOBAL_VARIABLE_TOTAL_SIZE, sizeof(total), &total, nullptr)
           == CL_SUCCESS));
  CHECK(total == 20);
  REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
  CHECK(session.server.allocationCount() == before);
}

TEST_CASE("OpenCL builds reject program-scope globals above the device limit", "[opencl][capability]") {
  Session session;
  size_t limit = 0;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_MAX_GLOBAL_VARIABLE_SIZE, sizeof(limit), &limit, nullptr) == CL_SUCCESS));
  session.server.programCatalogue({{.name = "groups", .linkageName = "groups"}},
                                  {{.name = "huge", .virtualAddress = 0, .size = limit + 1, .pointerOffsets = {}}});
  cl_int error = CL_SUCCESS;
  auto *program =
      clCreateProgramWithIL(session.context, gemstone_test::RpcFixture::Spirv, sizeof(gemstone_test::RpcFixture::Spirv), &error);
  REQUIRE((program && error == CL_SUCCESS));
  CHECK(session.build(program, "") == CL_BUILD_PROGRAM_FAILURE);
  cl_build_status status = CL_BUILD_NONE;
  REQUIRE((clGetProgramBuildInfo(program, session.device, CL_PROGRAM_BUILD_STATUS, sizeof(status), &status, nullptr) == CL_SUCCESS));
  CHECK(status == CL_BUILD_ERROR);
  clReleaseProgram(program);
}

TEST_CASE("OpenCL C compiles program-scope global variables", "[opencl][capability]") {
  Session const session;
  const char *source = R"CLC(
    #ifndef __opencl_c_program_scope_global_variables
    #error program-scope global variables are not enabled
    #endif
    global int counter = 5;
    global int *pointer = &counter;
    kernel void step(global int *out, uint seed) {
      static global int calls = 0;
      out[0] = *pointer + calls++ + (int)seed;
    })CLC";
  cl_int error = CL_SUCCESS;
  auto *program = clCreateProgramWithSource(session.context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  CHECK(session.build(program, "-cl-std=CL3.0") == CL_SUCCESS);
  clReleaseProgram(program);
}

TEST_CASE("OpenCL device and host timers share the profiling clock", "[opencl][capability]") {
  Session const session;
  cl_platform_id platform = nullptr;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_PLATFORM, sizeof(platform), &platform, nullptr) == CL_SUCCESS));
  cl_ulong resolution = 0;
  REQUIRE((clGetPlatformInfo(platform, CL_PLATFORM_HOST_TIMER_RESOLUTION, sizeof(resolution), &resolution, nullptr) == CL_SUCCESS));
  CHECK(resolution > 0);
  cl_ulong device = 0;
  cl_ulong host = 0;
  cl_ulong before = 0;
  cl_ulong after = 0;
  CHECK(clGetDeviceAndHostTimer(session.device, &device, nullptr) == CL_INVALID_VALUE);
  CHECK(clGetHostTimer(session.device, nullptr) == CL_INVALID_VALUE);
  REQUIRE(clGetHostTimer(session.device, &before) == CL_SUCCESS);
  REQUIRE(clGetDeviceAndHostTimer(session.device, &device, &host) == CL_SUCCESS);
  const cl_queue_properties properties[] = {CL_QUEUE_PROPERTIES, CL_QUEUE_PROFILING_ENABLE, 0};
  cl_int error = CL_SUCCESS;
  auto *queue = clCreateCommandQueueWithProperties(session.context, session.device, properties, &error);
  REQUIRE((queue && error == CL_SUCCESS));
  cl_event marker = nullptr;
  REQUIRE((clEnqueueMarkerWithWaitList(queue, 0, nullptr, &marker) == CL_SUCCESS));
  REQUIRE((clWaitForEvents(1, &marker) == CL_SUCCESS));
  cl_ulong ended = 0;
  cl_ulong later = 0;
  REQUIRE((clGetEventProfilingInfo(marker, CL_PROFILING_COMMAND_END, sizeof(ended), &ended, nullptr) == CL_SUCCESS));
  REQUIRE(clGetDeviceAndHostTimer(session.device, &later, &after) == CL_SUCCESS);
  CHECK(before <= host);
  CHECK(host <= after);
  CHECK(device <= ended);
  CHECK(ended <= later);
  clReleaseEvent(marker);
  clReleaseCommandQueue(queue);
}

TEST_CASE("OpenCL libraries stay unloaded until linked into an executable", "[opencl][capability]") {
  Session session;
  session.server.programCatalogue({{.name = "step", .linkageName = "step"}}, {});
  const char *librarySource = "int helper(int *value) { return *value + 1; }";
  const char *kernelSource = "extern int helper(int *value);\n"
                             "kernel void step(global int *out, uint seed) { out[0] = helper(out) + (int)seed; }";
  cl_int error = CL_SUCCESS;
  auto *library = clCreateProgramWithSource(session.context, 1, &librarySource, nullptr, &error);
  REQUIRE((library && error == CL_SUCCESS));
  REQUIRE((clCompileProgram(library, 1, &session.device, "-cl-std=CL3.0", 0, nullptr, nullptr, nullptr, nullptr) == CL_SUCCESS));
  auto *object = clCreateProgramWithSource(session.context, 1, &kernelSource, nullptr, &error);
  REQUIRE((object && error == CL_SUCCESS));
  REQUIRE((clCompileProgram(object, 1, &session.device, "-cl-std=CL3.0", 0, nullptr, nullptr, nullptr, nullptr) == CL_SUCCESS));
  const auto loads = session.server.programLoads.load();
  auto *linkedLibrary = clLinkProgram(session.context, 1, &session.device, "-create-library", 1, &library, nullptr, nullptr, &error);
  REQUIRE(error == CL_SUCCESS);
  CHECK(session.server.programLoads.load() == loads);
  cl_program_binary_type type = CL_PROGRAM_BINARY_TYPE_NONE;
  REQUIRE((clGetProgramBuildInfo(linkedLibrary, session.device, CL_PROGRAM_BINARY_TYPE, sizeof(type), &type, nullptr) == CL_SUCCESS));
  CHECK(type == CL_PROGRAM_BINARY_TYPE_LIBRARY);
  const cl_program inputs[] = {object, linkedLibrary};
  auto *executable = clLinkProgram(session.context, 1, &session.device, "", 2, inputs, nullptr, nullptr, &error);
  REQUIRE(error == CL_SUCCESS);
  CHECK(session.server.programLoads.load() == loads + 1);
  auto *kernel = clCreateKernel(executable, "step", &error);
  CHECK((kernel && error == CL_SUCCESS));
  clReleaseKernel(kernel);
  clReleaseProgram(executable);
  clReleaseProgram(linkedLibrary);
  clReleaseProgram(object);
  clReleaseProgram(library);
}

TEST_CASE("OpenCL reports OpenCL C 2.0 together with every OpenCL C 2.0 feature", "[opencl][capability]") {
  auto *const device = gpu();
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_VERSION, 0, nullptr, &size) == CL_SUCCESS));
  std::string version(size, '\0');
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_VERSION, size, version.data(), nullptr) == CL_SUCCESS));
  CHECK(version.rfind("OpenCL C 2.0 ", 0) == 0);
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_ALL_VERSIONS, 0, nullptr, &size) == CL_SUCCESS));
  std::vector<cl_name_version> versions(size / sizeof(cl_name_version));
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_ALL_VERSIONS, size, versions.data(), nullptr) == CL_SUCCESS));
  bool listed = false;
  for (const auto &entry : versions)
    listed |= entry.version == CL_MAKE_VERSION(2, 0, 0) && std::string_view(entry.name) == "OpenCL C";
  CHECK(listed);
  for (const auto *feature : {"__opencl_c_int64", "__opencl_c_generic_address_space", "__opencl_c_program_scope_global_variables",
                              "__opencl_c_images", "__opencl_c_read_write_images", "__opencl_c_3d_image_writes", "__opencl_c_pipes",
                              "__opencl_c_device_enqueue", "__opencl_c_work_group_collective_functions", "__opencl_c_atomic_order_acq_rel",
                              "__opencl_c_atomic_order_seq_cst", "__opencl_c_atomic_scope_device", "__opencl_c_atomic_scope_all_devices"}) {
    INFO(feature);
    CHECK(hasFeature(device, feature));
  }
}

TEST_CASE("OpenCL advertises cl_khr_integer_dot_product for packed and unpacked 8-bit inputs", "[opencl][capability]") {
  Session session;
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS_WITH_VERSION, 0, nullptr, &size) == CL_SUCCESS));
  std::vector<cl_name_version> extensions(size / sizeof(cl_name_version));
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS_WITH_VERSION, size, extensions.data(), nullptr) == CL_SUCCESS));
  bool listed = false;
  for (const auto &entry : extensions)
    listed |= std::string_view(entry.name) == "cl_khr_integer_dot_product" && entry.version == CL_MAKE_VERSION(2, 0, 0);
  CHECK(listed);
  cl_device_integer_dot_product_capabilities_khr capabilities = 0;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_INTEGER_DOT_PRODUCT_CAPABILITIES_KHR, sizeof(capabilities), &capabilities, nullptr)
           == CL_SUCCESS));
  CHECK((capabilities & CL_DEVICE_INTEGER_DOT_PRODUCT_INPUT_4x8BIT_KHR) != 0);
  CHECK((capabilities & CL_DEVICE_INTEGER_DOT_PRODUCT_INPUT_4x8BIT_PACKED_KHR) != 0);
  for (const auto name : {CL_DEVICE_INTEGER_DOT_PRODUCT_ACCELERATION_PROPERTIES_8BIT_KHR,
                          CL_DEVICE_INTEGER_DOT_PRODUCT_ACCELERATION_PROPERTIES_4x8BIT_PACKED_KHR}) {
    cl_device_integer_dot_product_acceleration_properties_khr properties{};
    REQUIRE((clGetDeviceInfo(session.device, name, 0, nullptr, &size) == CL_SUCCESS));
    CHECK(size == sizeof(properties));
    CHECK(clGetDeviceInfo(session.device, name, sizeof(properties), &properties, nullptr) == CL_SUCCESS);
  }
  CHECK(hasFeature(session.device, "__opencl_c_integer_dot_product_input_4x8bit"));
  CHECK(hasFeature(session.device, "__opencl_c_integer_dot_product_input_4x8bit_packed"));
  session.server.programCatalogue({{.name = "dots", .linkageName = "dots"}}, {});
  auto *program = session.source(R"CLC(
    #if !defined(__opencl_c_integer_dot_product_input_4x8bit) || !defined(__opencl_c_integer_dot_product_input_4x8bit_packed)
    #error integer dot products are not enabled
    #endif
    kernel void dots(global const char4 *a, global const uint *p, global int *out) {
      out[0] = dot(a[0], a[1]);
      out[1] = dot_acc_sat_4x8packed_ss_int(p[0], p[1], out[2]);
    })CLC",
                                 "-cl-std=CL3.0");
  clReleaseProgram(program);
}

TEST_CASE("OpenCL advertises the cl_khr_subgroups extension family and compiles its built-ins", "[opencl][capability]") {
  Session session;
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS, 0, nullptr, &size) == CL_SUCCESS));
  std::string extensions(size, '\0');
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS, size, extensions.data(), nullptr) == CL_SUCCESS));
  for (const auto *extension : {"cl_khr_subgroups", "cl_khr_subgroup_ballot", "cl_khr_subgroup_non_uniform_vote",
                                "cl_khr_subgroup_non_uniform_arithmetic", "cl_khr_subgroup_shuffle", "cl_khr_subgroup_shuffle_relative",
                                "cl_khr_subgroup_extended_types", "cl_khr_subgroup_clustered_reduce", "cl_khr_subgroup_rotate"}) {
    INFO(extension);
    CHECK((" " + extensions + " ").contains(std::string(" ") + extension + " "));
  }
  cl_bool progress = CL_FALSE;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_SUB_GROUP_INDEPENDENT_FORWARD_PROGRESS, sizeof(progress), &progress, nullptr)
           == CL_SUCCESS));
  CHECK(progress == CL_TRUE);
  session.server.programCatalogue({{.name = "k", .linkageName = "k"}}, {});
  auto *program = session.source(R"CLC(
    #if !defined(cl_khr_subgroup_ballot) || !defined(cl_khr_subgroup_rotate) || !defined(cl_khr_subgroup_clustered_reduce)
    #error subgroup extensions are not enabled
    #endif
    kernel void k(global uint *out, global int *ints, global float *fs, global char *cs, global double *ds) {
      uint id = get_sub_group_local_id();
      uint4 b = sub_group_ballot(id & 1);
      out[0] = sub_group_ballot_bit_count(b) + sub_group_ballot_inclusive_scan(b) + sub_group_ballot_exclusive_scan(b)
             + sub_group_ballot_find_lsb(b) + sub_group_ballot_find_msb(b) + sub_group_inverse_ballot(b) + sub_group_ballot_bit_extract(b, 3);
      out[1] = sub_group_non_uniform_broadcast(id, 2) + sub_group_broadcast_first(id);
      out[2] = sub_group_elect() + sub_group_non_uniform_all(id > 0) + sub_group_non_uniform_any(id > 3) + sub_group_non_uniform_all_equal(id);
      ints[0] = sub_group_non_uniform_reduce_add(ints[id]) + sub_group_non_uniform_scan_inclusive_mul(ints[id]) + sub_group_non_uniform_scan_exclusive_max(ints[id])
              + sub_group_non_uniform_reduce_and(ints[id]) + sub_group_non_uniform_reduce_logical_xor(ints[id] > 0);
      fs[0] = sub_group_non_uniform_reduce_min(fs[id]) + sub_group_clustered_reduce_add(fs[id], 4) + sub_group_clustered_reduce_mul(fs[id], 2);
      ints[1] = sub_group_clustered_reduce_and(ints[id], 8) + sub_group_clustered_reduce_logical_or(ints[id] > 1, 4);
      ints[2] = sub_group_shuffle(ints[id], 3) + sub_group_shuffle_xor(ints[id], 1) + sub_group_shuffle_up(ints[id], 1) + sub_group_shuffle_down(ints[id], 2);
      cs[0] = sub_group_broadcast(cs[id], 0) + sub_group_reduce_add(cs[id]) + sub_group_scan_inclusive_max(cs[id]);
      ds[0] = sub_group_reduce_add(ds[id]);
      ints[3] = sub_group_rotate(ints[id], 1) + sub_group_clustered_rotate(ints[id], 1, 4);
    }
    )CLC",
                                 "-cl-std=CL3.0");
  clReleaseProgram(program);
}

TEST_CASE("OpenCL advertises cl_khr_extended_bit_ops and compiles its built-ins", "[opencl][capability]") {
  Session session;
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS, 0, nullptr, &size) == CL_SUCCESS));
  std::string extensions(size, '\0');
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS, size, extensions.data(), nullptr) == CL_SUCCESS));
  CHECK((" " + extensions + " ").contains(" cl_khr_extended_bit_ops "));
  session.server.programCatalogue({{.name = "bits", .linkageName = "bits"}}, {});
  auto *program = session.source(R"CLC(
    kernel void bits(global uint *u, global int *i, global uchar4 *v) {
      u[0] = bitfield_insert(u[0], u[1], 3, 5);
      i[0] = bitfield_extract_signed(i[1], 2, 6);
      u[2] = bitfield_extract_unsigned(i[2], 1, 4);
      v[0] = bit_reverse(v[1]);
    })CLC",
                                 "-cl-std=CL3.0");
  clReleaseProgram(program);
}

TEST_CASE("OpenCL advertises the SPIR-V hint extensions the interpreter accepts", "[opencl][capability]") {
  auto *const device = gpu();
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_EXTENSIONS, 0, nullptr, &size) == CL_SUCCESS));
  std::string extensions(size, '\0');
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_EXTENSIONS, size, extensions.data(), nullptr) == CL_SUCCESS));
  for (const auto *extension : {"cl_khr_expect_assume", "cl_khr_spirv_no_integer_wrap_decoration", "cl_khr_spirv_linkonce_odr"}) {
    INFO(extension);
    CHECK((" " + extensions + " ").contains(std::string(" ") + extension + " "));
  }
}

TEST_CASE("OpenCL suggests the local size an implicit launch uses", "[opencl][capability]") {
  Session session;
  session.server.programCatalogue({{.name = "store", .linkageName = "store"}}, {});
  cl_platform_id platform = nullptr;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_PLATFORM, sizeof(platform), &platform, nullptr) == CL_SUCCESS));
  auto suggest = reinterpret_cast<clGetKernelSuggestedLocalWorkSizeKHR_fn>(
      clGetExtensionFunctionAddressForPlatform(platform, "clGetKernelSuggestedLocalWorkSizeKHR"));
  REQUIRE(suggest);
  for (const char *options : {"", "-cl-std=CL3.0"}) {
    INFO("options " << options);
    auto *program = session.source(StoreSource, options);
    auto *kernel = session.kernel(program, "store");
    const size_t global[] = {100, 9};
    const size_t offset[] = {3, 1};
    size_t suggested[3] = {0, 0, 0};
    REQUIRE((suggest(session.queue, kernel, 2, offset, global, suggested) == CL_SUCCESS));
    REQUIRE(session.enqueue(kernel, 2, offset, global, nullptr) == CL_SUCCESS);
    const auto launch = session.server.lastLaunch();
    CHECK((std::cmp_equal(suggested[0], launch.groupSize.x.value)));
    CHECK((std::cmp_equal(suggested[1], launch.groupSize.y.value)));
    CHECK(suggest(session.queue, kernel, 0, nullptr, global, suggested) == CL_INVALID_WORK_DIMENSION);
    CHECK(suggest(session.queue, kernel, 2, nullptr, nullptr, suggested) == CL_INVALID_GLOBAL_WORK_SIZE);
    CHECK(suggest(session.queue, kernel, 2, nullptr, global, nullptr) == CL_INVALID_VALUE);
    clReleaseKernel(kernel);
    clReleaseProgram(program);
  }
}

TEST_CASE("OpenCL creates command queues through cl_khr_create_command_queue", "[opencl][capability]") {
  Session const session;
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS, 0, nullptr, &size) == CL_SUCCESS));
  std::string extensions(size, '\0');
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS, size, extensions.data(), nullptr) == CL_SUCCESS));
  CHECK((" " + extensions + " ").contains(" cl_khr_create_command_queue "));
  CHECK((" " + extensions + " ").contains(" cl_khr_suggested_local_work_size "));
  cl_platform_id platform = nullptr;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_PLATFORM, sizeof(platform), &platform, nullptr) == CL_SUCCESS));
  auto create = reinterpret_cast<clCreateCommandQueueWithPropertiesKHR_fn>(
      clGetExtensionFunctionAddressForPlatform(platform, "clCreateCommandQueueWithPropertiesKHR"));
  REQUIRE(create);
  const cl_queue_properties_khr properties[] = {CL_QUEUE_PROPERTIES, CL_QUEUE_PROFILING_ENABLE, 0};
  cl_int error = CL_SUCCESS;
  auto *queue = create(session.context, session.device, properties, &error);
  REQUIRE((queue && error == CL_SUCCESS));
  cl_command_queue_properties reported = 0;
  REQUIRE((clGetCommandQueueInfo(queue, CL_QUEUE_PROPERTIES, sizeof(reported), &reported, nullptr) == CL_SUCCESS));
  CHECK(reported == CL_QUEUE_PROFILING_ENABLE);
  clReleaseCommandQueue(queue);
}

TEST_CASE("OpenCL reports the device and driver UUIDs that Vulkan reports for the same device", "[opencl][capability]") {
  auto *const device = gpu();
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_EXTENSIONS, 0, nullptr, &size) == CL_SUCCESS));
  std::string extensions(size, '\0');
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_EXTENSIONS, size, extensions.data(), nullptr) == CL_SUCCESS));
  CHECK((" " + extensions + " ").contains(" cl_khr_device_uuid "));
  std::array<cl_uchar, CL_UUID_SIZE_KHR> deviceUuid{};
  std::array<cl_uchar, CL_UUID_SIZE_KHR> driverUuid{};
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_UUID_KHR, deviceUuid.size(), deviceUuid.data(), nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceInfo(device, CL_DRIVER_UUID_KHR, driverUuid.size(), driverUuid.data(), nullptr) == CL_SUCCESS));
  for (size_t index = 0; index < CL_UUID_SIZE_KHR; ++index) {
    CHECK(deviceUuid[index] == 0x47 + index);
    CHECK(driverUuid[index] == 0x67 + index);
  }
  cl_bool luidValid = CL_TRUE;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_LUID_VALID_KHR, sizeof(luidValid), &luidValid, nullptr) == CL_SUCCESS));
  CHECK(luidValid == CL_FALSE);
  std::array<cl_uchar, CL_LUID_SIZE_KHR> luid{};
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_LUID_KHR, luid.size(), luid.data(), nullptr) == CL_SUCCESS));
  cl_uint nodeMask = 1;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_NODE_MASK_KHR, sizeof(nodeMask), &nodeMask, nullptr) == CL_SUCCESS));
  CHECK(nodeMask == 0);
}

TEST_CASE("OpenCL C compiles Intel subgroup block reads and writes", "[opencl][capability]") {
  Session session;
  session.server.programCatalogue({{.name = "blocks", .linkageName = "blocks"}}, {});
  auto *program = session.source(R"CLC(
    kernel void blocks(global uint *in, global uint *out, read_only image2d_t source, write_only image2d_t target) {
      uint4 values = intel_sub_group_block_read4(in);
      uint texel = intel_sub_group_block_read(source, (int2)(0, 0));
      intel_sub_group_block_write(out, values.x + texel);
      intel_sub_group_block_write(target, (int2)(0, 1), values.y);
    })CLC",
                                 "-cl-std=CL3.0");
  clReleaseProgram(program);
}

TEST_CASE("OpenCL reports the SPIR-V instruction sets, extensions and capabilities it executes", "[opencl][capability]") {
  auto *const device = gpu();
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_EXTENSIONS, 0, nullptr, &size) == CL_SUCCESS));
  std::string extensions(size, '\0');
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_EXTENSIONS, size, extensions.data(), nullptr) == CL_SUCCESS));
  CHECK((" " + extensions + " ").contains(" cl_khr_spirv_queries "));
  const auto strings = [&](cl_device_info name) {
    REQUIRE((clGetDeviceInfo(device, name, 0, nullptr, &size) == CL_SUCCESS));
    std::vector<const char *> values(size / sizeof(const char *));
    REQUIRE((clGetDeviceInfo(device, name, size, values.data(), nullptr) == CL_SUCCESS));
    return std::vector<std::string>(values.begin(), values.end());
  };
  CHECK(strings(CL_DEVICE_SPIRV_EXTENDED_INSTRUCTION_SETS_KHR) == std::vector<std::string>{"OpenCL.std"});
  const auto spirv = strings(CL_DEVICE_SPIRV_EXTENSIONS_KHR);
  for (const auto *extension :
       {"SPV_KHR_expect_assume", "SPV_KHR_bit_instructions", "SPV_KHR_integer_dot_product", "SPV_KHR_no_integer_wrap_decoration",
        "SPV_KHR_subgroup_rotate", "SPV_INTEL_subgroups", "SPV_KHR_linkonce_odr", "SPV_KHR_shader_clock", "SPV_INTEL_bfloat16_conversion",
        "SPV_INTEL_split_barrier", "SPV_INTEL_media_block_io", "SPV_INTEL_subgroup_matrix_multiply_accumulate"}) {
    INFO(extension);
    CHECK((spirv ^ contains(std::string(extension))));
  }
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_SPIRV_CAPABILITIES_KHR, 0, nullptr, &size) == CL_SUCCESS));
  std::vector<cl_uint> capabilities(size / sizeof(cl_uint));
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_SPIRV_CAPABILITIES_KHR, size, capabilities.data(), nullptr) == CL_SUCCESS));
  // Addresses, Kernel, ImageReadWrite, GenericPointer, Pipes, DeviceEnqueue, SubgroupDispatch, GroupNonUniformRotateKHR,
  // SubgroupBufferBlockIOINTEL, ShaderClockKHR and SubgroupMatrixMultiplyAccumulateINTEL.
  for (const cl_uint capability : {4U, 6U, 14U, 38U, 17U, 19U, 58U, 6026U, 5569U, 5055U, 6236U}) {
    INFO("capability " << capability);
    CHECK((capabilities ^ contains(capability)));
  }
}

TEST_CASE("OpenCL reports cl_khr_kernel_clock at every scope and its OpenCL C features", "[opencl][capability]") {
  auto *const device = gpu();
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_EXTENSIONS, 0, nullptr, &size) == CL_SUCCESS));
  std::string extensions(size, '\0');
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_EXTENSIONS, size, extensions.data(), nullptr) == CL_SUCCESS));
  CHECK((" " + extensions + " ").contains(" cl_khr_kernel_clock "));
  cl_device_kernel_clock_capabilities_khr clocks = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_KERNEL_CLOCK_CAPABILITIES_KHR, sizeof(clocks), &clocks, nullptr) == CL_SUCCESS));
  CHECK(clocks
        == (CL_DEVICE_KERNEL_CLOCK_SCOPE_DEVICE_KHR | CL_DEVICE_KERNEL_CLOCK_SCOPE_WORK_GROUP_KHR
            | CL_DEVICE_KERNEL_CLOCK_SCOPE_SUB_GROUP_KHR));
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_FEATURES, 0, nullptr, &size) == CL_SUCCESS));
  std::vector<cl_name_version> features(size / sizeof(cl_name_version));
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_FEATURES, size, features.data(), nullptr) == CL_SUCCESS));
  for (const auto *feature :
       {"__opencl_c_kernel_clock_scope_device", "__opencl_c_kernel_clock_scope_work_group", "__opencl_c_kernel_clock_scope_sub_group"}) {
    INFO(feature);
    CHECK((features ^ exists([&](const cl_name_version &entry) { return std::string(entry.name) == feature; })));
  }
}
